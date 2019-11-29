package io.github.dreamylost.actor.http.routing.dsl

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }


/**
 *
 * 一个小型Web服务器，该服务器负责记录构建作业及其状态和持续时间，按ID和状态查询作业，以及清除作业历史记录。 首先，我们先定义行为，该行为将充当构建作业信息的存储库
 * 注意，actor相关版本必须2.6
 *
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
object WebServer_3 extends App {

  //定义表示任务的状态
  sealed trait Status

  final object Successful extends Status

  final object Failed extends Status

  final case class Job(id: Long, projectName: String, status: Status, duration: Long)

  final case class Jobs(jobs: Seq[Job])

  object BuildJobRepository {

    //定义响应的成功或失败
    sealed trait Response

    case object OK extends Response

    final case class KO(reason: String) extends Response

    //表示可以发送到此行为的所有可能的消息
    sealed trait BuildJobProtocol

    final case class AddJob(job: Job, replyTo: ActorRef[Response]) extends BuildJobProtocol

    final case class GetJobById(id: Long, replyTo: ActorRef[Option[Job]]) extends BuildJobProtocol

    final case class GetJobByStatus(status: Status, replyTo: ActorRef[Seq[Job]]) extends BuildJobProtocol

    final case class ClearJobs(replyTo: ActorRef[Response]) extends BuildJobProtocol

    //此行为处理所有可能的传入消息，并将状态保留在函数参数中
    def apply(jobs: Map[Long, Job] = Map.empty): Behavior[BuildJobProtocol] = Behaviors.receiveMessage {
      case AddJob(job, replyTo) if jobs.contains(job.id) =>
        replyTo ! KO("Job already exists")
        Behaviors.same
      case AddJob(job, replyTo) =>
        replyTo ! OK
        BuildJobRepository(jobs.+(job.id -> job))
      case GetJobById(id, replyTo) =>
        replyTo ! jobs.get(id)
        Behaviors.same
      case ClearJobs(replyTo) =>
        replyTo ! OK
        BuildJobRepository(Map.empty)
    }
  }


  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
  import spray.json.{ DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat }

  //定义json序列化与反序列化
  trait JsonSupport extends SprayJsonSupport {
    //导入默认的编码器(Int, String, Lists etc)

    import DefaultJsonProtocol._

    implicit object StatusFormat extends RootJsonFormat[Status] {
      def write(status: Status): JsValue = status match {
        case Failed => JsString("Failed")
        case Successful => JsString("Successful")
      }

      def read(json: JsValue): Status = json match {
        case JsString("Failed") => Failed
        case JsString("Successful") => Successful
        case _ => throw new DeserializationException("Status unexpected")
      }
    }

    implicit val jobFormat = jsonFormat4(Job)

    implicit val jobsFormat = jsonFormat1(Jobs)
  }

  import BuildJobRepository._
  import akka.actor.typed.ActorSystem
  import akka.http.scaladsl.model.StatusCodes
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.server.Route
  import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, post }
  import akka.http.scaladsl.server.directives.PathDirectives.path
  import akka.http.scaladsl.server.directives.RouteDirectives.complete
  import akka.util.Timeout

  import scala.concurrent.Future
  import scala.concurrent.duration._

  //定义将与先前定义的行为进行通信并处理其所有可能响应的Route
  class JobRoutes(system: ActorSystem[_], buildJobRepository: ActorRef[BuildJobProtocol]) extends JsonSupport {

    import akka.actor.typed.scaladsl.AskPattern._

    // asking someone requires a timeout and a scheduler, if the timeout hits without response
    // the ask is failed with a TimeoutException
    implicit val timeout: Timeout = 3.seconds
    implicit val scheduler = system.scheduler

    lazy val theJobRoutes: Route =
      pathPrefix("jobs") {
        concat(
          pathEnd {
            concat(
              post {
                entity(as[Job]) { job =>
                  val operationPerformed: Future[Response] = buildJobRepository.ask(replyTo => AddJob(job, replyTo))
                  onSuccess(operationPerformed) {
                    case OK => complete("Job added")
                    case KO(reason) => complete(StatusCodes.InternalServerError -> reason)
                  }
                }
              },
              delete {
                val operationPerformed: Future[Response] = buildJobRepository.ask(replyTo => ClearJobs(replyTo))
                onSuccess(operationPerformed) {
                  case OK => complete("Jobs cleared")
                  case KO(reason) => complete(StatusCodes.InternalServerError -> reason)
                }
              }
            )
          },
          (get & path(LongNumber)) { id =>
            val maybeJob: Future[Option[Job]] = buildJobRepository.ask(replyTo => GetJobById(id, replyTo))
            rejectEmptyResponse {
              complete(maybeJob)
            }
          }
        )
      }
  }


  //引导我们的Web服务器并实例化我们的Behavior

  import akka.actor.typed.scaladsl.adapter._
  import akka.http.scaladsl.Http
  import akka.stream.ActorMaterializer
  import akka.{ Done, actor }

  import scala.concurrent.ExecutionContextExecutor
  import scala.util.{ Failure, Success }

  val system = ActorSystem[Done](Behaviors.setup[Done] { ctx =>
    //http不知道akka输入的类型，因此需要创建没有类型的system/materializer
    implicit val untypedSystem: actor.ActorSystem = ctx.system.toClassic //2.5使用toUntyped方法
    implicit val materializer: ActorMaterializer = ActorMaterializer()(ctx.system.toClassic)
    implicit val ec: ExecutionContextExecutor = ctx.system.executionContext

    val buildJobRepository = ctx.spawn(BuildJobRepository(), "BuildJobRepositoryActor")

    val routes = new JobRoutes(ctx.system, buildJobRepository)

    val serverBinding: Future[Http.ServerBinding] = Http()(untypedSystem).bindAndHandle(routes.theJobRoutes, "localhost", 8080)
    serverBinding.onComplete {
      case Success(bound) =>
        println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
      case Failure(e) =>
        Console.err.println(s"Server could not start!")
        e.printStackTrace()
        ctx.self ! Done
    }
    Behaviors.receiveMessage {
      case Done =>
        Behaviors.stopped
    }

  }, "BuildJobsServer")

}