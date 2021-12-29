package com.sa

object ExmQuill extends App{

  import io.getquill._
  import io.getquill.context.jdbc.JdbcContext
  import io.getquill.context.sql.idiom.SqlIdiom

  case class Laptop(id: Long, name: String)

  class LaptopDaoQuill[I <: SqlIdiom, N <: NamingStrategy](implicit ctx: JdbcContext[I, N]) {

    import ctx._

    def insert(obj: Laptop) = {
      val laptops = quote {
        querySchema[Laptop]("laptops")
      }
      implicit val personInsertMeta = insertMeta[Laptop](_.id)

      val q = quote {
        laptops.insert(lift(obj))
      }
      ctx.run(q)
    }

    def byName(name: String) = {

      val laptops = quote {
        querySchema[Laptop]("laptops")
      }

//      val q = quote {
//        for {
//          p <- query[Laptop] if (p.id == 1L)
//        } yield {
//          (p.id, p.name)
//        }
//      }

      val q1 = quote {
//        laptops.filter(p => p.name == "laptop")
//          .map(_.id)
        laptops.filter(p => p.id == 1L)
          .map(_.name)
      }

//      val res = ctx.run(q)
        val res = ctx.run(q1)
            println(s";;;;; ${res}")
            res

    }
  }

//  object Main {
//    def main(args: Array[String]): Unit = {
      //    implicit val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
//      implicit val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
      implicit val ctx = new H2JdbcContext(Literal, "testH2DB")

      val laptopDao = new LaptopDaoQuill

      laptopDao.insert(Laptop(0L, "laptopuuu"))

      laptopDao.byName("laptop")
//    }
//  }
}
