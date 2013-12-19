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
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {
  private static final String TAG = "LoginActivity";

  private Button continueButton;
  private Button cancelButton;
  private EditText usernameEditText;
  private EditText passwordEditText;
  private EditText emailEditText;

  private String username;
  private String password;
  private String email;

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

  public void done(List<ParseUser> arg0, ParseException arg1) {
    dismissProgressDialog();
    if (arg1 == null) {
      if (arg0 != null && arg0.size() > 0) {
        ParseUser user = arg0.get(0);
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
            emailEditText.setText("");
            emailEditText.requestFocus();
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
  
  
  
  
  
  
  
  
  private final FindCallback<ParseUser> userFindCallback = new FindCallback<ParseUser>() {
    @Override
    public void done(List<ParseUser> arg0, ParseException arg1) {
      dismissProgressDialog();
      if (arg1 == null) {
        if (arg0 != null && arg0.size() > 0) {
          ParseUser user = arg0.get(0);
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
              emailEditText.setText("");
              emailEditText.requestFocus();
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
  };

  private void showToast(String message) {
    ScavengerHuntApplication.getInstance().showToast(LoginActivity.this,
        message);
  }


  private void setupButtonCallbacks() {
    usernameEditText = (EditText) findViewById(R.id.textbox_loginUsername);
    passwordEditText = (EditText) findViewById(R.id.textbox_loginPassword);
    emailEditText = (EditText) findViewById(R.id.textbox_loginEmail);
    continueButton = (Button) findViewById(R.id.loginbutton_continue);
    continueButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();
        if (username == null || username.length() == 0) {
          showToast(getString(R.string.hint_username));
          return;
        }
        if (password == null || password.length() == 0) {
          showToast(getString(R.string.hint_password));
          return;
        }
        progressDialog = ProgressDialog.show(LoginActivity.this,
            getString(R.string.label_login_please_wait),
            getString(R.string.label_query_in_progress) + " '"
                + username + "'");

        List<ParseQuery<ParseUser>> parseUserQueryList = new ArrayList<ParseQuery<ParseUser>>();

        ParseQuery<ParseUser> parseUsernameQuery = ParseUser.getQuery();
        parseUsernameQuery.whereEqualTo("username", username);
        parseUserQueryList.add(parseUsernameQuery);
        ParseQuery<ParseUser> parseEmailQuery = ParseUser.getQuery();
        parseEmailQuery.whereEqualTo("email", email);
        parseUserQueryList.add(parseEmailQuery);

        ParseQuery<ParseUser> parseUserQuery = ParseQuery.or(parseUserQueryList);
        parseUserQuery.findInBackground(userFindCallback);
      }
    });

    cancelButton = (Button) findViewById(R.id.loginbutton_cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        ParseUser.logOut();
        finish();
      }
    });
  }

  private final SignUpCallback signinCallback = new SignUpCallback() {
    @Override
    public void done(ParseException arg0) {
      dismissProgressDialog();
      if (arg0 == null) {
        Log.d(TAG + ".doSignUp",
            "Success!  User account created for username="
                + LoginActivity.this.username);
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
        getString(R.string.label_signup_in_progress));
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);
    user.setEmail(email);
    user.signUpInBackground(signinCallback);
  }

  private final LogInCallback loginCallback = new LogInCallback() {
    @Override
    public void done(ParseUser arg0, ParseException arg1) {
      dismissProgressDialog();
      if (arg0 != null) {
        Log.d(TAG + ".doParseLogin",
            "Success!  Current User ObjectId: "
                + arg0.getObjectId());
        startActivity(new Intent(LoginActivity.this,
            MainMenuActivity.class));
        finish();
      } else {
        Log.d(TAG + ".doParseLogin", "Failed", arg1);
        showToast(getString(R.string.label_loginErrorMessage) + " "
            + arg1.getMessage() + ".  "
            + getString(R.string.label_loginPleaseTryAgainMessage));
      }
    }
  };

  private void doLogin() {
    progressDialog = ProgressDialog.show(LoginActivity.this,
        getString(R.string.label_login_please_wait),
        getString(R.string.label_login_in_progress) + " '" + username
            + "'");

    ParseUser.logInInBackground(username, password, loginCallback);
  }

}