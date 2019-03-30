package actor

import actor.ConfiguredChildActor.GetConfig
import akka.actor.Actor
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import play.api.Configuration

/**
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
class ConfiguredChildActor @Inject()(configuration: Configuration,
                                     @Assisted key: String) extends Actor {

    //根据key获取值
    val config = configuration.getOptional[String](key).getOrElse("none")

    def receive = {
        case GetConfig => sender() ! config
    }
}

object ConfiguredChildActor {

    case object GetConfig

    //定义了一个Factory，需要key，并返回Actor
    trait Factory {
        def apply(key: String): Actor
    }

}