package services

import entity.User
import play.api.libs.json.Json

/**
 * @author 梦境迷离
 * @version 1.0, 2019-03-16
 */
class UserService {

    def getJsonByUser() = {

        //json序列化
        val user = User(1, "张三")
        val userJson = Json.toJson[User](user)
        println(userJson.toString())

        //构建json
        //直接构建，key必须一致
        val json = Json.obj(
            "id" -> 1,
            "name" -> "我叫哈哈哈"
        )
        //失败则拿不到user
        val use: User = Json.fromJson[User](json).get
        println(use)
    }
}

object TestUserService extends App {
    new UserService getJsonByUser()

}