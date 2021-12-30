package zhq

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{H2ZioJdbcContext, Literal}
import zio._

import javax.sql.DataSource

//import io.getquill._
import io.getquill.context.ZioJdbc._
import zio.console.putStrLn


case class Person(id: Int, name: String, age: Int)

object PersonDb {

  object MyH2Context extends H2ZioJdbcContext(Literal)

  import MyH2Context._
  val zioDS = DataSourceLayer.fromPrefix("testH2DB")

  val persons = quote {
    querySchema[Person]("Person")
  }
  implicit val personInsertMeta = insertMeta[Person](_.id)

  val q = quote {
    persons.insert(lift(Person(101, "Alex", 45)))
  }

  // type alias to use for other layers
  type PersonDbEnv = Has[PersonDb.Service]

  // service definition
  trait Service {
    def insert(person: Person): Task[Unit]
  }

  // layer - service implementation
  val live: ZLayer[Any, Nothing, PersonDbEnv] = ZLayer.succeed {
    new Service {
      override def insert(person: Person): Task[Unit] = Task {
        // can replace this with an actual DB SQL string
        MyH2Context.run(q).onDataSource.provideCustomLayer(zioDS)
        println(s"[Database] insert into public.user values ('${person.name}')")
      }
    }
  }

  // accessor
  def insert(person: Person): ZIO[PersonDbEnv, Throwable, Unit] =
    ZIO.accessM(_.get.insert(person))
}