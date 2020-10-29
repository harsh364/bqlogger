package backend

object Schema {
  case class BQLogsDetails(start_time: Option[String] = None, email: Option[String] = None, query: Option[String] = None, duration: Option[String] = None,status: Option[String] = None );
  case class BQLogsResult(details: List[BQLogsDetails])
  case class LoggerArgs()
}