package com.crimzie.workshop.components

import cats.Applicative
import cats.effect.Async
import com.crimzie.workshop.model._
import monix.execution.Scheduler
import outwatch.dom._
import outwatch.dom.dsl._

import scala.language.higherKinds
import scala.util.Random

object CatsComponents {
  def mkUserCmp[F[_] : Async](userName: Handler[String ## User])
    (implicit sc: Scheduler): F[VNode] =
    Handler.create[String].map { h =>
      div(
        input(onInput.value --> h),
        button("log in", onClick(h).map { _ ## User } --> userName),
        span(
          "Hello, ",
          span(textContent <-- userName),
          visibility <--
            userName.map(_ => "visible").startWith("hidden" :: Nil)))
    }.to[F]

  def mkListCmp(
      list: Observable[Seq[Cat]],
      remove: Sink[String ## Id]): VNode =
    div(
      ul(children <-- list.map {
        _ map { c =>
          li(
            span(s"${c.name}: ${c.color} / ${c.size}"),
            button("-", onClick(c.id) --> remove))
        }
      }))

  def mkAddCatCmp[F[_] : Async](add: Sink[Cat])
    (implicit sc: Scheduler): F[VNode] =
    Applicative[F].map3(
      Handler.create[String ## Name].to[F],
      Handler.create[String ## Color].to[F],
      Handler.create[Int ## Size](1 ## Size).to[F]) { case (nam, clr, siz) =>
      val tupl: Observable[(String ## Name, String ## Color, Int ## Size)] =
        Observable.combineLatest3(nam, clr, siz)
      val disbl: Observable[Boolean] = tupl map { case (n, c, sz) =>
        !(n.length > 1 && c.length > 1 && sz > 0 && sz < 6)
      }
      val cat: Observable[Cat] =
        tupl filter { case (n, c, sz) =>
          n.length > 1 && c.length > 1 && sz > 0 && sz < 6
        } map { case (n, c, sz) =>
          Cat(Random.nextInt.abs.toString ## Id, n, c, sz)
        }
      div(
        "Name: ",
        input(onInput.value.map { _ ## Name } --> nam),
        "Color: ",
        input(onInput.value.map { _ ## Color } --> clr),
        "Size: ",
        input(
          tpe := "range",
          onInput.value.map { n => (n.toInt / 20) ## Size } --> siz),
        button(
          "+",
          onClick(cat) --> add,
          disabled <-- disbl))
    }
}
