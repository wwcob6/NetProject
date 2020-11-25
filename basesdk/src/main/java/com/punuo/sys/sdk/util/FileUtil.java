package com.punuo.sys.sdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    public static String SDPATH = Environment.getExternalStorageDirectory() + "/punuo/moment/";
    public static String TEMP_PATH = Environment.getExternalStorageDirectory() + "/punuo/temp/";

    public static Bitmap compressBitmap(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(
                            new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String saveBitmap(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return "";
        }
        File dirFile = new File(TEMP_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String tempPath = TEMP_PATH + fileName + ".jpg";
        try {
            File file = new File(tempPath);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return tempPath;
    }

    public static void deleteTempDir() {
        deleteDir(TEMP_PATH);
    }

    public static void deleteDir(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
            } else if (file.isDirectory()) {
                deleteDir(); // 递规的方式删除文件夹
            }
        }
        dir.delete();// 删除目录本身
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}
