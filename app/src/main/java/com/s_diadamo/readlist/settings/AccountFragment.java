package com.s_diadamo.readlist.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;

public class AccountFragment extends Fragment {

    private TextView emailAddressText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        TextView accountLabel = (TextView) rootView.findViewById(R.id.account_label);
        emailAddressText = (TextView) rootView.findViewById(R.id.account_email_address);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());

        String email = prefs.getString(Utils.EMAIL_ADDRESS, "");
        if (email != null && email.isEmpty()) {
            emailAddressText.setText("Email address not set");
        } else {
            emailAddressText.setText(email);
        }

        accountLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkUserIsLoggedIn(rootView.getContext())) {
                    launchEnterEmailDialog(rootView.getContext());
                }
            }
        });

        return rootView;
    }

    private void launchEnterEmailDialog(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View emailDialog = inflater.inflate(R.layout.dialog_enter_email, null);
        alert.setView(emailDialog);
        alert.setTitle(R.string.your_email_address);
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText emailAddressInput = (EditText) emailDialog.findViewById(R.id.enter_email);
                String emailAddress = emailAddressInput.getText().toString();
                if (emailAddress.isEmpty()) {
                    Utils.showToast(context, "Please enter your email address");
                    return;
                }
                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    user.setEmail(emailAddress);
                    emailAddressText.setText(emailAddress);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Utils.EMAIL_ADDRESS, emailAddress);
                    editor.apply();

                    dialog.dismiss();
                } else {
                    Utils.showToast(context, "Please login before editing your email address");
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
