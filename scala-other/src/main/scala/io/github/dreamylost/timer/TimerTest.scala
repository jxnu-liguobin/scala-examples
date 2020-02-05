package io.github.dreamylost.timer

import akka.actor.{ Actor, ActorSystem, Props, Timers }

import scala.concurrent.duration._

/**
 * actor自己处理定时
 *
 * @see https://doc.akka.io/docs/akka/current/actors.html#actors-timers
 * @author 梦境迷离
 * @since 2020-01-30
 * @version v1.0
 */
object TimerTest extends App {

  object MyActor {

    case object TickKey

    case object FirstTick

    case object Tick

  }

  class MyActor extends Actor with Timers {

    import MyActor._

    //启动计时器，在之后将“msg”发送一次给自己
    timers.startSingleTimer(TickKey, FirstTick, 500.millis)

    //TimerScheduler它不是线程安全的
    def receive = {
      case FirstTick =>
        // 2.6 startTimerWithFixedDelay
        println(s"=======${System.currentTimeMillis()}========")
        //timer 定时发送的消息 延迟
        timers.startPeriodicTimer(TickKey, FirstTick, 1.second)
      case Tick =>
    }
  }

  override def main(args: Array[String]): Unit = {
    import MyActor._
    val s = ActorSystem("testtimer")
    val actor = s.actorOf(Props(classOf[MyActor]))
    actor ! FirstTick

  }
}
