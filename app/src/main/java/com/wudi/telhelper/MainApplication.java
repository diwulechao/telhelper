package com.wudi.telhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

/**
 * Created by wudi on 12/15/2015.
 */
public class MainApplication extends Application {
    public static Context context;
    public static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler(getMainLooper());

        StorageHelper.init();

        TelephonyManager tmgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
        tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        registerCustomUEH();
    }

    private void registerCustomUEH() {
        final Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable throwable) {
                String type = throwable == null ? "null" : throwable.getClass().getName();

                StringBuilder sb = new StringBuilder();
                sb.append(Log.getStackTraceString(throwable));

                SharedPreferences mySharedPreferences = getSharedPreferences("crash_log",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("log", sb.toString());
                editor.putLong("time", System.currentTimeMillis());
                editor.commit();

                // pass it to default handler
                if (defaultUEH != null) {
                    defaultUEH.uncaughtException(thread, throwable);
                }
            }
        });
    }

}
