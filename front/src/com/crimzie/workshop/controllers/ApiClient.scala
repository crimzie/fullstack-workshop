package com.crimzie.workshop.controllers

import com.crimzie.workshop.model._
import monix.eval.Task

trait ApiClient {
  def listCats(user: String ## User): Task[Seq[Cat]]

  def addCat(user: String ## User, cat: Cat): Task[Unit]

  def removeCat(user: String ## User, name: String ## Id): Task[Unit]
}
