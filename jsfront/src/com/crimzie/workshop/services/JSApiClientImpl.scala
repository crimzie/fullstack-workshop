package com.crimzie.workshop.services

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.crimzie.workshop.model._

import scala.scalajs.js

@JSExportTopLevel("ApiClient")
object JSApiClientImpl {
  @JSExport
  def listCats(user: String): js.Thenable[Seq[Cat]] =
    JSApiEndpoints.listCats(user ## User)

  @JSExport
  def addCat(
      user: String,
      id: String,
      name: String,
      color: String,
      size: Int): js.Thenable[Unit] =
    JSApiEndpoints.addCat(
      user ## User,
      Cat(id ## Id, name ## Name, color ## Color, size ## Size))

  @JSExport
  def removeCat(user: String, id: String): js.Thenable[Unit] =
    JSApiEndpoints.removeCat(user ## User, id ## Id)
}
