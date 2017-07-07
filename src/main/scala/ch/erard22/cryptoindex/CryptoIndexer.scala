package ch.erard22.cryptoindex

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import ch.erard22.cryptoindex.pages.{AlcoinActor, BitrexActor}

import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

object CryptoIndexer extends App {

  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("cryptoIndex")

  val alcoinActor = system.actorOf(Props(classOf[AlcoinActor]))
  val bitrexActor = system.actorOf(Props(classOf[BitrexActor]))

  val calculatingActors = List[ActorRef](alcoinActor, bitrexActor)

  val schedulingActor = system.actorOf(Props(classOf[SchedulingActor], calculatingActors))

  system.scheduler.schedule(5 seconds, 5 seconds, schedulingActor, "tick")
}





