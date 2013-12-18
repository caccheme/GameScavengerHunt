package com.example.scavengerhuntapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.SaveCallback;


public class NewGameActivity extends Activity {  
  private EditText userInput;
  private Button newGameButton;
  private Button cancelButton;
  private EditText userInputStartDate;
  private EditText userInputStartTime;
  private EditText userInputEndDate;
  private EditText userInputEndTime;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newgamecreate);
    setupButtonCallbacks();
  }

  private void setupButtonCallbacks() {
    userInput = (EditText) findViewById(R.id.edit_gameName);
    userInputStartDate = (EditText) findViewById(R.id.editStartDate);
    userInputStartTime = (EditText) findViewById(R.id.editStartTime);
    userInputEndDate = (EditText) findViewById(R.id.editEndDate);
    userInputEndTime = (EditText) findViewById(R.id.editEndTime);    
    newGameButton = (Button) findViewById(R.id.newGameButton_continue);  
    newGameButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          final ParseObject gameInfo = new ParseObject("gameInfo");
          final String gameName = userInput.getText().toString().trim();          
          final String gameStartDate = userInputStartDate.getText().toString().trim();
          final String gameStartTime = userInputStartTime.getText().toString().trim();
          final String gameEndDate = userInputEndDate.getText().toString().trim();
          final String gameEndTime = userInputEndTime.getText().toString().trim();
          
          gameInfo.put("gameName", gameName);
          gameInfo.put("start_datetime", (gameStartDate + " " + gameStartTime));
          gameInfo.put("end_datetime", (gameEndDate + " " + gameEndTime));
          ScavengerHuntApplication.getInstance().showToast(NewGameActivity.this, "Game Created!");
          gameInfo.saveInBackground(
             new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                 if (e == null) {
                   final String gameInfoId = gameInfo.getObjectId();
                   Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                   i.putExtra("gameInfoId", gameInfoId);
                   NewGameActivity.this.startActivity(i);
                }
                else{
                   Context context = getApplicationContext();
                   CharSequence text = "Sorry, app has encountered a problem.";
                   int duration = Toast.LENGTH_SHORT;
                   Toast.makeText(context, text, duration).show();
                   Log.d("ScavengerHuntApp", Log.getStackTraceString(e));
                   finish();
                   startActivity(getIntent());
                 }
               }
                }); 
        } 
    } );
    cancelButton = (Button) findViewById(R.id.newGameButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        Intent i = new Intent(NewGameActivity.this, MainMenuActivity.class);
        NewGameActivity.this.startActivity(i);
      }
    });
  }
}