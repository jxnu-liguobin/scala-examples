package dao

import javax.inject.Inject
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * 使用postgresqldb
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-04
 */
class UserPostgresqlDAO @Inject()(@NamedDatabase("postgresqldb") protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

    import profile.api._

    private val Users = TableQuery[UsersTable]

    //Table[(Int, String)]，用元组太麻烦了
    private class UsersTable(tag: Tag) extends Table[User](tag, "user") {

        def id = column[Int]("id", O.PrimaryKey)

        def userName = column[String]("user_name")

        def * = (id, userName) <> ((User.apply _).tupled, User.unapply)

        //        def * : ProvenShape[(Int, String)] = (id, userName)
    }

    //根据id查找
    def findById(id: Int): Future[Seq[User]] = db.run(Users.filter(_.id === id).result)

    //查询所有，根据姓名排序
    def all(): Future[Seq[User]] = db.run(Users.sortBy(_.userName).result)

    //插入
    def insert(user: User): Future[Unit] = db.run(Users += user).map(_ => ())

    //部分有值
    def insert2(user: User): Future[Unit] = db.run(Users.map(user => (user.id, user.userName)) += (user.id, user.userName)).map(_ => ())

    //根据id删除
    def deleteUserById(id: Int): Future[Int] = db.run(Users.filter(_.id === id).delete)

    //更新单列姓名
    def updateUserName(user: User) = db.run(Users.filter(_.id === user.id).map(_.userName).update(user.userName))

    //根据传入的user对象，查询id，并修改值 (修改多列)
    def updateUser(user: User) = db.run(Users.filter(_.id === user.id).map(user => (user.id, user.userName)).update(user.id, user.userName))


}
