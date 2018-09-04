package com.crimzie.workshop.services

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.effect.Async
import com.crimzie.workshop.api.Api
import com.crimzie.workshop.daos.CatsDao
import com.crimzie.workshop.typeclass.RunAsync
import endpoints.akkahttp.server.{Endpoints, JsonEntitiesFromCodec}

import scala.language.higherKinds

object ApiServer extends Api with Endpoints with JsonEntitiesFromCodec {
  def routes[F[_] : Async : RunAsync](d: CatsDao[F]): Route =
    listCats.implementedByAsync { RunAsync[F].toFuture(d.list(_)) } ~
      addCat.implementedByAsync { RunAsync[F].toFuture(d.add.tupled(_)) } ~
      removeCat.implementedByAsync { RunAsync[F].toFuture(d.remove.tupled(_)) }
}
