package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;


public class LoginActivity extends AppCompatActivity {

    private EditText login_phone;
    private EditText login_password;
    private String phone, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_phone = (EditText) findViewById(R.id.login_phone_edt);
        login_password = (EditText) findViewById(R.id.login_password_edt);
    }

    public void login(View view) {
        phone = login_phone.getText().toString();
        password = login_password.getText().toString();
        Toast.makeText(LoginActivity.this, phone + " and " + password,
                Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    public void forgetPassword(View view) {
        //Intent intent = new Intent(LoginActivity.this,.class);
        //startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
