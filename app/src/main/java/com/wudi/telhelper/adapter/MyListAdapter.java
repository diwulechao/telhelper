package com.wudi.telhelper.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wudi.telhelper.Contact;
import com.wudi.telhelper.MainApplication;
import com.wudi.telhelper.R;
import com.wudi.telhelper.StorageHelper;
import com.wudi.telhelper.ViewUtils;
import com.wudi.telhelper.activity.NoteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<String> number;
    private ArrayList<Contact> contacts;
    private HashMap<String, String> cache = new HashMap<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    private void init(String tag) {
        number = new ArrayList<>();
        contacts = new ArrayList<>();
        if (StorageHelper.map != null) {
            Iterator it = StorageHelper.map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (tag == null || tag.equals(((Contact) pair.getValue()).tag)) {
                    number.add((String) pair.getKey());
                    contacts.add((Contact) pair.getValue());
                }
            }
        }
    }

    public void refresh(String tag) {
        init(tag);
        this.notifyDataSetChanged();
    }

    public MyListAdapter() {
    }

    @Override
    public MyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cardview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Contact contact = contacts.get(position);
        ((TextView) holder.mView.findViewById(R.id.info_text)).setText(cache.containsKey(number.get(position)) ? cache.get(number.get(position)) : ViewUtils.getFriendName(MainApplication.context, number.get(position)));
        holder.mView.findViewById(R.id.ban_icon).setVisibility(contact.ban ? View.VISIBLE : View.INVISIBLE);
        holder.mView.findViewById(R.id.record_icon).setVisibility(contact.alwaysRecord ? View.VISIBLE : View.INVISIBLE);

        if (contact.note != null && contact.note.size() > 0) {
            Pair<String, Long> pair = Contact.getNote(contact.note, contact.note.size() - 1);
            ((TextView) holder.mView.findViewById(R.id.note_text_card)).setText(pair.first);

            Date date = new Date(pair.second);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            ((TextView) holder.mView.findViewById(R.id.note_text_card_time)).setText(pair.second == 0 ? "" : dateFormat.format(date));
        } else {
            ((TextView) holder.mView.findViewById(R.id.note_text_card)).setText("");
            ((TextView) holder.mView.findViewById(R.id.note_text_card_time)).setText("");
        }

        (holder.mView.findViewById(R.id.card_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainApplication.context, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("number", number.get(position));
                MainApplication.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return number == null ? 0 : number.size();
    }
}