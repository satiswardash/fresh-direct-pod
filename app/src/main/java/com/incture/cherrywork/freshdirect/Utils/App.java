package com.incture.cherrywork.freshdirect.Utils;

import android.content.Context;

/**
 * Created by Arun on 21-10-2016.
 */

public class App extends android.app.Application {

    private static android.app.Application application;

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public void onCreate() {
        super.onCreate();
        application = this;
    }
}