package io.growing.akka.actor

import akka.actor.{ Actor, ActorLogging, ActorRef }

/**
 * 使用PartialFunction链扩展Actor
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object PartialFunction_Actor {


  trait ProducerBehavior {
    this: Actor =>

    val producerBehavior: Receive = {
      case GiveMeThings =>
        sender() ! Give("thing")
    }
  }

  trait ConsumerBehavior {
    this: Actor with ActorLogging =>

    val consumerBehavior: Receive = {
      case ref: ActorRef =>
        ref ! GiveMeThings

      case Give(thing) =>
        log.info("Got a thing! It's {}", thing)
    }
  }

  class Producer extends Actor with ProducerBehavior {
    def receive = producerBehavior
  }

  class Consumer extends Actor with ActorLogging with ConsumerBehavior {
    def receive = consumerBehavior
  }

  class ProducerConsumer extends Actor with ActorLogging with ProducerBehavior with ConsumerBehavior {

    def receive = producerBehavior.orElse[Any, Unit](consumerBehavior)
  }

  // protocol
  case object GiveMeThings

  final case class Give(thing: Any)

}
