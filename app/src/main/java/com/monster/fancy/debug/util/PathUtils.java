package com.monster.fancy.debug.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by fancy on 2017/5/5.
 */

public class PathUtils {

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static File getAvailableCacheDir() {
        if (isExternalStorageWritable()) {
            return MyLeanCloudApp.ctx.getExternalCacheDir();
        } else {
            return MyLeanCloudApp.ctx.getCacheDir();
        }
    }

    public static String checkAndMkdirs(String dir) {
        File file = new File(dir);
        if (file.exists() == false) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getAvatarCropPath() {
        return new File(getAvailableCacheDir(), "avatar_crop.jpg").getAbsolutePath();
    }

    public static String getAvatarTmpPath() {
        return new File(getAvailableCacheDir(), "avatar_tmp.jpg").getAbsolutePath();
    }
}
