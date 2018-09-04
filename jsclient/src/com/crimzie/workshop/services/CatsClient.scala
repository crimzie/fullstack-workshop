package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import com.crimzie.workshop.model._
import endpoints.xhr.JsonEntitiesFromCodec
import endpoints.xhr.thenable.Endpoints

import scala.scalajs.js.Thenable
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.Random

@JSExportTopLevel("CatsClient")
object CatsClient {

  object JSApiEndpoints extends Api with Endpoints with JsonEntitiesFromCodec

  @JSExport
  def listCats(user: String): Thenable[Seq[Cat]] =
    JSApiEndpoints.listCats(user ## User)

  @JSExport
  def addCat(
      user: String,
      name: String,
      color: String,
      size: Int): Thenable[Seq[Cat]] =
    JSApiEndpoints.addCat(user ## User, Cat(
      Random.alphanumeric.take(10).mkString ## Id,
      name ## Name,
      color ## Color,
      size ## Size))

  @JSExport
  def updateCat(
      user: String,
      id: String,
      name: String,
      color: String,
      size: Int): Thenable[Seq[Cat]] =
    JSApiEndpoints.updateCat(user ## User, id ## Id, Cat(
      id ## Id,
      name ## Name,
      color ## Color,
      size ## Size))

  @JSExport
  def removeCat(user: String, id: String): Thenable[Seq[Cat]] =
    JSApiEndpoints.removeCat(user ## User, id ## Id)
}
