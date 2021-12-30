package zhq

import io.getquill.util.LoadConfig
import io.getquill.{H2ZioJdbcContext, JdbcContextConfig, Literal}
import zio._

import java.io.Closeable
import javax.sql.DataSource

import io.getquill.context.ZioJdbc._

case class Person(id: Int, name: String, age: Int)

object PersonDb {

  import io.getquill.context.qzio.ImplicitSyntax._

  val impDs: DataSource with Closeable = JdbcContextConfig(LoadConfig("testH2DB")).dataSource
  implicit val env: Implicit[Has[DataSource with Closeable]] = Implicit(Has(impDs))

  object Ctx extends H2ZioJdbcContext(Literal)
  import Ctx._

  val persons = quote { querySchema[Person]("Person") }
//  implicit val personInsertMeta = insertMeta[Person](_.id)

  // type alias to use for other layers
  type PersonDbEnv = Has[PersonDb.Service]

  // service definition
  trait Service {
    def insert(person: Person): Task[Long]
    def get(): Task[List[Person]]
    def byName(name: String): Task[List[Person]]
  }

  // layer - service implementation
  val live: ZLayer[ZEnv, Nothing, PersonDbEnv] = ZLayer.succeed {
    new Service {
      override def insert(person: Person): Task[Long] = {
        val insertQuery = quote { persons.insert(lift(person)) }
        for {
          i <- Ctx.run(insertQuery).implicitDS
//          _ <- Task.effect(println(s"inserted person"))
        } yield i
      }

      override def get(): Task[List[Person]] = for {
        ps <- Ctx.run(persons).implicitDS
//        _ <- Task.effect(println(s"persons $ps"))
      } yield ps

      override def byName(name: String): Task[List[Person]] = {
        val filterQuery = quote {
          query[Person].filter(p => p.name == lift(name))
        }
        for {
          ps <- Ctx.run(filterQuery).implicitDS
//          _ <- Task.effect(println(s"filtered persons $ps"))
        } yield ps
      }
    }
  }

  // accessor
  def insert(person: Person): ZIO[PersonDbEnv, Throwable, Long] =
    ZIO.accessM(_.get.insert(person))

  def get(): ZIO[PersonDbEnv,Throwable,List[Person]] =
    ZIO.accessM(_.get.get())

  def byName(name: String): ZIO[PersonDbEnv,Throwable,List[Person]] =
    ZIO.accessM(_.get.byName(name))

}