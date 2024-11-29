package com.example.schedify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String savedUsername = "";
    String savedPassword = "";

    private final String PREF_FILE_NAME = "AppData";
    private final int PRIVATE_MODE = 0;

    private final String KEY_IF_LOGGED_IN = "key_logged_in";
    private final String KEY_USERNAME = "key_session_name";
    private final String KEY_PASSWORD = "key_session_phno";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);

        savedUsername = sp.getString(KEY_USERNAME, "");
        savedPassword = sp.getString(KEY_PASSWORD, "");
    }

    public boolean checkSession() {
        boolean isLogin = sp.getBoolean(KEY_IF_LOGGED_IN, false);

        if (isLogin && sp.contains(KEY_IF_LOGGED_IN)) {
            return true;
        } else {
            return false;
        }
    }

    //----------We are storing data in Shared Preference File----------------
    public void createSession(String username, String password) {
        savedUsername = username;
        savedPassword = password;

        sp = context.getSharedPreferences(PREF_FILE_NAME, 0);
        editor = sp.edit();
        editor.clear();
        editor.commit();

        editor = sp.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IF_LOGGED_IN, true);
        editor.commit();
    }

    public String getSessionDetails(String key) {
        String value = sp.getString(key, null);
        return value;
    }

    public void signOut() {
        editor = sp.edit();
        editor.putBoolean(KEY_IF_LOGGED_IN, false);
        editor.commit();
        Intent intent = new Intent(context, com.example.schedify.Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure intent works with non-Activity context
        context.startActivity(intent);
    }

    public String[] retrieveSaveCredential() {
        sp = context.getSharedPreferences(PREF_FILE_NAME, 0);
        savedUsername = sp.getString(KEY_USERNAME, "");
        savedPassword = sp.getString(KEY_PASSWORD, "");
        return new String[] {savedUsername, savedPassword};
    }
}
