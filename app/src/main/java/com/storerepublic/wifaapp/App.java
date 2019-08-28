package com.storerepublic.wifaapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.libraries.places.api.Places;

import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.LocaleHelper;
import com.storerepublic.wifaapp.utills.NoInternet.AppLifeCycleManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        AppLifeCycleManager.init(this);
        String apiKey = getString(R.string.places_api_key);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
        MultiDex.install(this);
    }
}
