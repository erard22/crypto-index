package ch.erard22.cryptoindex

import java.util.Date

import akka.actor.Actor
import ch.erard22.cryptoindex.bo.ExchangeRate
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.ElasticDsl._

import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.concurrent.Future

class PersistingActor extends Actor {

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))


  def receive = {
    case rate: ExchangeRate =>
      persist(rate)
  }

  def persist(rate: ExchangeRate) = {
    Future {
      client.execute {
        indexInto("rates" / rate.currency) fields (
          "timestamp" -> new Date(rate.timestamp),
          "page" -> rate.page,
          "currency" -> rate.currency,
          "rate" -> rate.rate
        )
      }
    }
    println("persisted " + rate)
  }
}
