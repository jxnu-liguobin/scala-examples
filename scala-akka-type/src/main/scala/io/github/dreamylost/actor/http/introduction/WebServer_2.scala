package io.github.dreamylost.actor.http.introduction

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
// json序列化与反序列化支持需要的依赖
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.io.StdIn

/**
 * Marshalling,JSON Support by https://doc.akka.io/docs/akka-http/current/common/json-support.html
 *
 * 测试：
 * curl -H "Content-Type: application/json" -X POST -d '{"items":[{"name":"hhgtg","id":42}]}' http://localhost:8080/create-order
 * curl http://localhost:8080/item/42
 */
object WebServer_2 {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  var orders: List[Item] = Nil

  // 实体类
  final case class Item(name: String, id: Long)

  final case class Order(items: List[Item])

  // 类似play-json的format，json格式化使用
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)

  //mock一个数据库查询接口
  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    orders.find(o => o.id == itemId)
  }

  def saveOrder(order: Order): Future[Done] = {
    orders = order match {
      case Order(items) => items ::: orders
      case _ => orders
    }
    Future {
      Done
    }
  }

  def main(args: Array[String]) {

    //使用DSL构建路由，支持多个HTTP method
    val route: Route =
      concat(
        //get请求路由
        get {
          //LongNumber是路径参数  item/2  id=2
          pathPrefix("item" / LongNumber) { id =>
            // 可能没有值
            val maybeItem: Future[Option[Item]] = fetchItem(id)
            onSuccess(maybeItem) {
              case Some(item) => complete(item)
              case None => complete(StatusCodes.NotFound)
            }
          }
        },
        //post请求路由
        post {
          path("create-order") {
            entity(as[Order]) { order =>
              val saved: Future[Done] = saveOrder(order)
              onComplete(saved) { _ =>
                complete("order created")
              }
            }
          }
        }
      )

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}