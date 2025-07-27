name := "Scala3-TG"
version:="0.3.0"
scalaVersion := "3.3.3"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
mainClass in assembly := Some("s1.telegrambots.YourBot")

libraryDependencies+= ("com.bot4s" %% "telegram-core" % "5.4.2").cross(CrossVersion.for3Use2_13)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "2.0.0"
libraryDependencies += "org.scalanlp" %% "breeze" % "2.0"
