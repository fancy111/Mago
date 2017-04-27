package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_phone_edt;
    private EditText reg_password_edt;
    private EditText passwordAgain_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_phone_edt = (EditText) findViewById(R.id.reg_phone_edt);
        reg_password_edt = (EditText) findViewById(R.id.reg_password_edt);
        passwordAgain_edt = (EditText) findViewById(R.id.passwordAgain_edt);
    }

    public void back(View view) {
        finish();
    }

    public void nextStep(View view) {
//        Intent intent = new Intent(RegisterActivity.this, MessageIdentifyActivity.class);
//        startActivity(intent);

        attempRegister();
    }

    private void attempRegister() {
        final String phoneNumber = reg_phone_edt.getText().toString();
        String password = reg_password_edt.getText().toString();
        String passwordConfirm = passwordAgain_edt.getText().toString();

        boolean cancel = false;
        //the edit text should be focused
        View focusView = null;

        //init the user info
        AVUser nuser = new AVUser();

        // Check for the comfirm password, if the user entered one.
        if (!TextUtils.isEmpty(passwordConfirm) && !passwordConfirm.equals(password)) {
            passwordAgain_edt.setError(getString(R.string.error_notequal_password));
            focusView = passwordAgain_edt;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            reg_password_edt.setError(getString(R.string.error_invalid_password));
            focusView = reg_password_edt;
            cancel = true;
        }
        // Check for a valid phone number, if the user entered one.
        if (!isMobileNumberValid(phoneNumber)) {
            reg_phone_edt.setError(getString(R.string.error_invalid_mobile_phone));
            focusView = reg_phone_edt;
            cancel = true;
        }

        //if invalid,focus to the wrong edit text
        if (cancel) {
            focusView.requestFocus();
        } else {
            nuser.setUsername("fancy");
            nuser.setMobilePhoneNumber(phoneNumber);
            nuser.setPassword(password);
            //sign up in the backup
            nuser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Intent intent = new Intent(getApplicationContext(), MessageIdentifyActivity.class);
                        Bundle b = new Bundle();
                        b.putString("phoneNumber", phoneNumber);
                        intent.putExtras(b);
                        startActivity(intent);
                    } else {
                        Log.e("SignUp", e.getMessage());
                    }
                }
            });
        }

    }


    /**
     * 验证手机号是否符合大陆的标准格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNumberValid(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 推荐密码至少长度为 6 位
     *
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }


}
