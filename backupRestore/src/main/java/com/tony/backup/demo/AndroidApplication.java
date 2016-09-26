package com.tony.backup.demo;

import android.app.Application;

import net.sqlcipher.database.SQLiteDatabase;

public class AndroidApplication extends Application {

    private static AndroidApplication instance;

    public static AndroidApplication getInstance() {
        return instance;
    }


    //@DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SQLiteDatabase.loadLibs(this);

    }


    //@DebugLog
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
