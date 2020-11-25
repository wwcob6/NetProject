package com.punuo.sys.sdk;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

/**
 * Created by han.chen.
 * Date on 2019/4/4.
 **/
public class PnApplication extends Application {
    private static PnApplication instance;

    public static PnApplication getInstance() {
        if (instance == null) {
            instance = new PnApplication();
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        MMKV.initialize(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (DeviceHelper.isApkInDebug()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        initCrashReport();
    }

    public void initCrashReport() {
        try {
            if (DeviceHelper.isApkInDebug()) {
                return;
            }
            final Context context = getApplicationContext();
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
            strategy.setAppChannel("Android"); //渠道

            CrashReport.initCrashReport(context, "686a76cb6a", DeviceHelper.isApkInDebug(), strategy);
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        }
    }
}
