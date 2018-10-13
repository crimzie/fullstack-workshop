package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import com.crimzie.workshop.model._
import endpoints.xhr
import endpoints.algebra.Documentation
import org.scalajs.dom.XMLHttpRequest

import scala.scalajs.js
import scala.scalajs.js.Thenable
import scala.scalajs.js.annotation.JSExport

@JSExport
object CatsClient {

  object CatsApiClient
    extends Api
      with xhr.thenable.Endpoints
      with xhr.JsonEntitiesFromCodec

  @JSExport
  def listCats(user: String): Thenable[Seq[Cat]] =
    CatsApiClient.listCats(user.tag[User])
}
