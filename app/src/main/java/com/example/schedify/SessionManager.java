package com.example.sharedpreferencedemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private final String PREF_FILE_NAME = "slaying";
    private final int PRIVATE_MODE = 0;

    private final String KEY_IF_LOGGED_IN = "key_logged_in";
    private final String KEY_NAME = "key_session_name";
    private final String KEY_EMAIL = "key_session_email";
    private final String KEY_PHNO = "key_session_phno";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(PREF_FILE_NAME, 0);
        editor = sp.edit();
    }

    public boolean checkSession() {
        if (sp.contains(KEY_IF_LOGGED_IN)) {
            return true;
        } else {
            return false;
        }
    }

    //----------We are storing data in Shared Preference File----------------
    public void createSession(String name, String email, String phno) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHNO, phno);
        editor.putBoolean(KEY_IF_LOGGED_IN, true);
        editor.commit();
    }

    public String getSessionDetails(String key) {
        String value = sp.getString(key, null);
        return value;
    }

    public void signOut() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, com.example.sharedpreferencedemo.Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure intent works with non-Activity context
        context.startActivity(intent);
    }
}
