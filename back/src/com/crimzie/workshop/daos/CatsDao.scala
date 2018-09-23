package com.crimzie.workshop.daos

import cats.effect.Async
import com.crimzie.workshop.model._
import monix.eval.MVar
import monix.execution.Scheduler.Implicits.global

import scala.language.{higherKinds, postfixOps}

trait CatsDao[F[_]] {
  val list: UserStr => F[Seq[Cat]]
  val add: (UserStr, Cat) => F[Seq[Cat]]
  val remove: (UserStr, IdStr) => F[Seq[Cat]]
}

class CatsMemDao[F[_] : Async](mem: MVar[Map[UserStr, Seq[Cat]]])
  extends CatsDao[F] {
  val list: UserStr => F[Seq[Cat]] =
    user => mem.read.map { _ (user) }.to[F]

  val add: (UserStr, Cat) => F[Seq[Cat]] =
    (user, cat) => (for {
      m <- mem.take
      u = m.updated(user, m(user).filterNot { _.id == cat.id } :+ cat)
      _ <- mem put u
    } yield u(user)).to[F]

  val remove: (UserStr, IdStr) => F[Seq[Cat]] =
    (user, id) =>
      (for {
        m <- mem.take
        u = m.updated(user, m(user).filterNot { _.id == id })
        _ <- mem put u
      } yield u(user)).to[F]
}
