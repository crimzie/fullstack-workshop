package com.crimzie.workshop.services

import scalatags.Text.all._

object Webpages {
  val index: String =
    html(
      head(
        meta(charset := "UTF-8"),
        tag("title")("Full-stack Scala"),
        script(src := "js/front.js"),
      ),
      body(onload := "Main.run(document)"),
    ).render
}
