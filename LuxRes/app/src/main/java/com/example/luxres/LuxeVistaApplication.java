package com.example.luxres; // Your base package

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.luxres.Constants;

public class LuxeVistaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Apply the saved theme mode preference when the app starts
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedMode = sharedPreferences.getInt(Constants.PREF_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedMode);
    }
}