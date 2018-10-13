package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import endpoints.scalaj

class CatsApiEndpoints(val address: String)
  extends Api
    with scalaj.client.Endpoints
    with scalaj.client.JsonEntitiesFromCodec
