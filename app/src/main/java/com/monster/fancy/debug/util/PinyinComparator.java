package com.monster.fancy.debug.util;

import com.monster.fancy.debug.dao.Friend;

import java.util.Comparator;

/**
 * Created by fancy on 2017/4/23.
 */
public class PinyinComparator implements Comparator<Friend> {

    @Override
    public int compare(Friend o1, Friend o2) {
        if (o1.getSortLetter().equals("☆")) {
            return -1;
        } else if (o2.getSortLetter().equals("☆")) {
            return 1;
        } else if (o1.getSortLetter().equals("#")) {
            return -1;
        } else if (o2.getSortLetter().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }
}
