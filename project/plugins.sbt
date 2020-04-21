// needed for creating Docker images
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.5.2")

// running scalafmt from sbt
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.0")

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("https://dl.bintray.com/sbt/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)
