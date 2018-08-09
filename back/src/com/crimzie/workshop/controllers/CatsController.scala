package com.crimzie.workshop.controllers

import com.crimzie.workshop.model._
import monix.eval.{MVar, Task}
import monix.execution.Scheduler.Implicits.global
import monix.execution.schedulers.CanBlock.permit

import scala.concurrent.duration._
import scala.language.postfixOps

object CatsController {
  private val mem: MVar[Map[String ## User, Seq[Cat]]] =
    MVar(Map(
      "user".tagged[User] -> Seq(
        Cat("01" ## Id, "Fluffy" ## Name, "White" ## Color, 3 ## Size),
      )) withDefaultValue Seq.empty).runSyncUnsafe(1 second)

  val list: String ## User => Task[Seq[Cat]] =
    user => mem.read map { _ (user) }

  val add: (String ## User, Cat) => Task[Unit] =
    (user, cat) => for {
      m <- mem.take
      _ <- mem put m.updated(user, m(user).filterNot { _.id == cat.id } :+ cat)
    } yield ()

  val remove: (String ## User, String ## Id) => Task[Unit] =
    (user, id) => for {
      m <- mem.take
      _ <- mem put m.updated(user, m(user).filterNot { _.id == id })
    } yield ()
}
