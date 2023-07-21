package net.westaystay

import play.api.libs.ws._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class TargetHandler(ws: WSClient, defaultTarget: Int) {
  def handleOptionalTarget(targetOpt: Option[Int])(implicit ec: ExecutionContext): Int = {
    val request = ws.url("https://httpbin.org/get")
    val requestWithQuery = targetOpt.fold(request)(target => request.withQueryStringParameters("target" -> target.toString))
    val responseTarget = requestWithQuery.get().map { response =>
      (response.json \ "args" \ "target").as[Int]
    }

    val resultingTarget = responseTarget.recover(_ => defaultTarget)
    Await.result(resultingTarget, 2000.millis)
  }
}
