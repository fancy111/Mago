package com.monster.fancy.debug.mago;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.monster.fancy.debug.dao.Friend;

import java.util.Arrays;
import java.util.List;


public class FriendInfoActivity extends AppCompatActivity {

    private Dialog dlgConfirm;
    private Dialog dlgDeleteFriend;
    private TextView username_textview;
    private TextView realname_textview;
    private TextView phone_textview;
    private TextView remark_textview;
    private TextView signature_textview;
    private Switch setStar_btn;
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        username_textview = (TextView) findViewById(R.id.friend_username);
        realname_textview = (TextView) findViewById(R.id.friend_realname);
        phone_textview = (TextView) findViewById(R.id.friend_phone);
        remark_textview = (TextView) findViewById(R.id.friend_remark);
        signature_textview = (TextView) findViewById(R.id.friend_description);
        setStar_btn = (Switch) findViewById(R.id.setStar_btn);

        //accept the parameter from address_list page
        Intent intent = this.getIntent();
        friend = (Friend) intent.getSerializableExtra("friend");
        //set the friend information
        setFriendInfo(friend);

        //initialize the dialog
        createDialog();
        createDeleteDialog();

        //set the listener for switch button
        setStar_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if user click the button to true,then set this friend to star
                if(isChecked) {
                    friend.setStarFriend(true);
                    Toast.makeText(getApplicationContext(), friend.isStarFriend() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //create dialog
    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("确认对话框");
        builder.setMessage("发起位置共享？");
        builder.setIcon(R.drawable.help_icon);

        builder.setPositiveButton("是", new SharePosOnClickListenerImpl());
        builder.setNegativeButton("否", null);

        this.dlgConfirm = builder.create();
    }

    //create dialog
    private void createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("删除对话框");
        builder.setMessage("确认删除好友？");
        builder.setIcon(R.drawable.alert_icon);

        builder.setPositiveButton("是", new SharePosOnClickListenerImpl());
        builder.setNegativeButton("否", null);

        this.dlgDeleteFriend = builder.create();
    }

    //set the basic information of the friend in the layout
    private void setFriendInfo(Friend friend) {
        username_textview.setText(friend.getUsername());
        phone_textview.setText(friend.getPhone());

        if (!TextUtils.isEmpty(friend.getRealName()))
            realname_textview.setText(friend.getRealName());

        if (!TextUtils.isEmpty(friend.getRemark()))
            remark_textview.setText(friend.getRemark());

        if (!TextUtils.isEmpty(friend.getSignature()))
            signature_textview.setText(friend.getSignature());
    }

    //the onclick method for back button
    public void back(View view) {
        finish();
    }

    //the onclick method for find friend button
    public void findFriend(View view) {
        this.dlgConfirm.show();
    }

    //the onclick method for delete button
    public void deleteFriend(View view) {
        this.dlgDeleteFriend.show();
    }

    //the listener of the dialog
    private class  SharePosOnClickListenerImpl implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //if is the find friend dialog,call the friend
            if (dialog == dlgConfirm && which == Dialog.BUTTON_POSITIVE)
                Toast.makeText(getApplicationContext(), "find", Toast.LENGTH_SHORT).show();

            //if is the delete friend button,unfollow the friend
            if (dialog == dlgDeleteFriend && which == Dialog.BUTTON_POSITIVE) {
                //取消关注
                AVUser.getCurrentUser().unfollowInBackground(friend.getID(), new FollowCallback() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        //unfollow failed
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "delete friend failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //对方也应取消关注
                AVQuery<AVObject> followerQuery = new AVQuery<>("_Follower");
                followerQuery.whereEqualTo("follower", AVObject.createWithoutData("_User", friend.getID()));
                AVQuery<AVObject> userQuery = new AVQuery<>("_Follower");
                userQuery.whereEqualTo("user", AVUser.getCurrentUser());

                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(followerQuery, userQuery));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null && list.size() == 1) {
                            list.get(0).deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e != null) {
                                        Toast.makeText(getApplicationContext(), "delete friend failed: selete _follower failed", Toast.LENGTH_SHORT).show();
                                        Log.e("delete", "delete _follower failed");
                                    } else {
                                        Log.d("delete", "delete _follower success");
                                        //Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                                        //finish();
                                    }
                                }
                            });
                        }
                    }
                });

                AVQuery<AVObject> userFQuery = new AVQuery<>("_Followee");
                userFQuery.whereEqualTo("user", AVObject.createWithoutData("_User", friend.getID()));
                AVQuery<AVObject> followeeFQuery = new AVQuery<>("_Followee");
                followeeFQuery.whereEqualTo("followee", AVUser.getCurrentUser());

                AVQuery<AVObject> queryF = AVQuery.and(Arrays.asList(followeeFQuery, userFQuery));
                queryF.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null && list.size() == 1) {
                            list.get(0).deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Log.d("delete", "delete _followee success");
                                        Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent intent = new Intent(FriendInfoActivity.this, AdressListActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "delete friend failed: delete _followee failed", Toast.LENGTH_SHORT).show();
                                        Log.e("delete", "delete _followee failed");
                                    }

                                }
                            });
                        }
                    }
                });
            }


        }
    }
}
