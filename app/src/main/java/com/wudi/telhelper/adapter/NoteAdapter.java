package com.wudi.telhelper.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wudi.telhelper.Contact;
import com.wudi.telhelper.R;
import com.wudi.telhelper.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wudi on 1/4/2016.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public List<String> notes;
    public boolean[] selected;
    public EditModeListener editModeListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cardview4, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String s = Contact.getNote(notes, position).first;
        float length = s == null ? 0 : s.length();
        if (length > 20) length = 20;
        TextView tv = ((TextView) holder.mView.findViewById(R.id.note_text));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40 - length);
        tv.setText(s);

        long time = Contact.getNote(notes, position).second;
        TextView tv2 = ((TextView) holder.mView.findViewById(R.id.note_text_date));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(Contact.getNote(notes, position).second);
        tv2.setText(time == 0 ? "" : dateFormat.format(date));

        ViewGroup container = (ViewGroup) holder.mView.findViewById(R.id.cardview_container);
        if (selected[position]) {
            container.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.seleted_background));
        } else {
            container.setBackground(null);
        }

        int tp = (int) ViewUtils.dipToPixels(5);
        container.setPadding(tp + tp, tp, tp + tp, tp);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (editModeListener != null) {
                    if (!editModeListener.isEditMode()) {
                        editModeListener.enter();
                        selected[position] = true;
                        notifyItemChanged(position);
                        return true;
                    }
                }
                return false;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editModeListener != null) {
                    if (editModeListener.isEditMode()) {
                        selected[position] = !selected[position];
                        notifyItemChanged(position);

                        int cnt = 0;
                        for (Boolean b : selected) if (b) cnt++;
                        if (cnt == 0) editModeListener.exit();
                    }
                }
            }
        });

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

    public void exitEditMode() {
        if (selected != null) for (int i = 0; i < selected.length; i++) selected[i] = false;
        notifyDataSetChanged();
    }

    public interface EditModeListener {
        void enter();

        boolean isEditMode();

        void exit();
    }
}
