import sbt._

object Dependencies {
  private val zioVersion       = "1.0.0-RC17"
  private val zioCatsVersion   = "2.0.0.0-RC6"
  private val calibanVersion   = "0.7.5"
  private val googleCloudVersion = "1.98.0"
  private val scalatestVersion = "3.0.5"
  private val scalajVersion    = "2.4.2"
  private val json4sVersion    = "3.6.7"
  private val rollBarVersion   = "1.3.1"
  private val doobieVersion    = "0.8.8"
  private val http4sVersion    = "0.21.3"

  lazy val logging = List(
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val jwt = List(
    "com.pauldijou" %% "jwt-core" % "4.2.0"
  )

  lazy val scalaj = List(
    "org.scalaj" %% "scalaj-http" % scalajVersion
  )
  lazy val json4s = List(
    "org.json4s" %% "json4s-native" % json4sVersion
  )
  lazy val caliban = List(
    "com.github.ghostdogpr" %% "caliban" % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-http4s" % calibanVersion,
    "com.github.ghostdogpr" %% "caliban-akka-http" % calibanVersion,
    "de.heikoseeberger"     %% "akka-http-circe" % "1.31.0",
    "ch.megard" %% "akka-http-cors" % "0.4.3"
  )

  lazy val test = List(
    "org.scalatest" %% "scalatest" % scalatestVersion,
//    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "dev.zio"    %% "zio-test"            % zioVersion,
    "dev.zio"    %% "zio-test-sbt"        % zioVersion
  ).map(_ % Test)

  lazy val http4s = List(
    "org.http4s" %% "http4s-prometheus-metrics" % http4sVersion
  )
  lazy val postgres = List(
    "org.tpolecat" %% "doobie-core"     % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
    "org.tpolecat" %% "doobie-hikari"   % doobieVersion,
    "org.tpolecat" %% "doobie-h2"       % doobieVersion,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion
  )
}
