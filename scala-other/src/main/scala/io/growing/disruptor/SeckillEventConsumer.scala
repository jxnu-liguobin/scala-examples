package io.growing.disruptor

import com.lmax.disruptor.EventHandler
import com.typesafe.scalalogging.LazyLogging

/**
 * Disruptor消费者
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
object SeckillEventConsumer extends EventHandler[SeckillEvent] with LazyLogging {
  override def onEvent(seckillEvent: SeckillEvent, l: Long, b: Boolean): Unit = {
    logger.info(s"Consumer SeckillEvent: $seckillEvent")
    SeckillServiceImpl.newInstance.startSeckil(seckillEvent.seckillId, seckillEvent.userId)
  }
}
