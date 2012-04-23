scalaVersion := "2.10.0-M2"

resolvers += "Glassfish Repo" at "http://download.java.net/maven/glassfish/"

resolvers += "java.net Repo" at "http://download.java.net/maven/2/"
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype" at "http://oss.sonatype.org/content/repositories/releases"
 
libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.0-M2"

libraryDependencies += "org.specs2" % "specs2_2.9.2" % "1.9" % "test"

libraryDependencies += "junit" % "junit" % "4.8.2" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.8.5" % "test"  

fork in run := true

javaOptions in run += "-Xmx2G"
