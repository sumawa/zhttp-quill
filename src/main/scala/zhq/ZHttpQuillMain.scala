package zhq

import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

import scala.util.Try

object ZHttpQuillMain extends App {
  // Set a port
  private val PORT = 8090

  val personBackendLayer = PersonDb.live
  private val app = Http.collectM[Request] {
    case Method.GET -> !! / "person"    =>
        for {
          i <- PersonDb.insert(Person(102, "Alice", 27))
          persons <- PersonDb.get()
        } yield (Response.text(s"Persons: $persons"))
  }.provideCustomLayer(personBackendLayer)

  private val server =
    Server.port(PORT) ++              // Setup port
      Server.paranoidLeakDetection ++ // Paranoid leak detection (affects performance)
      Server.app(app)       // Setup the Http app

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