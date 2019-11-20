package io.growing.schedule;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2019/9/12
 */
public class TimerTest1 {

    private Timer timer;
    public long start;

    public TimerTest1() {
        this.timer = new Timer();
        start = System.currentTimeMillis();
    }

    //定时器1
    public void timerOne() {
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerOne invoked ,the time:" + (System.currentTimeMillis() - start));
                try {
                    Thread.sleep(4000); // 线程休眠4000
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    //定时器2
    public void timerTwo() {
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("timerTwo invoked ,the time:" + (System.currentTimeMillis() - start));
            }
        }, 2000);
    }

    public static void main(String[] args) {
        TimerTest1 test = new TimerTest1();
        //前面的任务执行时间太长，导致后面任务实际经过delay的值大于预期值
        //也就是说这种情况下，任务调度时机不可控
        test.timerOne();
        test.timerTwo();
    }
}
