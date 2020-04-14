package io.github.dreamylost.actor.fibonacci

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.delivery.ConsumerController
import akka.actor.typed.scaladsl.Behaviors

/**
 * 消费者
 *
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object FibonacciConsumer {


  sealed trait Command

  final case class FibonacciNumber(n: Long, value: BigInt) extends Command

  private case class WrappedDelivery(d: ConsumerController.Delivery[Command]) extends Command

  def apply(consumerController: ActorRef[ConsumerController.Command[FibonacciConsumer.Command]]): Behavior[Command] = {
    Behaviors.setup { context =>
      val deliveryAdapter =
        context.messageAdapter[ConsumerController.Delivery[FibonacciConsumer.Command]](WrappedDelivery)
      consumerController ! ConsumerController.Start(deliveryAdapter)

      Behaviors.receiveMessagePartial {
        case WrappedDelivery(ConsumerController.Delivery(FibonacciNumber(n, value), confirmTo)) =>
          context.log.info("Processed fibonacci {}: {}", n, value)
          //通过向 confirmTo 发送 ConsumerController.Confirmed 消息，ConsumerController 将向生产者发送消息（处理）确认。
          //隐藏了消息ID等技术细节，开发人员不需要关心应该发送哪条消息的确认消息，这简化了 API，同时可以让代码更专注于业务逻辑。
          confirmTo ! ConsumerController.Confirmed
          Behaviors.same
      }
    }
  }
}