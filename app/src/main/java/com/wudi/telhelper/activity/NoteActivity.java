package com.wudi.telhelper.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.wudi.telhelper.Contact;
import com.wudi.telhelper.R;
import com.wudi.telhelper.StorageHelper;
import com.wudi.telhelper.adapter.NoteAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    private EditText editText;
    private Button addButton;
    private Spinner spinner;
    private Toolbar toolbar;
    private String number;
    private Contact contact;
    private boolean editMode;
    private MenuItem deleteItem;

    private String[] tagNames = {"None", "Family and Friend", "Telemarketing", "Delivery"};
    private String[] tagTypes = {null, "family", "market", "delivery"};

    private StaggeredGridLayoutManager staggeredGridLayoutManagerVertical;
    private RecyclerView myRecyclerView;
    private NoteAdapter myRecyclerViewAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.ban_check:
                item.setChecked(!item.isChecked());
                StorageHelper.banNumber(number, item.isChecked());
                return true;
            case R.id.record_check:
                item.setChecked(!item.isChecked());
                StorageHelper.recordNumber(number, item.isChecked());
                return true;
            case R.id.button_delete:
                deleteNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_activity_menu, menu);
        deleteItem = menu.findItem(R.id.button_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (contact.ban) menu.findItem(R.id.ban_check).setChecked(true);
        if (contact.alwaysRecord) menu.findItem(R.id.record_check).setChecked(true);

        return super.onPrepareOptionsMenu(menu);
    }

    private void deleteNote() {
        if (contact.note != null) {
            ArrayList<String> array = new ArrayList<>();
            for (int i = 0; i < contact.note.size(); i++) {
                if (!myRecyclerViewAdapter.selected[i]) array.add(contact.note.get(i));
            }

            contact.note = array;
            StorageHelper.map.put(number, contact);
            StorageHelper.commit();
            setNotes(contact);
        }

        exitEditMode();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noteactivity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHideOnContentScrollEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) this.findViewById(R.id.edit_note);
        spinner = (Spinner) this.findViewById(R.id.spinner_tag);
        addButton = (Button) this.findViewById(R.id.add_note);

        staggeredGridLayoutManagerVertical =
                new StaggeredGridLayoutManager(2,
                        LinearLayoutManager.VERTICAL);

        myRecyclerView = (RecyclerView) findViewById(R.id.myrecyclerview);
        myRecyclerViewAdapter = new NoteAdapter();
        myRecyclerViewAdapter.editModeListener = new NoteAdapter.EditModeListener() {
            @Override
            public void enter() {
                enterEditMode();
            }

            @Override
            public boolean isEditMode() {
                return editMode;
            }

            @Override
            public void exit() {
                exitEditMode();
            }
        };

        myRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerView.setLayoutManager(staggeredGridLayoutManagerVertical);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, tagNames);
        spinner.setAdapter(adapter);
    }

    private void enterEditMode() {
        editMode = true;
        deleteItem.setVisible(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(NoteActivity.this, R.color.grey));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(NoteActivity.this, R.color.grey)));
        }
    }

    private void exitEditMode() {
        editMode = false;
        deleteItem.setVisible(false);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(NoteActivity.this, R.color.colorPrimary));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(NoteActivity.this, R.color.colorPrimary)));
        }

        myRecyclerViewAdapter.exitEditMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.number = this.getIntent().getStringExtra("number");
        getSupportActionBar().setTitle(number);

        contact = StorageHelper.getContact(number);
        setNotes(contact);

        spinner.setOnItemSelectedListener(null);
        if (contact.tag == null) spinner.setSelection(0, false);
        else
            for (int i = 1; i < tagTypes.length; i++) {
                if (tagTypes[i].equals(contact.tag)) {
                    spinner.setSelection(i, false);
                    break;
                }
            }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StorageHelper.setTag(number, tagTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    StorageHelper.addNote(number, editText.getText().toString());
                    setNotes(StorageHelper.getContact(number));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            exitEditMode();
        } else {
            finish();
        }
    }

    public void setNotes(Contact contact) {
        myRecyclerViewAdapter.notes = contact.note;
        myRecyclerViewAdapter.selected = new boolean[contact.note == null ? 0 : contact.note.size()];
        myRecyclerViewAdapter.notifyDataSetChanged();
    }
}
