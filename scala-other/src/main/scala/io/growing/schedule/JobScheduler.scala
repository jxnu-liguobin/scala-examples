package io.growing.schedule

import java.util.concurrent.TimeUnit

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/17
 */
class JobScheduler(jobActor: ActorRef, system: ActorSystem)(implicit ec: ExecutionContext) {
  def start() {
    //两个定时任务，task1，Future内部异常被捕获
    // task2，Future内部异常被捕获，且Future外还抛出异常，两个task的异常都只会出现一次。
    system.scheduler.schedule(0 seconds, Duration.create(1, TimeUnit.SECONDS), jobActor, "task1")
    system.scheduler.schedule(0 seconds, Duration.create(3, TimeUnit.SECONDS), jobActor, "task2")
    //下面这种直接使用Runnable的方法，在抛出异常后task2永远不会被调用@see https://doc.akka.io/docs/akka/2.5.3/scala/scheduler.html
    //    system.scheduler.schedule(0 seconds, Duration.create(3, TimeUnit.SECONDS), new Runnable {
    //      override def run(): Unit = {
    //        UserTask.task2()
    //      }
    //    })
  }
}

class JobActor extends Actor {
  def receive = {
    case "task1" => {
      println("receive message from task1...")
      UserTask.task1()
    }
    case "task2" => {
      println("receive message from task2...")
      UserTask.task2()
    }
  }
}

object TestJobScheduler extends App {

  implicit val ec = ExecutionContext.global
  val system = ActorSystem.apply("schedulerJob")
  val jobActor = system.actorOf(Props[JobActor])
  val schedule = new JobScheduler(jobActor, system)
  schedule.start()
}
