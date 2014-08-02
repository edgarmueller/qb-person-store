package controllers

import org.qbproject.api.schema.QBSchema._
import org.qbproject.api.mongo.MongoSchemaExtensions._
import controllers.QBView._

object PersonSchema {

  val personSchema = qbClass(
    "id" -> objectId,
    "firstName" -> qbString,
    "lastName" -> qbString,
    "nationality" -> qbString,
    "gender" -> qbEnum("Male", "Female"),
    "address" -> qbString,
    "age" -> qbInteger,
    "weight" -> qbNumber,
    "active" -> qbBoolean,
    "registrationTime" -> qbDateTime
  )

  val viewSchema = QBViewModel(
    personSchema,
    QBHorizontalLayout(
      QBVerticalLayout(
        QBLabel("Name Details"),
        QBViewControl("First Name", QBViewPath("firstName")),
        QBViewControl("Last Name", QBViewPath("lastName")),
        QBViewControl("Gender", QBViewPath("gender"))
      ),
      QBVerticalLayout(
        QBGroup("Optional data",
          QBViewControl("Age", QBViewPath("age")),
          QBViewControl("Weight", QBViewPath("weight")),
          QBHorizontalLayout(
            QBViewControl("Active", QBViewPath("active")),
            QBViewControl("Time of registration", QBViewPath("registrationTime"))
          )
        )
      )
    )
  )
}