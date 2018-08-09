package com.crimzie.workshop

import com.crimzie.workshop.controllers.{CatsController, UIHandles}
import com.crimzie.workshop.model._
import com.crimzie.workshop.services.ApiClientImpl
import org.scalajs.dom.{html, MouseEvent}
import org.scalajs.dom.html.UList
import scalatags.JsDom.all._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Main")
object Main extends UIHandles {
  val controller = new CatsController(ApiClientImpl, this)

  val userDisp: html.Div = div().render
  val userChangeFld: html.Input = input(`type` := "text").render
  val userChangeBtn: html.Button = button().render
  userChangeBtn.onclick = { _: MouseEvent =>
    if (userChangeFld.value.nonEmpty)
      controller.switchUser(userChangeFld.value ## User)
  }

  val userUi = div(userDisp, userChangeFld, userChangeBtn)
  val listUi: UList = ul().render

  @JSExport
  def run(doc: html.Document): Unit = doc.body = body(userUi, listUi).render

  override def updateUsername(name: String ## User): Unit =
    userDisp.textContent = name

  override def updateList(list: Seq[Cat]): Unit = {
    val ns = listUi.childNodes
    if (ns.length > 0)
      0 until ns.length map { ns(_) } foreach { listUi.removeChild }
    list foreach { c =>
      val removeBtn = button("-").render
      removeBtn.onclick = { _: MouseEvent =>
        controller.removeCat(userDisp.textContent ## User, c.id)
      }
      listUi appendChild
        li(s"${c.name}: ${c.color}, ${c.size}", removeBtn).render
    }
  }
}
