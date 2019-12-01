package io.github.dreamylost.actor.http.client

import akka.actor.{ Actor, ActorLogging }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.util.ByteString

/**
 * 在actor内部使用基于Future的API
 *
 * @author 梦境迷离
 * @since 2019-12-01
 * @version v1.0
 */
object WebClient_2 {

  class Myself extends Actor with ActorLogging {

    import akka.pattern.pipe
    import context.dispatcher

    //2.6过期，暂时忽略。为了兼容WebServer_3，本模块的actor均使用2.6
    final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

    val http = Http(context.system)

    override def preStart() = {
      http.singleRequest(HttpRequest(uri = "http://akka.io"))
        .pipeTo(self) //不应从Future的回调中访问actor的状态（map,onComplete等）
    }

    def receive = {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
          log.info("Got response, body: " + body.utf8String)
        }
      case resp@HttpResponse(code, _, _, _) =>
        log.info("Request failed, response code: " + code)
        resp.discardEntityBytes()
    }

  }

}
