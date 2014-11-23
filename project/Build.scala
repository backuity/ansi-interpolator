import sbt.Keys._
import sbt._

object BuildSettings {
  val buildSettings = Seq(
    organization := "org.backuity",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.4",
    version := "1.0-SNAPSHOT",

    scalacOptions ++= Seq("-deprecation", "-unchecked"),

    // Sonatype OSS deployment
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
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
}

object AnsiInterpolatorBuild extends Build {

  import BuildSettings._

  lazy val root = Project("root",
    file("."),
    settings = buildSettings ++ Seq(publishArtifact := false)
  ) aggregate(macros, tests)

  lazy val macros = Project("macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      name := "ansi-interpolator",
      libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _),
      libraryDependencies ++= Seq(
          // Tests
          "org.backuity" %% "matchete" % "1.11" % "test",
          "com.novocode" % "junit-interface" % "0.11" % "test")
    )
  )

  lazy val tests = Project("tests",
    file("tests"),
    settings = buildSettings ++ Seq(publishArtifact := false,

      // for testing with partest
      libraryDependencies += "org.scala-lang.modules" %% "scala-partest-interface" % "0.4.0",

      // the actual partest the interface calls into -- must be binary version close enough to ours
      // so that it can link to the compiler/lib we're using (testing)
      libraryDependencies += "org.scala-lang.modules" %% "scala-partest" % "1.0.1" % "test",

      fork in Test := true,

//      javaOptions in Test += "-Xmx1G",

      testFrameworks += new TestFramework("scala.tools.partest.Framework"),

      definedTests in Test += new sbt.TestDefinition( "partest",
        // marker fingerprint since there are no test classes to be discovered by sbt:
        new sbt.testing.AnnotatedFingerprint {
          def isModule = true
          def annotationName = "partest"
        },
        true,
        Array()
      )

    )
  ) dependsOn macros
}