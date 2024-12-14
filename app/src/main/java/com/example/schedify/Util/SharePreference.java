package com.example.schedify.Util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class SharePreference {

    public static void saveJson(Context context, JSONObject jsonObject) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jsonData", jsonObject.toString());
        editor.apply();
    }

    public static JSONObject loadJson(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        String jsonData = prefs.getString("jsonData", null);
        try {
            return jsonData != null ? new JSONObject(jsonData) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveHTML(Context context, String HtmlDoc) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("HTMLData", HtmlDoc);
        editor.apply();
    }

    public static String loadHTML(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        return prefs.getString("HTMLData", null);
    }
}
