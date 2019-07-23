import Dependencies._

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.8"

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `darnassus` = (project in file("."))
  .aggregate(`job-coordinator`)

lazy val `job-coordinator` = (project in file("job-coordinator"))
  .settings(libraryDependencies ++= coreDeps)
