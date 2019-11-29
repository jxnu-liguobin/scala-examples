package io.github.dreamylost.actor.http.routing.dsl.upload

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, Multipart, StatusCodes }
import akka.http.scaladsl.server.Directives._

/**
 *
 * def uploadedFile(fieldName: String): Directive1[(FileInfo, File)]
 *
 * 该指令会将请求的内容流式传输到一个文件中，但是只有在文件完全写入后才能开始处理这些内容。
 * 对于流式API，最好使用fileUpload指令，因为它允许对传入数据字节进行流式处理。
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class UploadedFile {


  /**
   * 将使用给定文件名的multipart提交的文件的字节流到磁盘上的临时文件中。
   * 如果写入磁盘时出错，请求将失败，并引发异常，如果没有
   * 字段请求将被拒绝，如果有多个同名的文件部分，则第一个将是
   * 已使用，后续的被忽略
   */
  val route =
    uploadedFile("csv") {
      case (metadata, file) =>
        file.delete()
        complete(StatusCodes.OK)
    }

  val multipartForm =
    Multipart.FormData(
      Multipart.FormData.BodyPart.Strict(
        "csv",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
        Map("filename" -> "primes.csv")))

  /**
   * {{{
   *    Post("/", multipartForm) ~> route ~> check {
   *     status shouldEqual StatusCodes.OK
   *   }
   * }}}
   */
}
