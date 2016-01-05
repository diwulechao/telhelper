package com.wudi.telhelper.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wudi.telhelper.R;
import com.wudi.telhelper.ViewUtils;
import com.wudi.telhelper.view.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wudi on 12/31/2015.
 */
public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {
    private ArrayList<Contact> contacts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewGroup container;
        public Contact contact;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public void refresh() {
        HashMap<String, ArrayList<String>> map = ViewUtils.getRecords();
        contacts = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Contact contact = new Contact();
            contact.number = (String) pair.getKey();
            contact.records = (ArrayList<String>) pair.getValue();
            contact.position = contacts.size();
            contacts.add(contact);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public RecordingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cardview3, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.container = (ViewGroup) vh.mView.findViewById(R.id.cardview_container);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecordingAdapter.ViewHolder holder, int position) {
        ((TextView) holder.mView.findViewById(R.id.info_text)).setText(contacts.get(position).number);
        holder.contact = contacts.get(position);
        resize(position, holder);
        holder.mView.findViewById(R.id.call_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + holder.contact.number));
                if (ActivityCompat.checkSelfPermission(holder.mView.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                holder.mView.getContext().startActivity(intent);
            }
        });

        (holder.mView.findViewById(R.id.card_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.contact.expanded = !holder.contact.expanded;
                resize(holder.contact.position, holder);
            }
        });
    }

    private void resize(final int position, final ViewHolder holder) {
        int cnt = contacts.get(position).records.size();
        if (!contacts.get(position).expanded) {
            holder.container.removeAllViews();
        } else {
            holder.container.removeAllViews();
            for (int i = 0; i < cnt; i++) {
                final RecordItem item = new RecordItem(holder.mView.getContext());
                final int ii = i;
                item.setInfo(contacts.get(position).records.get(i));
                item.setAfterDeleteCallback(new RecordItem.AfterDeleteCallBack() {
                    @Override
                    public void after() {
                        contacts.get(position).records.remove(ii);
                        if (contacts.get(position).records.size() == 0) {
                            contacts.remove(position);
                            for (int i = position; i < contacts.size(); i++) {
                                contacts.get(i).position = i;
                            }

                            notifyItemRemoved(position);
                        } else {
                            //resize(position, holder);
                            notifyItemChanged(position);
                        }
                    }
                });

                holder.container.addView(item);
            }
        }

        ((TextView) holder.mView.findViewById(R.id.note_text_card)).setText("Total: " + contacts.get(position).records.size() + " records");
        float size = ViewUtils.dipToPixels(75 + (contacts.get(position).expanded ? 40 * cnt : 0));
        ViewGroup.LayoutParams params = holder.mView.getLayoutParams();
        params.height = (int) size;
        holder.mView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class Contact {
        public String number;
        public ArrayList<String> records;
        public int position;
        public boolean expanded;
    }
}
