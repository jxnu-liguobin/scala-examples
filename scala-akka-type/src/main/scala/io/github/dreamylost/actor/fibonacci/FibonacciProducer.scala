package io.github.dreamylost.actor.fibonacci

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.delivery.ProducerController
import akka.actor.typed.scaladsl.Behaviors

/**
 * 生产者
 *
 * 计算从1开始到1000的斐波那契数例
 *
 * @see https://doc.akka.io/docs/akka/current/typed/reliable-delivery.html#point-to-point-example
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object FibonacciProducer {

  sealed trait Command

  private case class WrappedRequestNext(r: ProducerController.RequestNext[FibonacciConsumer.Command]) extends Command

  def apply(
    producerController: ActorRef[ProducerController.Command[FibonacciConsumer.Command]]): Behavior[Command] = {
    Behaviors.setup { context =>
      val requestNextAdapter =
        context.messageAdapter[ProducerController.RequestNext[FibonacciConsumer.Command]](WrappedRequestNext)
      producerController ! ProducerController.Start(requestNextAdapter)
      fibonacci(0, 1, 0)
    }
  }

  private def fibonacci(n: Long, b: BigInt, a: BigInt): Behavior[Command] = {
    Behaviors.receive {
      case (context, WrappedRequestNext(next)) =>
        context.log.info("Generated fibonacci {}: {}", n, a)
        next.sendNextTo ! FibonacciConsumer.FibonacciNumber(n, a)

        if (n == 1000)
          Behaviors.stopped
        else
          fibonacci(n + 1, a + b, b)
    }
  }
}