package com.crimzie.workshop.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class Cat(
    id: IdStr,
    name: NameStr,
    color: ColorStr,
    size: SizeInt,
)

object Cat {
  implicit val catEnc: Encoder[Cat] = deriveEncoder[Cat]
  implicit val catDec: Decoder[Cat] = deriveDecoder[Cat]
}
