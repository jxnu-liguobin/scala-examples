package io.github.dreamylost.actor.http.routing.dsl.upload


import java.io.File

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, Multipart, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.FileInfo

/**
 * 将以multipart形式上传的文件内容以流式传输到磁盘上的文件，并提供有关上载的文件和元数据。(不稳定的API)
 *
 * 单个文件 def storeUploadedFile(fieldName: String, destFn: FileInfo => File): Directive[(FileInfo, File)]
 * 多个：def storeUploadedFiles(fieldName: String, destFn: FileInfo => File): Directive1[immutable.Seq[(FileInfo, File)]]
 *
 * 如果向磁盘写入错误，则请求将失败，并抛出异常。如果没有给定名称的字段，该请求将被拒绝。
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class FIleUpload_4 {

  def tempDestination(fileInfo: FileInfo): File = File.createTempFile(fileInfo.fileName, ".tmp")

  val route =
    storeUploadedFile("csv", tempDestination) {
      case (metadata, file) =>
        //处理文件。。
        file.delete()
        complete(StatusCodes.OK)
    }

  val multipartForm =
    Multipart.FormData(
      Multipart.FormData.BodyPart.Strict(
        "csv",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
        Map("filename" -> "primes.csv")))

  /** *
   * {{{
   *     Post("/", multipartForm) ~> route ~> check {
   *     status shouldEqual StatusCodes.OK
   *   }
   * }}}
   */


  def tempDestinations(fileInfo: FileInfo): File = File.createTempFile(fileInfo.fileName, ".tmp")

  val routes =
    storeUploadedFiles("csv", tempDestinations) { files =>
      val finalStatus = files.foldLeft(StatusCodes.OK) {
        case (status, (metadata, file)) =>
          file.delete()
          status
      }

      complete(finalStatus)
    }

  val multipartForms = Multipart.FormData(
    Multipart.FormData.BodyPart.Strict(
      "csv",
      HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
      Map("filename" -> "primesA.csv")),
    Multipart.FormData.BodyPart.Strict(
      "csv",
      HttpEntity(ContentTypes.`text/plain(UTF-8)`, "41,43,47\n53,59,6167,71\n73,79,83\n"),
      Map("filename" -> "primesB.csv")))

  /**
   * {{{
   *    Post("/", multipartForms) ~> routes ~> check {
   *     status shouldEqual StatusCodes.OK
   *   }
   * }}}
   */
}
