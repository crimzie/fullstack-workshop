package com.crimzie.workshop.services

import cats.effect.{Async, Effect, IO, LiftIO}
import com.crimzie.workshop.api.Api
import endpoints.xhr
import endpoints.algebra.Documentation
import monix.execution.Scheduler
import outwatch.Pipe

import scala.language.higherKinds

class CatsApiClient[F[_] : LiftIO : Effect]
  extends Api
    with xhr.Endpoints
    with xhr.JsonEntitiesFromCodec {
  override type Result[A] = F[A]

  override def endpoint[A, B](
      request: Request[A],
      response: Response[B],
      summary: Documentation,
      description: Documentation,
  ): Endpoint[A, B] =
    new Endpoint[A, B](request) {
      override def apply(a: A): Result[B] =
        implicitly[Async[F]] async { callback =>
          performXhr(request, response, a)(
            callback,
            xhr => IO raiseError new Exception(xhr.responseText))
        }
    }

  def pipe[A, B](
      endp: this.type => Endpoint[A, B],
      start: B,
  )(
      implicit
      sc: Scheduler,
  ): F[Pipe[A, B]] =
    Pipe.create[A].map {
      _ transformSource { _ mapEval endp(this).apply startWith start :: Nil }
    }.to[F]
}
