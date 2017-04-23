package com.monster.fancy.debug.util;

import com.monster.fancy.debug.dao.User;

import java.util.Comparator;

/**
 * Created by fancy on 2017/4/23.
 */
public class PinyinComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
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
