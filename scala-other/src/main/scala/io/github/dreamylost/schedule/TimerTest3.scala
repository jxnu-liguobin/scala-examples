package io.github.dreamylost.schedule

import java.util.{ Timer, TimerTask }


/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/12
 */
object TimerTest3 extends App {

  pull()

  lazy val timer = new Timer()

  //这里使用main方法测试，使用val不会被初始化，除非加lazy@see https://www.jianshu.com/p/f99e09effea4
  def LongServerTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      UserTask.task1()
    }
  }

  def pull(): Unit = {
    timer.schedule(LongServerTask, 3000, 3000)
    timer.schedule(updateHotDataTask, 3000, 3000)
  }

  def updateHotDataTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      UserTask.task2()
    }
  }
}
