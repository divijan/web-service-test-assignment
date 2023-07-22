package net.westaystay

import org.scalatest._
import funspec.AnyFunSpec
import matchers._

import javax.inject.Inject

class AddendsFinderSpec extends AnyFunSpec with should.Matchers {
  val addendsFinder = new AddendsFinder

  describe("AddendsFinder") {
    describe("Normal cases") {
      it("should correctly process data = [3,8,10,14], target = 18") {
        val result = addendsFinder.findAddends(Array(3,8,10,14), 18)
        result should contain (Addends((1, 2), (8, 10)))
      }

      it("should correctly process data = [3,1,6], target = 7") {
        val result = addendsFinder.findAddends(Array(3,1,6), 7)
        result should contain (Addends((1, 2), (1, 6)))
      }

      it("should correctly process data = [3,1,6], target = 8") {
        val result = addendsFinder.findAddends(Array(3,1,6), 8)
        result shouldBe empty
      }

      it("should correctly process data = [3,3], target = 6") {
        val result = addendsFinder.findAddends(Array(3,3), 6)
        result should contain (Addends((0, 1), (3, 3)))
      }
    }
  }
}
