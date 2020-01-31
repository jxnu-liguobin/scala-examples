package io.github.dreamylost.wheel

import java.util
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.{ ConcurrentHashMap, CopyOnWriteArrayList, TimeUnit }

import scala.collection.JavaConverters._

/**
 * 连接建立时，把一个连接放入 TimingWheel 中进入 timeout 倒计时
 * 每次收到长连接心跳时，重新加入一次TimingWheel 相当于重置了 timer
 * timeout 时间到达时触发 EXPIRY_PROCESSING
 * EXPIRY_PROCESSING 实际就是关闭超时的连接。
 *
 * 一个实例只能支持一个固定的 timeout 时长调度
 *
 * @see https://blog.csdn.net/mindfloating/article/details/8033340
 * @author 梦境迷离
 * @since 2020-01-31
 * @version v1.0
 */
class TimeWheel[E] {

  private var tickDuration: Long = _
  private var ticksPerWheel: Int = _
  @volatile private var currentTickIndex: Int = _
  private var workerThread: Thread = _

  //轮子，是一个列表，每个元素都是Slot结构
  private var wheel: util.ArrayList[Slot[E]] = _

  //过期监听器
  private val expirationListeners = new CopyOnWriteArrayList[ExpirationListener[E]]

  //indicator的主要作用是记录插入的元素e和slot槽位的对应关系,方便后续删除一个元素
  //如删除一个元素,可以先通过indicator定位到slot位置,然后再进一步删除slot中的对应元素
  private val indicator = new ConcurrentHashMap[E, Slot[E]]

  private val shutdown = new AtomicBoolean(false)
  private val lock = new ReentrantReadWriteLock

  //初始化时间轮
  def this(tickDuration: Int, ticksPerWheel: Int, timeUnit: TimeUnit) {
    this()
    if (timeUnit == null) throw new NullPointerException("unit")
    if (tickDuration <= 0) throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration)
    if (ticksPerWheel <= 0) throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel)
    this.wheel = new util.ArrayList[Slot[E]]
    this.tickDuration = TimeUnit.MILLISECONDS.convert(tickDuration, timeUnit)
    this.ticksPerWheel = ticksPerWheel + 1
    (0 until this.ticksPerWheel).foreach { i =>
      wheel.add(new Slot[E](i))
    }
    wheel.trimToSize()
    workerThread = new Thread(new TickWorker(), "Timing-Wheel")
  }

  def start(): Unit = {
    if (shutdown.get) throw new IllegalStateException("Cannot be started once stopped")
    if (!workerThread.isAlive) workerThread.start()
  }

  def stop: Boolean = {
    if (!shutdown.compareAndSet(false, true)) return false
    var interrupted = false
    while (workerThread.isAlive) {
      workerThread.interrupt()
      try workerThread.join(100) catch {
        case e: InterruptedException =>
          interrupted = true
      }
    }
    if (interrupted) Thread.currentThread.interrupt()
    true
  }

  def addExpirationListener(listener: ExpirationListener[E]): Unit = {
    expirationListeners.add(listener)
  }

  def removeExpirationListener(listener: ExpirationListener[E]): Unit = {
    expirationListeners.remove(listener)
  }

  /**
   * START_TIMER(Interval, Request_ID, Expiry_Action)
   *
   * @param e
   * @return
   */
  def add(e: E): Long = {
    def checkAdd(e: E): Unit = {
      val slot = indicator.get(e)
      if (slot != null) slot.removeTask(e)
    }

    def getPreviousTickIndex: Int = {
      lock.readLock.lock()
      try {
        val cti: Int = currentTickIndex
        if (cti == 0) return ticksPerWheel - 1
        cti - 1
      } finally lock.readLock.unlock()
    }

    synchronized {
      checkAdd(e)
      val previousTickIndex: Int = getPreviousTickIndex
      val slot = wheel.get(previousTickIndex)
      slot.insertTask(e)
      indicator.put(e, slot)
      (ticksPerWheel - 1) * tickDuration
    }
  }

  /**
   * STOP_TIMER(Request_ID)
   *
   * @param e
   * @return
   */
  def remove(e: E): Boolean = {
    synchronized {
      val slot = indicator.get(e)
      if (slot == null) return false
      indicator.remove(e)
      slot.removeTask(e)
    }
  }

  trait ExpirationListener[E] {
    def expired(expiredObject: E): Unit
  }

  class TickWorker extends Runnable {
    var startTime, tick: Long = _

    /**
     * PER_TICK_BOOKKEEPING
     */
    override def run(): Unit = {

      /**
       * EXPIRY_PROCESSING
       * O(n)
       *
       * @param idx
       */
      def notifyExpired(idx: Int): Unit = {
        val slot = wheel.get(idx)
        val elements = slot.elements
        for (e <- elements.asScala) {
          slot.removeTask(e)
          synchronized {
            val latestSlot = indicator.get(e)
            if (latestSlot.equals(slot)) indicator.remove(e)
          }
          for (listener <- expirationListeners.asScala) {
            listener.expired(e)
          }
        }
      }

      startTime = System.currentTimeMillis
      tick = 1
      var i = 0
      while (!shutdown.get()) {
        if (i == wheel.size) i = 0
        lock.writeLock.lock
        try currentTickIndex = i
        finally lock.writeLock.unlock()
        notifyExpired(currentTickIndex)
        waitForNextTick()
        i += 1
      }
    }

    private[this] def waitForNextTick(): Unit = {
      import scala.util.control.Breaks._
      breakable {
        while (true) {
          val currentTime = System.currentTimeMillis()
          val sleepTime = tickDuration * tick - (currentTime - startTime)
          if (sleepTime <= 0) {
            break()
          }
          try {
            Thread.sleep(sleepTime)
          } catch {
            case e: InterruptedException =>
              return
          }
        }
        tick += 1
      }
    }
  }

  case class Slot[E](id: Int = 0) {

    private val eles = new ConcurrentHashMap[E, E]
    private final lazy val expiredTask: util.Vector[E] = new util.Vector[E]

    def removeTask(task: E): Boolean = {
      this.expiredTask.remove(task)
    }

    def insertTask(e: E): Unit = {
      eles.put(e, e)
    }

    def elements: ConcurrentHashMap.KeySetView[E, E] = eles.keySet

  }

}
