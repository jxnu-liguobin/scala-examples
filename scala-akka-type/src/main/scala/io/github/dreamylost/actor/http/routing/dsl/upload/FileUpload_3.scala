package io.github.dreamylost.actor.http.routing.dsl.upload


import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, Multipart }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Framing
import akka.util.ByteString

import scala.concurrent.Future

/**
 *
 * 此伪指令将所有文件缓冲到磁盘上具有akka-http-upload前缀的文件中的临时文件中。这是为了解决HTTP multipart格式的限制。
 *
 * 要仅上载一个文件，可能首选使用fileUpload指令，因为它直接传输文件而无需缓冲
 *
 * def fileUploadAll(fieldName: String): Directive1[immutable.Seq[(FileInfo, Source[ByteString, Any])]]
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class FileUpload_3 {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = extractRequestContext { ctx =>
    fileUploadAll("csv") {
      case byteSources =>
        //累积每个文件的总和
        val sumF: Future[Int] = byteSources.foldLeft(Future.successful(0)) {
          case (accF, (metadata, byteSource)) =>
            //计算数值的和，最多帧1024。
            //解码时允许帧的最大长度。如果超过最大长度，该Flow将使得流失效。
            val intF = byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
              .mapConcat(_.utf8String.split(",").toVector)
              .map(_.toInt)
              .runFold(0) { (acc, n) => acc + n }

            accF.flatMap(acc => intF.map(acc + _))
        }

        onSuccess(sumF) { sum => complete(s"Sum: $sum") }
    }
  }

  // 测试
  val multipartForm =
    Multipart.FormData(
      Multipart.FormData.BodyPart.Strict(
        "csv",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
        Map("filename" -> "primesA.csv")),
      Multipart.FormData.BodyPart.Strict(
        "csv",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "41,43,47\n53,59,61,67,71\n73,79,83\n"),
        Map("filename" -> "primesB.csv")))

  /**
   * {{{
   *     Post("/", multipartForm) ~> route ~> check {
   *     status shouldEqual StatusCodes.OK
   *     responseAs[String] shouldEqual "Sum: 855"
   *   }
   * }}}
   */
}
