package com.example.schedify;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class Helper {

    public static void saveJsonToPreferences(Context context, JSONObject jsonObject) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jsonData", jsonObject.toString());
        editor.apply();
    }

    public static JSONObject loadJsonFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        String jsonData = prefs.getString("jsonData", null);
        try {
            return jsonData != null ? new JSONObject(jsonData) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveHTMLToPreferences(Context context, String HtmlDoc) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("HTMLData", HtmlDoc);
        editor.apply();
    }

    public static String loadHTMLFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppData", MODE_PRIVATE);
        return prefs.getString("HTMLData", null);
    }
}
