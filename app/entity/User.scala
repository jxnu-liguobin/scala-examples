package entity

import play.api.libs.json.Json

/**
 * @author 梦境迷离
 * @time 2019-03-15
 */
case class User(var id: Int, var name: String)

object User {
    implicit val userWrites = Json.writes[User]
    implicit val userReads = Json.reads[User]

    /**
     * 隐式转换提交的User
     * `/foo?p.index=5&p.size=42`
     *
     * @param stringBinder
     * @return
     */
    //    implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[User] {
    //        override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, User]] = {
    //            Some({
    //                val userId = stringBinder.bind(key + ".id", params)
    //                val userName = stringBinder.bind(key + ".name", params)
    //                (userId, userName) match {
    //                    case (Some(Right(id)), Some(Right(name))) => Right(User(id.toInt, name))
    //                    case _ => Left("Unable to bind User")
    //                }
    //            })
    //        }
    //
    //        override def unbind(key: String, user: User): String = {
    //            stringBinder.unbind(key + ".id", user.id.toString) + "&" + stringBinder.unbind(key + ".name", user.name)
    //        }
    //    }

    var users = List(
        User(1, "张三"),
        User(2, "李四"),
        User(3, "王五"),
        User(4, "周六")
    )

    def addUser(user: User) = {
        if (user.id == 0) {
            val r = scala.util.Random
            user.id = Some(r.nextInt(10000000)).get
        }
        users = users ::: List(user)
    }

    def getUser(id: Int): Option[User] = {
        val finds = users filter { user =>
            user.id.equals(id)
        }
        //存在多个就取第一
        finds.size match {
            case 1 => Some(finds(0))
            case _ => None
        }
    }

    def deleteUser(id: Int) = {
        val filters = users filterNot { user =>
            user.id.equals(id)
        }
        users = filters
    }

    def updateUser(user: User) = {

        //存在则修改，不存在则插入
        val isHas = users filter { u => u.id.equals(user.id) }

        val filters = users filterNot { u =>
            u.id.equals(user.id)
        }
        if (isHas != null) {
            user.name = "update-" + System.currentTimeMillis() + "-" + user.name
            users = filters ::: List(user)
        } else {
            users = users ::: List(user)
        }
    }

}