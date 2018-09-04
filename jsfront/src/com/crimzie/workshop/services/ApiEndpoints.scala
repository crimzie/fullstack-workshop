package com.crimzie.workshop.services

import cats.effect.{Effect, IO, LiftIO}
import com.crimzie.workshop.api.Api
import endpoints.algebra.Documentation
import endpoints.xhr.{Endpoints, JsonEntitiesFromCodec}
import monix.execution.Scheduler
import org.scalajs.dom.XMLHttpRequest
import outwatch.Pipe

import scala.language.{higherKinds, postfixOps}
import scala.scalajs.js

class ApiEndpoints[F[_]: LiftIO: Effect]
  extends Api with Endpoints with JsonEntitiesFromCodec {
  override type Result[A] = F[A]

  override def endpoint[A, B](
      request: Request[A],
      response: js.Function1[XMLHttpRequest, Either[Exception, B]],
      summary: Documentation,
      description: Documentation): Endpoint[A, B] =
    new Endpoint[A, B](request) {
      override def apply(a: A): Result[B] =
        IO.async[B] { callback =>
          performXhr(request, response, a)(
            callback,
            xhr => IO raiseError new Exception(xhr.responseText))
        }.to[F]
    }

  def pipe[A, B](endp: this.type => Endpoint[A, B], start: B)
    (implicit s: Scheduler): F[Pipe[A, B]] =
    Pipe.create[A].map {
      _ transformSource {
        _ mapEval endp(this).apply startWith Seq(start)
      }
    }.to[F]
}
