package io.github.dreamylost.schedule

import java.util.concurrent.{ Executors, _ }


/**
 * @see https://www.jianshu.com/p/925dba9f5969
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/16
 */
object ScheduledThreadPoolExecutorTest extends App {

  pull()

  //自定义异常处理器
  lazy val delegate = Executors.defaultThreadFactory
  lazy val tf = new ThreadFactory() {
    override def newThread(r: Runnable): Thread = {
      val res = delegate.newThread(r)
      res.setUncaughtExceptionHandler((t: Thread, e: Throwable) => {
        println("uncaughtException捕获到异常了")
        e.printStackTrace()
      })
      res
    }
  }

  /**
   * 线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。 说明：Executors各个方法的弊端：
   * 1）newFixedThreadPool和newSingleThreadExecutor: 主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。
   * 2）newCachedThreadPool和newScheduledThreadPool: 主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至OOM。
   * 线程池能按时间计划来执行任务，允许用户设定计划执行任务的时间，int类型的参数是设定
   * 线程池中线程的最小数目。当任务较多时，线程池可能会自动创建更多的工作线程来执行任务
   */
  //  val s = new ScheduledThreadPoolExecutor(5, tf)
  //      只有通过execute提交的任务，才能将它抛出的异常交给UncaughtExceptionHandler，
  //      而通过submit提交的任务，无论是抛出的未检测异常还是已检查异常，都将被认为是任务返回状态的一部分，可以捕获Future.get
  lazy val executor = Executors.newScheduledThreadPool(5, tf)
  //      println("发生异常，线程池异常处理器捕获到了")
  //  val scheduledThreadPool = new ScheduledThreadPoolExecutor(5)
  lazy val task1 = new Runnable {
    override def run(): Unit = {
      UserTask.task1()
    }
  }

  lazy val task2 = new Runnable {
    override def run(): Unit = {
      UserTask.task2()
    }
  }

  def pull(): Unit = {
    //scheduleAtFixedRate固定频率-下一次执行时间相当于是上一次的执行时间加上period
    //scheduleWithFixedDelay固定周期-下一次执行时间是上一次任务执行完的系统时间加上period，因而具体执行时间不是固定的，但周期是固定的，是采用相对固定的延迟来执行任务
    executor.scheduleWithFixedDelay(task1, 6, 6, TimeUnit.SECONDS)
    executor.scheduleWithFixedDelay(task2, 6, 6, TimeUnit.SECONDS)
  }

}