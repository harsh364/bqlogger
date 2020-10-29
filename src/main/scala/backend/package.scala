package backends

import backend.Schema._
import zio.{Has, ZIO}


package object bq_logs {

  object BQLogsDB {

    trait Service {

      def getBQLogs()
      : ZIO[LoggerAkka, Throwable, List[BQLogsDetails]]
    }


    type LoggerAkka = Has[BQLogsDB.Service]

    def getBQLogs()
    = ZIO.accessM[LoggerAkka](_.get.getBQLogs())
  }
}
