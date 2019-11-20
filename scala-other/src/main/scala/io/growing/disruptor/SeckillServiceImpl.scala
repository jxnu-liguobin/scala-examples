package io.growing.disruptor

import com.typesafe.scalalogging.LazyLogging

/** Disruptor具体服务实现
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
class SeckillService extends LazyLogging {

  def startSeckil(userId: Int, seckillId: Int): Unit = {
    logger.info("Start seckill: " + seckillId + " userId:  " + userId)
  }

}

object SeckillServiceImpl {
  val newInstance = new SeckillService
}
