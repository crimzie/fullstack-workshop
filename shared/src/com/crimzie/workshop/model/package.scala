package com.crimzie.workshop

import io.circe.{Decoder, Encoder, HCursor}

package object model {

  sealed trait Tag[T]
  sealed trait CanTag[T]

  implicit class WithTag[A](private val a: A) extends AnyVal {
    def ##[T](t: T)(implicit ev: CanTag[A with Tag[T]]): A with Tag[T] =
      a.asInstanceOf[A with Tag[T]]

    def tag[T](implicit ev: CanTag[A with Tag[T]]): A with Tag[T] =
      a.asInstanceOf[A with Tag[T]]
  }

  def taggedEncDec[A, T](
      implicit
      enc: Encoder[A],
      dec: Decoder[A],
      ev: CanTag[A with Tag [T]],
  ): (Encoder[A with Tag[T]], Decoder[A with Tag[T]]) =
    ((a: A with Tag[T]) => enc(a.asInstanceOf[A]),
      (c: HCursor) => c.as[A].map(_.tag[T]))

  sealed trait User
  type UserStr = String with Tag[User]
  val User: User = new User {}
  implicit val userTag: CanTag[String with Tag[User]] =
    new CanTag[String with Tag[User]] {}
  implicit lazy val (usrEnc, usrDec) = taggedEncDec[String, User]

  sealed trait Id
  type IdStr = String with Tag[Id]
  val Id: Id = new Id {}
  implicit val idTag: CanTag[String with Tag[Id]] =
    new CanTag[String with Tag[Id]] {}
  implicit lazy val (idEnc, idDec) = taggedEncDec[String, Id]

  sealed trait Name
  type NameStr = String with Tag[Name]
  val Name: Name = new Name {}
  implicit val nameTag: CanTag[String with Tag[Name]] =
    new CanTag[String with Tag[Name]] {}
  implicit lazy val (nameEnc, nameDec) = taggedEncDec[String, Name]

  sealed trait Color
  type ColorStr = String with Tag[Color]
  val Color: Color = new Color {}
  implicit val colorTag: CanTag[String with Tag[Color]] =
    new CanTag[String with Tag[Color]] {}
  implicit lazy val (clrEnc, clrDec) = taggedEncDec[String, Color]

  sealed trait Volume
  type VolumeDbl = Double with Tag[Volume]
  val Volume: Volume = new Volume {}
  implicit val volumeTag: CanTag[Double with Tag[Volume]] =
    new CanTag[Double with Tag[Volume]] {}
  implicit lazy val (volEnc, volDec) = taggedEncDec[Double, Volume]
}
