package com.crimzie.workshop.services

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.effect.Async
import com.crimzie.workshop.api.Api
import com.crimzie.workshop.daos.CatsDao
import com.crimzie.workshop.typeclass.RunAsync
import com.crimzie.workshop.typeclass.RunAsync._
import endpoints.akkahttp

import scala.language.higherKinds

object ApiServer
  extends Api
    with akkahttp.server.Endpoints
    with akkahttp.server.JsonEntitiesFromCodec {
  def routes[F[_] : Async : RunAsync](d: CatsDao[F]): Route =
    listCats.implementedByAsync { d.list(_).future } ~
      addCat.implementedByAsync { d.add.tupled(_).future } ~
      removeCat.implementedByAsync { d.remove.tupled(_).future }
}
