name := """web-service-test-assignment"""
organization := "net.westaystay"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DockerPlugin)

scalaVersion := "2.13.11"

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.typelevel" %% "cats-effect" % "3.5.1",
  "org.julienrf" %% "play-json-derived-codecs" % "7.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "de.leanovate.play-mockws" %% "play-mockws" % "2.8.1" % Test
)

Global / onChangedBuildSource := ReloadOnSourceChanges

Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null",
  "-Dlogback.configurationFile=logback-docker.xml"
)

dockerExposedPorts += 9000

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "net.westaystay.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "net.westaystay.binders._"
