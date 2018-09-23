package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import endpoints.scalaj.client.{Endpoints, JsonEntitiesFromCodec}

import scala.language.postfixOps

class ApiEndpoints(val address: String)
  extends Endpoints with Api with JsonEntitiesFromCodec
