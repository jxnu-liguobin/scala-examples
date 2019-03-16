package services

import java.time.{Clock, Instant}

import javax.inject._
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
 * 此类演示如何在应用程序启动和停止时执行代码。它在应用程序启动时启动计时器。当应用程序停止时，它会打印出应用程序运行的时间
 *
 * 单例 饥汉模式
 */
@Singleton
class ApplicationTimer @Inject()(clock: Clock, appLifecycle: ApplicationLifecycle) {

    // 应用程序启动时调用此代码
    private val start: Instant = clock.instant
    Logger.logger.info(s"ApplicationTimer demo: Starting application at $start.")

    //当应用程序启动时，使用ApplicationLifecycle实例注册停止钩子
    //停止钩内的代码将应用程序停止时运行
    appLifecycle.addStopHook { () =>
        val stop: Instant = clock.instant
        val runningTime: Long = stop.getEpochSecond - start.getEpochSecond
        Logger.logger.info(s"ApplicationTimer demo: Stopping application at ${clock.instant} after ${runningTime}s.")
        Future.successful(())
    }
}
