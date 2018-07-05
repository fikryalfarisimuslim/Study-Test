package com.sunway.averychoke.studywifidirect3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private SharedPreferences customCachedPrefs;
    private Context context;

    public PreferenceHelper(Context context) {
        super();
        this.context = context;
        customCachedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Context getContex() {
        return context;
    }

    public SharedPreferences getCustomPref() {
        return customCachedPrefs;
    }

    public void setFirstTime(Boolean first) {
        SharedPreferences.Editor mEditor = customCachedPrefs.edit();
        mEditor.putBoolean("first",first);
        mEditor.apply();
    }

    public Boolean getFirstTime() {
        return customCachedPrefs.getBoolean("first", true);
    }

    public void setNIM(String nim) {
        SharedPreferences.Editor mEditor = customCachedPrefs.edit();
        mEditor.putString("nim",nim);
        mEditor.apply();
    }

    public String getNIM() {
        return customCachedPrefs.getString("nim", "nim");
    }

    public void setName(String name) {
        SharedPreferences.Editor mEditor = customCachedPrefs.edit();
        mEditor.putString("name",name);
        mEditor.apply();
    }

    public String getName() {
        return customCachedPrefs.getString("name", "name");
    }


}
