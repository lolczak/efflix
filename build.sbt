ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "efflix"

lazy val efflix = (project in file("."))
  .settings(version := "0.1")
  .aggregate(`type-classes`, `zio-instances`)

lazy val `type-classes` = (project in file("type-classes"))

lazy val `zio-instances` = (project in file("zio-instances"))
  .settings(libraryDependencies ++= Seq("dev.zio" %% "zio" % "1.0.0-RC18-2"))
