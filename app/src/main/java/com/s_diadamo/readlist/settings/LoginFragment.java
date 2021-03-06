package com.s_diadamo.readlist.settings;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Analytics;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

public class LoginFragment extends Fragment {

    private final int CREATE_ACCOUNT_MODE = 0;
    private final int LOGIN_MODE = 1;
    private final int FORGOT_PASSWORD_MODE = 2;

    private int currentMode = LOGIN_MODE;

    private EditText userNameInput;
    private EditText emailAddressInput;
    private EditText passwordInput;
    private Button login;
    private TextView userNameLabel;
    private TextView emailAddressLabel;
    private TextView createAccount;
    private TextView forgotPassword;
    private TextView passwordLabel;
    private CheckBox rememberMe;
    private Context context;
    private boolean loginCancelled = false;
    private boolean forgotPasswordCancelled = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(false);
        toggleActionBar(false);

        userNameInput = (EditText) rootView.findViewById(R.id.login_user_name);
        userNameLabel = (TextView) rootView.findViewById(R.id.login_user_name_label);
        emailAddressInput = (EditText) rootView.findViewById(R.id.login_email_address);
        emailAddressLabel = (TextView) rootView.findViewById(R.id.login_email_address_label);
        passwordInput = (EditText) rootView.findViewById(R.id.login_password);
        passwordLabel = (TextView) rootView.findViewById(R.id.login_password_label);

        login = (Button) rootView.findViewById(R.id.login_login);
        createAccount = (TextView) rootView.findViewById(R.id.login_create_account);
        forgotPassword = (TextView) rootView.findViewById(R.id.login_forgot_password);
        rememberMe = (CheckBox) rootView.findViewById(R.id.login_remember_me);

        if (Utils.checkRememberMe(context)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            userNameInput.setText(prefs.getString(Utils.USER_NAME, ""));
            passwordInput.setText(prefs.getString(Utils.PASSWORD, ""));
            rememberMe.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedWithAction();
            }
        });

        final Bundle bundle = getArguments();
        if (bundle != null && bundle.getInt(Utils.CREATE_ACCOUNT_FROM_MAIN) == 100) {
            switchToCreateMode();
        }

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == LOGIN_MODE) {
                    switchToCreateMode();
                } else {
                    if (bundle != null && bundle.getInt(Utils.CREATE_ACCOUNT_FROM_MAIN) == 100) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        switchToLoginMode();
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToForgotPasswordMode();
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return rootView;
    }

    private void proceedWithAction() {
        final String userName = userNameInput.getText().toString().trim();
        final String password = passwordInput.getText().toString().trim();

        if (currentMode != FORGOT_PASSWORD_MODE) {
            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in fields", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (currentMode == CREATE_ACCOUNT_MODE) {
            createAccount(userName, password);
        } else if (currentMode == LOGIN_MODE) {
            login(userName, password);
        } else if (currentMode == FORGOT_PASSWORD_MODE) {
            forgotPassword();
        }
    }

    private void forgotPassword() {
        final String emailAddress = emailAddressInput.getText().toString();
        if (emailAddress.isEmpty()) {
            Toast.makeText(context, "Please fill in your email address", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                forgotPasswordCancelled = true;
            }
        });
        progressDialog.show();
        ParseUser.requestPasswordResetInBackground(emailAddress, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (!forgotPasswordCancelled) {
                    if (e == null) {
                        Toast.makeText(context, "An email was sent to your account", Toast.LENGTH_LONG).show();
                        toggleActionBar(true);
                        Utils.hideKeyBoard(getActivity());
                        Utils.launchSettingsFragment(getActivity().getSupportFragmentManager());
                    } else {
                        if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS || e.getCode() == ParseException.EMAIL_NOT_FOUND) {
                            Toast.makeText(context, "No user associated with this address", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    private void login(final String userName, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loginCancelled = true;
            }
        });
        progressDialog.show();

        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                progressDialog.dismiss();
                if (!loginCancelled) {
                    if (parseUser != null) {
                        ParseAnalytics.trackEventInBackground(Analytics.LOGGED_IN);
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                        rememberUser(userName, password);
                        completeLogin();
                    } else {
                        Toast.makeText(context, "Login failed, please try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void createAccount(String userName, String password) {
        ParseAnalytics.trackEventInBackground(Analytics.STARTED_CREATING_ACCOUNT);

        if (password.length() < 4) {
            Toast.makeText(context, "Please use a longer password", Toast.LENGTH_LONG).show();
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);

        String emailAddress = emailAddressInput.getText().toString();
        if (!emailAddress.isEmpty()) {
            user.setEmail(emailAddress);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Utils.EMAIL_ADDRESS, emailAddress);
            editor.apply();
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    ParseAnalytics.trackEventInBackground(Analytics.CREATED_ACCOUNT);
                    Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                    switchToLoginMode();
                } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                    Toast.makeText(context, "The username has already been used", Toast.LENGTH_SHORT).show();
                } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                    Toast.makeText(context, "This email address has been used. Did you forget your password?", Toast.LENGTH_SHORT).show();
                } else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                    Toast.makeText(context, "The email address entered is invalid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void completeLogin() {
        if (Utils.isNetworkAvailable(getActivity())) {
            SyncData syncData = new SyncData(context, true);
            syncData.syncAllData();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Utils.LOGGED_IN, true);

        if (rememberMe.isChecked()) {
            editor.putBoolean(Utils.REMEMBER_ME, true);
        } else {
            editor.putBoolean(Utils.REMEMBER_ME, false);
        }
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            editor.putString(Utils.EMAIL_ADDRESS, user.getEmail());
        }

        editor.apply();
        toggleActionBar(true);

        Utils.hideKeyBoard(getActivity());
        Utils.launchSettingsFragment(getActivity().getSupportFragmentManager());
    }

    private void switchToCreateMode() {
        currentMode = CREATE_ACCOUNT_MODE;

        userNameInput.setVisibility(View.VISIBLE);
        userNameLabel.setVisibility(View.VISIBLE);

        emailAddressLabel.setVisibility(View.VISIBLE);
        emailAddressLabel.setText(R.string.email_address_optional);
        emailAddressInput.setVisibility(View.VISIBLE);

        passwordLabel.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.VISIBLE);
        rememberMe.setVisibility(View.GONE);

        login.setText("Create Account");

        createAccount.setText("Cancel");
        forgotPassword.setVisibility(View.GONE);
    }

    private void switchToLoginMode() {
        currentMode = LOGIN_MODE;

        userNameInput.setVisibility(View.VISIBLE);
        userNameLabel.setVisibility(View.VISIBLE);

        emailAddressLabel.setVisibility(View.GONE);
        emailAddressInput.setVisibility(View.GONE);

        passwordLabel.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.VISIBLE);
        rememberMe.setVisibility(View.VISIBLE);

        login.setText("Login");

        createAccount.setText("Create Account");
        forgotPassword.setVisibility(View.VISIBLE);
    }

    private void switchToForgotPasswordMode() {
        currentMode = FORGOT_PASSWORD_MODE;

        userNameInput.setVisibility(View.GONE);
        userNameLabel.setVisibility(View.GONE);

        emailAddressLabel.setVisibility(View.VISIBLE);
        emailAddressLabel.setText(R.string.email_address);
        emailAddressInput.setVisibility(View.VISIBLE);

        passwordLabel.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);
        rememberMe.setVisibility(View.GONE);

        login.setText("Send Email");

        createAccount.setText("Cancel");
        forgotPassword.setVisibility(View.GONE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        emailAddressInput.setText(prefs.getString(Utils.EMAIL_ADDRESS, ""));
    }

    private void rememberUser(String userName, String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Utils.USER_NAME, userName);
        editor.putString(Utils.PASSWORD, password);
        editor.apply();
    }

    private void toggleActionBar(boolean show) {
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            if (show) {
                ab.show();
            } else {
                ab.hide();
            }
        }
    }
}
