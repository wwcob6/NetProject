package com.punuo.sys.sdk.app;

import android.app.Activity;
import android.os.Message;

import com.punuo.sys.sdk.util.BaseHandler;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;


/**
 * APP状态管理器，负责管理App切换到后台/前台状态
 **/
public class AppStateManager implements BaseHandler.MessageHandler {

    private static final String TAG = AppStateManager.class.getSimpleName();
    private static final int TYPE_NOTIFY_SUBSCRIBER = 1;
    /**
     * 延迟校验是否进入后台时间，单位毫秒（默认80s，用于满足app调起第三方app执行业务动作）
     */
    private static final long DELAY_CHECK_BACKGROUND_STATE_TIME = 80 * 1000;

    private WeakReference<Activity> mFrontActivity;
    private AppState mAppState = AppState.FALL_ASLEEP;

    private BaseHandler mHandler;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case TYPE_NOTIFY_SUBSCRIBER:
                notifySubscriber();
                break;
        }
    }

    private AppStateManager() {
        mHandler = new BaseHandler(this);
    }

    public void onActivityVisible(Activity activity) {

        //update frontActivity
        if (mFrontActivity != null) {
            mFrontActivity.clear();
        }
        mFrontActivity = new WeakReference<>(activity);


        if (isAppStateChange()) {
            sendNotifyEvent();
        }
    }


    public void onActivityInvisible(Activity activity) {

        if (mFrontActivity != null) {
            //若前台activity和消失的activity是同一个对象，设置前台activity置null
            Activity frontActivity = mFrontActivity.get();
            if (frontActivity == activity) {
                mFrontActivity.clear();
            }
        }

        if (isAppStateChange()) {
            sendNotifyEvent();
        }
    }


    private boolean isAppStateChange() {

        AppState oldAppState = mAppState;

        AppState newAppState = (mFrontActivity != null && mFrontActivity.get() != null) ? AppState.WEAK_UP :AppState.FALL_ASLEEP;

        mAppState = newAppState;

        return oldAppState != newAppState;
    }

    private void sendNotifyEvent() {

        if (mHandler.hasMessages(TYPE_NOTIFY_SUBSCRIBER)) {
            mHandler.removeMessages(TYPE_NOTIFY_SUBSCRIBER);
        } else if (mAppState == AppState.WEAK_UP) {
            mHandler.sendEmptyMessage(TYPE_NOTIFY_SUBSCRIBER);
        } else if (mAppState == AppState.FALL_ASLEEP) {
            mHandler.sendEmptyMessageDelayed(TYPE_NOTIFY_SUBSCRIBER, DELAY_CHECK_BACKGROUND_STATE_TIME);
        }
    }

    private void notifySubscriber() {

        if (mAppState == AppState.WEAK_UP) {
            EventBus.getDefault().post(new AppWakeUpEvent());
        } else if (mAppState == AppState.FALL_ASLEEP) {
            EventBus.getDefault().post(new AppFallAsleepEvent());

        }
    }

    public boolean isAppInBackground() {
        return mAppState == AppState.FALL_ASLEEP;
    }


    private static class AppStateHolder{
        private static final AppStateManager INSTANCE = new AppStateManager();
    }

    public static AppStateManager getInstance() {
        return AppStateHolder.INSTANCE;
    }
}
