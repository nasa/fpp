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
    fpp_check,
    fpp_depend,
    fpp_filenames,
    fpp_format,
    fpp_locate_defs,
    fpp_locate_uses,
    fpp_syntax,
    fpp_to_xml,
  )

lazy val fpp_check = (project in file("tools/fpp-check"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_depend = (project in file("tools/fpp-depend"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_filenames = (project in file("tools/fpp-filenames"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_format = (project in file("tools/fpp-format"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_locate_defs = (project in file("tools/fpp-locate-defs"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_locate_uses = (project in file("tools/fpp-locate-uses"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_syntax = (project in file("tools/fpp-syntax"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val fpp_to_xml = (project in file("tools/fpp-to-xml"))
  .settings(settings)
  .dependsOn(lib)
  .enablePlugins(ScalaNativePlugin)

lazy val lib = project
  .settings(settings)
  .enablePlugins(ScalaNativePlugin)

