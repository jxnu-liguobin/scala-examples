package controllers

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy, ThrottleMode}
import models.ThrottledRequest
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
class AsyncController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext,
                                                                                    materializer: Materializer) extends AbstractController(cc) {

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

    /**
     * 限制请求速度
     *
     * @return
     */
    def throttledAction = throttle {
        Action { implicit request =>
            Ok("Finish.")
        }
    }


    //负责拦截所有发往目标 Action 的请求<为每个请求创建“开关”并发送给“限速器”
    //只有通过限速器(sourceQueue)的请求才会被执行
    private def throttle[A](action: Action[A]) = Action.async(action.parser) { request =>
        val promise = Promise[Boolean]()
        sourceQueue.offer(ThrottledRequest(promise, System.currentTimeMillis()))
        promise.future.flatMap { isTimeout =>
            if (!isTimeout) {
                action(request)
            } else {
                Future.successful(Forbidden("Timeout."))
            }
        }
    }

    //缓冲区大小为 100，缓冲区溢出时的处理策略为 backpressure ，以防止请求丢失
    //通过 throttle 方法设置请求处理速度为 1 个每秒，Sink 负责处理请求的放行和超时
    private val sourceQueue = Source.
      queue[ThrottledRequest](100, OverflowStrategy.backpressure).
      throttle(1, 1.second, 1, ThrottleMode.shaping).
      toMat(Sink.foreach { r =>
          // 处理未超时请求
          if (System.currentTimeMillis() - r.time <= 15000) {
              r.promise.success(false)
          } else {
              r.promise.success(true)
          }
      })(Keep.left).run() //需要implicit materializer

}
