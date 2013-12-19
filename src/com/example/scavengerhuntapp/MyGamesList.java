package com.example.scavengerhuntapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MyGamesList extends Activity {  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_games_list);
    listMyGames();
    //final Button cancelButton;
  }
  
  private void listMyGames(){
    //below is copied from GameItemsActivity and is just a guide. NOT correct.
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("mygamesInfo");
    final Intent i = getIntent(); 
    query.getInBackground(i.getStringExtra("currentUser"), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject mygamesInfo, ParseException e) {
        //need to query and get all game records that have the currentUser as the gameCreator and then print out a list of
        //the game names ....maybe later those names will have buttons/links so user can see one games details
      }
    });  
  }
}