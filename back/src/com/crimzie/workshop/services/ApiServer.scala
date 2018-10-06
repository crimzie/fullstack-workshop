package com.crimzie.workshop
package services

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.effect.Async
import com.crimzie.workshop.api.Api
import com.crimzie.workshop.daos.CatsDao
import com.crimzie.workshop.typeclass.CanRunFuture
import com.crimzie.workshop.typeclass.CanRunFuture._
import endpoints.akkahttp

import scala.language.higherKinds

object ApiServer
  extends Api
    with akkahttp.server.Endpoints
    with akkahttp.server.JsonEntitiesFromCodec {
  def routes[F[_] : Async : CanRunFuture](dao: CatsDao[F]): Route =
    listCats.implementedByAsync { dao.list(_).future } ~
      addCat.implementedByAsync { dao.add.tupled(_).future } ~
      removeCat.implementedByAsync { dao.remove.tupled(_).future }
}
