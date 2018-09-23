package com.crimzie.workshop

import cats.effect.IO
import com.crimzie.workshop.components.CatsComponents
import com.crimzie.workshop.model._
import com.crimzie.workshop.services.ApiEndpoints
import monix.execution.Scheduler.Implicits.global
import outwatch.dom._
import outwatch.dom.dsl._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Main")
object Main {
  @JSExport
  def main(args: Array[String]): Unit =
    (for {
      _ <- IO.unit
      endpoints = new ApiEndpoints[IO]
      user <- Handler.create[UserStr]
      user2cats <- endpoints.pipe(_.listCats, Nil)
      _ <- user2cats <-- user
      add <- Handler.create[Cat] map {
        _ transformSource { _.withLatestFrom(user) { (c, u) => u -> c } }
      }
      withNewCat <- endpoints.pipe(_.addCat, Nil)
      _ <- withNewCat <-- add
      remove <- Handler.create[IdStr] map {
        _ transformSource { _.withLatestFrom(user) { (c, u) => u -> c } }
      }
      withoutCat <- endpoints.pipe(_.removeCat, Nil)
      _ <- withoutCat <-- remove
      catsList = Observable.merge(user2cats, withNewCat, withoutCat)
      userCmp <- CatsComponents.userCmp[IO](user)
      listCmp = CatsComponents.listCmp(catsList, remove)
      catAdderCmp <- CatsComponents.catAdderCmp[IO](add)
      _ <- OutWatch.renderInto("#main", div(userCmp, listCmp, catAdderCmp))
    } yield {}).unsafeRunSync
}
