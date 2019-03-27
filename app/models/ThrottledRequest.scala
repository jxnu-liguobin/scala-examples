package models

import scala.concurrent.Promise

/**
 * 完全异步非阻塞的请求限速
 *
 * 用于存放“开关”和请求到达时间，
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-27
 */
case class ThrottledRequest(promise: Promise[Boolean], time: Long)

