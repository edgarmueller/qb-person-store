name := "qb-person-store"

version := "1.0-SNAPSHOT"

resolvers += "QB repository" at "http://dl.bintray.com/qb/maven"

libraryDependencies ++= Seq(
  "org.qbproject"     %% "qbschema" % "0.3",
  "org.qbproject"     %% "qbplay"   % "0.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

play.Project.playScalaSettings