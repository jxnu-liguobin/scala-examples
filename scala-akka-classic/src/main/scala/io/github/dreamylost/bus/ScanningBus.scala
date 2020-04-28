package io.github.dreamylost.bus

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.event.{ EventBus, ScanningClassification }

/**
 * 订阅时发布长度小于或等于指定长度的String类型消息
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/4/27
 */
class ScanningBusImpl extends EventBus with ScanningClassification {

  type Event = String //事件
  type Classifier = Int //分类器
  type Subscriber = ActorRef //订阅者actor

  //需要确定匹配的分类器并将其存储在有序集合中
  override protected def compareClassifiers(a: Classifier, b: Classifier): Int =
    if (a < b) -1 else if (a == b) 0 else 1

  //需要将订阅者存储在有序集合中
  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int =
    a.compareTo(b)

  //确定给定的分类器是否应匹配给定的事件； 对于所有收到的事件，每次订阅都会调用它
  override protected def matches(classifier: Classifier, event: Event): Boolean =
  //事件长度大于分类器长度的事件不会被收到，如event=xyzabc，分类器=3
    event.length <= classifier

  //注册了事件分类器的所有订阅者的每个事件都将被调用
  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }
}

class TestActor2 extends Actor {
  def receive = {
    case t =>
      println(s"receive: $t")
  }
}

object ScanningBus extends App {

  val system = ActorSystem()
  val testActor: ActorRef = system.actorOf(Props[TestActor2])
  val scanningBus = new ScanningBusImpl
  scanningBus.subscribe(testActor, 3)
  //下面消息按分类器的大小有序的被actor接收，因为实现了compareSubscribers
  scanningBus.publish("ab")
  scanningBus.publish("xyzabc")
  scanningBus.publish("abc")

}
