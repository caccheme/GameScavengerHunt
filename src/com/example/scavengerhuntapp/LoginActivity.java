package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.scavengerhuntapp.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    
    private ProgressDialog progressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setupButtonCallbacks();
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && currentUser.getObjectId() != null) {
            final String username = currentUser.getUsername();
            EditText usernameEditText = (EditText) findViewById(R.id.textbox_loginUsername);
            
            usernameEditText.setText(username);
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    private final FindCallback <ParseUser> userFindCallback = new FindUserCallback();
    
    private void showToast(String message) {
        ScavengerHuntApplication.getInstance().showToast(LoginActivity.this,
                message);
    }

    private void setupButtonCallbacks() {
        findViewById(R.id.loginbutton_continue).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (getUserNameInput() != null && getUserNameInput().length() != 0) {
                    showToast(getString(R.string.hint_username));
                  }
                if (getUserPasswordInput() != null && getUserPasswordInput().length() != 0) {
                    showToast(getString(R.string.hint_password));
                  }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(getUserEmailInput()).matches() 
                		&& getUserEmailInput() != null) {
                    showToast(getString(R.string.hint_email));
                  }
                else {};
                queryForUser();                 
            }
        });
        findViewById(R.id.loginbutton_cancel).setOnClickListener(new OnClickListener() {
            public final void onClick(View v) {
                ParseUser.logOut();
                finish();
            }
        });
    }

    private String getUserNameInput(){
        return getUserInput(R.id.textbox_loginUsername);
    }
    
    private String getUserPasswordInput(){
        return getUserInput(R.id.textbox_loginPassword);
    }
    
    private String getUserEmailInput(){
        return getUserInput(R.id.textbox_loginEmail);
    }
           
    private String getUserInput(int id){
         EditText input = (EditText) findViewById(id);
         return input.getText().toString();
    }
    
    private void queryForUser(){
        progressDialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.label_login_please_wait),
                getString(R.string.label_login_in_progress) + " '" + 
                getUserNameInput() + "'");

        List<ParseQuery<ParseUser>> parseUserQueryList = new ArrayList<ParseQuery<ParseUser>>();
        final ParseQuery<ParseUser> parseUsernameQuery = ParseUser.getQuery();
        
        parseUsernameQuery.whereEqualTo("username", getUserNameInput());
        parseUserQueryList.add(parseUsernameQuery);
       
        final ParseQuery<ParseUser> parseEmailQuery = ParseUser.getQuery();
        parseEmailQuery.whereEqualTo("email", getUserEmailInput());
        parseUserQueryList.add(parseEmailQuery);
        
        final ParseQuery<ParseUser> parseUserQuery = ParseQuery.or(parseUserQueryList);
        parseUserQuery.findInBackground(userFindCallback);
    }
    
    private final SignUpCallback signinCallback = new SignUpCallback() {
        @Override
        public void done(ParseException exception) {
            dismissProgressDialog();
            if (exception == null) {
                Log.d(TAG + ".doSignUp",
                        "Success! User account created for username="
                                + LoginActivity.this.getUserNameInput());
                startActivity(new Intent(LoginActivity.this,
                        MainMenuActivity.class));
                finish();
            } else {
                showToast(getString(R.string.label_signupErrorMessage) + " "
                        + getString(R.string.label_loginPleaseTryAgainMessage));
            }
        }
    };

    private void doSignUp() {
        progressDialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.label_login_please_wait),
                getString(R.string.label_signup_in_progress) + " '" + getUserNameInput()
                        + "'");
        ParseUser user = new ParseUser();
        user.setUsername(getUserNameInput());
        user.setPassword(getUserPasswordInput());
        user.setEmail(getUserEmailInput());
        user.signUpInBackground(signinCallback);
    }

    private final LogInCallback loginCallback = new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException exception) {
            dismissProgressDialog();
            if (user != null) {
                Log.d(TAG + ".doParseLogin",
                        "Success! Current User ObjectId: "
                                + user.getObjectId());
                startActivity(new Intent(LoginActivity.this,
                        MainMenuActivity.class));
                finish();
            } else {
                Log.d(TAG + ".doParseLogin", "Failed", exception);
                showToast(getString(R.string.label_loginErrorMessage) + " "
                        + exception.getMessage() + ". "
                        + getString(R.string.label_loginPleaseTryAgainMessage));
            }
        }
    };
    private void doLogin() {
        progressDialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.label_login_please_wait),
                getString(R.string.label_login_in_progress) + 
                " '" + getUserNameInput() + "'");
        ParseUser.logInInBackground(getUserNameInput(), getUserPasswordInput(), loginCallback);
    }

    class FindUserCallback extends FindCallback <ParseUser> {
        @Override
        public void done(List<ParseUser> userList, ParseException exception) {
            String username = getUserNameInput();
            String email = getUserEmailInput();
            EditText usernameEditText = (EditText) findViewById(R.id.textbox_loginUsername);
            dismissProgressDialog();
            if (exception == null) {
                if (userList != null && userList.size() > 0) {
                    ParseUser user = (ParseUser) userList.get(0);
                    if (username != null) {
                        String existingUsername = user.getUsername();
                        if (!username.equals(existingUsername)) {
                            usernameEditText.setText("");
                            usernameEditText.requestFocus();
                            username = null;
                            showToast(getString(R.string.label_loginUsernameAlreadyExists));
                            return;
                        }
                    }
                    if (email != null) {
                        String existingEmail = user.getEmail();
                        if (!email.equals(existingEmail)) {
                            email = null;
                            showToast(getString(R.string.label_loginEmailAlreadyExists));
                            return;
                        }
                    }
                    doLogin();
                } else
                    doSignUp();
            } else
                showToast(getString(R.string.label_signupErrorMessage) + " "
                        + getString(R.string.label_loginPleaseTryAgainMessage));
        }
    }

}