package io.github.dreamylost.exception

import java.util

import scala.collection.JavaConversions._

/**
 * 模拟单线程出现ConcurrentModificationException
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/23
 */
object SingleConcurrentModificationException extends App {


  //不能对已有迭代器使用集合的add remove put等修改结构的方法
  testConcurrentModificationException1()
  testConcurrentModificationException2()
  testConcurrentModificationException3()
  testConcurrentModificationException4()

  def testConcurrentModificationException1(): Unit = {
    try {
      val arr = new util.ArrayList[String]
      arr.add("1")
      arr.add("2")
      arr.add("3")
      for (i <- arr) {
        arr.add("4")
      }
    } catch {
      case e: Exception =>
        println("arraylist:" + e)
    }
  }

  def testConcurrentModificationException2(): Unit = {
    try {
      val arr = new util.HashMap[String, String]
      arr.put("1", "1")
      arr.put("2", "2")
      arr.put("3", "3")
      for (i <- arr) {
        arr.put("4", "4")
      }
    } catch {
      case e: Exception =>
        println("hashmap1:" + e)
    }
  }

  def testConcurrentModificationException3(): Unit = {
    try {
      val arr = new util.HashMap[String, String]
      arr.put("1", "1")
      arr.put("2", "2")
      arr.put("3", "3")
      //迭代器已经生成
      val it = arr.iterator
      //修改集合的结构
      arr.put("4", "4")
      //再次使用原迭代器，报异常
      println(it.foreach(x => println(x)))
    } catch {
      case e: Exception =>
        println("hashmap2:" + e)
    }
  }

  def testConcurrentModificationException4() = {
    try {
      val arr = new util.concurrent.ConcurrentHashMap[String, String]
      arr.put("1", "1")
      arr.put("2", "2")
      arr.put("3", "3")
      while (true) {
        //模拟不使用迭代器
        arr.put("4", "4")
        arr.get("1")
      }
    } catch {
      case e: Exception =>
        println("hashmap3:" + e)
    }
  }

}
