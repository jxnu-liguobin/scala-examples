package controllers

import actor.HelloActor
import actor.HelloActor.SayHello
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
//5.seconds需要
import scala.concurrent.duration._

/**
 * 同一个系统中不能有两个相同名称的参与者，控制器必须是单例的才能保证不会重复创建actor
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
@Singleton
class ActorController @Inject()(implicit exec: ExecutionContext, system: ActorSystem, cc: ControllerComponents)
  extends AbstractController(cc) {
    //若要创建或使用actor，需要ActorSystem，implicit不能放在后面，除非使用柯里化
    //使用@Named注入刚刚绑定的actor

    //创建hello actor
    val helloActor = system.actorOf(HelloActor.props, "hello-actor")

    //一个超时的隐式对象
    implicit val timeout: Timeout = 5.seconds

    /**
     * 异步的action，使用ask模式
     *
     * @param name
     * @return
     */
    def sayHello(name: String) = Action.async {
        //向helloActor发送name，receive收到后会回复一个 hello+name
        (helloActor ? SayHello(name)).mapTo[String].map { message =>
            Ok(message)
        }
    }
}
