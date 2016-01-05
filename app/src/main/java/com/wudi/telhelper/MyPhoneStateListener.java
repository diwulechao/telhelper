package com.wudi.telhelper;

import android.telephony.PhoneStateListener;
import android.text.TextUtils;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d("MyPhoneListener", state + "   incoming no:" + incomingNumber);
        incomingNumber = ViewUtils.normalizeNumber(incomingNumber);
        if (TextUtils.isEmpty(incomingNumber)) return;

        Contact contact = StorageHelper.getContact(incomingNumber);
        if (contact.ban) ViewUtils.rejectCall();
        else {
            if (state == 1) ViewUtils.createOverlay(MainApplication.context, incomingNumber);
            else if (state == 0) {
                ViewUtils.stopRecord();
                if (OverlayView.Instance != null) OverlayView.Instance.setRecordVisible(false);
                MainApplication.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.removeOverlay(MainApplication.context);
                    }
                }, 10000);
            } else if (state == 2) {
                if (contact.alwaysRecord) ViewUtils.startRecord(incomingNumber);
                else {
                    ViewUtils.setAnswered();
                }
            }
        }
    }
}
