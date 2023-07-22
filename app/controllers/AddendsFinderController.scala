package controllers

import net.westaystay.{Addends, AddendsFinder, TargetHandler}

import javax.inject._
import play.api._
import play.api.mvc._
import views.{FindInput, FindOutput}
import net.westaystay.{ValidationError, ConfigurationError}
import net.westaystay.Error
import Error._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddendsFinderController @Inject()(addendsFinder: AddendsFinder,
                                        targetHandler: TargetHandler,
                                        val controllerComponents: ControllerComponents)
                                       (implicit ec: ExecutionContext) extends BaseController {
  def find() = Action.async(parse.json) { implicit request =>
    request.body.asOpt[FindInput].fold(
      Future.successful(BadRequest(Json.toJson(
        ValidationError("could not parse data array of ints and optional target int from request body"))
      )))
      { findInput =>
        val wrappedAddends: Future[Either[Error, Option[Addends]]] = for {
          validatedData <- Future.successful(findInput.validateData)
          validatedTarget <- targetHandler.handleOptionalTarget(findInput.target)
        } yield {
          for {
            target <- validatedTarget
            data <- validatedData
          } yield addendsFinder.findAddends(data, target)
        }
        wrappedAddends.map(either => either.fold({
          case e: ValidationError => BadRequest(Json.toJson(e))
          case e: ConfigurationError => InternalServerError(Json.toJson(e))
        }, option => option.fold(NoContent) { case Addends(indices, numbers) =>
          Ok(Json.toJson(FindOutput(Array(indices._1, indices._2), Array(numbers._1, numbers._2))))
        }))
      }
  }
}
