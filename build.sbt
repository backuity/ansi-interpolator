import sbt.Keys._
import sbt._

lazy val root = (project in file(".")).
  settings(
    organization := "org.backuity",
    name := "ansi-interpolator",
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.11.8", "2.12.0"),
    version := "1.2.0",

    scalacOptions ++= Seq("-deprecation", "-unchecked"),

    libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,

        // Tests
        "org.backuity" %% "matchete" % "1.11" % "test",
        "com.chuusai" %% "shapeless" % "2.3.2" % "test",
        "com.novocode" % "junit-interface" % "0.11" % "test"),

    // Sonatype OSS deployment
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,

    // replace publish by publishSigned
    publish := com.typesafe.sbt.pgp.PgpKeys.publishSigned.value,

    pomIncludeRepository := { _ => false},

    homepage := Some(url("https://github.com/backuity/ansi-interpolator")),
    licenses := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,

    pomExtra :=
      <scm>
        <url>git@github.com:backuity/ansi-interpolator.git</url>
        <connection>scm:git:git@github.com:backuity/ansi-interpolator.git</connection>
      </scm>
        <developers>
          <developer>
            <id>backuitist</id>
            <name>Bruno Bieth</name>
            <url>https://github.com/backuitist</url>
          </developer>
        </developers>
  )
