package dao

import javax.inject.Inject
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.{GetResult, JdbcProfile}

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

    private lazy val Users = TableQuery[UsersTable]

    //    type User = (Int,String)
    //    type User = (Int, String)

    //Table[(Int, String)]，用元组太麻烦了
    private class UsersTable(tag: Tag) extends Table[User](tag, "user") {

        def id = column[Int]("id", O.PrimaryKey)

        def userName = column[String]("user_name")


        def * = (id, userName) <> ((User.apply _).tupled, User.unapply) //使用case class 不需要定义type

        //        def * = (id, userName) //定义Table[User],此时使用_1 _2 替换原来的id,userName,插入需要传元组
        //        def * : ProvenShape[(Int, String)] = (id, userName) //直接定义Table[(Int,String)]
    }

    //根据id查找
    def findById(id: Int): Future[Seq[User]] = db.run(Users.filter(_.id === id).result)

    //查询所有，根据姓名排序
    def all(): Future[Seq[User]] = db.run(Users.sortBy(_.userName).result)

    //根据id倒序
    def allDesc(): Future[Seq[User]] = db.run(Users.sortBy(_.id.desc).result)

    //可使用flatMap替代for
    //内连接:返回a.id,b.userName
    def innerJoin(): Future[Seq[(Int, String)]] = {
        val q = for {
            a <- Users
            b <- Users
            if a.id === b.id
        } yield (a.id, b.userName)

        db.run(q.result)
    }

    //左外连接:返回a.id,b.userName
    def leftJoin(): Future[Seq[(Int, String)]] = {
        val q = for {
            (a, b) <- Users.joinLeft(Users.distinctOn(_.id)).on(_.id === _.id)
        } yield (a.id, a.userName)

        db.run(q.result)
    }

    //使用自定义sql需要的隐式对象
    implicit val getUserResult = GetResult(r => User(r.<<, r.<<))

    //直接使用自定义sql
    //$是用来传递变量的，但$穿过来的值会被加上单引号，#不会使用转义（虽然此处未用）
    def plainSql(): Future[Vector[User]] = {
        val action = sql""" SELECT "id", "user_name" FROM "user" """.as[User]
        db.run(action)
    }

    //根据用户id分组，并取出id>2且倒序显示
    //group只能查询到被用来分组或在聚合函数中的字段，分组后的过滤是having
    def groupByUserId() = {
        val q = Users.groupBy(_.id).map(_._1).filter(_.? > 2).sortBy(_.?.desc)
        db.run(q.result)
    }

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
