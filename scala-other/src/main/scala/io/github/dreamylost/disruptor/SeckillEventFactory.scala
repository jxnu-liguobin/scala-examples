package io.github.dreamylost.disruptor

import com.lmax.disruptor.EventFactory

/**
 * Disruptor事件生成工厂
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
object SeckillEventFactory extends EventFactory[SeckillEvent] {
  override def newInstance(): SeckillEvent = SeckillEvent()
}
