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
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewGameActivity extends Activity {  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newgamecreate);
    setupButtonCallbacks();
  }
// This is to commit a change and check that everything is set up with github
  private void setupButtonCallbacks() {
    final EditText userInput = (EditText) findViewById(R.id.edit_gameName);
    final EditText userInputStartDate = (EditText) findViewById(R.id.editStartDate);
    final EditText userInputStartTime = (EditText) findViewById(R.id.editStartTime);
    final EditText userInputEndDate = (EditText) findViewById(R.id.editEndDate);
    final EditText userInputEndTime = (EditText) findViewById(R.id.editEndTime);    
    Button newGameButton = (Button) findViewById(R.id.newGameButton_continue);  
    newGameButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          final ParseObject game = new ParseObject("game");
          final String gameName = userInput.getText().toString().trim();          
          final String gameStartDate = userInputStartDate.getText().toString().trim();
          final String gameStartTime = userInputStartTime.getText().toString().trim();
          final String gameEndDate = userInputEndDate.getText().toString().trim();
          final String gameEndTime = userInputEndTime.getText().toString().trim();
          
          game.put("gameCreator", ParseUser.getCurrentUser());
          game.put("gameName", gameName);
          game.put("start_datetime", (gameStartDate + " " + gameStartTime));
          game.put("end_datetime", (gameEndDate + " " + gameEndTime));
          
          game.saveInBackground(
             new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                 if (e == null) {
                   final String gameId = game.getObjectId();
                   final Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                   i.putExtra("gameId", gameId);
                   NewGameActivity.this.startActivity(i);
                }
                else{
                   Context context = getApplicationContext();
                   CharSequence text = "Sorry, app has encountered a problem.";
                   final int duration = Toast.LENGTH_SHORT;
                   Toast.makeText(context, text, duration).show();
                   Log.d("ScavengerHuntApp", Log.getStackTraceString(e));
                   finish();
                   startActivity(getIntent());
                 }
               }
                }); 
        } 
    } );
    final Button cancelButton = (Button) findViewById(R.id.newGameButton_cancel); 
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