package action

import com.google.common.net.HttpHeaders
import com.typesafe.scalalogging.LazyLogging
import models.User
import play.api.http.Writeable
import play.api.libs.json.{ Json, Writes }
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/12/2
 */
class ComponentsController()(implicit ce: ExecutionContext, cc: ControllerComponents) extends BaseController with LazyLogging {

  //携带用户信息的请求
  class UserRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request) {
    def userId: Int = user.id
  }

  //携带授权后的用户信息
  class AuthRequest[A](val role: String, userRequest: UserRequest[A]) extends UserRequest(userRequest.user, userRequest)

  //授权校验动作
  object AuthAction extends ActionBuilder[UserRequest, AnyContent] with ActionRefiner[Request, UserRequest] {
    override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

    override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
      def getUserFromRequest[A](request: Request[A]): Future[Option[User]] = {
        //从请求中提取出用户信息，包装为User
        ???
      }

      getUserFromRequest(request).map {
        case Some(u) => Right(new UserRequest(u, request))
        case None => Left(Unauthorized.withHeaders(HttpHeaders.WWW_AUTHENTICATE -> """无权限"""))
      }

    }

    override protected def executionContext: ExecutionContext = ce
  }

  //将UserRequest转换为AuthRequest
  def AuthRequestTransformer: ActionTransformer[UserRequest, AuthRequest] = new ActionTransformer[UserRequest, AuthRequest] {
    override protected def transform[A](request: UserRequest[A]): Future[AuthRequest[A]] = {
      Future.successful(new AuthRequest[A]("root", request))
    }

    override protected def executionContext: ExecutionContext = ce
  }

  /**
   * 权限校验
   */
  def PermissionCheckAction(permission: String): ActionFunction[UserRequest, AuthRequest] = AuthRequestTransformer andThen new ActionFilter[AuthRequest] {
    override protected def filter[A](request: AuthRequest[A]): Future[Option[Result]] = {
      if (permission == "root") {
        //root或无权限的，直接通过
        Future.successful(None)
      } else {
        //其他角色需要查询db
        Future.successful(None)
      }
    }

    override protected def executionContext: ExecutionContext = ce
  }

  protected def controllerComponents: ControllerComponents = cc

  implicit def writeableOf_caseClass[T <: Product](implicit codec: Codec, tjs: Writes[T]): Writeable[T] = {
    Writeable(a => codec.encode(Json.stringify(Json.toJson(a))), Some("application/json"))
  }

  implicit def writeableOf_seqOfCaseClass[T <: Product](implicit codec: Codec, tjs: Writes[T]): Writeable[Seq[T]] = {
    Writeable(a => codec.encode(Json.stringify(Json.toJson(a))), Some("application/json"))
  }

}
