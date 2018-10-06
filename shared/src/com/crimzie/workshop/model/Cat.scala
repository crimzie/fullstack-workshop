package com.crimzie.workshop.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class Cat(
    id: IdStr,
    name: NameStr,
    color: ColorStr,
    volume: VolumeDbl,
)

object Cat {
  implicit val enc: Encoder[Cat] = deriveEncoder[Cat]
  implicit val dnc: Decoder[Cat] = deriveDecoder[Cat]
}
