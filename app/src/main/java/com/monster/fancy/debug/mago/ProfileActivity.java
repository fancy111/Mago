package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView username_text;
    private TextView realname_text;
    private TextView gender_text;
    private TextView phone_text;
    private TextView signature_text;
    private ImageView avatar_img;

    private AVUser avUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        avUser = AVUser.getCurrentUser();

        username_text = (TextView) findViewById(R.id.username_text);
        realname_text = (TextView) findViewById(R.id.realname_text);
        gender_text = (TextView) findViewById(R.id.gender_text);
        phone_text = (TextView) findViewById(R.id.phone_text);
        signature_text = (TextView) findViewById(R.id.signature_text);
        avatar_img = (ImageView) findViewById(R.id.avatar_img);

        //set the values
        this.setTextViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //set the values
        this.setTextViews();
    }

    //set the contents of the user information
    private void setTextViews() {
        //set the values
        username_text.setText(avUser.getUsername());
        phone_text.setText(avUser.getMobilePhoneNumber());
        if (!TextUtils.isEmpty(avUser.getString("gender")))
            gender_text.setText(avUser.getString("gender"));
        if (!TextUtils.isEmpty(avUser.getString("realname")))
            realname_text.setText(avUser.getString("realname"));
        if (!TextUtils.isEmpty(avUser.getString("signature")))
            signature_text.setText(avUser.getString("signature"));
        if (avUser.getAVFile("avatar") != null) {
            AVFile avatarFile = avUser.getAVFile("avatar");
            Picasso.with(ProfileActivity.this).load(avatarFile.getUrl()).into(avatar_img);
        }
    }

    public void editProfile(View view) {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void back(View view) {
        finish();
    }
}
