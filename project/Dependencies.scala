import sbt._

object Dependencies {
  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.1.9"

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion
  )

  val logbackVersion = "1.2.3"

  lazy val logging = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion
  )
  
  val scalatestVersion = "3.0.8"
  val scalamockVersion = "4.1.0"
  
  lazy val testing = Seq(
    "org.scalactic" %% "scalactic" % scalatestVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "org.scalamock" %% "scalamock" % scalamockVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )

  lazy val coreDeps = akka ++ logging ++ testing
}
