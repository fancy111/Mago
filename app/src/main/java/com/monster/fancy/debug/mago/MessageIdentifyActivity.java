package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

public class MessageIdentifyActivity extends AppCompatActivity {

    private EditText identify_authCode_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the phone number from register activity
        Bundle b = getIntent().getExtras();
        String phoneNumber = b.getString("phoneNumber");
        setContentView(R.layout.activity_message_identify);

        identify_authCode_edt = (EditText) findViewById(R.id.identify_authCode_edt);
        identify_authCode_edt.requestFocus();
    }

    public void back(View view) {
        finish();
    }

    public void nextStep(View view) {
//        Intent intent = new Intent(MessageIdentifyActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();

        String verifyCode = identify_authCode_edt.getText().toString();
        //check if the verify code valid
        if (isVerifyCodeValid(verifyCode)) {
            AVUser.verifyMobilePhoneInBackground(verifyCode, new AVMobilePhoneVerifyCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        identify_authCode_edt.setError(getString(R.string.error_wrong_sms_code));
                    } else {
                        AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        });
                    }
                }
            });
        } else {
            identify_authCode_edt.setError(getString(R.string.error_invalid_sms_code_format));
        }
    }

    /**
     * 判断验证码是否为 6 位纯数字，LeanCloud 统一的验证码均为 6  位纯数字。
     *
     * @param verifyCode
     * @return
     */
    public static boolean isVerifyCodeValid(String verifyCode) {
        String regex = "^\\d{6}$";
        return verifyCode.matches(regex);
    }
}
