package wknyc.model

sealed trait Role
case object Curator extends Role
case object CuratorAssistant extends Role
case object Art extends Role
case object Copy extends Role
case object HumanResources extends Role
