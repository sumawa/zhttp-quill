package com.sa

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{H2ZioJdbcContext, Literal}
import zio._

import javax.sql.DataSource

//import io.getquill._
import io.getquill.context.ZioJdbc._
import zio.console.putStrLn
import zio.{App, ExitCode, Has, URIO, ZIO}

/**
 * Simple wrapper over NioEventLoopGroup
 */
object XModule extends scala.App {

  object MyH2Context extends H2ZioJdbcContext(Literal)

  import MyH2Context._

  case class Person(id: Int, name: String, age: Int)

  val zioDS = DataSourceLayer.fromPrefix("testH2DB")

  val persons = quote {
    querySchema[Person]("Person1")
  }
  implicit val personInsertMeta = insertMeta[Person](_.id)

  val q = quote {
    persons.insert(lift(Person(101, "Alex", 45)))
  }

  case class Person1(id: Int, name: String, age: Int)
  val people = quote {
    query[Person1]
  }

  import java.io.Closeable
  import java.sql.SQLException

  val z: ZIO[Has[DataSource with Closeable], SQLException, (Long, List[Person1])] =
    (for {
      i <- MyH2Context.run(q).onDataSource
      ps <- MyH2Context.run(people).onDataSource
    } yield (i, ps.map(_.copy(id = i.toInt))))

  val r: ZIO[zio.ZEnv, Throwable, (Long, List[Person1])] = z
    .tap(res => putStrLn(res.toString()))
    .provideCustomLayer(zioDS)

  type PersonData = Has[(Long, List[Person1])]
  def live: ZLayer[Has[DataSource with Closeable], SQLException, PersonData] = z.toLayer

  val env = zioDS >>> live

  val c = zio.Runtime.default.unsafeRun(r.provideCustomLayer(env))
  println(s"c: $c")
  //  object Live {
//    def nio(nThreads: Int): ZManaged[Any, Nothing, channel.EventLoopGroup] =
//      make(UIO(new channel.nio.NioEventLoopGroup(nThreads)))
//
//    def make(eventLoopGroup: UIO[channel.EventLoopGroup]): ZManaged[Any, Nothing, channel.EventLoopGroup] =
//      eventLoopGroup.toManaged(ev => ChannelFuture.unit(ev.shutdownGracefully).orDie)
//  }


//  def nio(nThreads: Int = 0): ZLayer[Any, Nothing, XModule] = Live.nio(nThreads).toLayer
//
//  object Live {
//    def nio(nThreads: Int): ZManaged[Any, Nothing, channel.EventLoopGroup] =
//      make(UIO(new channel.nio.NioEventLoopGroup(nThreads)))
//
//    def make(eventLoopGroup: UIO[channel.EventLoopGroup]): ZManaged[Any, Nothing, channel.EventLoopGroup] =
//      eventLoopGroup.toManaged(ev => ChannelFuture.unit(ev.shutdownGracefully).orDie)
//  }

}