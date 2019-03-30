package actor

import akka.actor.Actor
import javax.inject.Inject
import play.api.Configuration

/**
 * 依赖注入actor
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
class ConfiguredActor @Inject()(configuration: Configuration) extends Actor {

    import ConfiguredActor._

    val config = configuration.getOptional[String]("hello").getOrElse("none")

    //收到，并回复config
    def receive = {
        case GetConfig => sender() ! config
        case _ => sender() ! "Not found"
    }
}

object ConfiguredActor {

    case object GetConfig

}
