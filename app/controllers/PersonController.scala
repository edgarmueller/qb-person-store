package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.modules.reactivemongo.MongoController
import org.qbproject.api.schema.QBSchema._
import org.qbproject.api.controllers.{JsonHeaders, QBCrudController}
import org.qbproject.api.mongo.{QBCollectionValidation, QBMongoCollection}
import org.qbproject.api.routing.QBRouter
import play.api.libs.json.{JsUndefined, JsValue, Json}

object PersonController extends MongoController with QBCrudController {

  lazy val collection = new QBMongoCollection("person")(db) with QBCollectionValidation {
    override def schema = PersonSchema.personSchema
  }

  override def createSchema = PersonSchema.personSchema -- "id"

  def getViewModel = JsonHeaders {
    Action {
      Ok(Json.toJson(PersonSchema.viewSchema))
    }
  }

  def getWithViewModel = JsonHeaders {
    Action.async {
      collection.findOne(Json.obj()).map { oneOption =>
        Ok(Json.obj(
          //          "schema" -> Json.toJson(BlogSchema.blogSchema),
          "viewModel" -> Json.toJson(PersonSchema.viewSchema),
          "data" -> oneOption.fold[JsValue] {
            JsUndefined("No data available")
          } {
            identity
          }
        ))
      }
    }
  }
}

object PersonRouter extends QBRouter {
  override def qbRoutes = PersonController.crudRoutes
}