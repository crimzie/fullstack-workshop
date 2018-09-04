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
      user <- Handler.create[String ## User]
      user2cats <- endpoints.pipe(_.listCats, Nil)
      _ <- user2cats <-- user
      add <- Handler.create[Cat] map {
        _ transformSource { _.withLatestFrom(user) { (c, u) => u -> c } }
      }
      withNewCat <- endpoints.pipe(_.addCat, Nil)
      _ <- withNewCat <-- add
      remove <- Handler.create[String ## Id] map {
        _ transformSource { _.withLatestFrom(user) { (c, u) => u -> c } }
      }
      withoutCat <- endpoints.pipe(_.removeCat, Nil)
      _ <- withoutCat <-- remove
      withUpdCat <- endpoints.pipe(_.updateCat, Nil)
      catsList = Observable.merge(user2cats, withNewCat, withoutCat, withUpdCat)
      userCmp <- CatsComponents.mkUserCmp[IO](user)
      listCmp = CatsComponents.mkListCmp(catsList, remove)
      addCatCmp <- CatsComponents.mkAddCatCmp[IO](add)
      _ <- OutWatch.renderInto("#main", div(userCmp, listCmp, addCatCmp))
    } yield {}).unsafeRunSync
}
