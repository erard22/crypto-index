name := "crypto-index"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.2"
val akkaHttpVersion = "10.0.9"
val elastic4sVersion = "5.4.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
