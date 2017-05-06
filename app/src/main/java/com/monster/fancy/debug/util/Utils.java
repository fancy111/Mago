package com.monster.fancy.debug.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;

import com.monster.fancy.debug.mago.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fancy on 2017/5/5.
 */

public class Utils {


    public static ProgressDialog showSpinnerDialog(Activity activity) {
        //activity = modifyDialogContext(activity);
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage(MyLeanCloudApp.ctx.getString(R.string.chat_utils_hardLoading));
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }
}
