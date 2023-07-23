package views

import play.api.libs.json.{Json, Writes}
import net.westaystay.{ConfigurationError, ValidationError}

object ErrorWrites {
  implicit val validationErrorWrites: Writes[ValidationError] = new Writes[ValidationError] {
    def writes(a: ValidationError) = Json.obj(
      "type" -> "ValidationError",
      "message" -> a.message
    )
  }

  implicit val configErrorWrites: Writes[ConfigurationError] = new Writes[ConfigurationError] {
    def writes(a: ConfigurationError) = Json.obj(
      "type" -> "ConfigurationError",
      "message" -> a.message
    )
  }

}
