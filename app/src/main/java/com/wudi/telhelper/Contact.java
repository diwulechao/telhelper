package com.wudi.telhelper;

import android.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wudi on 12/15/2015.
 */
public class Contact implements Serializable {
    public List<String> note;
    public boolean ban, alwaysRecord;
    public String tag;

    public static Pair<String, Long> getNote(List<String> note, int position) {
        String s = note.get(position);
        if (s != null && s.indexOf("@#") == 0) {
            int pos = s.indexOf("@#", 5);
            long time = Long.valueOf(s.substring(2, pos));
            s = s.substring(pos + 2);
            return new Pair<>(s, time);
        }

        return new Pair<>(s, 0L);
    }
}
