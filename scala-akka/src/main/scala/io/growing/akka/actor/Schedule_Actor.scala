package io.growing.akka.actor

import akka.actor.{ Actor, Timers }

import scala.concurrent.duration._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object Schedule_Actor {


  //定时器、定时消息
  object MyActor_2 {

    private case object TickKey

    private case object FirstTick

    private case object Tick

  }

  class MyActor_2 extends Actor with Timers {

    import MyActor_2._

    timers.startSingleTimer(TickKey, FirstTick, 500.millis)

    def receive = {
      case FirstTick =>
        // do something useful here
        timers.startPeriodicTimer(TickKey, Tick, 1.second)
      case Tick =>
      // do something useful here
    }
  }

}
