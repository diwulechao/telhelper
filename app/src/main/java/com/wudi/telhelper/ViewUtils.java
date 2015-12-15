package com.wudi.telhelper;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wudi on 12/15/2015.
 */
public class ViewUtils {
    private static AudioRecorder audioRecorder;

    public static void createOverlay(Context context, String number) {
        final WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams param = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2010, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, -3);
        param.gravity = Gravity.CENTER;
        param.screenOrientation = 1;
        if (OverlayView.Instance == null) {
            OverlayView.Instance = new OverlayView(context, number);
            mWindowManager.addView(OverlayView.Instance, param);
        }
    }

    public static void removeOverlay(Context context) {
        if (OverlayView.Instance != null) {
            final WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.removeView(OverlayView.Instance);
            OverlayView.Instance = null;
        }
    }

    public static void setAnswered() {
        if (OverlayView.Instance != null) {
            OverlayView.Instance.setRecordVisible(true);
        }
    }

    public static void rejectCall() {
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startRecord(String number) {
        if (audioRecorder == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            audioRecorder = new AudioRecorder("/record/" + number + '_' + currentDateandTime);
            try {
                audioRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (OverlayView.Instance != null) {
                OverlayView.Instance.setRecordVisible(false);
            }
        }
    }

    public static void stopRecord() {
        if (audioRecorder != null) {
            try {
                audioRecorder.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }

            audioRecorder = null;
        }
    }
}
