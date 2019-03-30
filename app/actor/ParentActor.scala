package actor

import akka.actor.{Actor, ActorRef}
import javax.inject.Inject
import play.api.libs.concurrent.InjectedActorSupport

object ParentActor {

    case class GetChild(key: String)

}

class ParentActor @Inject()(childFactory: ConfiguredChildActor.Factory) extends Actor with InjectedActorSupport {

    import ParentActor._

    def receive = {
        case GetChild(key: String) => {
            //使用injectedChild，若要创建和获取对子参与者的引用，请传入key。第二个参数key将用作子actor的名称
            val child: ActorRef = injectedChild(childFactory(key), key)
            val name = child.toString()
            //返回actor的名称，Actor[akka://application/user/parent-actor/hello#1408509607]
            sender() ! name
        }
    }
}