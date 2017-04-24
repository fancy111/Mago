package com.monster.fancy.debug.mago;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.monster.fancy.debug.dao.User;

public class FriendInfoActivity extends AppCompatActivity {

    private Dialog dlgConfirm;
    private TextView nickname_textview;
    private TextView realname_textview;
    private TextView phone_textview;
    private TextView remark_textview;
    private TextView signature_textview;
    private Switch setStar_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        nickname_textview = (TextView) findViewById(R.id.friend_nickname);
        realname_textview = (TextView) findViewById(R.id.friend_realname);
        phone_textview = (TextView) findViewById(R.id.friend_phone);
        remark_textview = (TextView) findViewById(R.id.friend_remark);
        signature_textview = (TextView) findViewById(R.id.friend_description);
        setStar_btn = (Switch) findViewById(R.id.setStar_btn);

        //accept the parameter from address_list page
        Intent intent = this.getIntent();
        final User friend = (User) intent.getSerializableExtra("friend");
        //set the friend information
        setFriendInfo(friend);

        //initialize the dialog
        createDialog();

        //set the listener for switch button
        setStar_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if user click the button to true,then set this friend to star
                if(isChecked) {
                    friend.setUserStar(true);
                    Toast.makeText(getApplicationContext(),friend.isUserStar()+"",Toast.LENGTH_SHORT).show();
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

    //set the basic information of the friend in the layout
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
        this.dlgConfirm.show();
    }

    //the onclick method for delete button
    public void deleteFriend(View view) {
    }

    //the listener of the dialog
    private class  SharePosOnClickListenerImpl implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == Dialog.BUTTON_POSITIVE) {
                //Intent intent = new Intent()
                Toast.makeText(getApplicationContext(),"confirm",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
