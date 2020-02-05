package io.github.dreamylost.timer

import akka.actor.{ Actor, ActorSystem, Props }

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/2/4
 */
object ScheduleTest extends App {

  val Tick = "tick"

  class TickActor extends Actor {
    def receive = {
      case Tick => println(System.currentTimeMillis())
    }
  }

  val system = ActorSystem()

  val tickActor = system.actorOf(Props(classOf[TickActor]))

  val cancellable = system.scheduler.schedule(Duration.Zero, 500.milliseconds, tickActor, Tick)

  //取消
  //  cancellable.cancel()
}
