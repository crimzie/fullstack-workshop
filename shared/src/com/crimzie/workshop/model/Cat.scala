package com.crimzie.workshop.model

import io.circe.{Decoder, ObjectEncoder}
import io.circe.generic.semiauto

case class Cat(
    id: String ## Id,
    name: String ## Name,
    color: String ## Color,
    size: Int ## Size,
)

object Cat {
  implicit val enc: ObjectEncoder[Cat] = semiauto.deriveEncoder[Cat]
  implicit val dec: Decoder[Cat] = semiauto.deriveDecoder[Cat]
}
