package io.growing.other

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/19
 */
object HttpRequestTest extends App {

  var count = new AtomicInteger()
  var trueCount = new AtomicInteger()

  def test() = {
    var spand = 0L
    val start = System.nanoTime()
//    val res = requests.get(s"http://127.0.0.1:9001/.well-known/assetlinks.json")
    val res = requests.get(s"http://127.0.0.1:9001/apple-app-site-association")
    val end = System.nanoTime()
    if (!res.text().contains("delegate_permission")) {
      count.addAndGet(1)
    } else {
      trueCount.addAndGet(1)
    }
    println("错误次数：" + count)
    println("正确次数：" + trueCount)
  }

  val threads = Executors.newFixedThreadPool(20)
  (0 until 2000).foreach {
    i =>
      threads.execute(() => test())
  }
}
