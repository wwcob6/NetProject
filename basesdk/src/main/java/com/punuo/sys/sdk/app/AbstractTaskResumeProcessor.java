package com.punuo.sys.sdk.app;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public abstract class AbstractTaskResumeProcessor {
    private long mPauseTaskTime = System.currentTimeMillis();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppWakeUpEvent event) {
        //判断轮询任务是否已停止，若未停止，无须启动新任务
        if (isTaskStopped()) {
            long currentTime = System.currentTimeMillis();
            long constTimeInBack = currentTime - mPauseTaskTime;
            if (constTimeInBack >= getInternalTime()) {
                //压入后台事件不小于间隔事件，直接恢复任务
                restartTask();
            } else {
                Observable.empty().delay(getInternalTime() - constTimeInBack, TimeUnit.MILLISECONDS)
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                try {
                                    restartTask();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .subscribe();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppFallAsleepEvent event) {
        mPauseTaskTime = System.currentTimeMillis();
        stopTask();
    }


    /**
     * 重启任务
     */
    protected abstract void restartTask();


    /**
     * 终止任务
     */
    protected abstract void stopTask();


    /**
     * 任务是否停止
     * @return
     */
    protected abstract boolean isTaskStopped();

    /**
     * 轮询时间(单位毫秒)
     * @return
     */
    protected abstract long getInternalTime();

    /**
     * 设置轮询任务名，便于后期追踪定位
     * @return
     */
    protected abstract String getTaskName();
}
