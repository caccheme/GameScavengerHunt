package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewGame extends Activity {
  final List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("gameId");
        getGame();
        setContentView(R.layout.viewgame_layout);
//        setupButtonCallbacks(gameId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewgame_menu, menu);
        return true;
    }

//    private void setupButtonCallbacks(final String gameId) {
//        final Button editGameButton = (Button) findViewById(R.id.button_editGame);
//        editGameButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              Intent intent = new Intent(ViewGame.this, EditGame.class);
//              intent.putExtra("gameId", gameId);
//              Log.d("gameId", "game id is " + gameId);
//              startActivity(intent);
//            }
//        });
//    }

    private void setItemList(ParseObject game) {
      final ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
      final Intent i = getIntent(); 
      final Context context = this;
      query.getInBackground(i.getStringExtra("gameId"), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject game, ParseException e) {
          if (e == null) {
            JSONArray items = game.getJSONArray("itemsList"); 
            if (items != null) {        
              final List<String> itemsList = new ArrayList<String>();
              for(int i = 0; i < items.length(); i++){
                try{             
                  itemsList.add(items.getString(i));
                }
                catch (Exception exc) {
                  Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
                }
              }
              ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList); 
              ListView listView = (ListView) findViewById(R.id.listview_gameItems);
              listView.setAdapter(adapter);  
            }
          }
          else {
            CharSequence text = "There was a problem. Please hold.";
            int duration = Toast.LENGTH_SHORT;                     
            Toast.makeText(context, text, duration).show();
            finish();
            startActivity(getIntent()); 
          }
        }
      });  
    }

    private void getGame() {
        Bundle extras = getIntent().getExtras();
        String gameId = extras.getString("gameId");
        Log.d("Game Info", "Game View ID is " + gameId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("gameName"));

                    setupUsernameListView();
                    
                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
                    TextView startDatetime = (TextView) findViewById(R.id.text_startDatetime);
                    TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);

                    gameName.setText(game.getString("gameName"));
                    startDatetime.setText(game.getString("start_datetime"));
                    endDatetime.setText(game.getString("end_datetime"));
                    setItemList(game);                  
                    setPlayerList();
                    
                } else {
                    Log.w("error", "failed to retrieve game");
                }
            }
        });
    }
   
  private void setPlayerList() {
      ParseQuery<ParseObject> query = ParseQuery.getQuery("gamePlayer");
      Bundle extras = getIntent().getExtras();
      final String gameId = extras.getString("gameId");
      query.whereEqualTo("gameId", gameId);
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> playerList, ParseException e) {
              if (e == null) {
                  Log.d("User List", "Retrieved " + playerList.size()
                          + " player(s)");
                  getUsernameList(playerList);
              } else {
                  Log.w("Parse Error", "game retreival failure");
              }
          }
      });
    }
    
  private void getUsernameList(List<ParseObject> playerList) {
      for (int i = 0; i < playerList.size(); i++) {
        playerList.get(i).getParseObject("userId").fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                      @Override
                      public void done(ParseUser User, ParseException e) {
                          if (e == null) {
                              Log.d("Username", "Retrieved User "
                                      + User.getString("username"));
                              addToListView(User.getString("username"),
                                      getPlayerAdapter());
                          } else {
                              Log.w("Parse Error", "game retreival failure");

                          }
                      }
                  });
      }
  }

  private void setupUsernameListView() {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1);
      final ListView playerListView = (ListView) findViewById(R.id.listview_currentPlayers);
      playerListView.setAdapter(adapter);
  }

  private void addToListView(String item, ArrayAdapter<String> adapter) {
      adapter.add(item);
      adapter.notifyDataSetChanged();
  }

  private ArrayAdapter<String> getPlayerAdapter() {
      final ListView playerListView = (ListView) findViewById(R.id.listview_currentPlayers);
      final ArrayAdapter<String> adapter = (ArrayAdapter<String>) playerListView
              .getAdapter();
      return adapter;
  }
  
}