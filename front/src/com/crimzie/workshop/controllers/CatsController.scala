package com.crimzie.workshop.controllers

import com.crimzie.workshop.model._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import rx._

import scala.language.postfixOps

class CatsController(
    api: ApiClient,
    ui: => UIHandles)(implicit ctx: Ctx.Owner) {

  private val user: Var[Option[String ## User]] = Var(None)
  private val list: Var[Seq[Cat]] = Var(Nil)

  Rx { user() foreach { ui.updateUsername } }
  Rx {
    user().fold(Task pure Seq.empty[Cat])(api.listCats) foreach { list() = _ }
  }
  Rx { ui updateList list() }

  def switchUser(u: String ## User): Unit = user() = Some(u)

  def addCat(u: String ## User, c: Cat): Unit =
    if (user.now contains u) for {
      _ <- api.addCat(u, c)
      l <- api.listCats(u)
    } list() = l

  def editCat(
      u: String ## User,
      id: String ## Id,
      name: Option[String ## Name] = None,
      color: Option[String ## Color] = None,
      size: Option[Int ## Size] = None,
  ): Unit =
    if (user.now.contains(u) && list.now.exists { _.id == id }) for {
      _ <- Task.unit
      cat = list.now.find { _.id == id }.get
      _ <- api.addCat(u, Cat(
        id,
        name getOrElse cat.name,
        color getOrElse cat.color,
        size getOrElse cat.size))
      l <- api.listCats(u)
    } list() = l

  def removeCat(u: String ## User, id: String ## Id): Unit =
    if (user.now contains u) for {
      _ <- api.removeCat(u, id)
      l <- api.listCats(u)
    } list() = l
}
