scalaVersion := "2.9.0-1"

resolvers += "Akka Repo" at "http://akka.io/repository"

resolvers += "Glassfish Repo" at "http://download.java.net/maven/glassfish/"

resolvers += "java.net Repo" at "http://download.java.net/maven/2/"

libraryDependencies += "se.scalablesolutions.akka" % "akka-actor" % "1.1.2"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.9.0-1"

libraryDependencies += "org.specs2" %% "specs2" % "1.4" % "test"

libraryDependencies += "junit" % "junit" % "4.8.2" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.8.5" % "test"  

