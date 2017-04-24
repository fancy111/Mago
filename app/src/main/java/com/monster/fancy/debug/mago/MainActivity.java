package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
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
                intent = new Intent(MainActivity.this, AdressListActivity.class);
                startActivity(intent);
                break;
            case R.id.message_text:
                intent = new Intent(MainActivity.this, AdressListActivity.class);
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
