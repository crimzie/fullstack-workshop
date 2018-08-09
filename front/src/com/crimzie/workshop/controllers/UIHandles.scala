package com.crimzie.workshop.controllers

import com.crimzie.workshop.model._

trait UIHandles {
  def updateUsername(name: String ## User): Unit

  def updateList(list: Seq[Cat]): Unit
}
