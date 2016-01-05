package com.wudi.telhelper.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wudi.telhelper.MainApplication;
import com.wudi.telhelper.R;
import com.wudi.telhelper.ViewUtils;
import com.wudi.telhelper.activity.NoteActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {
    private ArrayList<MyContact> contacts;
    private Context context;
    private HashMap<String, Bitmap> cache = new HashMap<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public void refresh() {
        callDetails(context);
        this.notifyDataSetChanged();
    }

    public CallLogAdapter(Context context) {
        this.context = context;
    }

    private void callDetails(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_CALL_LOG)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return;
        }

        contacts = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int pho = managedCursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        while (managedCursor.moveToNext()) {
            MyContact contact = new MyContact();
            contact.number = ViewUtils.normalizeNumber(managedCursor.getString(number));
            contact.name = managedCursor.getString(name);
            String uri = managedCursor.getString(pho);
            if (!TextUtils.isEmpty(uri))
                contact.uri = Uri.parse(uri);

            String callDate = managedCursor.getString(date);
            contact.date = new Date(Long.valueOf(callDate));

            if (contacts.size() == 0 || !contact.number.equals(contacts.get(contacts.size() - 1).number))
                contacts.add(contact);
        }
        managedCursor.close();
    }

    @Override
    public CallLogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cardview2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyContact contact = contacts.get(position);
        String name = TextUtils.isEmpty(contact.name) ? contact.number : contact.name;
        if (TextUtils.isEmpty(name)) name = "private number";
        ((TextView) holder.mView.findViewById(R.id.info_text)).setText(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ((TextView) holder.mView.findViewById(R.id.note_text_card)).setText(contact.date == null ? "" : dateFormat.format(contact.date));

        holder.mView.findViewById(R.id.call_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + contact.number));
                if (ActivityCompat.checkSelfPermission(holder.mView.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                holder.mView.getContext().startActivity(intent);
            }
        });

        Bitmap bitmap = null;
        if (contact.uri != null) {
            if (cache.containsKey(contact.uri.toString()))
                bitmap = cache.get(contact.uri.toString());
            else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contact.uri);
                    cache.put(contact.uri.toString(), bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bitmap != null) {
            ((ImageView) holder.mView.findViewById(R.id.contact_icon)).setImageBitmap(bitmap);
        } else {
            ((ImageView) holder.mView.findViewById(R.id.contact_icon)).setImageResource(R.drawable.people_icon);
        }

        (holder.mView.findViewById(R.id.card_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = contacts.get(position).number;
                if (TextUtils.isEmpty(number)) return;
                Intent intent = new Intent(MainApplication.context, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("number", number);
                MainApplication.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    class MyContact {
        String name, number;
        Uri uri;
        Date date;
    }
}