import java.time.Clock

import com.google.inject.AbstractModule
import services.{ApplicationTimer, AtomicCounter, Counter}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

    override def configure() = {

        //使用的系统时钟作为默认的执行点
        bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
        //告知Guice在应用启动时创建ApplicationTimer的实例
        bind(classOf[ApplicationTimer]).asEagerSingleton()
        //将atomiccounter设置为counter的实现
        bind(classOf[Counter]).to(classOf[AtomicCounter])
    }

}
