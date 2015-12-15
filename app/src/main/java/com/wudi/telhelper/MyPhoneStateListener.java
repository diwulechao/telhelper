package com.wudi.telhelper;

import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.View;

public class MyPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("MyPhoneListener", state + "   incoming no:" + incomingNumber);

        Contact contact = StorageHelper.getContact(incomingNumber);
        if (contact.ban) ViewUtils.rejectCall();
        else {
            if (state == 1) ViewUtils.createOverlay(MainApplication.context, incomingNumber);
            else if (state == 0) {
                ViewUtils.stopRecord();
                MainApplication.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.removeOverlay(MainApplication.context);
                    }
                }, 5000);
            } else if (state == 2) {
                ViewUtils.setAnswered();
            }
        }
    }
}
