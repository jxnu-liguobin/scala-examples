package io.github.dreamylost.exception

import java.util

import scala.collection.JavaConversions._

/**
 * 模拟多线程出现ConcurrentModificationException
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/23
 */
object MultileConcurrentModificationException extends App {

  lazy val myList = new util.concurrent.CopyOnWriteArrayList[String]()
  myList.add("1")
  myList.add("1")
  myList.add("1")
  myList.add("1")
  myList.add("1")

  new Thread(() => {
    while (true) {
      //防止这个执行完，下面的还没开始
      myList.add("1")
    }
  }).start()

  new Thread(() => {
    while (true) {
      for (string <- myList) {
        println("遍历集合 value = " + string)
      }
    }
  }).start()
}
