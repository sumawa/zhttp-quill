package com.sa

import io.getquill._
import io.getquill.context.ZioJdbc._
import zio.console.putStrLn
import zio.{App, ExitCode, Has, URIO, ZIO}

import java.io.Closeable
import java.sql.SQLException
import javax.sql.DataSource

object ZioApp1 extends App {

  //  object MyPostgresContext extends PostgresZioJdbcContext(Literal)
  object MyH2Context extends H2ZioJdbcContext(Literal)

  import MyH2Context._

  case class Person(id: Int, name: String, age: Int)

  val zioDS = DataSourceLayer.fromPrefix("testH2DB")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val persons = quote {
      querySchema[Person]("Person1")
    }
    implicit val personInsertMeta = insertMeta[Person](_.id)

    val q = quote {
      persons.insert(lift(Person(101, "Alex", 45)))
    }
    MyH2Context.run(q).onDataSource

    //    val people = quote {
    //      query[Person].filter(p => p.name == "Alex")
    //    }

    case class Person1(id: Int, name: String, age: Int)
    val people = quote {
      query[Person1]
    }

    //    val (inserted, product) =
    val z: ZIO[Has[DataSource with Closeable], SQLException, (Long, List[Person1])] =
      (for {
        i <- MyH2Context.run(q).onDataSource
        ps <- MyH2Context.run(people).onDataSource
      } yield (i, ps.map(_.copy(id = i.toInt))))

    z
      .tap(res => putStrLn(res.toString()))
      .provideCustomLayer(zioDS)
      .exitCode
  }
}