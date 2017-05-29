package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.monster.fancy.debug.dao.Friend;

import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private EditText addFriend_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        addFriend_edt = (EditText) findViewById(R.id.addFriend_edt);
    }

    public void onSearchFriend(View view) {
        //get the phone number
        String phoneNum = addFriend_edt.getText().toString();

        //construct the sql
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        //find the user through phone number
        userQuery.whereEqualTo("mobilePhoneNumber", phoneNum);
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null) {
                    //if the user is found
                    if (list.size() == 1) {
                        AVUser avFriend = list.get(0);
                        Log.d("find friend", "find friend\n username:" + avFriend.getUsername() + "\n phone:" + avFriend.getMobilePhoneNumber());
                        //set the friend
                        Friend friend = new Friend();
                        friend.setID(avFriend.getObjectId());
                        friend.setUsername(avFriend.getUsername());
                        friend.setPhone(avFriend.getMobilePhoneNumber());
                        if (!TextUtils.isEmpty(avFriend.getString("realname")))
                            friend.setRealName(avFriend.getString("realname"));
                        if (!TextUtils.isEmpty(avFriend.getString("signature")))
                            friend.setSignature(avFriend.getString("signature"));
                        //page jump
                        finish();
                        Intent intent = new Intent(AddFriendActivity.this, AddFriendInfoActivity.class);
                        intent.putExtra("friend", friend);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "the friend is not exist!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "find friend error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void back(View view) {
        Intent intent = new Intent(AddFriendActivity.this, AdressListActivity.class);
        finish();
        startActivity(intent);
    }
}
