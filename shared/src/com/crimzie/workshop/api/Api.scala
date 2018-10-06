package com.crimzie.workshop
package api

import com.crimzie.workshop.model._
import endpoints.algebra

trait Api extends algebra.Endpoints with algebra.circe.JsonEntitiesFromCodec {
  private val catsPath: Path[Unit] = path / "cats"
  private val catsResponse: Response[Seq[Cat]] = jsonResponse[Seq[Cat]]()
  val listCats =
    endpoint(
      post(
        catsPath / "list",
        jsonRequest[UserStr]()),
      catsResponse)
  val addCat =
    endpoint(
      post(
        catsPath / "add",
        jsonRequest[(UserStr, Cat)]()),
      catsResponse)
  val removeCat =
    endpoint(
      post(
        catsPath / "remove",
        jsonRequest[(UserStr, IdStr)]()),
      catsResponse)
}
