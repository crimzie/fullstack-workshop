package com.crimzie.workshop.typeclass

import monix.eval.Task

import scala.concurrent.Future
import scala.language.higherKinds

trait RunAsync[A[_]] {
  def toFuture[B](a: A[B]): Future[B]
}

object RunAsync {
  def apply[A[_]](implicit ev: RunAsync[A]): RunAsync[A] = ev

  implicit class RunAsyncOps[A[_] : RunAsync, B](lhs: A[B]) {
    def future: Future[B] = apply[A].toFuture(lhs)
  }

  implicit val taskAsync: RunAsync[Task] = new RunAsync[Task] {

    import monix.execution.Scheduler.Implicits.global

    override def toFuture[A](x: Task[A]): Future[A] = x.runAsync
  }
}
