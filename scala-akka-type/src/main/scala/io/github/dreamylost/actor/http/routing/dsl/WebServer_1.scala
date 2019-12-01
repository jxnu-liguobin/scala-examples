package io.github.dreamylost.actor.http.routing.dsl

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.{ HttpApp, Route }

/**
 *
 * 定义服务端
 *
 * @see https://doc.akka.io/docs/akka-http/current/routing-dsl/index.html
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
object WebServer_1 extends HttpApp with App {

  override def routes: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  WebServer_1.startServer("localhost", 8080)

}