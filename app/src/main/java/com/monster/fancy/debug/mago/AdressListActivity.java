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

import com.monster.fancy.debug.adapter.UserFriendListAdapter;
import com.monster.fancy.debug.dao.User;
import com.monster.fancy.debug.util.CharacterParser;
import com.monster.fancy.debug.util.PinyinComparator;
import com.monster.fancy.debug.view.SideBar;

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

    private List<User> sortDataList = new ArrayList<User>();

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
                User friend = sortDataList.get(position);
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

        User friend1 = new User();
        friend1.setUserNickName("大屁");
        friend1.setSortLetter("d");

        User friend2 = new User();
        friend2.setUserNickName("猴子");
        friend2.setSortLetter("h");

        User friend3 = new User();
        friend3.setUserNickName("鱼鱼");
        friend3.setSortLetter("y");

        User friend4 = new User();
        friend4.setUserNickName("东东");
        friend4.setSortLetter("d");

        User friend5 = new User();
        friend5.setUserNickName("孙子");
        friend5.setSortLetter("s");

        User friend6 = new User();
        friend6.setUserNickName("阿皮");
        friend6.setSortLetter("a");

        User friend7 = new User();
        friend7.setUserNickName("小鱼儿");
        friend7.setSortLetter("x");

        User friend8 = new User();
        friend8.setUserNickName("笔笔");
        friend8.setSortLetter("b");

        User friend9 = new User();
        friend9.setUserNickName("丢丢");
        friend9.setSortLetter("d");

        User friend10 = new User();
        friend10.setUserNickName("西瓜");
        friend10.setSortLetter("x");

        sortDataList.add(friend1);
        sortDataList.add(friend2);
        sortDataList.add(friend3);
        sortDataList.add(friend4);
        sortDataList.add(friend5);
        sortDataList.add(friend6);
        sortDataList.add(friend7);
        sortDataList.add(friend8);
        sortDataList.add(friend9);
        sortDataList.add(friend10);

        fillData(sortDataList);
        // sort the list in a-z order
        Collections.sort(sortDataList, pinyinComparator);
        friendListAdapter = new UserFriendListAdapter(this, sortDataList);
        friend_lst.setAdapter(friendListAdapter);
        searchFriend_edt.addTextChangedListener(this);
    }

    //fill the data in the list
    private void fillData(List<User> list) {
        for (User friendInfo : list) {
            if (friendInfo != null && friendInfo.getUserNickName() != null) {
                String pinyin = characterParser.getSelling(friendInfo.getUserNickName());

                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (friendInfo.isUserStar()) {// if is a star friend
                    friendInfo.setSortLetter("☆");
                } else if (sortString.matches("[A-Z]")) {// if the first character is the english
                    friendInfo.setSortLetter(sortString);
                } else {
                    friendInfo.setSortLetter("#");
                }
            }
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

    private void filterData(String filterStr, List<User> list) {
        List<User> filterDateList = new ArrayList<User>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            filterDateList.clear();
            for (User sortModel : list) {
                String name = sortModel.getUserNickName();
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
