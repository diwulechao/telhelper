package com.wudi.telhelper;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NoteActivity extends Activity {
    private String number;
    private CheckBox banCheck, recordCheck;
    private EditText editText;
    private Button saveButton, addButton;
    private TextView notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noteactivity);
        number = this.getIntent().getStringExtra("number");
        TextView numberText = (TextView) this.findViewById(R.id.num_text);
        numberText.setText("Number: " + number);

        final Contact contact = StorageHelper.getContact(number);
        banCheck = (CheckBox) this.findViewById(R.id.check_ban);
        recordCheck = (CheckBox) this.findViewById(R.id.check_record);
        editText = (EditText) this.findViewById(R.id.edit_note);
        //saveButton = (Button) this.findViewById(R.id.button_save);
        addButton = (Button) this.findViewById(R.id.add_note);
        notes = (TextView) this.findViewById(R.id.all_notes);

        if (contact.ban) banCheck.setChecked(true);
        if (contact.alwaysRecord) recordCheck.setChecked(true);

        setNotes(contact);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    StorageHelper.addNote(number, editText.getText().toString());
                    setNotes(StorageHelper.getContact(number));
                }
            }
        });

        banCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StorageHelper.banNumber(number, isChecked);
            }
        });

        recordCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StorageHelper.recordNumber(number, isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void setNotes(Contact contact) {
        String t = "";
        if (contact.note != null)
            for (String s : contact.note) t = s + "\n" + t;

        notes.setText(t);
    }
}
