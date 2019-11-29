package io.github.dreamylost.actor

import akka.actor.{ Actor, Props, ReceiveTimeout, Terminated }

import scala.concurrent.duration._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object Watch_Actor {

  /**
   * 为了在另一个参与者终止时（即永久停止，不是暂时性的故障并重新启动）被通知时，一个参与者可以注册自己，以接收Terminated另一个参与者终止时发送的消息
   *
   * 应该注意的是，终止消息的产生与注册和终止发生的顺序无关。特别地，即使观看者在注册时已经被终止，观看者也将收到终止消息。
   */
  class WatchActor extends Actor {
    val child = context.actorOf(Props.empty, "child")
    context.watch(child) // <-- this is the only call needed for registration
    var lastSender = context.system.deadLetters

    def receive = {
      case "kill" =>
        context.stop(child); lastSender = sender()
      case Terminated(`child`) => lastSender ! "finished"
    }
  }

  //接收超时
  class MyActor_1 extends Actor {
    // To set an initial delay
    context.setReceiveTimeout(30 milliseconds)

    def receive = {
      case "Hello" =>
        // To set in a response to a message
        context.setReceiveTimeout(100 milliseconds)
      case ReceiveTimeout =>
        // To turn it off
        context.setReceiveTimeout(Duration.Undefined)
        throw new RuntimeException("Receive timed out")
    }
  }


}
