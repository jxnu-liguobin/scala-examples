package controllers.mysql

import dao.UserMySqlDAO
import javax.inject.{Inject, Singleton}
import models.User
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

/**
 * 使用mysql+slick进行增删查改
 *
 * 成功统一返回json
 *
 * 使用slick时需要删除jdbc依赖
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-03
 */
@Singleton
class MysqlSlickController @Inject()(userDao: UserMySqlDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {


    /**
     * 根据路径参数查询一个单一的用户
     *
     * @param id
     * @return
     */
    def findOne(id: Int) = Action.async {
        userDao.findById(id).map {
            case user => Ok(Json.toJson(user))
        }
    }

    /**
     * 查询所有用户
     *
     * @return
     */
    def findAll = Action.async {
        userDao.all().map {
            case user => Ok(Json.toJson(user))
        }
    }

    /**
     * 插入数据库
     *
     * 重复插入无效
     *
     * @return
     */
    def insertUser = Action(parse.json) { implicit request =>
        val user = request.body.validate[User]
        user.fold(
            errors => {
                BadRequest(Json.obj("status" -> "ERROR", "message" -> JsError.toJson(errors)))
            },
            userData => {
                userDao.insert(userData)
                Ok(Json.obj("status" -> "OK", "count" -> 1))
            }
        )
    }

    /**
     * 根据id删除，返回删除成功标记 1
     *
     * @param id
     * @return
     */
    def deleteById(id: Int) = Action.async {
        userDao.deleteUserById(id).map {
            case count => Ok(Json.obj("count" -> count))
        }

    }


    /**
     * 根据id更新，返回更新成功标记 1
     *
     * @return
     */
    def updateUserWithSlick = Action(parse.json).async { implicit request =>
        val user = request.body.validate[User]
        userDao.updateUser(user.get).map {
            case count => Ok(Json.obj("count" -> count))
        }
    }

}
