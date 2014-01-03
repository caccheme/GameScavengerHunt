package com.example.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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

  private void setupButtonCallbacks() {
	final Button createGameButton = (Button) findViewById(R.id.newGameButton_continue);
    createGameButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            final ParseObject game = new ParseObject("Game");
            final ParseUser currentUser = ParseUser.getCurrentUser();
            game.put("name", getUserInput(R.id.edit_gameName));
            game.put("creator", currentUser);
            game.put("start_datetime", getStartDateTime());
            game.put("end_datetime", getEndDateTime());
            game.saveInBackground(new SaveCallback() {
      		@Override
      		public void done(com.parse.ParseException e) {
                  if (e == null) {
                      Log.d("Game Creation", "Game Name/Times Created!");
                      final String GameId = game.getObjectId();
                      final Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                      i.putExtra("GameId", GameId);
                      NewGameActivity.this.startActivity(i);
                  } else {
                      Log.d("Game Creation", "Error creating game: " + e);
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

  private Date getStartDateTime() {
	  final SimpleDateFormat dateFormat = new SimpleDateFormat(
              "MM-dd-yyyy h:mm a", Locale.US);
      Date convertedDate = new Date();
      try {
          convertedDate = dateFormat.parse(getUserInput(R.id.editStartDate) + " "
                  + getUserInput(R.id.editStartTime));
      } 
      catch (java.text.ParseException e) {
          e.printStackTrace();
      }
      return convertedDate;   
  }

  private Date getEndDateTime() {
	  final SimpleDateFormat dateFormat = new SimpleDateFormat(
              "MM-dd-yyyy h:mm a", Locale.US);
      Date convertedDate = new Date();
      try {
          convertedDate = dateFormat.parse(getUserInput(R.id.editEndDate) + " "
                  + getUserInput(R.id.editEndTime));
      } 
      catch (java.text.ParseException e) {
          e.printStackTrace();
      }
      return convertedDate;   
  }

  private String getUserInput(int id) {
      EditText input = (EditText) findViewById(id);
      return input.getText().toString();
  }

  public void showStartDatePickerDialog(View v) {
      final DialogFragment newFragment = new DatePickerFragment();
      newFragment.show(getFragmentManager(), "startDatePicker");
  }

  public void showStartTimePickerDialog(View v) {
      final DialogFragment newFragment = new TimePickerFragment();
      newFragment.show(getFragmentManager(), "startTimePicker");
  }

  public void showEndDatePickerDialog(View v) {
      final DialogFragment newFragment = new DatePickerFragment();
      newFragment.show(getFragmentManager(), "endDatePicker");
  }

  public void showEndTimePickerDialog(View v) {
      final DialogFragment newFragment = new TimePickerFragment();
      newFragment.show(getFragmentManager(), "endTimePicker");
  }

}