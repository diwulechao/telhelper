package com.wudi.telhelper;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wudi on 12/15/2015.
 */
public class StorageHelper {
    public static HashMap<String, Contact> map;

    public static void init() {
        try {
            FileInputStream input = MainApplication.context.openFileInput("file.cfg");
            ObjectInputStream inputStream = new ObjectInputStream(input);
            map = (HashMap) inputStream.readObject();
            inputStream.close();
            input.close();
            Log.d("hello", "read complete");
        } catch (Exception e) {
            e.printStackTrace();
            map = new HashMap<>();
        }

    }

    public static void commit() {
        try {
            FileOutputStream file = MainApplication.context.openFileOutput("file.cfg", Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(file);
            outputStream.writeObject(map);
            outputStream.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearData() {
        map = new HashMap<>();
        commit();
    }

    public static void banNumber(String number, boolean ban) {
        Contact contact = map.get(number);
        if (contact == null) contact = new Contact();
        contact.ban = ban;
        map.put(number, contact);
        commit();
    }

    public static void recordNumber(String number, boolean record) {
        Contact contact = map.get(number);
        if (contact == null) contact = new Contact();
        contact.alwaysRecord = record;
        map.put(number, contact);
        commit();
    }

    public static Contact getContact(String number) {
        Contact contact = map.get(number);
        if (contact == null) {
            contact = new Contact();
            map.put(number, contact);
        }

        return contact;
    }

    public static void addNote(String number, String note) {
        Contact contact = map.get(number);
        if (contact == null) {
            contact = new Contact();
            map.put(number, contact);
        }

        if (contact.note == null) contact.note = new ArrayList<>();
        contact.note.add(note);
        commit();
    }

    public static void setTag(String number, String tag) {
        Contact contact = map.get(number);
        if (contact == null) {
            contact = new Contact();
            map.put(number, contact);
        }

        contact.tag = tag;
        commit();
    }
}
