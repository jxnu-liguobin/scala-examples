package io.github.dreamylost.actor.http.routing.dsl

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.coding.Deflate
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.StatusCodes.MovedPermanently
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext

/**
 *
 * 以下是一个Akka HTTP路由定义，试图展示一些功能。产生的服务实际上并没有做任何有用的事情，但是其定义应该使您对路由DSL的实际API定义有一个感觉
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class WebServer_2 {

  // API路由使用的类型
  type Money = Double //仅用于演示目的
  type TransactionResult = String

  case class User(name: String)

  case class Order(email: String, amount: Money)

  case class Update(order: Order)

  case class UpdateOrderItem(order: OrderItem)

  case class OrderItem(i: Int, os: Option[String], s: String)

  // 编组通常将使用库自动得出，类似Play-json
  implicit val orderUM: FromRequestUnmarshaller[Order] = ???
  implicit val orderM: ToResponseMarshaller[Order] = ???
  implicit val orderSeqM: ToResponseMarshaller[Seq[Order]] = ???
  implicit val timeout: Timeout = ??? // actor ask 需要一个隐式超时
  implicit val ec: ExecutionContext = ???
  implicit val mat: ActorMaterializer = ???
  implicit val sys: ActorSystem = ???

  // 后端入口点，校验权限
  def myAuthenticator: Authenticator[User] = ???

  def retrieveOrdersFromDB: Seq[Order] = ???

  def myDbActor: ActorRef = ???

  def processOrderRequest(id: Int, complete: Order => Unit): Unit = ???

  val route = concat(
    path("orders") {
      authenticateBasic(realm = "admin area", myAuthenticator) { user =>
        concat(
          get {
            encodeResponseWith(Deflate) {
              complete {
                //使用域内的编码器序列化自定义对象
                retrieveOrdersFromDB
              }
            }
          },
          post {
            //如果需要，压缩请求
            decodeRequest {
              //使用域内的解码器的反序列传来的json
              entity(as[Order]) { order =>
                complete {
                  //写入到数据库
                  "Order received"
                }
              }
            }
          })
      }
    },
    //将URI路径元素提取为Int，获得一个路径参数
    pathPrefix("order" / IntNumber) { orderId =>
      concat(
        pathEnd {
          concat(
            (put | parameter('method ! "put")) {
              // 从multipart 或 www-url-encoded 表单中提取表单
              formFields(('email, 'total.as[Money])).as(Order) { order =>
                complete {
                  // 完成序列化的Future结果
                  (myDbActor ? Update(order)).mapTo[TransactionResult]
                }
              }
            },
            get {
              // 调试助手
              logRequest("GET-ORDER") {
                //使用一个域内的编码器去创建一个完成函数
                completeWith(instanceOf[Order]) { completer =>
                  // 用户处理
                  processOrderRequest(orderId, completer)
                }
              }
            })
        },
        path("items") {
          get {
            // 参数提取为case class对象
            parameters(('size.as[Int], 'color ?, 'dangerous ? "no"))
              .as(OrderItem) { orderItem =>
                //使用从必需和可选查询参数创建的case类实例路由，这里是转化为了一个OrderItem对象
                complete {
                  // 完成序列化的Future结果
                  (myDbActor ? UpdateOrderItem(orderItem)).mapTo[TransactionResult]
                }

              }
          }
        })
    },
    pathPrefix("documentation") {
      // 如果客户端接受压缩的响应，（可选）使用Gzip或Deflate压缩响应
      encodeResponse {
        //提供来自JAR资源的静态内容
        getFromResourceDirectory("docs")
      }
    },
    path("oldApi" / Remaining) { pathRest =>
      redirect("http://oldapi.example.com/" + pathRest, MovedPermanently)
    }
  )
}