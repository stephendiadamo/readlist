package com.s_diadamo.readlist.syncExternalData;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;

public class SyncExternalDataFragment extends Fragment {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private View rootView;
    private String mEmail;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/books";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sync_external_data, container, false);

        TextView syncGoogleData = (TextView) rootView.findViewById(R.id.sync_external_data_google);


        // TODO: Check internet connection
        syncGoogleData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail == null) {
                    pickUserAccount();
                } else {
                    if (Utils.isNetworkAvailable((Activity) rootView.getContext())) {
                        new GetUsernameTask((Activity) rootView.getContext(), mEmail, SCOPE).execute();
                    } else {
                        Utils.showToast(rootView.getContext(), "Not connected internet bru");
                    }
                }
            }
        });

        return rootView;
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, "Readlist", null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_ACCOUNT:
                if (resultCode == Activity.RESULT_OK) {
                    mEmail = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    getUsername();
                    break;
                } else {
                    Utils.showToast(rootView.getContext(), "Login failed");
                }
        }

        // TODO: Handle exceptions
    }

    private void getUsername() {
        new GetUsernameTask((Activity) rootView.getContext(), mEmail, SCOPE).execute();
    }
}
