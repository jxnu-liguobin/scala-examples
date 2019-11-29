package io.github.dreamylost.actor.http.introduction

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

/**
 * Streaming,The resulting response is rendered as json and returned when the response arrives from the actor.
 *
 * 测试：
 * curl -X PUT http://localhost:8080/auction?bid=22&user=MartinO 在MAC iterm2上测试失败，需要使用postman
 * curl http://localhost:8080/auction
 *
 */
object WebServer_4 {

  case class Bid(userId: String, offer: Int)

  case object GetBids

  case class Bids(bids: List[Bid])

  class Auction extends Actor with ActorLogging {
    var bids = List.empty[Bid]

    def receive = {
      case bid@Bid(userId, offer) =>
        bids = bids :+ bid
        log.info(s"Bid complete: $userId, $offer")
      case GetBids => sender() ! Bids(bids)
      case _ => log.info("Invalid message")
    }
  }

  implicit val bidFormat = jsonFormat2(Bid) //Case classes with 2 parameters
  implicit val bidsFormat = jsonFormat1(Bids) //Case classes with 1 parameters

  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher //actor运行上下文（调度器）
    implicit val timeout: Timeout = 5.seconds //ask(?)发送的超时

    //创建一个名为auction的Auction类型的actor
    val auction = system.actorOf(Props[Auction], "auction")

    //构造路由 /auction，包含put/get方法
    val route =
      path("auction") {
        concat(
          put {
            parameter("bid".as[Int], "user") { (bid, user) =>
              //发送异步消息
              auction ! Bid(user, bid)
              //使用给定的参数完成请求
              complete((StatusCodes.Accepted, "bid placed"))
            }
          },
          get {
            //向actor查询当前状态，?有回调与!不同
            val bids: Future[Bids] = (auction ? GetBids).mapTo[Bids]
            complete(bids)
          }
        )
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}