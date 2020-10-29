package backends.sales_performance_dashboard
import java.io.{PrintWriter, StringWriter}

import backend.Schema.{BQLogsDetails, BQLogsResult}
import backends.bq_logs.BQLogsDB.LoggerAkka
import backends.bq_logs.BQLogsDB
import backends.viewership.oneview.BQLogsObject
import caliban.CalibanError.ExecutionError
import cats.effect.Blocker
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import org.slf4j.{Logger, LoggerFactory}
import zio._

import scala.concurrent.ExecutionContext


object BQLogsService {

    def liveAkka(connectEC: ExecutionContext, dbUrl: String, dbUser: String, dbPass: String): ZLayer[Any, Throwable, LoggerAkka] = ZLayer.fromEffect(Task {
      import zio.interop.catz._
      val dataSource = new HikariDataSource()
      dataSource.setDriverClassName("org.postgresql.Driver")
      dataSource.setJdbcUrl(dbUrl)
      dataSource.setUsername(dbUser)
      dataSource.setPassword(dbPass)
      val pgTransactor: HikariTransactor[Task] = HikariTransactor[Task](dataSource, connectEC, Blocker.liftExecutionContext(connectEC))
      BQLogsService(pgTransactor)
    })

    def liveHttp4s(pgTransactor: HikariTransactor[Task]): ZLayer[Any, Throwable, LoggerAkka] =
      ZLayer.fromEffect(Task(BQLogsService(pgTransactor)))

}

final case class BQLogsService(trans : HikariTransactor[Task]) extends BQLogsDB.Service {

  val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def getBQLogs()
  :ZIO[LoggerAkka,Throwable,List[BQLogsDetails]] =  {
    val t1 = System.nanoTime
    try {
      for {
        t <- BQLogsObject.apply(trans)
        _ <- Task{logger.info(s"logger Api took ${(System.nanoTime - t1) / 1e9d}")}
      } yield t
    }
    catch {
      case e: Throwable =>
        logger.info(s"logger Api took ${(System.nanoTime - t1) / 1e9d}")
//        logCustomErrorInRollbar(rollBarContext, e, "getOverallCprpMetrics", args.toString)
        throw e
    }
    }.mapError { e =>
    logger.error(e.getMessage)
    ExecutionError(e.getMessage)
  }
}