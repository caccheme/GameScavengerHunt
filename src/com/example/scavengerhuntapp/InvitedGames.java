package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InvitedGames extends Activity {

    ParseUser currentUser = ParseUser.getCurrentUser();
    List<ParseObject> currentGames = new ArrayList<ParseObject>();
    List<ParseObject> gameIdsList = new ArrayList<ParseObject>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_games_list);
        setCurrentGamesListView();
        setupButtonCallbacks();
    }

// @Override
// public boolean onCreateOptionsMenu(Menu menu) {
// // Inflate the menu; this adds items to the action bar if it is present.
// getMenuInflater().inflate(R.menu.mygameslist_menu, menu);
// return true;
// }

    private void setCurrentGamesListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final ListView currentGamesListview = (ListView) findViewById(R.id.listview_CurrentGames);
        currentGamesListview.setAdapter(adapter);
        findCurrentInvitedGames();
        currentGamesListview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            final View view, int position, long id) {
                        final ParseObject Game = currentGames.get(position);
                        launchGameView(Game.getObjectId());
                    }
                });
    }

    private void launchGameView(String GameId) {
        Intent intent = new Intent(InvitedGames.this, PlayGame.class);
        intent.putExtra("GameId", GameId);
        Log.d("GameId", "Game id is " + GameId);
        startActivity(intent);
    }

    private void findCurrentInvitedGames() {
      final ParseQuery<ParseObject> fquery = ParseQuery.getQuery("GamePlayer");
      fquery.whereEqualTo("user", currentUser);
      fquery.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> gameIdList, ParseException e) {
               	 ParseQuery<ParseObject> squery = ParseQuery.getQuery("Game");
				      squery.whereMatchesKeyInQuery("objectId", "game", fquery);        
				      squery.findInBackground(new FindCallback<ParseObject>() {
				          public void done(List<ParseObject> games, ParseException e) {
				              if (e == null) {
				                  for (ParseObject game : games) {
				                      Log.d("Parse GameName",
				                              "Retrieved Game Named: " + game.getString("name"));
				                  addToListView(game, getCurrentGamesAdapter());                    
				                  }
				              } else {
				                  Log.w("Parse Error", "game name retreival failure");
				              }
				        }
				   }); 
              }           
      });
    }

    private void addToListView(ParseObject game, ArrayAdapter<String> adapter) {
        adapter.add(game.getString("name"));
        adapter.notifyDataSetChanged();
    }
    
    private ArrayAdapter<String> getCurrentGamesAdapter() {
        final ListView currentGamesListView = (ListView) findViewById(R.id.listview_CurrentGames);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) currentGamesListView
                .getAdapter();
        return adapter;
    }
           
    private void setupButtonCallbacks() {
      final Button menuButton = (Button) findViewById(R.id.CurrentgameslistButton_menu);
      menuButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          InvitedGames.this.startActivity(new Intent(InvitedGames.this, MainMenuActivity.class));
          finish();
        }
      });
    }
}