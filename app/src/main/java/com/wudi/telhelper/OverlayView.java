package com.wudi.telhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wudi.telhelper.activity.NoteActivity;

/**
 * Created by wudi on 12/15/2015.
 */
public class OverlayView extends RelativeLayout {
    public static OverlayView Instance;
    private String number;
    private Button banButton, recordButton, noteButton;
    private ImageView closeButton;
    private TextView noteView;

    public OverlayView(Context context, final String number) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_overlayview, this);
        this.number = number;
        banButton = (Button) this.findViewById(R.id.ban);
        recordButton = (Button) this.findViewById(R.id.record);
        noteButton = (Button) this.findViewById(R.id.note);
        closeButton = (ImageView) this.findViewById(R.id.close);
        noteView = (TextView) this.findViewById(R.id.notetext);

        banButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.rejectCall(null);
                StorageHelper.banNumber(OverlayView.this.number, true);

                banButton.setEnabled(false);
                banButton.setTextColor(Color.GRAY);
                banButton.setText("Baned");

                MainApplication.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.removeOverlay(MainApplication.context);
                    }
                }, 5000);
            }
        });

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.removeOverlay(MainApplication.context);
            }
        });

        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.startRecord(number);
            }
        });

        Contact contact = StorageHelper.getContact(number);
        if (contact != null && contact.note != null)
            noteView.setText(Contact.getNote(contact.note, 0).first);

        noteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainApplication.context, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("number", number);
                MainApplication.context.startActivity(intent);
                ViewUtils.removeOverlay(MainApplication.context);
            }
        });
    }

    public void setRecordVisible(boolean visible) {
        recordButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
