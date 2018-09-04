package com.crimzie.workshop.daos

import cats.effect.Async
import com.crimzie.workshop.model._
import monix.eval.MVar
import monix.execution.Scheduler.Implicits.global

import scala.language.{higherKinds, postfixOps}

trait CatsDao[F[_]] {
  val list: String ## User => F[Seq[Cat]]
  val add: (String ## User, Cat) => F[Seq[Cat]]
  val remove: (String ## User, String ## Id) => F[Seq[Cat]]
}

class CatsMemDao[F[_] : Async](mem: MVar[Map[String ## User, Seq[Cat]]])
  extends CatsDao[F] {
  val list: String ## User => F[Seq[Cat]] =
    user => mem.read.map { _ (user) }.to[F]

  val add: (String ## User, Cat) => F[Seq[Cat]] =
    (user, cat) => (for {
      m <- mem.take
      u = m.updated(user, m(user).filterNot { _.id == cat.id } :+ cat)
      _ <- mem put u
    } yield u(user)).to[F]

  val remove: (String ## User, String ## Id) => F[Seq[Cat]] =
    (user, id) => (for {
      m <- mem.take
      u = m.updated(user, m(user).filterNot { _.id == id })
      _ <- mem put u
    } yield u(user)).to[F]
}
