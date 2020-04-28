package io.github.dreamylost.bus

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.event.{ EventBus, LookupClassification }


/**
 * 当MsgEnvelope的topic等于订阅时指定的字符串时，发布MsgEnvelope的负载
 *
 * 使用分类器上的相等性将订阅者映射到分类器以存储一组订阅者（因此需要compareSubscribers）
 * 通过分类方法将事件映射到分类器（因此它知道向谁发布）
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
class LookupBusImpl extends EventBus with LookupClassification {

  type Event = MsgEnvelope
  type Classifier = String
  type Subscriber = ActorRef

  //用于从传入事件中提取分类器
  //这个分类就是MsgEnvelope的topic字段
  override protected def classify(event: Event): Classifier = event.topic

  //注册了事件分类器的所有订阅者的每个事件都将被调用
  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    //将事件的payload作为消息发送被订阅者（TestActor）
    subscriber ! event.payload
  }

  //必须定义订阅者的完整顺序，如“java.lang.Comparable.compare”中预期的那样表示
  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int =
    a.compareTo(b)

  //确定内部使用的索引数据结构的初始大小（即不同分类器的预期数量）
  override protected def mapSize: Int = 128

}

//订阅主题和内容
final case class MsgEnvelope(topic: String, payload: Any)

class TestActor1 extends Actor {
  def receive = {
    case "hello" =>
      println(s"hello: msg")
    case t =>
      println(s"time: $t")

  }
}

object LookupBus extends App {

  //初始化actor系统
  val system = ActorSystem()
  //初始化处理订阅的目标actor
  val testActor: ActorRef = system.actorOf(Props[TestActor1])
  //初始化消息总线，使用LookupClassification分类器
  val lookupBus = new LookupBusImpl
  //订阅两个topic
  lookupBus.subscribe(testActor, "time")
  lookupBus.subscribe(testActor, "greetings")

  //发送消息给2个topic（该topic已经被TestActor订阅）
  lookupBus.publish(MsgEnvelope("greetings", "hello"))
  lookupBus.publish(MsgEnvelope("time", System.currentTimeMillis()))

}

