package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.monster.fancy.debug.dao.User;

public class FriendInfoActivity extends AppCompatActivity {

    TextView nickname_textview;
    TextView realname_textview;
    TextView phone_textview;
    TextView remark_textview;
    TextView signature_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        nickname_textview = (TextView) findViewById(R.id.friend_nickname);
        realname_textview = (TextView) findViewById(R.id.friend_realname);
        phone_textview = (TextView) findViewById(R.id.friend_phone);
        remark_textview = (TextView) findViewById(R.id.friend_remark);
        signature_textview = (TextView) findViewById(R.id.friend_description);

        //accept the parameter from address_list page
        Intent intent = this.getIntent();
        User friend = (User) intent.getSerializableExtra("friend");
        //set the friend information
        setFriendInfo(friend);
    }

    private void setFriendInfo(User friend) {
        if (friend.getUserNickName() != null)
            nickname_textview.setText(friend.getUserNickName());

        if (friend.getUserRealName() != null)
            realname_textview.setText(friend.getUserRealName());

        if (friend.getUserPhone() != null)
            phone_textview.setText(friend.getUserPhone());

        if (friend.getUserRemark() != null)
            remark_textview.setText(friend.getUserRemark());

        if (friend.getUserSignature() != null)
            signature_textview.setText(friend.getUserSignature());

    }

    //the onclick method for back button
    public void back(View view) {
        finish();
    }

    //the onclick method for find friend button
    public void findFriend(View view) {
    }

    //the onclick method for delete button
    public void deleteFriend(View view) {
    }
}
