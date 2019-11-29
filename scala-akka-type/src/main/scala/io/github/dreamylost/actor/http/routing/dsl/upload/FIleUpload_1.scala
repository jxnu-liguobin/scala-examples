package io.github.dreamylost.actor.http.routing.dsl.upload

import java.io.File

import akka.Done
import akka.actor.{ Actor, ActorSystem, Props }
import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.model.Multipart.FormData.BodyPart
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ FileIO, Framing, Sink }
import akka.util.ByteString

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * 文件上传
 *
 * @see https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/file-upload-directives/index.html
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
class FIleUpload_1 {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val uploadVideo =
    path("video") {
      entity(as[Multipart.FormData]) { formData =>
        //收集multipart并存进map
        val allPartsF: Future[Map[String, Any]] = formData.parts.mapAsync[(String, Any)](1) {
          case b: BodyPart if b.name == "file" =>
            //当文件的块到达时流存进到文件中，并将返回一个future file的存储位置
            val file = File.createTempFile("upload", "tmp")
            b.entity.dataBytes.runWith(FileIO.toPath(file.toPath)).map(_ =>
              (b.name -> file))
          case b: BodyPart =>
            //提取表单的字段值
            b.toStrict(2.seconds).map(strict =>
              (b.name -> strict.entity.data.utf8String))
        }.runFold(Map.empty[String, Any])((map, tuple) => map + tuple)

        val done = allPartsF.map { allParts =>
          //验证并解码
          db.create(Video(
            file = allParts("file").asInstanceOf[File],
            title = allParts("title").asInstanceOf[String],
            author = allParts("author").asInstanceOf[String]))
        }

        //处理完成后，为用户创建响应
        onSuccess(allPartsF) { allParts =>
          complete {
            "ok!"
          }
        }
      }
    }

  case class Video(file: File, title: String, author: String)

  object db {
    def create(video: Video) = ???

  }

  /**
   * 您可以在上传的文件到达时对其进行转换，而不是像前面的示例一样将它们存储在临时文件中。在此示例中会接受任意数量的.csv文件，将其解析为几行，
   * 并在将其发送给actor进行进一步处理之前将每一行拆分：
   */
  val splitLines = Framing.delimiter(ByteString("\n"), 256)

  val metadataActor = system.actorOf(Props(classOf[MetadataActor]))

  val csvUploads =
    path("metadata" / LongNumber) { id =>
      entity(as[Multipart.FormData]) { formData =>
        val done: Future[Done] = formData.parts.mapAsync(1) {
          case b: BodyPart if b.filename.exists(_.endsWith(".csv")) =>
            b.entity.dataBytes
              .via(splitLines)
              .map(_.utf8String.split(",").toVector)
              .runForeach(csv =>
                //仅演示作用，无任何实际操作
                metadataActor ! MetadataActor.Entry(id, csv)
              )
          //数据处理
          //metadataActor ! MetadataActor.Entry(id, csv)
          case _ => Future.successful(Done)
        }.runWith(Sink.ignore)

        onSuccess(done) { _ =>
          complete {
            "ok!"
          }
        }
      }
    }

  class MetadataActor extends Actor {
    override def receive: Receive = ???
  }

  object MetadataActor {

    case class Entry(id: Long, csv: Vector[String])

  }

}
