// See https://pbassiner.github.io/blog/defining_multi-project_builds_with_sbt.html

import scala.scalanative.build._

name := "fpp-compiler"
organization in ThisBuild := "gov.nasa.jpl"
scalaVersion in ThisBuild := "2.13.6"

lazy val settings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
  ),
  libraryDependencies ++= Seq(
    "com.github.scopt" %%% "scopt" % "4.0.1",
    "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.0.0",
    "org.scala-lang.modules" %%% "scala-xml" % "2.0.1",
    "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test",
  ),
  nativeConfig ~= {
    _.withMode(Mode.releaseFast).withLTO(LTO.thin)
  },
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oNCXELOPQRM"),
)

lazy val root = (project in file("."))
  .settings(settings)
  .aggregate(
    lib,
  )

lazy val lib = project
  .settings(settings)
  .enablePlugins(ScalaNativePlugin)

