package io.github.dreamylost.schedule

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/9/17
 */
object UserTask {

  //仅load内部抛出运行时异常
  def task1(): Unit = {
    println("我开始执行任务1")
    load(1).map { x =>
      println("任务1-我在执行更新数据 x: " + x)
    }
  }

  private var j = 0

  //load内部与外部各抛一次运行时异常
  def task2() = {
    println("我开始执行任务2")
    //主要问题出在：load方法出现异常时map无法被执行去更新cache，而且load反反复复的异常被onComplete捕获
    load(2).map { x =>
      //解决办法：1.若一般异常是由超时引起，在此进行重试
      // 2.使用ScheduledThreadPoolExecutor替代Timer，并启用UncaughtExceptionHandler（经验证并无效果）
      // 与Timer相比优势是不会妨碍其他任务
      println("任务2-我在执行更新数据 x: " + x)
    }
    j += 1
    //模拟仅抛出一次异常
    if (j == 1) {
      throw new Exception("Future 外抛出的异常") //使用Timer时这里抛出异常将导致整个timer执行失败，不会继续
    }
  }

  //@see https://docs.scala-lang.org/zh-cn/overviews/core/futures.html
  //当Future带着某个值就位时，我们就说该Future携带计算结果成功就位。
  //当Future因对应计算过程抛出异常而就绪，我们就说这个Future因该异常而失败。
  //如果此Future以异常完成，那么新的Future也将包含此异常
  private var i = 0

  private def load(name: Int) = {
    val f = Future {
      i += 1
      if (i < 2) {
        println(s"任务$name-我正在抛出异常")
        throw new Exception("Future 内抛出的异常") //任何Timer或ScheduledThreadPoolExecutor时候，这个异常都将被onComplete捕获了
      }
      1
    }
    f.onComplete {
      case Failure(t) => println(s"任务$name-load方法捕获到错误了")
      case _ =>
    }
    f
  }

}
