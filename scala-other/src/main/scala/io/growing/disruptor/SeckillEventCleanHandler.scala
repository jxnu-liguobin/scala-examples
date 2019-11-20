package io.growing.disruptor

import com.lmax.disruptor.EventHandler
import com.typesafe.scalalogging.LazyLogging

/** Disruptor清理事件处理器
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
object SeckillEventCleanHandler extends EventHandler[SeckillEvent] with LazyLogging {
  override def onEvent(t: SeckillEvent, l: Long, b: Boolean): Unit = {
    logger.info(s"Clean SeckillEvent: $t")
    t.seckillId = -1
    t.userId = -1
  }
}
