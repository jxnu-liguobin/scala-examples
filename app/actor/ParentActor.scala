package actor

import actor.ConfiguredChildActor.GetConfig
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import javax.inject.Inject
import models.AppConfig
import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.ExecutionContext

object ParentActor {

    case class GetChild(key: String)

}

class ParentActor @Inject()(configuration: Configuration, childFactory: ConfiguredChildActor.Factory)(implicit ec: ExecutionContext)
  extends Actor with InjectedActorSupport {

    import ParentActor._
    import akka.pattern.ask

    import scala.concurrent.duration._

    implicit val timeout: Timeout = 5.seconds

    override def receive = {
        //父actor收到GetChild消息
        case GetChild(key: String) => {
            val send = sender()
            //使用injectedChild，若要创建和获取对子参与者的引用，请传入key。第二个参数key将用作子actor的名称
            val child: ActorRef = injectedChild(childFactory(key), key)
            //返回actor的名称，Actor[akka://application/user/parent-actor/hello#1408509607]
            //根据key获取值
            (child ? GetConfig).mapTo[AppConfig].map { msg =>
                println(msg.getClass)
                send ! msg
            }
        }
    }
}