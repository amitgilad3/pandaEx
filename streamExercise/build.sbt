name := "streamExercise"

version := "1.0"

scalaVersion := "2.11.8"


val akkaVersion = "2.4.3"
val sprayVersion = "1.3.3"
val json4sVersion = "3.3.0"
val scalaEhCacheVersion = "0.7.5"
val scalaTestVersion = "2.2.6"


val commonBackendDep = Seq(
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
  "org.json4s" %% "json4s-native" % json4sVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.json4s" %% "json4s-ext" % json4sVersion,
  "com.github.cb372" %% "scalacache-ehcache" % scalaEhCacheVersion,
  "org.mongodb" %% "casbah" % "3.1.0"
)

val sprayDependencies = Seq(
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-http" % sprayVersion,
  "io.spray" %% "spray-httpx" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-json" % "1.3.2",//No 1.3.3 version
  "io.spray" %% "spray-testkit"  % sprayVersion % "test"
)

lazy val akkaDependencies = Seq(

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "2.0.4"

)

libraryDependencies ++= sprayDependencies ++ akkaDependencies ++ commonBackendDep