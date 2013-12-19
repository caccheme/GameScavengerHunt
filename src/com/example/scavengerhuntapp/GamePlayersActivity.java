package com.example.scavengerhuntapp;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GamePlayersActivity extends Activity {  
  private EditText userInput;
  private Button addplayerButton;
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayersmanage);
    listCurrentplayers();
    setupButtonCallbacks();
  }
  
  private void listCurrentplayers(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject gameInfo, ParseException e) {
        if (e == null) {
          JSONArray players = gameInfo.getJSONArray("playersList"); 
          if (players != null) {        
            //Now have to convert JSONArray 'players' to String Array 'playersList' so that ArrayAdapter will accept it as argument
            List<String> playersList = new ArrayList<String>();
            for(int i = 0; i < players.length(); i++){
              try{             
                playersList.add(players.getString(i));
              }
              catch (Exception exc) {
                Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
              }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, playersList); 
            ListView listView = (ListView) findViewById(R.id.listView1);
            listView.setAdapter(adapter);  
          }
        }
        else {
          CharSequence text = "Sorry, there was a problem. Just a sec.";
          int duration = Toast.LENGTH_SHORT;                     
          Toast.makeText(context, text, duration).show();
          finish();
          startActivity(getIntent()); 
        }
      }
    });  
  }
  
  private void setupButtonCallbacks() {
    userInput = (EditText) findViewById(R.id.enterText);
    addplayerButton = (Button) findViewById(R.id.manageplayersButton_addplayer);
    addplayerButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        final Intent i = getIntent();    
        query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject gameInfo, ParseException e) {
            if (e == null) {
              final String new_player = userInput.getText().toString().trim(); 
              JSONArray players = gameInfo.getJSONArray("playersList"); 
              if (players != null) {
                players.put(new_player); 
                gameInfo.put("playersList", players);   
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent()); 
              }  
              else { 
                JSONArray new_players = new JSONArray();
                new_players.put(new_player);
                gameInfo.put("playersList", new_players);
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent());
             }
            }    
            else{
              Context context = getApplicationContext();
              CharSequence text = "Sorry, player did not save. Please try again.";
              int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              finish();
              startActivity(getIntent());
            }
          }  
        });    
      } 
    }); 
    doneButton = (Button) findViewById(R.id.manageplayersButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "New Game Saved";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        Intent i = new Intent(GamePlayersActivity.this, MainMenuActivity.class);
        GamePlayersActivity.this.startActivity(i);
      } 
    });
    cancelButton = (Button) findViewById(R.id.manageplayersButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        Intent i = new Intent(GamePlayersActivity.this, GameItemsActivity.class);
        GamePlayersActivity.this.startActivity(i);
      }
    });
  }    

}
