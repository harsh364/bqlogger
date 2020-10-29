package Utils

import scala.io.Source

object Configs {
  lazy val ENV: String = "local"
  val DB_URL: String = ENV match{
    case "local" => "jdbc:postgresql://localhost:5432/postgres"
  }
  val DB_USER: String = ENV match {
    case "local" => "raih"
  }
  val DB_PASS: String = ENV match {
    case "local" => ""
  }
}
