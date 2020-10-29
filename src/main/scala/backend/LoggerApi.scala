package backends.sales_performance_dashboard
import backend.Schema.{BQLogsDetails, BQLogsResult, LoggerArgs}
import backends.bq_logs.BQLogsDB.LoggerAkka
import backends.bq_logs.BQLogsDB
import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.{GraphQL, RootResolver}
import zio.ZIO
import zio.clock.Clock
import zio.console.Console

import scala.language.postfixOps

object LoggerApi extends GenericSchema[LoggerAkka] {

  case class Queries(
                      bq_logs :  ZIO[LoggerAkka,Throwable, List[BQLogsDetails]]

                    )

  val api: GraphQL[Console with Clock with LoggerAkka] =
    graphQL(
      RootResolver(
        Queries(
          BQLogsDB.getBQLogs()
        )
      )
    )
}

