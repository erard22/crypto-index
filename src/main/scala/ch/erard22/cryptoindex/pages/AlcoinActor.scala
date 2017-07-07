package ch.erard22.cryptoindex.pages

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import ch.erard22.cryptoindex.CryptoIndexer.system
import ch.erard22.cryptoindex.PersistingActor
import ch.erard22.cryptoindex.bo.{ExchangeRate, Tick}

import scala.concurrent.{Await}
import scala.concurrent.duration._

class AlcoinActor extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  final val page = "alcoin.com"

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)
  val persistingActor = system.actorOf(Props(classOf[PersistingActor]))

  def receive = {
    case t: Tick =>
      sendRequest(t)

    case response: TickedHttpResponse =>
      val jsonResponse = unmarshal(response.httpResponse.entity)
      persist(response.tick, jsonResponse)
  }

  private def sendRequest(tick: Tick) = {
    http.singleRequest(HttpRequest(uri = "https://api.allcoin.com/api/v1/ticker?symbol=btc_usd"))
      .map(httpResp =>
        new TickedHttpResponse(tick, httpResp)
      )
      .pipeTo(self)
  }

  private def persist(tick: Tick, jsonResponse: AlcoinResponse): Unit = {
    persistingActor ! new ExchangeRate(tick.timestamp, page, "XBT", jsonResponse.ticker.last)
  }


  def unmarshal(entity: ResponseEntity): AlcoinResponse = {
    def f = entity.dataBytes.runFold(ByteString(""))(_ ++ _)
    JsonUtil.fromJson[AlcoinResponse](Await.result(f, 200 millis).utf8String)
  }
}
