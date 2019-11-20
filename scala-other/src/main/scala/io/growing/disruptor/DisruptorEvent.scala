package io.growing.disruptor

/**
 * Disruptor事件
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019-08-01
 */
abstract class DisruptorEvent {
  self =>

  var userId: Int
  var seckillId: Int

}
