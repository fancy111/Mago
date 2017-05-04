package com.monster.fancy.debug.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.monster.fancy.debug.dao.Friend;
import com.monster.fancy.debug.mago.R;

import java.util.List;

/**
 * Created by fancy on 2017/4/22.
 */
public class UserFriendListAdapter extends BaseAdapter implements SectionIndexer {

    private LayoutInflater inflater;
    private Activity myActivity;
    private List<Friend> friendList;

    public UserFriendListAdapter(Activity myActivity, List<Friend> friendList) {
        this.myActivity = myActivity;
        this.friendList = friendList;
    }


    //using when listView content changed
    public void updateListView(List<Friend> friendList) {
        this.friendList = friendList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.userinfo_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ivHead = (ImageView) convertView.findViewById(R.id.head);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.tvLine = (TextView) convertView.findViewById(R.id.line);
            viewHolder.tvContent = (LinearLayout) convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Friend friend = friendList.get(position);

        if (friend != null) {
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText("☆".equals(friend.getSortLetter()) ? friend.getSortLetter() + "(星标朋友)" : friend.getSortLetter());
                viewHolder.tvLine.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
                viewHolder.tvLine.setVisibility(View.GONE);
            }
            viewHolder.tvTitle.setText(friend.getUsername());
        }
        return convertView;
    }

    //the class contains all the widgets in a single item
    class ViewHolder {
        ImageView ivHead;
        TextView tvLetter;
        TextView tvTitle;
        TextView tvLine;
        LinearLayout tvContent;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = friendList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return friendList.get(position).getSortLetter().charAt(0);
    }
}
