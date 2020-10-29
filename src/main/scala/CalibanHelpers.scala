import Utils.Configs
import backends.sales_performance_dashboard.{BQLogsService, LoggerApi}
import caliban.{CalibanError, GraphQLInterpreter}
import cats.effect.{Blocker, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import zio.{ZEnv, ZIO, Task}
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object CalibanHelpers {

  def dbResource: Resource[Task, HikariTransactor[Task]] = {
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[Task](20)
      xa <- HikariTransactor.newHikariTransactor[Task](
        "org.postgresql.Driver", // driver classname
        Configs.DB_URL, // connect URL
        Configs.DB_USER, // username
        Configs.DB_PASS, // password
        connectEC, // await connection here
        Blocker.liftExecutionContext(connectEC) // transactEC // execute JDBC operations here
      )
    } yield xa
  }

  def loggerAkkaInterpreter(executionContext: ExecutionContext): ZIO[Any, CalibanError, GraphQLInterpreter[ZEnv, Throwable]] =
    BQLogsService.liveAkka(executionContext,Configs.DB_URL,Configs.DB_USER,Configs.DB_PASS)
      .memoize
      .use {
        layer => LoggerApi.api.interpreter.map(_.provideCustomLayer(layer))
      }

  def loggerHttp4sInterpreter(transactor: HikariTransactor[Task]): ZIO[Any, CalibanError, GraphQLInterpreter[ZEnv, Throwable]] =
    BQLogsService.liveHttp4s(transactor)
      .memoize
      .use {
        layer => LoggerApi.api.interpreter.map(_.provideCustomLayer(layer))
      }

}
