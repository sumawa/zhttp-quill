package com.sa

import io.getquill._
import io.getquill.context.ZioJdbc._
import zio.console.putStrLn
import zio.{ App, ExitCode, URIO }

object ZioApp extends App {

//  object MyPostgresContext extends PostgresZioJdbcContext(Literal)
  object MyH2Context extends H2ZioJdbcContext(Literal)
  import MyH2Context._

  case class Person(name: String, age: Int)

  val zioDS = DataSourceLayer.fromPrefix("testH2DB")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val persons = quote {
      querySchema[Person]("Person")
    }
    implicit val personInsertMeta = insertMeta[Person](_.name)

    val q = quote {
      persons.insert(lift(Person("Alex",45)))
    }
    MyH2Context.run(q).onDataSource

    val people = quote {
      query[Person].filter(p => p.name == "Alex")
    }
    MyH2Context.run(q).onDataSource
      .tap {
        result =>
          putStrLn(result.toString)
          MyH2Context.run(people).onDataSource
            .tap(result1 => putStrLn(result1.toString))
      }
      .provideCustomLayer(zioDS)
      .exitCode
  }
}