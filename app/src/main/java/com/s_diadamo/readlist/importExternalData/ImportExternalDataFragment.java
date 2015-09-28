package com.s_diadamo.readlist.importExternalData;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;

public class ImportExternalDataFragment extends Fragment {

    protected static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private View rootView;
    private String mEmail;
    private SharedPreferences mSharedPrefs;
    private final static String BOOKS_API_SCOPE = "oauth2:https://www.googleapis.com/auth/books";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sync_external_data, container, false);
        TextView syncGoogleData = (TextView) rootView.findViewById(R.id.sync_external_data_google);
        syncGoogleData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername();
            }
        });
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        return rootView;
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, "Readlist", null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUsername();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Utils.showToast(rootView.getContext(), "Cancelled");
            }
        } else {
            Utils.showToast(rootView.getContext(), "Sign in failed. Please try again.");
        }

        //TODO: Handle exceptions
    }

    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (Utils.isNetworkAvailable((Activity) rootView.getContext())) {
                AsyncTask<String, String, String> task = new GetUsernameTask(getActivity(), mEmail, BOOKS_API_SCOPE, mSharedPrefs);
                task.execute();
            } else {
                Utils.showToast(rootView.getContext(), "No internet");
            }
        }
    }
}
