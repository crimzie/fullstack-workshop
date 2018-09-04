package com.crimzie.workshop.typeclass

import monix.eval.Task

import scala.concurrent.Future
import scala.language.higherKinds

trait RunAsync[F[_]] {
  def toFuture[A, B](f: A => F[B]): A => Future[B]
}

object RunAsync {
  def apply[F[_]](implicit F: RunAsync[F]): RunAsync[F] = F

  import monix.execution.Scheduler.Implicits.global

  implicit val taskAsync: RunAsync[Task] = new RunAsync[Task] {
    override def toFuture[A, B](f: A => Task[B]): A => Future[B] = f(_).runAsync
  }
}
