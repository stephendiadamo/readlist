package com.s_diadamo.readlist.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Analytics;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.syncExternalData.SyncExternalDataFragment;

public class SettingsFragment extends Fragment {
    private static final String LOGIN = "LOGIN";
    private boolean mUserLoggedIn;

    private Context mContext;
    private TextView mLogin;
    private TextView mLoggedInAsLabel;
    private TextView mLoggedInAs;
    private boolean mSyncOnStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mContext = rootView.getContext();
        setHasOptionsMenu(false);

        mLogin = (TextView) rootView.findViewById(R.id.settings_login);
        mLoggedInAsLabel = (TextView) rootView.findViewById(R.id.settings_logged_in_user_label);
        mLoggedInAs = (TextView) rootView.findViewById(R.id.settings_logged_in_user);
        TextView syncData = (TextView) rootView.findViewById(R.id.settings_sync);
        TextView syncDataOnStart = (TextView) rootView.findViewById(R.id.settings_sync_on_start);
        final TextView syncDataOnStartOnOff = (TextView) rootView.findViewById(R.id.settings_sync_on_start_on_off);
        TextView syncExternalData = (TextView) rootView.findViewById(R.id.settings_sync_external_data);
        TextView emailUs = (TextView) rootView.findViewById(R.id.settings_email_us);
        TextView readList = (TextView) rootView.findViewById(R.id.settings_readlist);
        TextView readListVersion = (TextView) rootView.findViewById(R.id.settings_readlist_version);
        TextView shareReadlist = (TextView) rootView.findViewById(R.id.settings_share_readlist);

        setLoginLabels();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserLoggedIn) {
                    Utils.logout(mContext);
                    setLoginLabels();
                } else {
                    launchLoginFragment();
                }
            }
        });

        syncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUserLoggedIn) {
                    Utils.showToast(mContext, "You must be logged in to sync data");
                } else {
                    launchSyncData();
                }
            }
        });

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSyncOnStart = prefs.getBoolean(Utils.SYNC_ON_START, true);
        updateSyncOnOff(syncDataOnStartOnOff);

        syncDataOnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                mSyncOnStart = !mSyncOnStart;
                updateSyncOnOff(syncDataOnStartOnOff);
                editor.putBoolean(Utils.SYNC_ON_START, mSyncOnStart);
                editor.apply();
            }
        });

        syncDataOnStartOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                mSyncOnStart = !mSyncOnStart;
                updateSyncOnOff(syncDataOnStartOnOff);
                editor.putBoolean(Utils.SYNC_ON_START, mSyncOnStart);
                editor.apply();
            }
        });

        syncExternalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSyncExternalData();
            }
        });

        readList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAccountFragment();
            }
        });

        readListVersion.setText(MainActivity.PACKAGE_NAME);

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + "stephen.diadamo@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Readlist");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showToast(mContext, "Please install an email client");
                }
            }
        });

        shareReadlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check out this reading habits app: https://play.google.com/store/apps/details?id=com.s_diadamo.readlist");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.settings);
        }

        return rootView;
    }

    private void updateSyncOnOff(TextView syncDataOnStartOnOff) {
        if (mSyncOnStart) {
            syncDataOnStartOnOff.setText(R.string.on);
        } else {
            syncDataOnStartOnOff.setText(R.string.off);
        }
    }

    private void launchLoginFragment() {
        launchFragment(new LoginFragment());
    }

    private void launchSyncData() {
        ParseAnalytics.trackEventInBackground(Analytics.SYNCED_DATA);
        if (Utils.isNetworkAvailable(getActivity())) {
            SyncData syncData = new SyncData(mContext);
            syncData.syncAllData((AppCompatActivity) getActivity());
        } else {
            Utils.showToast(mContext, Utils.CHECK_INTERNET_MESSAGE);
        }
    }

    private void launchAccountFragment() {
        launchFragment(new AccountFragment());
    }

    private void launchSyncExternalData() {
        launchFragment(new SyncExternalDataFragment());
    }

    private void launchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(LOGIN)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setLoginLabels() {
        mUserLoggedIn = Utils.checkUserIsLoggedIn(getActivity());
        if (mUserLoggedIn) {
            mLogin.setText(R.string.logout);
            mLoggedInAsLabel.setVisibility(View.VISIBLE);
            mLoggedInAs.setVisibility(View.VISIBLE);
            mLoggedInAs.setText(Utils.getUserName(mContext));
        } else {
            mLogin.setText(R.string.login);
            mLoggedInAsLabel.setVisibility(View.GONE);
            mLoggedInAs.setVisibility(View.GONE);
        }
    }

}
