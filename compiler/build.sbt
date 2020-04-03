// See https://pbassiner.github.io/blog/defining_multi-project_builds_with_sbt.html

name := "fpp-compiler"
organization in ThisBuild := "gov.nasa.jpl"
scalaVersion in ThisBuild := "2.13.1"

lazy val settings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
  ),
  libraryDependencies ++= dependencies, 
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oNCXELOPQRM"),
)

lazy val dependencies = Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test",
  "com.github.scopt" %% "scopt" % "4.0.0-RC2",
)

lazy val root = (project in file("."))
  .settings(settings)
  .aggregate(
    lib,
    fpp_syntax,
    fpp_depend,
  )

lazy val lib = project
  .settings(settings)

lazy val fpp_syntax = (project in file("tools/fpp-syntax"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(AssemblyPlugin)

lazy val fpp_depend = (project in file("tools/fpp-depend"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(AssemblyPlugin)
