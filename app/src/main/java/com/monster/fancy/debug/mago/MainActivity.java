package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;
    private TextView username_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        username_text = (TextView) findViewById(R.id.username_text);
        username_text.setText(AVUser.getCurrentUser().getUsername());
    }

    public void pageJump(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.myfriend_text:
                intent = new Intent(MainActivity.this, AdressListActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_text:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.help_text:
                intent = new Intent(MainActivity.this, SystemHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.callrecord_text:
                intent = new Intent(MainActivity.this, CallRecordsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_text:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


}
