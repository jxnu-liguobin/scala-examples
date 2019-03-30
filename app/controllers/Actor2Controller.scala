package controllers

import actor.ConfiguredActor.GetConfig
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Named, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * 依赖注入actor
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
@Singleton
class Actor2Controller @Inject()(@Named("configured-actor") configuredActor: ActorRef, components: ControllerComponents)
                                (implicit ec: ExecutionContext) extends AbstractController(components) {

    implicit val timeout: Timeout = 5.seconds

    def getConfig = Action.async {
        (configuredActor ? GetConfig).mapTo[String].map { message =>
            Ok(message)
        }
    }

}
