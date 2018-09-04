package com.crimzie.workshop.services

import com.crimzie.workshop.api.Api
import com.softwaremill.sttp.SttpBackendOptions
import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import endpoints.sttp.client.{Endpoints, JsonEntitiesFromCodec}
import monix.eval.Task

import scala.concurrent.duration._
import scala.language.postfixOps

class ApiEndpoints(host: String, timeout: FiniteDuration)
  extends Endpoints[Task](
    host,
    AsyncHttpClientMonixBackend(SttpBackendOptions(timeout, None)))
    with Api with JsonEntitiesFromCodec[Task]
