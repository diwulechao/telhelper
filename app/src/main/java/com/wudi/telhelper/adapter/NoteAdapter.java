package com.wudi.telhelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wudi.telhelper.Contact;
import com.wudi.telhelper.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wudi on 1/4/2016.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public List<String> notes;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cardview4, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int p = getItemCount() - position - 1;

        String s = Contact.getNote(notes, p).first;
        float length = s == null ? 0 : s.length();
        if (length > 20) length = 20;
        TextView tv = ((TextView) holder.mView.findViewById(R.id.note_text));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40 - length);
        tv.setText(s);

        long time = Contact.getNote(notes, p).second;
        TextView tv2 = ((TextView) holder.mView.findViewById(R.id.note_text_date));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(Contact.getNote(notes, p).second);
        tv2.setText(time == 0 ? "" : dateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }
}
