val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-3-advanced",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
    )
  )
