package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import endpoints.algebra.Documentation
import endpoints.xhr.{Endpoints, JsonEntitiesFromCodec}
import monix.eval.Task
import org.scalajs.dom.XMLHttpRequest

import scala.scalajs.js

object ApiEndpoints extends Api with Endpoints with JsonEntitiesFromCodec {
  override type Result[A] = Task[A]

  override def endpoint[A, B](
      request: ApiEndpoints.Request[A],
      response: js.Function1[XMLHttpRequest, Either[Exception, B]],
      summary: Documentation,
      description: Documentation): Endpoint[A, B] =
    new Endpoint[A, B](request) {
      def apply(a: A): Task[B] =
        Task unsafeCreate { (_, callback) =>
          performXhr(request, response, a)(
            callback.apply,
            xhr => callback onError new Exception(xhr.responseText))
        }
    }
}
