package com.wudi.telhelper;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.os.Handler;

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

//        StorageHelper.addNote("123","hello world");
//        ViewUtils.createOverlay(context,"123");
    }
}
