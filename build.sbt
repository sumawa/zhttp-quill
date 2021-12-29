val ZIOVersion = "1.0.13"
//val QuillVersion = "3.5.0"
val QuillVersion = "3.8.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "zhttp-quill",
    organization := "com.sa",
    scalaVersion := "2.12.11",
//    scalaVersion := "2.13.1",
//    initialCommands in Compile in console :=
//      """|import zio._
//         |import zio.Console._
//         |implicit class RunSyntax[E, A](io: ZIO[ZEnv, E, A]){ def unsafeRun: A = Runtime.default.unsafeRun(io) }
//    """.stripMargin
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias(
  "check",
  "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck"
)

libraryDependencies ++= Seq(
  // ZIO
  "dev.zio" %% "zio"          % ZIOVersion,
  "dev.zio" %% "zio-streams"  % ZIOVersion,
  "dev.zio" %% "zio-test"     % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  // quill
//  "io.getquill" %% "quill-jdbc" % "3.12.1-SNAPSHOT"
  "com.h2database" % "h2" % "1.4.199",
  "com.typesafe"               %  "config"        % "1.4.1",
  "io.getquill" %% "quill-jdbc" % QuillVersion,
  "io.getquill" %% "quill-jdbc-zio" % QuillVersion
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions in Compile in console := Seq(
  "-Ypartial-unification",
  "-language:higherKinds",
  "-language:existentials",
  "-Yno-adapted-args",
  "-Xsource:2.13",
  "-Yrepl-class-based",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-Yrangepos",
  "-feature",
  "-Xfuture",
  "-unchecked",
  "-Xlint:_,-type-parameter-shadow",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-opt-warnings",
  "-Ywarn-extra-implicit",
  "-Ywarn-unused:_,imports",
  "-Ywarn-unused:imports",
  "-opt:l:inline",
  "-opt-inline-from:<source>",
  "-Ypartial-unification",
  "-Yno-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit"
)
