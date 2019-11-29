package io.github.dreamylost.actor

import akka.actor.Actor

/**
 * actor的实际终止是异步执行的，即停止可以在actor停止之前返回。
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/15
 */
object Stop_Actor {

  class MyActor extends Actor {

    import akka.actor.ActorRef

    val child: ActorRef = ???

    def receive = {
      case "interrupt-child" =>
        context.stop(child)

      case "done" =>
        context.stop(self)
    }

  }

}
