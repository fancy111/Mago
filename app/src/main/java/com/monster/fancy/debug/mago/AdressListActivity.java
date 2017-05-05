package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.monster.fancy.debug.adapter.UserFriendListAdapter;
import com.monster.fancy.debug.dao.Friend;
import com.monster.fancy.debug.util.CharacterParser;
import com.monster.fancy.debug.util.PinyinComparator;
import com.monster.fancy.debug.view.SideBar;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdressListActivity extends AppCompatActivity implements SideBar.OnTouchingLetterChangedListener, TextWatcher {

    private SideBar friend_sideBar;

    private ListView friend_lst;

    private EditText searchFriend_edt;

    private CharacterParser characterParser;// 汉字转拼音

    private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类

    private List<Friend> sortDataList = new ArrayList<Friend>();

    private UserFriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_list);

        friend_sideBar = (SideBar) findViewById(R.id.friend_sideBar);
        friend_lst = (ListView) findViewById(R.id.friend_lst);

        //set the listener for the listView
        friend_lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = sortDataList.get(position);
                Intent intent = new Intent(AdressListActivity.this, FriendInfoActivity.class);
                intent.putExtra("friend", friend);
                startActivity(intent);
            }
        });

        searchFriend_edt = (EditText) findViewById(R.id.searchFriend_edt);
        friend_sideBar.setOnTouchingLetterChangedListener(this);

        //set the friendList
        initData();
    }

    //initialize the friend list
    private void initData() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        fillData();
        //set the data list
        friendListAdapter = new UserFriendListAdapter(AdressListActivity.this, sortDataList);
        //set the adapter for the list view
        friend_lst.setAdapter(friendListAdapter);
        //set the listener for the search edit text
        searchFriend_edt.addTextChangedListener(AdressListActivity.this);
    }


    //fill the data in the list
    private void fillData() {
        sortDataList.clear();

        AVUser user = AVUser.getCurrentUser();

        try {
            AVQuery<AVUser> followeeQuery = user.followeeQuery(AVUser.class);
            followeeQuery.include("followee");
            followeeQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> list, AVException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            Friend friend = new Friend();
                            AVUser friendUser = list.get(i);
                            //set the basic information of the friend
                            friend.setID(friendUser.getObjectId());
                            friend.setUsername(friendUser.getUsername());
                            friend.setPhone(friendUser.getMobilePhoneNumber());
                            if (!TextUtils.isEmpty(friendUser.getString("gender")))
                                friend.setGender(friendUser.getString("gender"));
                            if (!TextUtils.isEmpty(friendUser.getString("realname")))
                                friend.setRealName(friendUser.getString("realname"));
                            if (!TextUtils.isEmpty(friendUser.getString("signature")))
                                friend.setSignature(friendUser.getString("signature"));
                            if (friendUser.getAVFile("avatar") != null) {
                                friend.setPhotoUrl(friendUser.getAVFile("avatar").getUrl());
                            }
                            //get the pinyin of the username
                            String pinyin = characterParser.getSelling(friend.getUsername());
                            //set the sort letter of the friend
                            if (friend.isStarFriend()) {// if is a star friend
                                friend.setSortLetter("☆");
                            } else
                                friend.setSortLetter(pinyin.substring(0, 1).toUpperCase());
                            //add the friend to the list
                            sortDataList.add(friend);
                            // sort the list in a-z order
                            Collections.sort(sortDataList, pinyinComparator);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "get friend list error!\n" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }

                }
            });
        } catch (AVException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTouchingLetterChanged(String s) {

        int position = 0;
        if (friendListAdapter != null) {
            position = friendListAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            friend_lst.setSelection(position);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString(), sortDataList);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filterData(String filterStr, List<Friend> list) {
        List<Friend> filterDateList = new ArrayList<Friend>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            filterDateList.clear();
            for (Friend sortModel : list) {
                String name = sortModel.getUsername();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        friendListAdapter.updateListView(filterDateList);
    }

    public void back(View view) {
        finish();
    }

    public void onAddFriend(View view) {
        Intent intent = new Intent(AdressListActivity.this,AddFriendActivity.class);
        startActivity(intent);
    }
}
