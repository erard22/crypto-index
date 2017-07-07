package ch.erard22.cryptoindex

import akka.actor.{Actor, ActorRef}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}
import ch.erard22.cryptoindex.bo.Tick

class SchedulingActor(calculatingActors: List[ActorRef]) extends Actor {


  var router = {
    Router(BroadcastRoutingLogic(), calculatingActors.map(x => ActorRefRoutee(x)).toIndexedSeq)
  }


  def receive = {
    case "tick" =>
      doWork
  }

  def doWork = {
    val timestamp: Long = System.currentTimeMillis
    router.route(new Tick(timestamp), sender())
  }
}
