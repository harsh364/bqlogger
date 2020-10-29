package backends.viewership.oneview

import backend.Schema.{BQLogsDetails, BQLogsResult}
import org.slf4j.{Logger, LoggerFactory}
import zio.Task
import doobie.Fragment
import doobie.hikari.HikariTransactor
import doobie.implicits._
import zio.interop.catz._

import scala.util.Try

object BQLogsObject {
  val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def apply(transactor: HikariTransactor[Task])
  : Task[List[BQLogsDetails]] = {

    val query = fr""" SELECT * FROM bqdump;""".stripMargin

    query.query[BQLogsDetails].to[List].transact(transactor)
  }
}
