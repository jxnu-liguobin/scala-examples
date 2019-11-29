package io.github.dreamylost.actor.http.routing.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

/**
 *
 * 以下是Akka HTTP路由定义，该定义允许在运行时动态添加新的或更新的模拟端点以及相关的请求-响应对。(动态路由)
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class WebServer_4 {

  case class MockDefinition(path: String, requests: Seq[JsValue], responses: Seq[JsValue])

  implicit val format = jsonFormat3(MockDefinition)

  @volatile var state = Map.empty[String, Map[JsValue, JsValue]]

  //固定路由更新状态
  val fixedRoute: Route = post {
    pathSingleSlash {
      entity(as[MockDefinition]) { mock =>
        val mapping = mock.requests.zip(mock.responses).toMap
        state = state + (mock.path -> mapping)
        complete("ok")
      }
    }
  }

  //基于当前状态的动态路由
  val dynamicRoute: Route = ctx => {
    val routes = state.map {
      case (segment, responses) =>
        post {
          path(segment) {
            entity(as[JsValue]) { input =>
              complete(responses.get(input))
            }
          }
        }
    }
    concat(routes.toList: _*)(ctx)
  }

  val route = fixedRoute ~ dynamicRoute

  /** *
   * 例如，假设我们对body执行POST请求：
   *
   * {{{
   *   {
   *   "path": "test",
   *   "requests": [
   *     {"id": 1},
   *     {"id": 2}
   *   ],
   *   "responses": [
   *     {"amount": 1000},
   *     {"amount": 2000}
   *   ]
   * }
   * }}}
   *
   * 后续对/test带有内容{"id": 1}的POST请求将以{"amount": 1000}进行响应。
   */
}
