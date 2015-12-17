package com.wudi.telhelper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wudi on 12/15/2015.
 */
public class Contact implements Serializable {
    public List<String> note;
    public boolean ban,alwaysRecord;
    public String tag;
}
