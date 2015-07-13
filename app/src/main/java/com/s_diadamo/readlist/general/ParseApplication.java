package com.s_diadamo.readlist.general;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, API.PARSE_APP_KEY, API.PARSE_CLIENT_KEY);
    }
}
