package com.crimzie.workshop

import io.circe.{Decoder, Encoder, HCursor}

package object model {

  sealed trait Tag[T]

  sealed trait CanTag[T]

  implicit class WithTag[A](private val v: A) extends AnyVal {
    def ##[T](t: T)(implicit ev: CanTag[A with Tag[T]]): A with Tag[T] =
      v.asInstanceOf[A with Tag[T]]

    def tag[T](implicit ev: CanTag[A with Tag[T]]): A with Tag[T] =
      v.asInstanceOf[A with Tag[T]]
  }

  def taggedEncDec[A, T](
      implicit
      enc: Encoder[A],
      dec: Decoder[A],
      ev: CanTag[A with Tag[T]],
  ): (Encoder[A with Tag[T]], Decoder[A with Tag[T]]) =
    ((a: A with Tag[T]) => enc(a.asInstanceOf[A]),
      (c: HCursor) => c.as[A].map(_.tag[T]))

  sealed trait User

  type UserStr = String with Tag[User]
  val User: User = new User {}
  implicit val userTag: CanTag[UserStr] = new CanTag[UserStr] {}
  implicit lazy val (userEnc, userDec) = taggedEncDec[String, User]

  sealed trait Id

  type IdStr = String with Tag[Id]
  val Id: Id = new Id {}
  implicit val idTag: CanTag[IdStr] = new CanTag[IdStr] {}
  implicit lazy val (idEnc, idDec) = taggedEncDec[String, Id]

  sealed trait Name

  type NameStr = String with Tag[Name]
  val Name: Name = new Name {}
  implicit val nameTag: CanTag[NameStr] = new CanTag[NameStr] {}
  implicit lazy val (nameEnc, nameDec) = taggedEncDec[String, Name]

  sealed trait Color

  type ColorStr = String with Tag[Color]
  val Color: Color = new Color {}
  implicit val colorTag: CanTag[ColorStr] = new CanTag[ColorStr] {}
  implicit lazy val (colorEnc, colorDec) = taggedEncDec[String, Color]

  sealed trait Size

  val Size: Size = new Size {}
  type SizeInt = Int with Tag[Size]
  implicit val sizeTag: CanTag[SizeInt] = new CanTag[SizeInt] {}
  implicit lazy val (sizeEnc, sizeDec) = taggedEncDec[Int, Size]
}

// TODO: solve boilerplate
