package com.punuo.sys.sdk.util;

import com.luck.picture.lib.config.PictureMimeType;

/**
 * Created by han.chen.
 * Date on 2019-07-23.
 **/
public class BitmapUtil {

    public static boolean isJPEG(String path) {
        String type = PictureMimeType.getLastImgType(path);
        return type.equals(".jpg") || type.equals(".JPEG") || type.equals(".jpeg");
    }
}
