package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GamePlayersActivity extends Activity {  
  private Button doneButton;
  private Button cancelButton;
  private List<ParseUser> playerList = new ArrayList<ParseUser>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayersmanage);
    setupListViews();
    setupButtonCallbacks();
  }
    
  private void setupListViews() {
    setParseUserList();
  }
  
  private void setParseUserList() {
    final ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.selectKeys(Arrays.asList("username"));
    query.findInBackground(new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> userList, ParseException e) {
            if (e == null) {
                final String[] usernameList = new String[userList.size()];
                Log.d("User List", "Retrieved " + userList.size());
                for (int i = 0; i < userList.size(); i++) {
                    Log.d("data", "Retrieved User: "
                            + userList.get(i).getString("username"));
                    usernameList[i] = userList.get(i).getString("username");
                }
                //will write method later to take playerList and capture the selected players for the specific game
                //right now playerList is a placeholder only and isn't called yet
                playerList = userList;
                setUsernameListView(usernameList);
            } else {
                Log.w("error", "game retreival failure");
                /* setGameInfo(game); */
            }
        }
    });
  }

  private void setUsernameListView(String[] usernameList) {
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, usernameList);
    final ListView playerListView = (ListView) findViewById(R.id.listView_players);
    playerListView.setAdapter(adapter);
    playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
  }
  
//buttons don't save anything at this point, need to pass info first, this slice was only to get list set up in view with 
  //checkboxes....next slice will be methods to pass info around
   private void setupButtonCallbacks() {
    //my guess thinking about players list with checkboxes
    // selectplayerCheckbox = (Checkbox) findViewById(R.id.manageplayersCheckbox_addplayer)   
     
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
