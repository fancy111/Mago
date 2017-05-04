package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText username_edt;
    private EditText realname_edt;
    private EditText phone_edt;
    private EditText sig_edt;
    private TextView gender_text;

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

        //set the values
        username_edt.setText(avUser.getUsername());
        phone_edt.setText(avUser.getMobilePhoneNumber());
        if (!TextUtils.isEmpty(avUser.getString("gender")))
            gender_text.setText(avUser.getString("gender"));
        if (!TextUtils.isEmpty(avUser.getString("realname")))
            realname_edt.setText(avUser.getString("realname"));
        if (!TextUtils.isEmpty(avUser.getString("signature")))
            sig_edt.setText(avUser.getString("signature"));
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
        //显示对话框，从本地相册选择或者拍照
    }
}
