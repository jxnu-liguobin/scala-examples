package io.github.dreamylost.redlock

import org.redisson.api.RLock

import scala.concurrent.duration.TimeUnit

/**
 *
 * @author 梦境迷离
 * @since 2019-09-13
 * @version v1.0
 */
trait DistributedLock {

  /**
   * 获取锁
   *
   * @param lockKey
   * @return
   */
  def lock(lockKey: String): RLock

  /**
   * 获取锁，设置锁超时时长
   *
   * @param lockKey
   * @param leaseTime
   * @return
   */
  def lock(lockKey: String, leaseTime: Long): RLock

  /**
   * 获取锁，设置锁超时时长
   *
   * @param lockKey
   * @param leaseTime
   * @param timeUnit
   * @return
   */
  def lock(lockKey: String, leaseTime: Long, timeUnit: TimeUnit): RLock

  /**
   * 尝试获取锁
   *
   * @param lockKey
   * @param waitTime
   * @param leaseTime
   * @param timeUnit
   * @return
   */
  def tryLock(lockKey: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean

  /**
   * 释放锁
   *
   * @param lockKey
   */
  def unLock(lockKey: String): Unit

  /**
   * 释放锁
   *
   * @param rLock
   */
  def unLock(rLock: RLock): Unit
}
