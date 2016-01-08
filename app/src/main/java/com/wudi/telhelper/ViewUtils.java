package com.wudi.telhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wudi.telhelper.activity.NoteActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

    public static void rejectCall(String number) {
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

            if (!TextUtils.isEmpty(number)) {
                // notification
                NotificationManager mNotificationManager = (NotificationManager) MainApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(MainApplication.context, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("number", number);

                PendingIntent contentIntent = PendingIntent.getActivity(MainApplication.context, 0, intent, 0);
                Notification notification = new Notification.Builder(MainApplication.context)
                        .setContentTitle("Call rejected")
                        .setContentText(number).setSmallIcon(R.drawable.close_icon)
                        .setContentIntent(contentIntent).setWhen(System.currentTimeMillis()).setAutoCancel(true)
                        .build();

                mNotificationManager.notify((int) (System.currentTimeMillis() % 10000), notification);
            }
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

    public static HashMap<String, ArrayList<String>> getRecords() {
        HashMap<String, ArrayList<String>> ret = new HashMap<>();
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record";

            File f = new File(path);
            File file[] = f.listFiles();
            if (file != null) {
                for (int i = 0; i < file.length; i++) {
                    String[] sa = file[i].getName().split("_");
                    if (ret.get(sa[0]) == null) {
                        ret.put(sa[0], new ArrayList<String>());
                    }

                    ret.get(sa[0]).add(file[i].getName());
                }
            }

            return ret;
        } catch (Exception e) {
            return ret;
        }
    }

    public static float dipToPixels(float dipValue) {
        DisplayMetrics metrics = MainApplication.context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static String normalizeNumber(String number) {
        if (TextUtils.isEmpty(number)) return number;
        if (number.indexOf("+86") == 0) return number.substring(3);
        return number;
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static String getFriendName(Context context, String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) return phoneNumber;
        String fname = getContactName(context, phoneNumber);
        if (!TextUtils.isEmpty(fname)) {
            return fname + " (" + phoneNumber + ')';
        }
        return phoneNumber;
    }
}
