package com.crimzie.workshop
package daos

import cats.effect.Async
import com.crimzie.workshop.model._
import monix.eval.MVar
import monix.execution.Scheduler

import scala.language.higherKinds

trait CatsDao[F[_]] {
  val list: UserStr => F[Seq[Cat]]
  val add: (UserStr, Cat) => F[Seq[Cat]]
  val remove: (UserStr, IdStr) => F[Seq[Cat]]
}

class CatsMemDao[F[_] : Async](
    mem: MVar[Map[UserStr, Seq[Cat]]])(
    implicit
    sc: Scheduler)
  extends CatsDao[F] {
  override val list: UserStr => F[Seq[Cat]] =
    user => mem.read.map { _ (user) }.to[F]
  
  override val add: (UserStr, Cat) => F[Seq[Cat]] =
    (user, cat) => (for {
      m <- mem.take
      u = m.updated(user, m(user).filterNot { _.id == cat.id} :+ cat)
      _ <- mem.put(u)
    } yield u(user)).to[F]

  override val remove: (UserStr, IdStr) => F[Seq[Cat]] =
    (user, id) => (for {
      m <- mem.take
      u = m.updated(user, m(user).filterNot { _.id == id })
      _ <- mem.put(u)
    } yield u(user)).to[F]
}
