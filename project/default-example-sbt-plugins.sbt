// Comment the line below if you want more information during initialization, as the log level should default to debug without it.
logLevel := Level.Warn

// Path.userHome is an SBT object.
resolvers += "Default example user's local Ivy repository" at "file:///" + Path.userHome + "/.ivy2/local"

resolvers += Classpaths.sbtPluginReleases

resolvers += "Repository Templemore" at "http://templemore.co.uk/repo"

resolvers += "Repository Typesafe" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.8.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

// Web plugins.
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

// Play Framework plugin.
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")

// Play framework enhancer - this automatically generates getters/setters for public fields and rewrites accessors of these fields to use the getters/setters. You can disable it on project level by adding "disablePlugins(PlayEnhancer)" in build.sbt or remove it completely by deleting the line below.
addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

// To enable Play Ebean support uncomment the line below. You can also enable it in the build.sbt with enablePlugins(SbtEbean). Adding Play Ebean plugin will automatically bring in the Play enhancer plugin, even if the line Play enhancer plugin line above is commented out.
// addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "1.0.0")

