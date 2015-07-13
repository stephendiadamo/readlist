package com.s_diadamo.readlist.general;


import android.app.ProgressDialog;
import android.content.Context;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.s_diadamo.readlist.R;

public class LoginFragment extends Fragment {

    private final int CREATE_ACCOUNT_MODE = 0;
    private final int LOGIN_MODE = 1;
    private final int FORGOT_PASSWORD_MODE = 2;

    private int currentMode = LOGIN_MODE;

    private EditText userNameInput;
    private EditText emailAddressInput;
    private EditText passwordInput;
    private EditText passwordRepeatInput;
    private Button login;
    private TextView userNameLabel;
    private TextView emailAddressLabel;
    private TextView createAccount;
    private TextView repeatPasswordLabel;
    private TextView forgotPassword;
    private TextView passwordLabel;
    private CheckBox rememberMe;
    private Context context;

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
        passwordRepeatInput = (EditText) rootView.findViewById(R.id.login_password_repeat);
        passwordLabel = (TextView) rootView.findViewById(R.id.login_password_label);

        login = (Button) rootView.findViewById(R.id.login_login);
        createAccount = (TextView) rootView.findViewById(R.id.login_create_account);
        forgotPassword = (TextView) rootView.findViewById(R.id.login_forgot_password);
        repeatPasswordLabel = (TextView) rootView.findViewById(R.id.login_password_repeat_label);
        rememberMe = (CheckBox) rootView.findViewById(R.id.login_remember_me);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userNameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                if (currentMode != FORGOT_PASSWORD_MODE) {
                    if (userName.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please fill in fields", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (currentMode == CREATE_ACCOUNT_MODE) {
                    String passwordRepeat = passwordRepeatInput.getText().toString();

                    if (password.length() < 4) {
                        Toast.makeText(context, "Please use a longer password", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (passwordRepeat.isEmpty()) {
                        Toast.makeText(context, "Please fill in fields", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!passwordRepeat.equals(password)) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ParseUser user = new ParseUser();
                    user.setUsername(userName);
                    user.setPassword(password);

                    String emailAddress = emailAddressInput.getText().toString();
                    if (!emailAddress.isEmpty()) {
                        user.setEmail(emailAddress);
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Creating account...");
                    progressDialog.show();

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                                switchToLoginMode();
                            } else if (e.getCode() == ParseException.ACCOUNT_ALREADY_LINKED) {
                                Toast.makeText(context, "This email address has been used. Did you forget your password?", Toast.LENGTH_SHORT).show();
                            } else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                                Toast.makeText(context, "The email address entered is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (currentMode == LOGIN_MODE) {
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    ParseUser.logInInBackground(userName, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            progressDialog.dismiss();
                            if (parseUser != null) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                                rememberUser(userName, password);
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = prefs.edit();
                                if (rememberMe.isChecked()) {
                                    editor.putString(Utils.REMEMBER_ME, "yes");
                                } else {
                                    editor.putString(Utils.REMEMBER_ME, "no");
                                }
                                editor.apply();
                                toggleActionBar(true);
                                Utils.hideKeyBoard(getActivity());
                                Utils.launchBookFragment(getActivity().getSupportFragmentManager());
                            } else {
                                Toast.makeText(context, "Login failed, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (currentMode == FORGOT_PASSWORD_MODE) {
                    final String emailAddress = emailAddressInput.getText().toString();
                    if (emailAddress.isEmpty()) {
                        Toast.makeText(context, "Please fill in your email address", Toast.LENGTH_LONG).show();
                        return;
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Verifying...");
                    progressDialog.show();
                    ParseUser.requestPasswordResetInBackground(emailAddress, new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                Toast.makeText(context, "An email was sent to your account", Toast.LENGTH_LONG).show();
                            } else {
                                if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS || e.getCode() == ParseException.EMAIL_NOT_FOUND) {
                                    Toast.makeText(context, "No user associated with this address", Toast.LENGTH_LONG).show();
                                }
                            }
                            toggleActionBar(true);
                            Utils.hideKeyBoard(getActivity());
                            Utils.launchBookFragment(getActivity().getSupportFragmentManager());
                        }
                    });
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == LOGIN_MODE) {
                    switchToCreateMode();
                } else {
                    switchToLoginMode();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToForgotPasswordMode();
            }
        });

        return rootView;
    }

    private void switchToCreateMode() {
        currentMode = CREATE_ACCOUNT_MODE;

        userNameInput.setVisibility(View.VISIBLE);
        userNameLabel.setVisibility(View.VISIBLE);

        emailAddressLabel.setVisibility(View.VISIBLE);
        emailAddressInput.setVisibility(View.VISIBLE);

        passwordLabel.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.VISIBLE);
        rememberMe.setVisibility(View.GONE);

        repeatPasswordLabel.setVisibility(View.VISIBLE);
        passwordRepeatInput.setVisibility(View.VISIBLE);

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

        repeatPasswordLabel.setVisibility(View.GONE);
        passwordRepeatInput.setVisibility(View.GONE);

        login.setText("Login");

        createAccount.setText("Create Account");
        forgotPassword.setVisibility(View.VISIBLE);
    }

    private void switchToForgotPasswordMode() {
        currentMode = FORGOT_PASSWORD_MODE;

        userNameInput.setVisibility(View.GONE);
        userNameLabel.setVisibility(View.GONE);

        emailAddressLabel.setVisibility(View.VISIBLE);
        emailAddressInput.setVisibility(View.VISIBLE);

        passwordLabel.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);
        rememberMe.setVisibility(View.GONE);

        repeatPasswordLabel.setVisibility(View.GONE);
        passwordRepeatInput.setVisibility(View.GONE);

        login.setText("Send Email");

        createAccount.setText("Cancel");
        forgotPassword.setVisibility(View.GONE);
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
