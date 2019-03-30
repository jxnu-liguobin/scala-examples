package modules

import actor.{ConfiguredActor, ConfiguredChildActor, ParentActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
class MyModule extends AbstractModule with AkkaGuiceSupport {

    override def configure = {
        //这个actor都将被命名为configured-actor，并可以被依赖注入到其他需要的地方
        bindActor[ConfiguredActor]("configured-actor")
        //Guice自动绑定ConfiguredChildActor.Factory，当它被实例化时，它将提供一个Configuration的实例并转换为ConfiguredChildActor
        bindActor[ParentActor]("parent-actor")
        bindActorFactory[ConfiguredChildActor, ConfiguredChildActor.Factory]

    }

}
