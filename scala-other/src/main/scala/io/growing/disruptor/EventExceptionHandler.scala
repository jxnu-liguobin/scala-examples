package io.growing.disruptor

import com.lmax.disruptor.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging

/**
 * Disruptor异常处理
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
object EventExceptionHandler extends ExceptionHandler[SeckillEvent] with LazyLogging {

  override def handleEventException(throwable: Throwable, l: Long, t: SeckillEvent): Unit = {
    logger.error(s"userId: ${t.userId}, seckillId: ${t.seckillId}, message: ${throwable.getLocalizedMessage}", throwable)
  }

  override def handleOnShutdownException(throwable: Throwable): Unit = {
    logger.error(throwable.getLocalizedMessage, throwable)
  }

  override def handleOnStartException(throwable: Throwable): Unit = {
    logger.error(throwable.getLocalizedMessage, throwable)
  }
}