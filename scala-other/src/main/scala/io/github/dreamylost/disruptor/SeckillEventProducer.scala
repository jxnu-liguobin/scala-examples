package io.github.dreamylost.disruptor

import com.lmax.disruptor.{ EventTranslatorTwoArg, RingBuffer }
import com.typesafe.scalalogging.LazyLogging

/**
 * Disruptor服务提供者
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
class SeckillEventProducer(ringBuffer: RingBuffer[SeckillEvent]) extends LazyLogging {

  object SeckillEventTranslatorVararg extends EventTranslatorTwoArg[SeckillEvent, SeckillEvent, Boolean] {
    override def translateTo(t: SeckillEvent, l: Long, a: SeckillEvent, b: Boolean): Unit = {
      logger.info("TranslateTo message")
      t.userId = a.userId
      t.seckillId = a.seckillId
    }
  }

  def seckill(userId: Int, seckillId: Int): Unit = {
    this.ringBuffer.publishEvent(SeckillEventTranslatorVararg, SeckillEvent(userId, seckillId), true)
  }
}
