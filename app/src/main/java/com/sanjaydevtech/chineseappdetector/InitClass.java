package com.sanjaydevtech.chineseappdetector;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class InitClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
