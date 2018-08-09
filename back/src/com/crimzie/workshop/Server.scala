package com.crimzie.workshop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import better.files.StringInterpolations
import com.crimzie.workshop.api.Api
import com.crimzie.workshop.controllers.CatsController
import com.crimzie.workshop.services.Webpages
import endpoints.akkahttp.server.{Endpoints, JsonEntitiesFromCodec}
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.io.StdIn

object Server
  extends App with Api with Endpoints with JsonEntitiesFromCodec {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val apiRoutes: Route =
    listCats.implementedByAsync { CatsController.list(_).runAsync } ~
      addCat.implementedByAsync { CatsController.add.tupled(_).runAsync } ~
      removeCat.implementedByAsync { CatsController.remove.tupled(_).runAsync }
  val fileRoutes: Route =
    Directives.pathSingleSlash {
      Directives.get {
        Directives.complete(HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          Webpages.index))
      }
    } ~
      Directives.path("js" / "front.js") {
        Directives.get {
          Directives.complete(HttpEntity(
            ContentTypes.`application/json`,
            file"out/jsfront/fastOpt/dest/out.js".contentAsString))
        }
      }
  val binding: Future[Http.ServerBinding] =
    Http().bindAndHandle(apiRoutes ~ fileRoutes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine
  for {
    b <- binding
    _ <- b.unbind
    _ <- system.terminate
  } println("Stopped.")
}
