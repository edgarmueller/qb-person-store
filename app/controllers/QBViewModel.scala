package controllers

import org.qbproject.api.schema.{QBJson, QBType}
import play.api.libs.json._

import scala.reflect.ClassTag

/**
 * Created by Edgar on 26.05.2014.
 */
object QBView {

  trait QBSerializable[A] {
    def toJson(a: A): JsObject
  }

  trait SerializableBox {
    type T
    val t : T
    val showInst : QBSerializable[T]
  }

  object SerializableBox {
    def apply[T0 : QBSerializable](t0 : T0) = new SerializableBox {
      type T = T0
      val t = t0
      val showInst = implicitly[QBSerializable[T]]
    }
  }

  // TODO: or reference via val?
  case class QBViewPath(domainPath: String)

  trait QBViewElement

  case class QBViewControl(name: String, path: QBViewPath) extends QBViewElement

  case class QBLabel(text: String) extends QBViewElement

  case class QBGroup(name: String, elements: QBViewElement*) extends QBContainer

  trait QBContainer extends QBViewElement {
    def elements: Seq[QBViewElement]
  }

  trait QBViewLayout extends QBContainer

  case class QBVerticalLayout(elements: QBViewElement*) extends QBViewLayout

  case class QBHorizontalLayout(elements: QBViewElement*) extends QBViewLayout

  case class QBViewModel(domainType: QBType, elements: QBViewElement*)

  def useSerializableBox(sb : SerializableBox) = {
    import sb._
    showInst.toJson(t)
  }


  import scala.reflect.runtime.{ universe => ru }
  import scala.reflect.runtime.universe._

  implicit def qbElementWriter = new QBSerializable[QBViewElement] {
    override def toJson(q: QBViewElement): JsObject = { case q =>
      val clazz = implicitly[ClassTag[QBViewElement]].runtimeClass
      val x = implicitly[QBSerializable[clazz.type]]
      x.toJson(q)
    }
  }

  def paramInfo[T: TypeTag](x: T): Type = {
    typeOf[T] match { case TypeRef(a, b, args) => a }
  }

  def getType[T](clazz: Class[T]): ru.Type = {
    val runtimeMirror =  ru.runtimeMirror(clazz.getClassLoader)
    runtimeMirror.classSymbol(clazz).toType
  }

  implicit def qbViewModelWriter = new QBSerializable[QBViewModel] {
    def toJson(v: QBViewModel) = {
      Json.obj(
        "domainType" -> "person", // TODO: how to reference
        "elements" -> v.elements.map(el => SerializableBox(el)).map(useSerializableBox)
      )
    }
  }

  implicit def qbViewControlWriter = new QBSerializable[QBViewControl] {
    override def toJson(viewControl: QBViewControl): JsObject = {
      Json.obj(
        "type" -> "Control",
        "path" -> viewControl.path.domainPath,
        "name" -> viewControl.name
      )
    }
  }

  implicit def qbViewLabelWriter = new QBSerializable[QBLabel] {
    override def toJson(label: QBLabel): JsObject = {
      Json.obj(
        "type" -> "Label",
        "text" -> label.text
      )
    }
  }

  implicit def qbViewGroupWriter = new QBSerializable[QBGroup] {
    override def toJson(group: QBGroup): JsObject = {
      Json.obj(
        "type" -> "Group",
        "name" -> group.name,
        "elements" -> Json.arr(
          group.elements.map(el => SerializableBox(el)).map(useSerializableBox)
        )
      )
    }
  }

  implicit def qbViewLayoutWriter = new QBSerializable[QBViewLayout] {
    override def toJson(layout: QBViewLayout): JsObject = {
      Json.obj(
        "type" -> layout.getClass.getSimpleName,
        "elements" -> Json.arr(
          layout.elements.map(el => SerializableBox(el)).map(useSerializableBox)
        )
      )
    }
  }

  implicit def qbViewElementWriter[Q : QBSerializable]: Writes[Q] = OWrites[Q] { q: Q =>
    val m = implicitly[QBSerializable[Q]]
    m.toJson(q)
  }
}