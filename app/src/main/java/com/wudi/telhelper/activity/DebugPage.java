package com.wudi.telhelper.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.wudi.telhelper.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugpage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = (TextView) this.findViewById(R.id.debug_textview);
        SharedPreferences my = getSharedPreferences("crash_log",
                Activity.MODE_PRIVATE);

        Date d = new Date(my.getLong("time", 0));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String s = sdf.format(d) + '\n' + my.getString("log", "");
        tv.setText(s);
    }
}
