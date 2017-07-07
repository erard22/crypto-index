package ch.erard22.cryptoindex.pages

import javax.net.ssl.{SSLContext, SSLParameters}

import akka.actor.{Actor, Props}
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, TLSClientAuth}
import akka.util.ByteString
import ch.erard22.cryptoindex.CryptoIndexer.system
import ch.erard22.cryptoindex.PersistingActor
import ch.erard22.cryptoindex.bo.{ExchangeRate, Tick}
import com.typesafe.sslconfig.akka.AkkaSSLConfig

import scala.collection.parallel.immutable
import scala.concurrent.Future

final case class Result(Bid: Double, Ask: Double, Last: Double)
final case class BitrexResponse(success: Boolean, message: String, result: Result)

class BitrexActor extends Actor {


  import akka.pattern.pipe
  import context.dispatcher

  final val page = "bitrex.com"

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
    http.singleRequest(HttpRequest(uri = "https://bittrex.com/api/v1.1/public/getticker?market=USDT-BTC"))
      .map(httpResp =>
        new TickedHttpResponse(tick, httpResp)
      )
      .pipeTo(self)
  }

  private def persist(tick: Tick, jsonResponse: BitrexResponse): Unit = {
    persistingActor ! new ExchangeRate(tick.timestamp, page, "XBT", jsonResponse.result.Last)
  }


  def unmarshal(entity: ResponseEntity): BitrexResponse = {
    import scala.concurrent.duration._
    val timeout = 500.millis

    println(entity.toString)

    val bs: Future[ByteString] = entity.toStrict(timeout).map { _.data }
    val s = bs.map(_.utf8String).value.get.get
    JsonUtil.fromJson[BitrexResponse](s)
  }
}
