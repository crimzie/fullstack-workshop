package com.crimzie.workshop

import io.circe.{Decoder, Encoder, HCursor}

package object model {

  sealed protected trait Tag[T]

  type ##[A, T] = A with Tag[T]

  def taggedEncoder[A, T](
      implicit enc: Encoder[A]): Encoder[A ## T] =
    (a: A ## T) => enc(a.asInstanceOf[A])

  def taggedDecoder[A, T](
      implicit dec: Decoder[A]): Decoder[A ## T] =
    (c: HCursor) => c.as[A].map(_.tagged[T])

  implicit class WithTag[A](private val v: A) extends AnyVal {
    def tagged[T]: A ## T = v.asInstanceOf[A ## T]

    def ##[T](u: T): A ## T = v.asInstanceOf[A ## T]
  }

  sealed trait User

  sealed trait Id

  sealed trait Name

  sealed trait Color

  sealed trait Size

  val User: User = new User {}
  val Id: Id = new Id {}
  val Name: Name = new Name {}
  val Color: Color = new Color {}
  val Size: Size = new Size {}

  implicit val stuEnc: Encoder[String ## User] = taggedEncoder[String, User]
  implicit val stuDec: Decoder[String ## User] = taggedDecoder[String, User]
  implicit val stiEnc: Encoder[String ## Id] = taggedEncoder[String, Id]
  implicit val stiDec: Decoder[String ## Id] = taggedDecoder[String, Id]
  implicit val stnEnc: Encoder[String ## Name] = taggedEncoder[String, Name]
  implicit val stnDec: Decoder[String ## Name] = taggedDecoder[String, Name]
  implicit val stcEnc: Encoder[String ## Color] = taggedEncoder[String, Color]
  implicit val stcDec: Decoder[String ## Color] = taggedDecoder[String, Color]
  implicit val itsEnc: Encoder[Int ## Size] = taggedEncoder[Int, Size]
  implicit val itsDec: Decoder[Int ## Size] = taggedDecoder[Int, Size]
}
