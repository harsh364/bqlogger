import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{path, _}
import caliban.interop.circe.AkkaHttpCirceAdapter
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import org.slf4j.{Logger, LoggerFactory}
import zio.{BootstrapRuntime, Runtime, ZEnv}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success}

object GQLServerAkka extends App with AkkaHttpCirceAdapter with BootstrapRuntime {

  implicit val system: ActorSystem                        = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val runtime: Runtime[ZEnv]                     = Runtime.default
  val logger: Logger                                      = LoggerFactory.getLogger(getClass.getName)

  val dashboardInterpreter        = unsafeRun(CalibanHelpers.loggerAkkaInterpreter(platform.executor.asEC))

  val route = cors() {
    pathEndOrSingleSlash {
      complete(HttpResponse(status = StatusCodes.OK, entity = "Welcome to Akka Server"))
    }~ path("api" / "bqlogger") {
      adapter.makeHttpService(dashboardInterpreter)
    }
  }
  logger.info(s"Starting Server at http://0.0.0.0:8080/")
  val binding =  Http().bindAndHandle(route, "0.0.0.0", 8080)
  binding.onComplete {
    case Success(_)     => logger.info("Started server!")
    case Failure(error) => logger.error(s"Failed: ${error.getMessage}")
  }
  Await.result(binding, 10.seconds)
}
