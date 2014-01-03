package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GamePlayersActivity extends Activity {
  private List<ParseUser> playerList = new ArrayList<ParseUser>();

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayers);
    setCurrentUserList();
    setupButtonCallbacks();
  }
 
  private void setCurrentUserList() {
    final ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.selectKeys(Arrays.asList("username"));
    query.findInBackground(new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> userList, ParseException e) {
            if (e == null) {
                final String[] usernameArray = new String[userList.size()];
                Log.d("User List", "Retrieved " + userList.size());
                for (int i = 0; i < userList.size(); i++) {
                    Log.d("data", "Retrieved User: "
                            + userList.get(i).getString("username"));
                    usernameArray[i] = userList.get(i).getString("username");
                }
                playerList = userList;
                setUsernameListView(usernameArray);
            } else {
                Log.w("error", "game retreival failure");
            }
        }
    });
  }
 
  private void setUsernameListView(String[] usernameArray) {
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, usernameArray);
    final ListView playerListView = (ListView) findViewById(R.id.listView_players);
    playerListView.setAdapter(adapter);
    playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
  }
   
   private void setupButtonCallbacks() {
     final Button finishCreateGameButton = (Button) findViewById(R.id.manageplayersButton_done);
     finishCreateGameButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
           final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");          
           Intent b = new Intent();
           query.getInBackground(b.getStringExtra("Game"), new GetCallback<ParseObject>() {   
                    
             @Override
               public void done(ParseObject game, ParseException e) {
                 if (e == null) {
                   Log.d("Game Creation", "Game Created!");
                   saveGamePlayers(getSelectedPlayerList());
                   finish();
                   Intent i = new Intent(GamePlayersActivity.this, MyGamesList.class);
                   GamePlayersActivity.this.startActivity(i);
            
                 } else {
                   Log.d("Game Creation", "Error creating game: " + e);
                 }
               }
           });
         }
     });
     final Button cancelButton = (Button) findViewById(R.id.manageplayersButton_cancel);
     cancelButton.setOnClickListener(new OnClickListener() {
       @Override
       public void onClick(View v) {
         finish();
         Intent i = new Intent(GamePlayersActivity.this, GameItemsActivity.class);
         GamePlayersActivity.this.startActivity(i);
       }
     });
   }

  private List<ParseUser> getSelectedPlayerList() {
     final List<ParseUser> chosenPlayers = new ArrayList<ParseUser>();
     final ListView playerListView = (ListView) findViewById(R.id.listView_players);
     SparseBooleanArray checkedItems = playerListView
             .getCheckedItemPositions();
     if (checkedItems != null) {
         for (int i = 0; i < checkedItems.size(); i++) {
             if (checkedItems.valueAt(i)) {
                 chosenPlayers.add(playerList.get(checkedItems.keyAt(i)));
             }
         }
     }
     return chosenPlayers;
   }

   private void saveGamePlayers(List<ParseUser> chosenPlayerList) {  
     for (int i = 0; i < chosenPlayerList.size(); i++) {     
         final ParseUser user = chosenPlayerList.get(i);
         Log.d("Player", user.toString());
         final Intent intent = getIntent();
         
         final ParseObject gamePlayer = new ParseObject("GamePlayer");
         gamePlayer.put("user", user);
         gamePlayer.put("game", intent.getStringExtra("GameId"));
         gamePlayer.saveInBackground(new SaveCallback() {
             @Override
             public void done(ParseException e) {
                 if (e == null) {
                     Log.d("Save", "gamePlayer data saved!");
                 } else {
                     Log.d("Save", "Error saving gamePlayer: " + e);
                 }
             }

         });
     }
  }

}