package controllers.postgresql

import dao.UserPostgresqlDAO
import javax.inject.Inject
import models.User
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

/**
 * 为了方便不使用服务层
 * 直接调dao
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-04
 */
class PostgresqlSlickController @Inject()(userDao: UserPostgresqlDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {


    /**
     * 查询所有用户，根据id逆序
     *
     * @return
     */
    def findAllDesc() = Action.async {
        userDao.allDesc().map {
            user => Ok(Json.toJson(user))
        }
    }


    /**
     * 内连接，自连接 ，a.id===b.id时，返回User(a.id,b.userName)
     *
     * @return
     */
    def innerJoinSelf() = Action.async {
        userDao.innerJoin().map {
            user => {
                val users = for ((id, name) <- user) yield User(id, name)
                Ok(Json.toJson(users))
            }
        }
    }

    /**
     * 左外连接，也是自连接，返回User(a.id,b.userName)
     *
     * @return
     */
    def leftJoinSelf() = Action.async {
        userDao.leftJoin().map {
            user => {
                val users = for ((id, name) <- user) yield User(id, name)
                Ok(Json.toJson(users))
            }
        }
    }

    /**
     * 使用自定义sql查询（所有用户）
     *
     * @return
     */
    def findAllWithPlainSql() = Action.async {
        userDao.plainSql().map {
            user => Ok(Json.toJson(user))
        }
    }

    /**
     * 分组查询，根据id分组（id唯一，等于查询所有，其他操作：只取id大于2的，且倒序）
     *
     * @return
     */
    def findGroupByUserId() = Action.async {
        userDao.groupByUserId().map {
            user => {
                val userIds = for (id <- user) yield id
                Ok(Json.obj("ids" -> userIds))
            }
        }
    }


    /**
     * 根据路径参数查询一个单一的用户
     *
     * @param id
     * @return
     */
    def findOne(id: Int) = Action.async {
        userDao.findById(id).map {
            user => Ok(Json.toJson(user))
        }
    }

    /**
     * 查询所有用户
     *
     * @return
     */
    def findAll = Action.async {
        userDao.all().map {
            user => Ok(Json.toJson(user))
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
            count => Ok(Json.obj("count" -> count))
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
            count => Ok(Json.obj("count" -> count))
        }
    }
}
