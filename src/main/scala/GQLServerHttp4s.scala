import caliban.Http4sAdapter
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.metrics.prometheus.{Prometheus, PrometheusExportService}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, Metrics}
import zio._
import zio.console.putStrLn
import zio.interop.catz._

import scala.concurrent.duration._

import scala.concurrent.duration._

object GQLServerHttp4s extends CatsApp {

  type ExampleTask[A] = RIO[ZEnv, A]

  object ioz extends Http4sDsl[ExampleTask]
  import ioz._
  val otherRoutes:HttpRoutes[ExampleTask] = HttpRoutes.of[ExampleTask] {
    case _@GET -> Root => Ok("Hello, Welcome to Scala HTTP4S Server")
  }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    //This method call will manage postgres database tables,types,functions in different environments individually.
    //DbMigration()
    val serverManaged = for {
      dbTransactor                          <- CalibanHelpers.dbResource.toManaged
      metricsSvc                            <- PrometheusExportService.build[ExampleTask].toManaged
      metrics                               <- Prometheus.metricsOps[ExampleTask](metricsSvc.collectorRegistry, "server").toManaged
      loggerInterpreter             <- CalibanHelpers.loggerHttp4sInterpreter(dbTransactor).toManaged_

      server                                <- BlazeServerBuilder[ExampleTask]
        .bindHttp(8080, "0.0.0.0")
        .withConnectorPoolSize(16)
        .withResponseHeaderTimeout(610.seconds)
        .withIdleTimeout(620.seconds)
        .withExecutionContext(platform.executor.asEC)
        .withHttpApp(
          Router[ExampleTask](
            "/"    -> otherRoutes,
            "/api/bqlogs"        -> CORS(Http4sAdapter.makeHttpService(loggerInterpreter)),
          ).orNotFound
        )
        .resource
        .toManaged

    } yield server
    serverManaged.useForever.as(0).catchAll(err => putStrLn(err.toString).as(1))
  }
}
