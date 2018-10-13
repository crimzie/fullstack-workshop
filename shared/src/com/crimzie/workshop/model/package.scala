package com.crimzie.workshop

import io.circe.{Decoder, Encoder}

package object model {

  sealed trait Tag[T]

  final class CanTag[From, T, To]()

  implicit class TagOps[A](private val a: A) extends AnyVal {
    def tag[T](implicit ev: CanTag[A, T, A with Tag[T]]): A with Tag[T] =
      a.asInstanceOf[A with Tag[T]]

    def untag[B, T](implicit ev: CanTag[B, T, A]): B = a.asInstanceOf[B]
  }

  implicit def taggedEnc[From, To](
      implicit
      ev: CanTag[From, _, To],
      e: Encoder[From]): Encoder[To] =
    e.contramap[To]{ _.asInstanceOf[From] }

  implicit def taggedDec[From, To](
      implicit
      ev: CanTag[From, _, To],
      d: Decoder[From]): Decoder[To] =
    d.map[To]{ _.asInstanceOf[To] }

  sealed trait Id
  type IdStr = String with Tag[Id]
  implicit val idTag: CanTag[String, Id, IdStr] =
    new CanTag[String, Id, IdStr]

  sealed trait User
  type UserStr = String with Tag[User]
  implicit val userTag: CanTag[String, User, UserStr] =
    new CanTag[String, User, UserStr]

  sealed trait Name
  type NameStr = String with Tag[Name]
  implicit val nameTag: CanTag[String, Name, NameStr] =
    new CanTag[String, Name, NameStr]

  sealed trait Color
  type ColorStr = String with Tag[Color]
  implicit val colorTag: CanTag[String, Color, ColorStr] =
    new CanTag[String, Color, ColorStr]

  sealed trait Volume
  type VolumeDbl = Double with Tag[Volume]
  implicit val volumeTag: CanTag[Double, Volume, VolumeDbl] =
    new CanTag[Double, Volume, VolumeDbl]
}
