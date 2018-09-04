package com.crimzie.workshop.api

import com.crimzie.workshop.model.{##, Cat, Id, User}
import endpoints.algebra.Endpoints
import endpoints.algebra.circe.JsonEntitiesFromCodec

trait Api extends Endpoints with JsonEntitiesFromCodec {
  private val catsPath: Path[Unit] = path / "cats"

  // rely on type inference for endpoints, otherwise the compilation will fail

  val listCats =
    endpoint(
      post(
        catsPath / "list",
        jsonRequest[String ## User]()),
      jsonResponse[Seq[Cat]]())
  val addCat =
    endpoint(
      post(
        catsPath / "add",
        jsonRequest[(String ## User, Cat)]()),
      jsonResponse[Seq[Cat]]())
  val updateCat =
    endpoint(
      post(
        catsPath / "update",
        jsonRequest[(String ## User, String ## Id, Cat)]()),
      jsonResponse[Seq[Cat]]())
  val removeCat =
    endpoint(
      post(
        catsPath / "remove",
        jsonRequest[(String ## User, String ## Id)]()),
      jsonResponse[Seq[Cat]]())
}
