package io.github.dreamylost.quartz

import akka.actor.{ Actor, ActorSystem, Props }
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

/**
 * akka quartz 拓展的更完善的定时处理
 *
 * @see https://github.com/enragedginger/akka-quartz-scheduler
 * @author 梦境迷离
 * @since 2020-01-30
 * @version v1.0
 */
object QuartzSchedulerActor extends App {

  case object Tick

  val system = ActorSystem("user-system")
  val cleaner = system.actorOf(Props[CleanupActor])
  val scheduler = QuartzSchedulerExtension(system)
  val s = QuartzSchedulerExtension(system).schedule("Every30Seconds", cleaner, Tick)
  println(s)

  //配置文件在application.conf
  class CleanupActor extends Actor {
    override def receive: Receive = {
      case Tick =>
        println("tick ...")
    }
  }

}
