package com.s_diadamo.readlist.general;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.s_diadamo.readlist.R;

public class LoginFragment extends Fragment {

    private View rootView;
    private boolean createAccountMode = false;

    private EditText emailAddressInput;
    private EditText passwordInput;
    private EditText passwordRepeatInput;
    private Button login;
    private TextView createAccount;
    private TextView forgotPassword;
    private TextView repeatPasswordLabel;
    private CheckBox rememberMe;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        context = rootView.getContext();

        emailAddressInput = (EditText) rootView.findViewById(R.id.login_email_address);
        passwordInput = (EditText) rootView.findViewById(R.id.login_password);
        passwordRepeatInput = (EditText) rootView.findViewById(R.id.login_password_repeat);

        login = (Button) rootView.findViewById(R.id.login_login);
        createAccount = (TextView) rootView.findViewById(R.id.login_create_account);
        forgotPassword = (TextView) rootView.findViewById(R.id.login_forgot_password);
        repeatPasswordLabel = (TextView) rootView.findViewById(R.id.login_password_repeat_label);
        rememberMe = (CheckBox) rootView.findViewById(R.id.login_remember_me);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailAddress = emailAddressInput.getText().toString();
                final String password = passwordInput.getText().toString();
                if (emailAddress.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill in fields", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 4) {
                    Toast.makeText(context, "Please use a longer password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (createAccountMode) {
                    String passwordRepeat = passwordRepeatInput.getText().toString();
                    if (passwordRepeat.isEmpty()) {
                        Toast.makeText(context, "Please fill in fields", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!passwordRepeat.equals(password)) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ParseUser user = new ParseUser();
                    user.setUsername(emailAddress);
                    user.setEmail(emailAddress);
                    user.setPassword(password);

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Creating account...");
                    progressDialog.show();

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                            } else if (e.getCode() == ParseException.ACCOUNT_ALREADY_LINKED) {
                                Toast.makeText(context, "This email address has been used. Did you forget your password?", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    ParseUser.logInInBackground(emailAddress, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            progressDialog.dismiss();
                            if (parseUser != null) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                                rememberUser(emailAddress, password);
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = prefs.edit();
                                if (rememberMe.isChecked()) {
                                    editor.putString(Utils.REMEMBER_ME, "yes");
                                } else {
                                    editor.putString(Utils.REMEMBER_ME, "no");
                                }
                                editor.apply();
                                Utils.launchBookFragment(getActivity().getSupportFragmentManager());
                            } else {
                                Toast.makeText(context, "Login failed, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!createAccountMode) {
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
        createAccountMode = true;
        login.setText("Create Account");
        createAccount.setText("Cancel");
        repeatPasswordLabel.setVisibility(View.VISIBLE);
        passwordRepeatInput.setVisibility(View.VISIBLE);
        rememberMe.setVisibility(View.GONE);
    }

    private void switchToLoginMode() {
        createAccountMode = false;
        login.setText("Login");
        createAccount.setText("Create Account");
        repeatPasswordLabel.setVisibility(View.GONE);
        passwordRepeatInput.setVisibility(View.GONE);
        rememberMe.setVisibility(View.VISIBLE);
    }

    private void switchToForgotPasswordMode() {
    }

    private void rememberUser(String emailAddress, String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Utils.USER_NAME, emailAddress);
        editor.putString(Utils.PASSWORD, password);
        editor.apply();
    }
}
