package net.westaystay

import play.api.libs.json.{Json, Writes}

sealed trait Error {
  val `type`: String
  val message: String
}

object Error {
  implicit val validationErrorWrites: Writes[ValidationError] = Json.writes[ValidationError]
  implicit val configurationErrorWrites: Writes[ConfigurationError] = Json.writes[ConfigurationError]
}


case class ValidationError(message: String) extends Error {
  val `type`: String = "Invalid input error"
}

case class ConfigurationError(message: String = "defaultTarget is not set in config") extends Error {
  val `type`: String = "Configuration error"
}