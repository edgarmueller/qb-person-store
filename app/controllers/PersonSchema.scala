package controllers

import org.qbproject.api.schema.QBSchema._
import org.qbproject.api.mongo.MongoSchemaExtensions._
import controllers.QBView.{QBViewModel, QBViewPath, QBViewControl}

object PersonSchema {

  val personSchema = qbClass(
    "id" -> objectId,
    "name" -> qbString,
    "nationality" -> qbString,
    "address" -> qbString
  )

  val viewSchema = QBViewModel(
    personSchema,
    QBViewControl(QBViewPath("name")),
    QBViewControl(QBViewPath("nationality")),
    QBViewControl(QBViewPath("address")
    )
  )
}