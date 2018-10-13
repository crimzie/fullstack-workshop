package com.crimzie.workshop.typeclass

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future
import scala.language.higherKinds

trait CanRunFuture[A[_]] {
  def future[B](a: A[B]): Future[B]
}

object CanRunFuture {
  def apply[A[_]](implicit F: CanRunFuture[A]): CanRunFuture[A] = F

  implicit class CanRunFutureOps[A[_] : CanRunFuture, B](lhs: A[B]) {
    def future: Future[B] = apply[A].future(lhs)
  }

  implicit def taskCanRunFuture(implicit sc: Scheduler): CanRunFuture[Task] =
    new CanRunFuture[Task] {
      override def future[B](a: Task[B]): Future[B] = a.runAsync
    }
}
