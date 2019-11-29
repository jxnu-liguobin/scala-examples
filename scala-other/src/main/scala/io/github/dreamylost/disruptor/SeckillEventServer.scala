package io.github.dreamylost.disruptor

import java.util.concurrent.ThreadFactory

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.lmax.disruptor.dsl.Disruptor

/**
 * Disruptor测试主类
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
object SeckillEventServer extends App {

  val disruptor = DisruptorEngine.create
  val ringBuffer = disruptor.start()
  val producer = new SeckillEventProducer(ringBuffer)
  (1 to 10000).foreach {
    x => producer.seckill(x, x + 1)
  }

  disruptor.shutdown()


  object DisruptorEngine {

    def create: Disruptor[SeckillEvent] = {
      val threadFactory: ThreadFactory = new ThreadFactoryBuilder().setNameFormat("sigkill-test-handler-%d").build()
      val disruptor: Disruptor[SeckillEvent] = new Disruptor[SeckillEvent](SeckillEventFactory, 1024, threadFactory)
      disruptor.handleEventsWith(SeckillEventConsumer).`then`(SeckillEventCleanHandler) //使用唯一的消费者，没有在这进行封装路由。
      disruptor.setDefaultExceptionHandler(EventExceptionHandler)
      disruptor
    }
  }

}