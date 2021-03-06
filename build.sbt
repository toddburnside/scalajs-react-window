name := "scalajs-react-window"

Global / onChangedBuildSource := ReloadOnSourceChanges

val reactWindow        = "1.8.6"
val reactWindowTypes   = "1.8.2"
val scalaJsReact       = "1.7.7"
val reactJS            = "16.13.1"
val reactTypes         = "16.9.56"
val reactDomTypes      = "16.9.10"
val scalaJsReactCommon = "0.11.2"
val uTest              = "0.7.4"

parallelExecution in (ThisBuild, Test) := false

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS::stopWebpackDevServer; demo/fastOptJS::startWebpackDevServer"
)

inThisBuild(
  List(
    scalaVersion := "2.13.4",
    organization := "io.github.toddburnside",
    sonatypeProfileName := "io.github.toddburnside",
    homepage := Some(
      url("https://github.com/toddburnside/scalajs-react-window")
    ),
    licenses := Seq(
      "BSD 3-Clause License" -> url(
        "https://opensource.org/licenses/BSD-3-Clause"
      )
    ),
    developers := List(
      Developer(
        "toddburnside ",
        "Todd Burnside",
        "toddburnside@gmail.com",
        url("https://github.com/toddburnside")
      ),
      Developer(
        "rpiaggio",
        "Raúl Piaggio",
        "rpiaggio@gmail.com",
        url("https://github.com/rpiaggio")
      ),
      Developer(
        "cquiroz",
        "Carlos Quiroz",
        "carlos.m.quiroz@gmail.com",
        url("https://github.com/cquiroz")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/toddburnside/scalajs-react-window"),
        "scm:git:git@github.com:toddburnside/scalajs-react-window.git"
      )
    )
  )
)

val root =
  project
    .in(file("."))
    .aggregate(facade, demo)
    .settings(
      name := "scalajs-react-window",
      // No, SBT, we don't want any artifacts for root.
      // No, not even an empty jar.
      publish := {},
      publishLocal := {},
      publishArtifact := false,
      Keys.`package` := file("")
    )

lazy val demo =
  project
    .in(file("demo"))
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      version in webpack := "4.32.0",
      version in startWebpackDevServer := "3.3.1",
      webpackConfigFile in fastOptJS := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      webpackConfigFile in fullOptJS := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (resourceDirectory in Compile).value,
      webpackResources := (baseDirectory.value / "webpack") * "*.js",
      includeFilter in webpackMonitoredFiles := "*",
      webpackExtraArgs := Seq("--progress"),
      useYarn := true,
      webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
      webpackBundlingMode in fullOptJS := BundlingMode.Application,
      test := {},
      scalaJSLinkerConfig in (Compile, fullOptJS) ~= { _.withSourceMap(false) },
      // NPM libs for development, mostly to let webpack do its magic
      npmDevDependencies in Compile ++= Seq(
        "postcss-loader"                     -> "3.0.0",
        "autoprefixer"                       -> "9.4.4",
        "url-loader"                         -> "1.1.1",
        "file-loader"                        -> "3.0.1",
        "css-loader"                         -> "2.1.0",
        "style-loader"                       -> "0.23.1",
        "less"                               -> "3.9.0",
        "less-loader"                        -> "4.1.0",
        "webpack-merge"                      -> "4.2.1",
        "mini-css-extract-plugin"            -> "0.5.0",
        "webpack-dev-server-status-bar"      -> "1.1.0",
        "cssnano"                            -> "4.1.8",
        "uglifyjs-webpack-plugin"            -> "2.1.1",
        "html-webpack-plugin"                -> "3.2.0",
        "optimize-css-assets-webpack-plugin" -> "5.0.1",
        "favicons-webpack-plugin"            -> "0.0.9",
        "why-did-you-update"                 -> "1.0.6"
      ),
      npmDependencies in Compile ++= Seq(
        "react"        -> reactJS,
        "react-dom"    -> reactJS,
        "react-window" -> reactWindow
      ),
      // don't publish the demo
      publish := {},
      publishLocal := {},
      publishArtifact := false,
      Keys.`package` := file("")
    )
    .dependsOn(facade)

lazy val facade =
  project
    .in(file("facade"))
    .enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterGenSourcePlugin)
    .settings(
      name := "facade",
      moduleName := "scalajs-react-window",
      // Requires the DOM for tests
      requireJsDomEnv in Test := true,
      // Use yarn as it is faster than npm
      useYarn := true,
      yarnExtraArgs := {
        if (insideCI.value) List("--frozen-lockfile") else List.empty
      },
      version in webpack := "4.44.1",
      version in installJsdom := "16.4.0",
      scalaJSUseMainModuleInitializer := false,
      // Compile tests to JS using fast-optimisation
      scalaJSStage in Test := FastOptStage,
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "core"   % scalaJsReact,
        "io.github.cquiroz.react"           %%% "common" % scalaJsReactCommon,
        "com.github.japgolly.scalajs-react" %%% "test"   % scalaJsReact % Test,
        "com.lihaoyi"                       %%% "utest"  % uTest        % Test
      ),
      npmDependencies in Compile ++= Seq(
        "react"               -> reactJS,
        "react-dom"           -> reactJS,
        "@types/react"        -> reactTypes,
        "@types/react-dom"    -> reactDomTypes,
        "react-window"        -> reactWindow,
        "@types/react-window" -> reactWindowTypes
      ),
      stUseScalaJsDom := true,
      stOutputPackage := "reactST",
      stFlavour := Flavour.Japgolly,
      stReactEnableTreeShaking := Selection.All,
      Compile / stMinimize := Selection.AllExcept("react-window"),
      scalacOptions ~= (_.filterNot(
        Set(
          // By necessity facades will have unused params
          "-Wdead-code",
          "-Wunused:params",
          "-Wunused:explicits",
          "-Wunused:imports",
          "-Wvalue-discard",
          "-Xlint:missing-interpolator"
        )
      )),
      // Some Scalablytyped generated Scaladocs are malformed.
      // Workaround: https://github.com/xerial/sbt-sonatype/issues/30#issuecomment-342532067
      Compile / doc / sources := Seq(),
      webpackConfigFile in Test := Some(
        baseDirectory.value / "test.webpack.config.js"
      ),
      testFrameworks += new TestFramework("utest.runner.Framework")
    )
