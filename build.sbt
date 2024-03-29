// compiler options explicitly disabled from https://github.com/DavidGregory084/sbt-tpolecat
val disabledScalacOptionsCompile = Set(
  "-Xfatal-warnings",
  "-Wunused:params",
  // "-Ywarn-unused:implicits",
  "-Wwarn-unused:locals",
  //  "-Ywarn-numeric-widen",
  "-Wvalue-discard",
  //  "-Xlint:missing-interpolator",
  "-Wunused:explicits",
  "-Wunused:locals",
  "-Wunused:privates",
  //  "-Wnumeric-widen",
  "-Wdead-code"
)

lazy val baseSettings: Seq[Setting[_]] = Seq(
  scalaVersion := "2.13.13",
  Compile / scalacOptions ~= ((options: Seq[String]) => options.filterNot(disabledScalacOptionsCompile)),
  Compile / scalacOptions ++= Seq(
    "-Wconf:any:warning-verbose", // print warnings with their category, site, and (for deprecations) origin and since-version
    // "-Xsource:3", // disabled until IJ Scala plugin has stable support
    "-Vimplicits", // makes the compiler print implicit resolution chains when no implicit value can be found
    "-Vtype-diffs", // turns type error messages into colored diffs between the two types
    "-Wconf:cat=other-match-analysis:error", // report incomplete case match as error
    "-Wconf:cat=other-pure-statement:silent", // silence "unused value of type [???] (add `: Unit` to discard silently)"
    // "-Wnonunit-statement",
  )
)

lazy val `turbocharging-tapir-scalar-2024` = project
  .in(file("."))
  .settings(name := "turbocharging-tapir-scalar-2024")
  .settings(baseSettings: _*)
  .aggregate(core, slides)
  .dependsOn(core, slides)

lazy val core = project
  .settings(name := "core")
  .settings(baseSettings: _*)
  .enablePlugins(JmhPlugin)
  .settings(
    libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "3.5.4",
      "co.fs2" %% "fs2-core" % "3.9.4",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "ch.qos.logback" % "logback-classic" % "1.5.2",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.9.11",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.9.11",
    )
  )

lazy val slides = project
  .settings(name := "slides")
  .settings(baseSettings: _*)
  .settings(
    mdocIn := baseDirectory.value / "mdoc",
    mdocOut := baseDirectory.value / "../docs",
    watchSources ++= (mdocIn.value ** "*.html").get
  )
  .dependsOn(core)
  .enablePlugins(MdocPlugin)
