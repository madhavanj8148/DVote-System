package com.example.dvotesystem;

import android.app.Application;
import android.content.Context;
import com.google.android.material.color.DynamicColors;

public class DVoteApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Apply Material 3 Dynamic Colors
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
