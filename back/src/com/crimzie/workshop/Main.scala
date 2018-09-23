package com.crimzie.workshop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.crimzie.workshop.daos.CatsMemDao
import com.crimzie.workshop.model.{Cat, User, _}
import com.crimzie.workshop.services.{ApiServer, FileServer}
import monix.eval.{MVar, Task}
import monix.execution.Scheduler.Implicits.global

import scala.io.StdIn
import scala.language.postfixOps

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  for {
    mem <- MVar(Map(
      "user".tag[User] -> Seq(
        Cat("01" ## Id, "Fluffy" ## Name, "White" ## Color, 3 ## Size),
      )) withDefaultValue Seq.empty).runAsync
    catsDao = new CatsMemDao[Task](mem)
    b <- Http().bindAndHandle(
      ApiServer.routes(catsDao) ~ FileServer.routes,
      "localhost",
      8080)
    _ = {
      println(
        s"Server online at http://localhost:8080/\nPress RETURN to stop...")
      StdIn.readLine
    }
    _ <- b.unbind
    _ = println("Stopped.")
    _ <- system.terminate
  } {}
}
