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

  val persons = quote {
    querySchema[Person]("Person")
  }
  implicit val personInsertMeta = insertMeta[Person](_.id)

  val people = quote {
    query[Person]
  }

  val q = quote {
    persons.insert(lift(Person(101, "Alex", 45)))
  }

  // type alias to use for other layers
  type PersonDbEnv = Has[PersonDb.Service]

  // service definition
  trait Service {
    def insert(person: Person): Task[Long]
    def get(): Task[List[Person]]
  }

  // layer - service implementation
  val live: ZLayer[ZEnv, Nothing, PersonDbEnv] = ZLayer.succeed {
    new Service {
      override def insert(person: Person): Task[Long] = for {
          i <- Ctx.run(q).implicitDS
          _ <- Task.effect(println(s"iiii: $i"))
        } yield i

      override def get(): Task[List[Person]] = for {
        ps <- Ctx.run(people).implicitDS
        _ <- Task.effect(println(s"persons $ps"))
      } yield ps
    }
  }

  // accessor
  def insert(person: Person): ZIO[PersonDbEnv, Throwable, Long] =
    ZIO.accessM(_.get.insert(person))

  def get(): ZIO[PersonDbEnv,Throwable,List[Person]] =
    ZIO.accessM(_.get.get())

  def get1(): ZIO[zio.ZEnv, Throwable, List[Person]] = ???

}