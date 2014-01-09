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

public class JoinGamesDraft extends Activity {
	ParseUser currentUser = ParseUser.getCurrentUser();
	final List<ParseObject> gameList = new ArrayList<ParseObject>();   
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setGameList();  
        setContentView(R.layout.current_games_list);
        setupButtonCallbacks();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.invited_game, menu);
//        return true;
//    }

    private void setupButtonCallbacks() {
        final Button menuButton = (Button) findViewById(R.id.CurrentgameslistButton_menu);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            JoinGamesDraft.this.startActivity(new Intent(JoinGamesDraft.this, MainMenuActivity.class));
            finish();
          }
        });
      }
        public void onResume() {
        super.onResume();
    }

//    long and wearying attempt to get the list filtered that ddidn't work..keeping it for history and will delete comments later
//        private void setGameList() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("GamePlayer");
//        query.whereEqualTo("user", currentUser);
//        query.selectKeys(Arrays.asList("game"));
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            public void done(ParseObject gamePlayer, ParseException e) {
//                if (e == null) {
//                  String[] gameIds = new String[gamePlayer.getJSONArray("game").length()]; //where showing nullpointerexception  
//                  JSONArray gameInfo = gamePlayer.getJSONArray("game");
//                    for (int i = 0; i < gameInfo.length(); i++) {
//                        String gameId = null;
//                         try {
//                             gameId = gameInfo.getJSONObject(i).getString("objectId");
//                         } catch (JSONException e1) {
//                             // TODO Auto-generated catch block
//                             e1.printStackTrace();
//                         }
//                          gameIds[i] = gameId;
//                    }
//
//                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//                      query.whereContainedIn("objectId",
//                              Arrays.asList(gameIds));
//                      query.findInBackground(new FindCallback<ParseObject>() {
//                          public void done(List<ParseObject> games, ParseException e) {
//                              if (e == null) {
//                                  for (ParseObject game : games) {
//                                      Log.d("Parse GameName",
//                                              "Retrieved Game " + game.getString("name"));
//                                      addToListView(game.getString("name"),
//                                              getGameAdapter());
//                                  }
//                              } else {
//                                  Log.w("Parse Error", "player username retreival failure");
//                              }
//                          }
//                      });
//
//                } else {
//                    Log.w("Parse Error", "game retreival failure");
//                }
//            }
//        });
//    }
//
//    private void addToListView(String item, ArrayAdapter<String> adapter) {
//        adapter.add(item);
//        adapter.notifyDataSetChanged();
//    }
//
//    private ArrayAdapter<String> getGameAdapter() {
//        final ListView gameListView = (ListView) findViewById(R.id.listview_CurrentGames);
//        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) gameListView
//                .getAdapter();
//        return adapter;
//    }

//    old and true method below

}