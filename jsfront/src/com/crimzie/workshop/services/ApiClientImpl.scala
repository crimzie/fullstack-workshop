package com.crimzie.workshop.services

import com.crimzie.workshop.model
import com.crimzie.workshop.controllers.ApiClient
import com.crimzie.workshop.model.{##, Cat}
import monix.eval.Task

object ApiClientImpl extends ApiClient {
  override def listCats(user: String ## model.User): Task[Seq[Cat]] =
    ApiEndpoints.listCats(user)

  override def addCat(user: String ## model.User, cat: Cat): Task[Unit] =
    ApiEndpoints.addCat(user, cat)

  override def removeCat(
      user: String ## model.User,
      id: String ## model.Id): Task[Unit] =
    ApiEndpoints.removeCat(user, id)
}
