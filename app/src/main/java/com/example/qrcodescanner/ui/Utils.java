package com.example.qrcodescanner.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.qrcodescanner.R;

public class Utils
{
    public static int sTheme;
    public final static int TEAL = 0;
    public final static int PURPLE = 1;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case TEAL:
                //activity.setTheme(R.style.Teal);
                break;
            case PURPLE:
                //activity.setTheme(R.style.Purple);
                break;

        }
    }

    public static void saveFlag(boolean flag, android.content.Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dark", flag);
        editor.commit();
    }
    public static boolean getFlag(android.content.Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("dark", false);
    }
}