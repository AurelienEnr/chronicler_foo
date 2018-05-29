name := "Chronicler macro test"

version := "0.0"

description := "Chronicler macro test"

scalaVersion := "2.12.3"

scalacOptions ++= Seq("-deprecation", "-feature")

// json
lazy val json4sVersion = "3.6.0-M2"
libraryDependencies += "org.json4s" %% "json4s-native" % json4sVersion

// logging
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.5"

// influx connector
val influxVersion = "0.2.3"
libraryDependencies += "com.github.fsanaulla" %% "chronicler-async-http" % influxVersion
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % influxVersion

// Demonstration and tests
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test

Revolver.settings
