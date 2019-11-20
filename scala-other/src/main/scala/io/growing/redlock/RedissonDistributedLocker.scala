package io.growing.redlock

import java.util.concurrent.TimeUnit

import io.growing.redlock.RedisPoolFactory._
import org.redisson.api.RLock

import scala.util.Try

/**
 *
 * @author 梦境迷离
 * @since 2019-09-13
 * @version v1.0
 */
class RedissonDistributedLocker extends DistributedLock {

  /**
   * 获取锁
   *
   * @param lockKey
   * @return
   */
  override def lock(lockKey: String): RLock = {
    val rLock: RLock = getRLock(lockKey)
    rLock.lock()
    rLock
  }

  /**
   * 获取锁，设置锁超时时长
   *
   * @param lockKey
   * @param leaseTime
   * @return
   */
  override def lock(lockKey: String, leaseTime: Long): RLock = {
    lock(lockKey, leaseTime, TimeUnit.SECONDS)
  }

  /**
   * 获取锁，设置锁超时时长
   *
   * @param lockKey
   * @param leaseTime
   * @param timeUnit
   * @return
   */
  override def lock(lockKey: String, leaseTime: Long, timeUnit: TimeUnit): RLock = {
    val rLock: RLock = getRLock(lockKey)
    rLock.lock(leaseTime, timeUnit)
    rLock
  }

  /**
   * 尝试获取锁
   *
   * @param lockKey
   * @param waitTime
   * @param leaseTime
   * @param timeUnit
   * @return
   */
  override def tryLock(lockKey: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean = {
    val rLock = getRLock(lockKey)
    Try(rLock.tryLock(waitTime, leaseTime, timeUnit)).getOrElse(false)
  }

  /**
   * 释放锁
   *
   * @param lockKey
   */
  override def unLock(lockKey: String): Unit = {
    val rLock: RLock = getRLock(lockKey)
    rLock.unlock()
  }

  /**
   * 释放锁
   *
   * @param rLock
   */
  override def unLock(rLock: RLock): Unit = {
    if (null == rLock) throw new NullPointerException("rLock cannot be null")
  }

  //优先使用哨兵
  private def getRLock(lockKey: String): RLock = {
    if (null == useSingleServer() && null == useSentinelServers()) {
      throw new NullPointerException("redisson client is null")
    }
    if (useSentinelServers() != null) {
      useSentinelServers().getLock(lockKey)
    } else {
      useSingleServer().getLock(lockKey)
    }
  }
}
