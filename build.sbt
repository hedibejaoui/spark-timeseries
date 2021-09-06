import sbt.Keys.libraryDependencies
import sbt._

ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.14"
ThisBuild / useCoursier := false
Global / onChangedBuildSource := IgnoreSourceChanges

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

lazy val sparkTimeSeries = (project in file("."))
  .settings(
    name := "spark-timeseries",
    // Hack to undo the "Provided" annotation on the dependencies when running energon locally
    run in Compile := Defaults
      .runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
      .evaluated,
    runMain in Compile := Defaults.runMainTask(fullClasspath in Compile, runner in (Compile, run)).evaluated,
    // These are necessary as of sbt 1.3.1 version. See:
    // https://github.com/sbt/sbt/issues/5075
    // https://stackoverflow.com/questions/44298847/why-do-we-need-to-add-fork-in-run-true-when-running-spark-sbt-application
    fork := true,
    baseDirectory in run := (baseDirectory in LocalRootProject).value,
    // exclude Scala library from assembly
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    // disable tests when running assembly
    test in assembly := {},
    // path where to generate the assembly
    assemblyOutputPath in assembly := file(
      "jars/" + name.value + "_" + scalaVersion.value + "_" + sbtVersion.value + ".jar"),
    // Dependencies
    libraryDependencies += dependencies.doxiaModuleMarkdown,
    libraryDependencies += dependencies.reflowVelocityTools,
    libraryDependencies += dependencies.velocity,
    libraryDependencies += dependencies.mavenScmProviderGitexe,
    libraryDependencies += dependencies.hadoopYarnClient,
    libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.2.2",
    libraryDependencies += dependencies.scalaLibrary,
    libraryDependencies += dependencies.scalap,
    libraryDependencies += dependencies.scalaCompiler,
    libraryDependencies += dependencies.sparkCore,
    libraryDependencies += dependencies.sparkSql,
    libraryDependencies += dependencies.sparkCatalyst,
    libraryDependencies += dependencies.sparkMllib,
    libraryDependencies += dependencies.threetenExtra,
    libraryDependencies += dependencies.breeze.exclude("junit", "junit").exclude("org.apache.commons", "commons-math3"),
    libraryDependencies += dependencies.breezeViz.exclude("junit", "junit").exclude("org.apache.commons", "commons-math3"),
    libraryDependencies += dependencies.commonsMath,
    libraryDependencies += dependencies.scalatest,
    libraryDependencies += dependencies.junit
  )

lazy val dependencies =
  new {
    val doxiaModuleMarkdownVersion    = "1.6"
    val reflowVelocityToolsVersion    = "1.1.1"
    val velocityVersion               = "1.7"
    val mavenScmProviderGitexeVersion = "1.9.4"
    val hadoopYarnClientVersion       = "3.2.2"
    val scalaLibraryVersion           = "2.12.14"
    val sparkVesion                   = "3.0.1"
    val threetenExtraVersion          = "0.9"
    val breezeVersion                 = "1.3"
    val commonsMathVersion            = "3.4.1"
    val scalatestVersion              = "3.0.2"
    val junitVersion                  = "4.12"

    val doxiaModuleMarkdown    = "org.apache.maven.doxia"  % "doxia-module-markdown"     % doxiaModuleMarkdownVersion
    val reflowVelocityTools    = "lt.velykis.maven.skins"  % "reflow-velocity-tools"     % reflowVelocityToolsVersion
    val velocity               = "org.apache.velocity"     % "velocity"                  % velocityVersion
    val mavenScmProviderGitexe = "org.apache.maven.scm"    % "maven-scm-provider-gitexe" % mavenScmProviderGitexeVersion
    val hadoopYarnClient       = "org.apache.hadoop"       % "hadoop-yarn-client"        % hadoopYarnClientVersion       % "provided"
    val scalaLibrary           = "org.scala-lang"          % "scala-library"             % scalaLibraryVersion
    val scalap                 = "org.scala-lang"          % "scalap"                    % scalaLibraryVersion
    val scalaCompiler          = "org.scala-lang"          % "scala-compiler"            % scalaLibraryVersion
    val sparkCore              = "org.apache.spark"        %% "spark-core"               % sparkVesion                   % "provided"
    val sparkSql               = "org.apache.spark"        %% "spark-sql"                % sparkVesion                   % "provided"
    val sparkCatalyst          = "org.apache.spark"        %% "spark-catalyst"           % sparkVesion                   % "provided"
    val sparkMllib             = "org.apache.spark"        %% "spark-mllib"              % sparkVesion                   % "provided"
    val threetenExtra          = "org.threeten"            % "threeten-extra"            % threetenExtraVersion
    val breeze                 = "org.scalanlp"            %% "breeze"                   % breezeVersion
    val breezeViz              = "org.scalanlp"            %% "breeze-viz"               % breezeVersion
    val commonsMath            = "org.apache.commons"      % "commons-math3"             % commonsMathVersion
    val scalatest              = "org.scalatest"           %% "scalatest"                % scalatestVersion              % "test"
    val junit                  = "junit"                   % "junit"                     % junitVersion                  % "test"

  }

assemblyMergeStrategy in assembly := {
  case PathList("org", "aopalliance", _ @_*)      => MergeStrategy.last
  case PathList("javax", "inject", _ @_*)         => MergeStrategy.last
  case PathList("javax", "servlet", _ @_*)        => MergeStrategy.last
  case PathList("javax", "activation", _ @_*)     => MergeStrategy.last
  case PathList("org", "apache", _ @_*)           => MergeStrategy.last
  case PathList("com", "google", _ @_*)           => MergeStrategy.last
  case PathList("com", "esotericsoftware", _ @_*) => MergeStrategy.last
  case PathList("com", "codahale", _ @_*)         => MergeStrategy.last
  case PathList("com", "yammer", _ @_*)           => MergeStrategy.last
  case PathList("META-INF", _ @_*)                => MergeStrategy.discard
  case "about.html"                               => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA"                    => MergeStrategy.last
  case "META-INF/mailcap"                         => MergeStrategy.last
  case "META-INF/mimetypes.default"               => MergeStrategy.last
  case "plugin.properties"                        => MergeStrategy.last
  case "log4j.properties"                         => MergeStrategy.last
  case _                                          => MergeStrategy.first
}
