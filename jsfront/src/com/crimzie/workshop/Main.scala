package com.crimzie.workshop

import com.crimzie.workshop.controllers.{CatsController, UIHandles}
import com.crimzie.workshop.model._
import com.crimzie.workshop.services.ApiClientImpl
import org.scalajs.dom.html
import scalatags.JsDom.all._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.Random

@JSExportTopLevel("Main")
object Main extends UIHandles {
  val controller = new CatsController(ApiClientImpl, this)

  val userDisp: html.Div = div().render
  val userChangeFld: html.Input = input(`type` := "text").render
  val userChangeBtn: html.Button = button("login").render
  userChangeBtn.onclick = { _ =>
    if (userChangeFld.value.nonEmpty)
      controller.switchUser(userChangeFld.value ## User)
  }

  private def currentUsr: String ## User = userDisp.textContent ## User

  val listDisp: html.UList = ul().render
  val listAddNameFld: html.Input = input(`type` := "text").render
  val listAddColorFld: html.Input = input(`type` := "text").render
  val listAddSizeFld: html.Input = input(`type` := "text").render
  val listAddBtn: html.Button = button("+").render
  listAddBtn.onclick = { _ =>
    if (listAddNameFld.value.nonEmpty &&
      listAddColorFld.value.nonEmpty &&
      listAddSizeFld.value.nonEmpty) controller.addCat(currentUsr, Cat(
      Random.nextInt.toString ## Id,
      listAddNameFld.value ## Name,
      listAddColorFld.value ## Color,
      listAddSizeFld.value.toInt ## Size))
  }

  val userUi = div(userDisp, userChangeFld, userChangeBtn)
  val listUi =
    div(listDisp,
      br(),
      "Name: ",
      listAddNameFld,
      "Color: ",
      listAddColorFld,
      "Size: ",
      listAddSizeFld,
      listAddBtn)

  @JSExport
  def run(doc: html.Document): Unit = doc.body = body(userUi, listUi).render

  override def updateUsername(name: String ## User): Unit =
    userDisp.textContent = name

  override def updateList(list: Seq[Cat]): Unit = {
    val ns = listDisp.childNodes
    if (ns.length > 0)
      0 until ns.length map { ns(_) } foreach { listDisp.removeChild }
    list foreach { c =>
      val removeBtn = button("-").render
      removeBtn.onclick = { _ =>
        controller.removeCat(currentUsr, c.id)
      }
      listDisp appendChild
        li(s"${c.name}: ${c.color}, ${c.size}", removeBtn).render
    }
  }
}
