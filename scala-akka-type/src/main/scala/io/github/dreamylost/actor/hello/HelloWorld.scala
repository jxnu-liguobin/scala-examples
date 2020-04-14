package io.github.dreamylost.actor.hello

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors

/**
 * @see https://doc.akka.io/docs/akka/current/typed/reliable-delivery.html#point-to-point-example
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object HelloWorld {

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    //收到Greet消息时，打印 Hello whom，并回复Greeted消息
    println(s"Hello ${message.whom}!")
    message.replyTo ! Greeted(message.whom, context.self)
    //通知系统重新使用以前的行为。这是为了避免在不需要时重新创建当前行为的分配开销
    Behaviors.same
  }
}

object HelloWorldBot {

  def apply(max: Int): Behavior[HelloWorld.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] =
    Behaviors.receive { (context, message) =>
      //actor一次处理一条消息，所以不需要AtomicInteger等并发处理
      val n = greetingCounter + 1
      println(s"Greeting $n for ${message.whom}")
      if (n == max) {
        Behaviors.stopped
      } else {
        message.from ! HelloWorld.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
}

object HelloWorldMain {

  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
  //behavior实例的创建推迟到actor启动
  //生成子actor
  //可以返回下一个behavior
    Behaviors.setup { context =>
      val greeter = context.spawn(HelloWorld(), "greeter")
      Behaviors.receiveMessage { message =>
        val replyTo = context.spawn(HelloWorldBot(max = 3), message.name)
        greeter ! HelloWorld.Greet(message.name, replyTo)
        Behaviors.same
      }
    }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[HelloWorldMain.SayHello] =
      ActorSystem(HelloWorldMain(), "hello")
    //1.发Greet消息给HelloWorld，并告知需要回复给HelloWorldBot
    //2.HelloWorldBot收到Greeted消息后，回复Greet消息给HelloWorld
    //3.HelloWorld收到Greet消息后再次回复Greeted消息给HelloWorldBot
    //4.直到HelloWorldBot处理的次数=max才停止
    system ! HelloWorldMain.SayHello("World")
    system ! HelloWorldMain.SayHello("Akka")
  }
}