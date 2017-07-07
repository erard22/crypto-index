package ch.erard22.cryptoindex.pages

import akka.http.scaladsl.model.HttpResponse
import ch.erard22.cryptoindex.bo.Tick


class TickedHttpResponse(t: Tick, resp: HttpResponse) {

  val tick = t
  val httpResponse = resp

}
