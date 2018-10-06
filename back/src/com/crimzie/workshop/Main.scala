package com.crimzie.workshop

import com.crimzie.workshop.model._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.crimzie.workshop.daos.{CatsDao, CatsMemDao}
import com.crimzie.workshop.services.ApiServer
import monix.eval.{MVar, Task}
import monix.execution.Scheduler.Implicits.global

import scala.io.StdIn

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  for {
    mem <- MVar(Map(
      "user".tag[User] -> Seq(
        Cat("001" ## Id, "Fluffy" ## Name, "White" ## Color, 3.0 ## Volume),
      )).withDefaultValue(Seq.empty)).runAsync
    catsDao = new CatsMemDao[Task](mem)
    apiRoutes = ApiServer.routes(catsDao)
    b <- Http().bindAndHandle(apiRoutes, "localhost", 8080)
    _ = {
      println(s"Server online at http://localhost:8080/\nPress RETURN to stop.")
      StdIn.readLine
    }
    _ <- b.unbind
    _ = println("Stopped.")
    _ <- system.terminate
  } {}
}
