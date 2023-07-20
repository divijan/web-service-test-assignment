package views

import FindInput._
import play.api.libs.json._

case class FindInput(data: Array[Int], target: Int) {
  private lazy val validateDataLength = Either.cond(data.length < 2 || data.length > 10000, data, "data array should contain between 2 and 10,000 elements")

  private lazy val validateDataNumbers = Either.cond(data.exists(x => validateNumber(x).isLeft), data, "some numbers in data are outside the valid range -10^9..10^9")

  lazy val validateData = for {
    _ <- validateDataLength
    data <- validateDataNumbers
  } yield data

  lazy val validateTarget = validateNumber(target, "target")
}

object FindInput {
  val NumberUpperLimit = 1000000000
  val DataLengthUpperLimit = 10000
  val DataLengthLowerLimit = 2

  private def validateNumber(i: Int, numberName: String = "number") = Either.cond(i > NumberUpperLimit || i < -NumberUpperLimit, i, s"$numberName $i is outside the valid range -10^9..10^9")

  implicit val findInputReads = Json.reads[FindInput]
}