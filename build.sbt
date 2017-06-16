name := "es-client"

organization := "com.github.piotr-kalanski"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/piotr-kalanski/es-client"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/piotr-kalanski/es-client"),
    "scm:git:ssh://github.com/piotr-kalanski/es-client.git"
  )
)

developers := List(
  Developer(
    id    = "kalan",
    name  = "Piotr Kalanski",
    email = "piotr.kalanski@gmail.com",
    url   = url("https://github.com/piotr-kalanski")
  )
)

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

coverageExcludedPackages := "com.datawizards.esclient.examples.*"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
