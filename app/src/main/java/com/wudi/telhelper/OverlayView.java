package com.wudi.telhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;

/**
 * Created by wudi on 12/15/2015.
 */
public class OverlayView extends RelativeLayout {
    public static OverlayView Instance;
    private String number;
    private Button banButton, recordButton, noteButton;
    private ImageView closeButton;

    public OverlayView(Context context, final String number) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_overlayview, this);
        this.number = number;
        banButton = (Button) this.findViewById(R.id.ban);
        recordButton = (Button) this.findViewById(R.id.record);
        noteButton = (Button) this.findViewById(R.id.note);
        closeButton = (ImageView) this.findViewById(R.id.close);

        banButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.rejectCall();
                StorageHelper.banNumber(OverlayView.this.number);
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
    }

    public void setRecordVisible(boolean visible) {
        recordButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
