package views

import play.api.libs.json._

final case class FindOutput(indices: Array[Int], addends: Array[Int])

object FindOutput {
  implicit val findOutputWrites = Json.writes[FindOutput]
}