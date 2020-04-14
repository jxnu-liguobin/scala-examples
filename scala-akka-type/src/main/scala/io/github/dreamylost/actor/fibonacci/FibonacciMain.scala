package io.github.dreamylost.actor.fibonacci

import java.util.UUID

import akka.actor.typed.{ ActorSystem, Behavior }
import akka.actor.typed.delivery.{ ConsumerController, ProducerController }
import akka.actor.typed.scaladsl.Behaviors

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/4/14
 */
object FibonacciMain {


  final case class start()

  def apply(): Behavior[start] = Behaviors.setup { context =>
    val consumerController = context.spawn(ConsumerController[FibonacciConsumer.Command](), "consumerController")
    context.spawn(FibonacciConsumer(consumerController), "consumer")
    val producerId = s"fibonacci-${UUID.randomUUID()}"
    val producerController = context.spawn(ProducerController[FibonacciConsumer.Command](producerId, durableQueueBehavior = None), "producerController")
    context.spawn(FibonacciProducer(producerController), "producer")

    consumerController ! ConsumerController.RegisterToProducerController(producerController)
    Behaviors.same
  }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[FibonacciMain.start] = ActorSystem(FibonacciMain(), "fibonacci")
    system ! FibonacciMain.start()
  }
}
