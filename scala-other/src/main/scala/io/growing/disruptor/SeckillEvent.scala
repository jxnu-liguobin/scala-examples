package io.growing.disruptor


/**
 * 事件对象
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
final case class SeckillEvent(var userId: Int, var seckillId: Int) extends DisruptorEvent

//初始化需要
object SeckillEvent {
  def apply(): SeckillEvent = SeckillEvent(-1, -1)
}