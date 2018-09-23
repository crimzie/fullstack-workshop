package com.crimzie.workshop.api

import com.crimzie.workshop.model._
import endpoints.algebra

trait Api extends algebra.Endpoints with algebra.circe.JsonEntitiesFromCodec {
  private val catsPath: Path[Unit] = path / "cats"
  private val catsListResponse: Response[Seq[Cat]] =
    jsonResponse[Seq[Cat]](Some(""))

  // rely on type inference for endpoints, otherwise the compilation will fail
  val listCats =
    endpoint(
      post(
        catsPath / "list",
        jsonRequest[UserStr](Some(""))),
      catsListResponse,
      Some(""),
      Some(""))
  val addCat =
    endpoint(
      post(
        catsPath / "add",
        jsonRequest[(UserStr, Cat)](Some(""))),
      catsListResponse,
      Some(""),
      Some(""))
  val removeCat =
    endpoint(
      post(
        catsPath / "remove",
        jsonRequest[(UserStr, IdStr)](Some(""))),
      catsListResponse,
      Some(""),
      Some(""))
}
