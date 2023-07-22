package views

import FindInput._
import net.westaystay.ValidationError
import play.api.Logging
import play.api.libs.json._

case class FindInput(data: Array[Int], target: Option[Int]) extends Logging {
  private lazy val validateDataLength = Either.cond(data.length >= DataLengthLowerLimit && data.length <= DataLengthUpperLimit,
    data,
    ValidationError("data array should contain between 2 and 10,000 elements"))

  private lazy val validateDataNumbers = Either.cond(data.forall(x => validateNumber(x).isRight),
    data,
    ValidationError("some numbers in data are outside the valid range -10^9..10^9"))

  lazy val validateData = for {
    _ <- validateDataLength
    data <- validateDataNumbers
  } yield {
    logger.debug("data validated successfully")
    data
  }

}

object FindInput {
  val NumberUpperLimit = 1000000000
  val DataLengthUpperLimit = 10000
  val DataLengthLowerLimit = 2

  def validateNumber(i: Int) = Either.cond(i <= NumberUpperLimit && i >= -NumberUpperLimit,
    i,
    ValidationError(s"number $i is outside the valid range -10^9..10^9"))

  implicit val findInputReads: Reads[FindInput] = Json.reads[FindInput]
}