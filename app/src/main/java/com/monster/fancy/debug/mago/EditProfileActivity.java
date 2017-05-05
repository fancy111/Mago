package com.monster.fancy.debug.mago;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.monster.fancy.debug.util.PathUtils;
import com.monster.fancy.debug.util.Utils;
import com.monster.fancy.debug.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class EditProfileActivity extends AppCompatActivity {
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    // 裁剪后图片的宽(X)和高(Y),130 X 130。
    private static int output_X = 200;
    private static int output_Y = 200;

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    private EditText username_edt;
    private EditText realname_edt;
    private EditText phone_edt;
    private EditText sig_edt;
    private TextView gender_text;
    private ImageView avatar_imgView;

    AVUser avUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        avUser = AVUser.getCurrentUser();

        username_edt = (EditText) findViewById(R.id.username_edt);
        realname_edt = (EditText) findViewById(R.id.realname_edt);
        phone_edt = (EditText) findViewById(R.id.phone_edt);
        gender_text = (TextView) findViewById(R.id.gender_edt);
        sig_edt = (EditText) findViewById(R.id.signature_edt);
        avatar_imgView = (ImageView) findViewById(R.id.avatar_imgView);

        //set the values
        username_edt.setText(avUser.getUsername());
        phone_edt.setText(avUser.getMobilePhoneNumber());
        if (!TextUtils.isEmpty(avUser.getString("gender")))
            gender_text.setText(avUser.getString("gender"));
        if (!TextUtils.isEmpty(avUser.getString("realname")))
            realname_edt.setText(avUser.getString("realname"));
        if (!TextUtils.isEmpty(avUser.getString("signature")))
            sig_edt.setText(avUser.getString("signature"));
        if (avUser.getAVFile("avatar") != null) {
            AVFile avatarFile = avUser.getAVFile("avatar");
            Picasso.with(EditProfileActivity.this).load(avatarFile.getUrl()).into(avatar_imgView);
        }
    }

    //the on click event for the finish button
    public void onFinishProfile(View view) {

        if (!TextUtils.isEmpty(realname_edt.getText().toString()))
            avUser.put("realname", realname_edt.getText().toString());
        if (!TextUtils.isEmpty(username_edt.getText().toString()))
            avUser.setUsername(username_edt.getText().toString());
        if (!TextUtils.isEmpty(sig_edt.getText().toString()))
            avUser.put("signature", sig_edt.getText().toString());
        if (!TextUtils.isEmpty(gender_text.getText().toString()))
            avUser.put("gender", gender_text.getText().toString());
        if (!TextUtils.isEmpty(phone_edt.getText().toString()))
            avUser.setMobilePhoneNumber(phone_edt.getText().toString());
        avUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                //若修改成功，则界面跳回
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //否则显示修改失败
                else {
                    Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //the on click event for the cancel button
    public void onCancelProfile(View view) {
        finish();
    }

    //the on click event for the gender picker button
    public void onOptionPicker(View view) {
        OptionPicker picker = new OptionPicker(this, new String[]{"男", "女", "保密"});
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setTextSize(11);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                gender_text.setText(item);
                Toast.makeText(getApplicationContext(), "index=" + index + ", item=" + item, Toast.LENGTH_SHORT).show();
            }
        });
        picker.show();
    }

    //the on click method for avatar
    public void onSetAvatar(View view) {
        //pop up a dialog to choose the method for setting avatar
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(new String[]{"从本地相册选择", "拍照", "取消"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            //choose from Gallery
                            case 0:
                                choseAvatarFromGallery();
                                break;
                            //take the photo
                            case 1:
                                choseAvatarFromCameraCapture();
                                break;
                            //cancel
                            case 2:
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    /**
     * chose a picture from gallery
     */
    private void choseAvatarFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CODE_GALLERY_REQUEST);
    }

    /**
     * take a picture
     */
    private void choseAvatarFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    /**
     * 界面跳转回来的时候进行的操作
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(data.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;

            case CODE_RESULT_REQUEST:
                if (data != null) {
                    final String path = setImageToHeadView(data);
                    AVUser user = AVUser.getCurrentUser();
                    try {
                        AVFile avatarFile = AVFile.withAbsoluteLocalPath("avatar_crop.jpg", path);
                        //用户保存头像至后台
                        user.put("avatar", avatarFile);
                        user.saveInBackground();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private String setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            if (photo != null) {
                //设置界面中头像
                avatar_imgView.setImageBitmap(photo);
                //获得头像的路径
                path = PathUtils.getAvatarCropPath();
                Utils.saveBitmap(path, photo);
//                if (photo != null && photo.isRecycled() == false) {
//                    //photo.recycle();
//                }
            }
        }
        return path;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

}
