package com.punuo.sys.sdk.util;

import android.util.DisplayMetrics;

import com.punuo.sys.sdk.PnApplication;

import java.io.InputStream;

/**
 * Created by han.chen.
 * Date on 2019/4/4.
 **/
public class CommonUtil {

    public static int getWidth() {
        DisplayMetrics dm = PnApplication.getInstance().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getHeight() {
        DisplayMetrics dm = PnApplication.getInstance().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = PnApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getAssetsData(String assetsName) {
        try {
            InputStream inputStream = PnApplication.getInstance().getAssets().open(assetsName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
