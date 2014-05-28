package controllers

import org.qbproject.api.schema.QBType
import play.api.libs.json.{JsObject, Json, Writes, OWrites}

/**
 * Created by Edgar on 26.05.2014.
 */
object QBView {

  // TODO: or reference via val?
  case class QBViewPath(domainPath: String)
  trait QBViewElement
  case class QBViewControl(path: QBViewPath) extends QBViewElement
  case class QBViewModel(domainType: QBType, elements: QBViewElement*)

  implicit def qbViewModelWriter: Writes[QBViewModel] = OWrites[QBViewModel] { viewModel =>
    Json.obj(
      "domainType" -> "person", // TODO: how to reference
      "elements" -> viewModel.elements.map(qbViewElementWriter.writes(_))
    )
  }

  implicit def qbViewControlWriter: Writes[QBViewControl] = OWrites[QBViewControl] { viewControl =>
    Json.obj(
      "path" -> viewControl.path.domainPath
    )
  }

  implicit def qbViewElementWriter: Writes[QBViewElement] = OWrites[QBViewElement] { viewElement =>
    viewElement match {
      case ctrl: QBViewControl => qbViewControlWriter.writes(ctrl).as[JsObject]
    }
  }
}
