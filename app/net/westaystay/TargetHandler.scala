package net.westaystay

import play.api.{Configuration, Logging}
import play.api.libs.ws._
import views.FindInput

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TargetHandler @Inject()(ws: WSClient, config: Configuration) extends Logging {
  def handleOptionalTarget(targetOpt: Option[Int])(implicit ec: ExecutionContext): Future[Either[Error, Int]] = {
    val validatedTarget = targetOpt.fold[Either[ValidationError, Option[Int]]](Right(None))(t => FindInput.validateNumber(t)
      .map(Some(_))
      .left
      .map(e => e.copy(message = "Provided target " + e.message))
    )
    validatedTarget.fold(
      l => Future.successful(Left(l)),
      r => {
        val request = ws.url("https://httpbin.org/get")
        val requestWithQuery = r.fold(request)(target => request.withQueryStringParameters("target" -> target.toString))
        logger.info(s"requesting target from httpbin")
        requestWithQuery.get()
          .map(resp => Right((resp.json \ "args" \ "target").as[String].toInt))
          .recover { _ =>
            logger.info("httpbin web service call failed, falling back to default target from config")
            for {
              defaultTarget <- config.getOptional[Int]("defaultTarget").toRight(ConfigurationError())
              validTarget <- FindInput.validateNumber(defaultTarget).left.map(e => e.copy(message = "Default target " + e.message))
            } yield validTarget
          }
      }
    )
  }
}
