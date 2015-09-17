package com.s_diadamo.readlist.syncExternalData;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.s_diadamo.readlist.general.Utils;

import java.io.IOException;


public class GetUsernameTask extends AsyncTask<Void, Void, Void> {

    Activity mActivity;
    String mScope;
    String mEmail;

    GetUsernameTask(Activity activity, String scope, String email) {
        mActivity = activity;
        mScope = scope;
        mEmail = email;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                // TODO: Get book stuff
                Log.i("----TOKEN-----", token);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            Utils.showToast(mActivity, "Failed to log in");
        } catch (GoogleAuthException fatalException) {
            Utils.showToast(mActivity, "Failed to log in due to wtf");
        }
        return null;
    }
}
