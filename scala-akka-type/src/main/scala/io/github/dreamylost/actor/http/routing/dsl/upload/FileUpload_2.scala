package io.github.dreamylost.actor.http.routing.dsl.upload


import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, Multipart }
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Framing
import akka.util.ByteString

import scala.concurrent.Future

/**
 * 简单文件上传
 *
 * def fileUpload(fieldName: String): Directive1[(FileInfo, Source[ByteString, Any])]
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class FileUpload_2 {
  //将整数添加为服务
  val route =
    extractRequestContext { ctx =>
      implicit val materializer = ctx.materializer

      fileUpload("csv") {
        case (metadata, byteSource) =>

          val sumF: Future[Int] =
          //将它们到达的数字相加，以便我们可以接受任何大小的文件
            byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
              .mapConcat(_.utf8String.split(",").toVector)
              .map(_.toInt)
              .runFold(0) { (acc, n) => acc + n }

          onSuccess(sumF) { sum => complete(s"Sum: $sum") }
      }
    }

  // 测试
  val multipartForm = Multipart.FormData(Multipart.FormData.BodyPart.Strict(
    "csv",
    HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
    Map("filename" -> "primes.csv")))

  /** *
   * {{{
   *
   *   Post("/", multipartForm) ~> route ~> check {
   *   status shouldEqual StatusCodes.OK
   *   responseAs[String] shouldEqual "Sum: 178"
   * }
   * }}}
   */

}
