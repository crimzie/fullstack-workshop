package com.crimzie.workshop.services

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.server.Directives._
import better.files.StringInterpolations

object FileServer {
  val routes: Route =
    Directives.pathSingleSlash {
      Directives.get {
        Directives.complete(HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          Webpages.index))
      }
    } ~ Directives.path("js" / "main.js") {
      Directives.get {
        Directives.complete(HttpEntity(
          ContentTypes.`application/json`,
          file"out/jsfront/fastOptWp/dest/out-bundle.js".contentAsString))
      }
    }
}
