package controllers

import entity.User
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

/**
 * Restful Api Controller
 *
 * @author 梦境迷离
 * @time 2019-03-15
 */
@Singleton
class RestfulApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


    /**
     * 无参数
     * /listUser
     *
     * @return
     */
    def listUser() = Action {
        Ok(Json.toJson(User.users))
    }

    /**
     * POST提交，并展示所有User
     * /saveUser/user
     *
     * @return
     */
    def saveUser = Action(BodyParsers.parse.json) { request =>
        val user = request.body.validate[User]
        user.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            user => {
                User.addUser(user)
                Ok(Json.obj("status" -> "OK", "users" -> User.users))
            }
        )
    }

    /**
     * 查询参数
     * /getUser?id=1
     *
     * @param id
     * @return
     */
    def getUser(id: Int) = Action {
        User.getUser(id) match {
            case Some(user) => Ok(Json.toJson(user))
            case _ => Ok(Json.obj("status" -> "OK", "message" -> "User Not Found!"))
        }
    }

    /**
     * 路径参数
     * /getUserByPathParam/1
     *
     * @param id
     * @return
     */
    def getUserByPathParam(id: Int) = Action {
        User.getUser(id) match {
            case Some(user) => Ok(Json.toJson(user))
            case _ => Ok(Json.obj("status" -> "OK", "message" -> "User Not Found!"))
        }
    }

    /**
     * 路径参数
     * /deleteUser/1
     *
     * @param id
     * @return
     */
    def deleteUser(id: Int) = Action {
        User.deleteUser(id)
        Ok(Json.obj("status" -> "OK"))
    }

    /**
     * /updateUser/user
     *
     * @return
     */
    def updateUser = Action(BodyParsers.parse.json) { request =>
        val user = request.body.validate[User]
        user.fold(
            errors => {
                BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
            },
            user => {
                User.updateUser(user)
                Ok(Json.obj("status" -> "OK", "users" -> User.users))
            }
        )
    }
}
