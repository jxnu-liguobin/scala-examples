package io.github.dreamylost.config

/**
 *
 * @author 梦境迷离
 * @since 2019-09-13
 * @version v1.0
 */
case class RedisConfig(host: String, port: Int, database: Int, timeout: Int, password: String, poolMaxTotal: Int, poolMaxIdle: Int, poolMaxWait: Int,
                       slaveConnectionPoolSize: Int,
                       masterConnectionPoolSize: Int,
                       sentinelAddresses: Seq[String], masterName: String)

object RedisConfig {

  def getRedisConfig = RedisConfig(
    ConfigLoader.getStringValue("redis.host").getOrElse("127.0.0.1"),
    ConfigLoader.getIntValue("redis.port").getOrElse(6379),
    ConfigLoader.getIntValue("redis.database").getOrElse(0),
    ConfigLoader.getIntValue("redis.timeout").getOrElse(10),
    ConfigLoader.getStringValue("redis.password").getOrElse(""),
    ConfigLoader.getIntValue("redis.poolMaxTotal").getOrElse(1000),
    ConfigLoader.getIntValue("redis.poolMaxIdle").getOrElse(500),
    ConfigLoader.getIntValue("redis.poolMaxWait").getOrElse(500),

    ConfigLoader.getIntValue("redis.slaveConnectionPoolSize").getOrElse(250),
    ConfigLoader.getIntValue("redis.masterConnectionPoolSize").getOrElse(250),
    ConfigLoader.getSeqValue("redis.sentinelAddresses").getOrElse(Seq[String]()),
    ConfigLoader.getStringValue("redis.masterName").getOrElse(""),

  )

}