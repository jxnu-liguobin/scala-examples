package controllers

import java.io._

import javax.inject.{Inject, Singleton}
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.util.Random

/**
 * @author 梦境迷离
 * @version 1.0, 2019-03-23
 */
@Singleton
class FileController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


    /**
     * 显示上传页面
     *
     * @return
     */
    def toUpload = Action {
        Ok(views.html.fileupload("文件上传"))
    }


    /**
     * 使用ajax的文件上传
     *
     * @return
     */
    def ajaxUpload() = Action(parse.multipartFormData) {
        implicit request => {
            request.body.file("file").map(file => uploadFile(file)).getOrElse(Ok(Json.obj("status" -> "FAIL")))
        }

    }

    /**
     * 文件上传
     *
     * @return
     */
    def upload() = Action(parse.multipartFormData) {
        implicit request => {
            request.body.file("file").map(file => uploadFile(file)).getOrElse(Ok(Json.obj("status" -> "FAIL")))
        }
    }

    private val uploadFile = (file: FilePart[Files.TemporaryFile]) => {
        val fileName = file.filename
        println(s"文件名:$fileName")
        var nameSuffix = ""
        //检测文件是否存在后缀名
        if (fileName.lastIndexOf(".") > -1) {
            nameSuffix = fileName.substring(fileName.lastIndexOf("."))
            println(s"后缀名:$nameSuffix")

        }
        //给文件取新的随机名
        val newName = new Random().nextInt(1000000000)
        //目标目录路径
        val toFile: File = new File("D:/git_project/scala_play_learn/temp/")
        if (!toFile.exists()) {
            //不存在就创建新的文件夹
            toFile.mkdir()
        }
        //准备存放的文件
        val f = new File(s"D:/git_project/scala_play_learn/temp/$newName$nameSuffix")
        //将文件移动到新的文件
        file.ref.moveFileTo(f, true)
        Ok(Json.obj("status" -> "OK"))
    }
}
