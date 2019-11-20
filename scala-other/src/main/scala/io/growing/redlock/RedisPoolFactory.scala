package io.growing.redlock

import io.growing.config.RedisConfig
import org.redisson.Redisson
import org.redisson.config.Config

/**
 * redis连接池
 *
 * @author 梦境迷离
 * @since 2019-08-05
 * @version v2.0
 */
object RedisPoolFactory {

  //哨兵
  def useSentinelServers() = {
    val config = new Config
    config.useSentinelServers()
      .setDatabase(RedisConfig.getRedisConfig.database)
      .addSentinelAddress(RedisConfig.getRedisConfig.sentinelAddresses: _*)
      .setMasterName(RedisConfig.getRedisConfig.masterName)
      .setTimeout(RedisConfig.getRedisConfig.timeout)
      .setMasterConnectionPoolSize(RedisConfig.getRedisConfig.masterConnectionPoolSize)
      .setSlaveConnectionPoolSize(RedisConfig.getRedisConfig.slaveConnectionPoolSize)
    Redisson.create(config)

  }

  //单机
  def useSingleServer() = {
    val config = new Config
    config.useSingleServer()
      .setAddress("redis://" + RedisConfig.getRedisConfig.host+":"+RedisConfig.getRedisConfig.port)
      .setDatabase(RedisConfig.getRedisConfig.database)
      .setTimeout(RedisConfig.getRedisConfig.timeout)
      .setConnectionPoolSize(RedisConfig.getRedisConfig.poolMaxTotal)
      .setConnectionMinimumIdleSize(RedisConfig.getRedisConfig.poolMaxIdle)
    Redisson.create(config)
  }

}
