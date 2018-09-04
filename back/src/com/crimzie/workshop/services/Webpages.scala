package com.crimzie.workshop.services

import scalatags.Text.all._

object Webpages {
  val index: String =
    html(
      head(
        meta(charset := "UTF-8"),
        tag("title")("Full-stack Scala"),
      ),
      body(
        div(id := "main"),
        script(src := "js/sbt.js"),
      ),
    ).render
}
