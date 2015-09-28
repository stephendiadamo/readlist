package com.s_diadamo.readlist.importExternalData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.search.RequestQueueSingleton;

import org.json.JSONObject;

import java.io.IOException;

public class GetUsernameTask extends AsyncTask<String, String, String> {

    private Activity mActivity;
    private String mEmail;
    private String mScope;
    String mToken;
    SharedPreferences mPrefs;

    private ProgressDialog progressDialog;

    GetUsernameTask(Activity activity, String email, String scope, SharedPreferences prefs) {
        mActivity = activity;
        mEmail = email;
        mScope = scope;
        mPrefs = prefs;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(mActivity, null, "Verifying Google account...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            userRecoverableException.printStackTrace();
            mActivity.startActivityForResult(userRecoverableException.getIntent(),
                    ImportExternalDataFragment.REQUEST_CODE_PICK_ACCOUNT);
        } catch (GoogleAuthException fatalException) {
            fatalException.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {
        progressDialog.dismiss();
        if (token != null) {
            mToken = token;
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Utils.GOOGLE_USER_TOKEN, token);
            editor.apply();
            getUserBooks();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String token = null;
        try {
            token = fetchToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    private void getUserBooks() {
        if (mToken == null || mToken.isEmpty()) {
            Utils.showToast(mActivity, "Google authorization failed");
            return;
        }

        progressDialog = ProgressDialog.show(mActivity, null, "Google account is valid, fetching books...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("books")
                .appendPath("v1")
                .appendPath("mylibrary")
                .appendPath("bookshelves")
                .appendPath("7")
                .appendPath("volumes")
                .appendQueryParameter("access_token", mToken);

        String url = builder.build().toString();
        url += "&fields=" + "items(volumeInfo(authors,imageLinks/smallThumbnail,pageCount,industryIdentifiers,subtitle,title))";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        GoogleJSONResultParser.parseGoogleJSONBookData(mActivity, jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                    }
                });
        RequestQueueSingleton.getInstance(mActivity).addToRequestQueue(jsonObjectRequest);
    }
}
