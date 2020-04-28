package io.github.dreamylost.bus

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }


/**
 * 使用eventStream对一个actor订阅不同的消息
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
abstract class AllKindsOfMusic {
  def artist: String
}

case class Jazz(artist: String) extends AllKindsOfMusic

case class Electronic(artist: String) extends AllKindsOfMusic

class ListenerActor extends Actor {
  def receive = {
    case m: Jazz => println(s"${self.path.name} is listening to: ${m.artist}")
    case m: Electronic => println(s"${self.path.name} is listening to: ${m.artist}")
  }
}

object EventStream extends App {

  //与Actor分类类似，EventStream将在订阅者终止时自动删除它们。
  val system = ActorSystem()

  val jazzListener: ActorRef = system.actorOf(Props[ListenerActor])
  val musicListener: ActorRef = system.actorOf(Props[ListenerActor])

  system.eventStream.subscribe(jazzListener, classOf[Jazz])
  system.eventStream.subscribe(musicListener, classOf[AllKindsOfMusic])

  //只有musicListener会收到此消息，因为它会监听所有类型的音乐
  system.eventStream.publish(Electronic("Parov Stelar"))

  //jazzListener和musicListener将收到有关Jazz的通知
  system.eventStream.publish(Jazz("Sonny Rollins"))
}