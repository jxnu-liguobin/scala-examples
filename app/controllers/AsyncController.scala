package controllers

import akka.actor.ActorSystem
import javax.inject._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}

/**
 * 异步响应
 *
 * @param cc          标准控制器组件
 * @param actorSystem 延迟需要使用
 * @param exec        需要ExecutionContext来执行异步代码，根据需要修改依赖注入
 */
@Singleton
class AsyncController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

    /**
     * 延迟后返回纯文本消息
     *
     * routes”文件中的配置意味着此方法，将在应用程序收到“get”请求调用且请求路径是“/message”的路径时被调用
     */
    def message = Action.async {
        getFutureMessage(1.second).map { msg => Ok(msg) }
    }

    private def getFutureMessage(delayTime: FiniteDuration): Future[String] = {
        val promise: Promise[String] = Promise[String]()
        //这是是柯里化函数
        actorSystem.scheduler.scheduleOnce(delayTime) {
            promise.success("Hello World!")
        }(actorSystem.dispatcher) //使用Actor运行延时任务
        promise.future
    }

}
