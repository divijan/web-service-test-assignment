package views

import play.api.libs.json._

case class Error(`type`:  String = "error", message: String)

object Error {
  implicit val errorWrites = Json.writes[Error]
}
