package com.wudi.telhelper.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wudi.telhelper.R;

import java.io.File;

/**
 * Created by wudi on 12/31/2015.
 */
public class RecordItem extends RelativeLayout {
    private TextView tv;
    private View dv, pv;
    private AfterDeleteCallBack afterDeleteCallBack;

    public RecordItem(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_record_item, this);
        tv = (TextView) this.findViewById(R.id.record_item_text);

        dv = this.findViewById(R.id.record_item_delete);
        pv = this.findViewById(R.id.record_item_play);
    }

    public void setAfterDeleteCallback(AfterDeleteCallBack afterDeleteCallback) {
        this.afterDeleteCallBack = afterDeleteCallback;
    }

    public void setInfo(final String s) {
        tv.setText(s);
        dv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/" + s);
                if (file.delete() && afterDeleteCallBack != null) {
                    afterDeleteCallBack.after();
                }
            }
        });

        pv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/" + s);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                pv.getContext().startActivity(intent);
            }
        });
    }

    public interface AfterDeleteCallBack {
        void after();
    }
}
