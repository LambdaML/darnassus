import sbt._

object Dependencies {
  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.1.9"

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion
  )
  
  lazy val coreDeps = akka
}
