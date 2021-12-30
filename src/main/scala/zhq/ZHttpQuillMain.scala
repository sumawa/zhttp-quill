package zhq

import zhq.PersonDb.PersonDbEnv
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

import java.util.UUID
import scala.util.Try

object ZHttpQuillMain extends App {
  // Set a port
  private val PORT = 8090

  val personBackendLayer: ZLayer[zio.ZEnv, Nothing, PersonDbEnv] =
    PersonDb.live

  import zhttp.http._
  private val app = Http.collectM[Request] {
    case Method.GET -> !! / "person" =>
      for {
        dyn <- ZIO.effect(UUID.randomUUID().toString)
        i <- PersonDb.insert(Person(102, dyn, 27))
        persons <- PersonDb.get()
      } yield (Response.text(s"Persons: $persons"))
    case Method.GET -> !! / "user" / name  =>
      for {
        persons <- PersonDb.byName(name)
      } yield (Response.text(s"Hello: $persons"))
  }.provideCustomLayer(personBackendLayer)

  private val server =
    Server.port(PORT) ++ // Setup port
      Server.paranoidLeakDetection ++ // Paranoid leak detection (affects performance)
      Server.app(app) // Setup the Http app

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    // Configure thread count using CLI
    val nThreads: Int = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    // Create a new server
    server.make
      .use(_ =>
        // Waiting for the server to start
        console.putStrLn(s"Server started on port $PORT")

          // Ensures the server doesn't die after printing
          *> ZIO.never,
      )
      .provideCustomLayer(ServerChannelFactory.auto ++ EventLoopGroup.auto(nThreads))
      .exitCode
  }
}