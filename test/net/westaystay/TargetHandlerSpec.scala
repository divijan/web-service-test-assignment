package net.westaystay

import akka.util.ByteString
import mockws.MockWS
import mockws.MockWSHelpers.Action
import org.scalatest._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers._
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.test.Helpers._
import play.api.Configuration
import play.api.http.HttpEntity
import play.api.mvc.{ResponseHeader, Result}
import views.FindInput

import scala.concurrent.ExecutionContext.Implicits.global

class TargetHandlerSpec extends AnyFunSpec with should.Matchers with EitherValues with BeforeAndAfterAll {
  val getResponse = """{
    |  "args": {},
    |  "headers": {
    |    "Accept": "*/*",
    |    "Accept-Encoding": "gzip, deflate, br",
    |    "Accept-Language": "en-US,en;q=0.5",
    |    "Host": "httpbin.org",
    |    "Sec-Fetch-Dest": "empty",
    |    "Sec-Fetch-Mode": "cors",
    |    "Sec-Fetch-Site": "same-origin",
    |    "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0",
    |    "X-Amzn-Trace-Id": "Root=1-64c29f3a-60002700032844a62fdf265a"
    |  },
    |  "origin": "176.100.1.212",
    |  "url": "https://httpbin.org/get"
    |}""".stripMargin
  def getTargetResponse(target: String) = s"""{
    |  "args": {
    |    "target": "$target"
    |  },
    |  "headers": {
    |    "Accept": "*/*",
    |    "Accept-Encoding": "gzip, deflate, br",
    |    "Accept-Language": "en-US,en;q=0.5",
    |    "Host": "httpbin.org",
    |    "Sec-Fetch-Dest": "empty",
    |    "Sec-Fetch-Mode": "cors",
    |    "Sec-Fetch-Site": "same-origin",
    |    "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0",
    |    "X-Amzn-Trace-Id": "Root=1-64c29fc3-777eb5ae5a555f3e6e266514"
    |  },
    |  "origin": "176.100.1.212",
    |  "url": "https://httpbin.org/get?target=6"
    |}""".stripMargin
  val invalidJson =
    """{
      |  "args": {}
      |  "headers": {
      |    "Accept": "*/*,
      |    "Accept-Encoding": "gzip, deflate, br",
      |    "Accept-Language": "en-US,en;q=0.5",
      |    "Host": "httpbin.org",
      |    "Sec-Fetch-Dest": "empty",
      |    "Sec-Fetch-Mode": "cors",
      |    "Sec-Fetch-Site": "same-origin",
      |    "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0",
      |    "X-Amzn-Trace-Id": "Root=1-64c29f3a-60002700032844a62fdf265a"
      |  },
      |  "origin": "176.100.1.212",
      |  "url": "https://httpbin.org/get"
      |}
      |""".stripMargin
  val addendsFinder = new AddendsFinder
  val noTargetConfig = Configuration.from(Map("maxRequestsPerMinute" -> 10))
  val config = Configuration.from(Map("maxRequestsPerMinute" -> 10, "defaultTarget" -> 10))

  val httpbinUrl = "https://httpbin.org/get"
  val ws = MockWS {
    case (GET, httpbinUrl) => Action { request =>
      request.getQueryString("target").fold[Result](Ok(Json.parse(getResponse))) { target =>
        Ok(Json.parse(getTargetResponse(target)))
      }
    }
  }

  val wrongWs = MockWS {
    case (GET, httpbinUrl) => Action {
      Result(ResponseHeader(200), HttpEntity.Strict(ByteString.fromString(invalidJson), Some("application/json")))
    }
  }

  val unreachableWs = MockWS {
    case (GET, "https://unreachable.com") => Action {
      Ok(Json.parse(getResponse))
    }
  }

  describe("TargetHandler") {
    describe("successful cases") {
      it("should successfully handle target = 6") {
        val targetHandler = new TargetHandler(ws, config)
        val result = targetHandler.handleOptionalTarget(Some(6))
        await(result) should be(Right(6))
      }

      it("should return 6 when 6 is passed even when no target is set in config") {
        val targetHandler = new TargetHandler(ws, config)
        val result = targetHandler.handleOptionalTarget(Some(6))
        await(result) should be(Right(6))
      }

      it("should successfully handle no target when default target is set in config") {
        val targetHandler = new TargetHandler(ws, config)
        val result = targetHandler.handleOptionalTarget(None)
        await(result) should be(Right(10))
      }
    }

    describe("error cases") {
      val configError = Left(ConfigurationError("defaultTarget is not set in config"))

      it("should return ConfigurationError when no target is passed as an argument and none set in config") {
        val targetHandler = new TargetHandler(ws, noTargetConfig)
        val result = targetHandler.handleOptionalTarget(None)
        await(result) should be(configError)
      }

      it("should return ValidationError when provided target is outside the limits") {
        val targetHandler = new TargetHandler(ws, config)
        val result = targetHandler.handleOptionalTarget(Some(FindInput.NumberUpperLimit + 1))
        await(result) should be(Left(ValidationError("Provided target number 1000000001 is outside the valid range -10^9..10^9")))
      }

      it("should return ConfigurationError when no default target set in config and httpbin is unreachable") {
        val targetHandler = new TargetHandler(unreachableWs, noTargetConfig)
        val result = targetHandler.handleOptionalTarget(None)
        await(result) should be(configError)
      }

      it("should return ConfigurationError when no default target set in config and httpbin returns malformed JSON") {
        val targetHandler = new TargetHandler(wrongWs, noTargetConfig)
        val result = targetHandler.handleOptionalTarget(Some(5))
        await(result) should be(configError)
      }
    }
  }

  override def afterAll(): Unit = {
    ws.close()
    super.afterAll()
  }
}
