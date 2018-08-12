package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import endpoints.xhr.JsonEntitiesFromCodec
import endpoints.xhr.thenable.Endpoints

object JSApiEndpoints extends Api with Endpoints with JsonEntitiesFromCodec
