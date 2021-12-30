assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*)                 => MergeStrategy.discard
  case PathList("org", "slf4j")                      => MergeStrategy.first
  case "mime.types"                                  => MergeStrategy.last
  case _                                             => MergeStrategy.first
}
