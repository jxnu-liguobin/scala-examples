package controllers

import actor.ParentActor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Named, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * 依赖注入子actor
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
@Singleton
class Actor3Controller @Inject()(@Named("parent-actor") configuredActor: ActorRef, components: ControllerComponents)
                                (implicit ec: ExecutionContext) extends AbstractController(components) {


    implicit val timeout: Timeout = 5.seconds

    /**
     * 根据名称，获取actor，并以key作为actor的前缀
     *
     * @param key
     * @return
     */
    def getChildConfig(key: String) = Action.async {
        (configuredActor ? ParentActor.GetChild(key)).mapTo[String].map { message =>
            Ok(message)
        }
    }
}
