package com.example.scavengerhuntapp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

public class MyGamesList extends Activity {

    ParseUser currentUser = ParseUser.getCurrentUser();
    List<ParseObject> myGames;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygameslist_layout);
        setMyGamesListView();
        setupButtonCallbacks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mygameslist_menu, menu);
        return true;
    }

    private void setMyGamesListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final ListView myGamesListview = (ListView) findViewById(R.id.listview_myGames);
        myGamesListview.setAdapter(adapter);
        findMyCreatedGames();
        myGamesListview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            final View view, int position, long id) {
                        final ParseObject gameInfo = myGames.get(position);
                        launchGameView(gameInfo.getObjectId());
                    }
                });
    }

    private void launchGameView(String gameInfoId) {
        Intent intent = new Intent(MyGamesList.this, ViewGame.class);
        intent.putExtra("gameInfoId", gameInfoId);
        Log.d("GameInfoId", "game id is " + gameInfoId);
        startActivity(intent);
    }

    private void addToListView(ParseObject gameInfo, ArrayAdapter<String> adapter) {
        adapter.add(gameInfo.getString("gameName"));
        adapter.notifyDataSetChanged();
    }

    private void findMyCreatedGames() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        query.whereEqualTo("gameCreator", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> games, ParseException e) {
                if (e == null) {
                    for (final ParseObject gameInfo : games) {
                        Log.d("Game Info",
                                "Game name is " + gameInfo.getString("gameName"));
                        addToListView(gameInfo, getMyGamesAdapter());
                    }
                    myGames = games;
                } else {
                    Log.w("error", "game retreival failure");
                }
            }
        });
    }

    private ArrayAdapter<String> getMyGamesAdapter() {
        final ListView myGamesListView = (ListView) findViewById(R.id.listview_myGames);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) myGamesListView
                .getAdapter();
        return adapter;
    }
    
    private void setupButtonCallbacks() {
      final Button menuButton = (Button) findViewById(R.id.mygameslistButton_menu); 
      menuButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
          Intent i = new Intent(MyGamesList.this, MainMenuActivity.class);
          MyGamesList.this.startActivity(i);
        }
      });
    }
}