package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FollowCallback;
import com.monster.fancy.debug.dao.Friend;

public class AddFriendInfoActivity extends AppCompatActivity {

    private TextView username_textview;
    private TextView realname_textview;
    private TextView phone_textview;
    private TextView signature_textview;
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_info);

        username_textview = (TextView) findViewById(R.id.add_friend_username);
        realname_textview = (TextView) findViewById(R.id.add_friend_realname);
        phone_textview = (TextView) findViewById(R.id.add_friend_phone);
        signature_textview = (TextView) findViewById(R.id.add_friend_description);

        //accept the parameter from address_list page
        Intent intent = this.getIntent();
        friend = (Friend) intent.getSerializableExtra("friend");
        //set the friend information
        if (friend != null)
            setFriendInfo(friend);
    }

    //set the basic information of the friend in the layout
    private void setFriendInfo(Friend friend) {
        username_textview.setText(friend.getUsername());
        phone_textview.setText(friend.getPhone());

        if (!TextUtils.isEmpty(friend.getRealName()))
            realname_textview.setText(friend.getRealName());

        if (!TextUtils.isEmpty(friend.getSignature()))
            signature_textview.setText(friend.getSignature());
    }

    //the on click method for add friend button
    public void onAddFriend(View view) {
        AVUser user = AVUser.getCurrentUser();
        //add the friend
        user.followInBackground(friend.getID(), new FollowCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                AVUser friend = (AVUser) avObject;
                //follow success
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "follow success!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(AddFriendInfoActivity.this, AdressListActivity.class);
                    startActivity(intent);
                }
                //has followed already
                else if (e.getCode() == AVException.DUPLICATE_VALUE) {
                    Toast.makeText(getApplicationContext(), "has followed the friend already!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "follow failed!\n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //the on click method for back button
    public void back(View view) {
        finish();
    }
}
