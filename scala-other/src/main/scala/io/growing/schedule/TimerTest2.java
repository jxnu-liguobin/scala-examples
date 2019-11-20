package io.growing.schedule;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2019/9/12
 */
public class TimerTest2 {

    private Timer timer;

    public TimerTest2() {
        this.timer = new Timer();
    }

    //定时器1
    public void timerOne() {
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("先入队的task中抛出的异常");
                throw new RuntimeException();
            }
        }, 1000);
    }

    //定时器2
    public void timerTwo() {
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("后面的task会不会被执行？");
            }
        }, 1000);
    }

    public static void main(String[] args) {
        TimerTest2 test = new TimerTest2();
        //前面的任务抛出非中断异常，后面任务将认为timer被cancel，不会执行后续任务
        test.timerOne();
        test.timerTwo();
    }
}
