package controllers

import net.westaystay.AddendsFinder

import javax.inject._
import play.api._
import play.api.mvc._
import views.{Error, FindInput, FindOutput}
import net.westaystay.Addends
import play.api.libs.json.Json

@Singleton
class AddendsFinderController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {  
  def find() = Action(parse.json) { implicit request =>
    val maybeAddends = for {
      input <- request.body.validate[FindInput].asEither.left.map(_ => "could not parse data array of ints and target int from input")
      data <- input.validateData
      target <- input.validateTarget
    } yield AddendsFinder.findAddends(data, target)
    maybeAddends.fold(l => BadRequest(Json.toJson(Error(message = l))),
      _.fold(NoContent) { case Addends(indices, numbers) =>
        Ok(Json.toJson(FindOutput(Array(indices._1, indices._2), Array(numbers._1, numbers._2))))
      }
    )
  }
}
