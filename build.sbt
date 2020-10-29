scalaVersion := "2.12.10"
version in ThisBuild := "0.1.0"
import Dependencies._

libraryDependencies ++=  scalaj ++ json4s  ++ caliban ++ logging ++ jwt ++ postgres ++ http4s

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions ++= Seq("-Ypartial-unification")
addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3")
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)


packageName in Docker := f"""mint-bi-reporting/uat/bqlogger"""
dockerRepository := Some("asia.gcr.io")
//mainClass in Compile := Some("GQLServerHttp4s")
mainClass in Compile := Some("GQLServerHttp4s")
dockerBaseImage := "openjdk:jre"
dockerExposedPorts := Seq(8080, 8080)