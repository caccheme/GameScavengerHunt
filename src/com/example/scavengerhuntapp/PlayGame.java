package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PlayGame extends Activity {
  final List<ParseObject> itemList = new ArrayList<ParseObject>();
  ParseObject game;
  List<ParseObject> userFoundItems;
  final ParseUser currentUser = ParseUser.getCurrentUser();
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("GameId");
        setContentView(R.layout.play_game_layout);
        getGame(gameId);
        setupButtonCallbacks(gameId);
    }

    private void setupButtonCallbacks(final String GameId) {
        final Button menuButton = (Button) findViewById(R.id.button_back);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            PlayGame.this.startActivity(new Intent(PlayGame.this, MainMenuActivity.class));
            finish();
          }
        });
    }    

    private void getGame(final String gameId) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(ParseObject currentGame, ParseException e) {
                if (e == null) {
                    game = currentGame;

                    final TextView gameName = (TextView) findViewById(R.id.title_gameName);
                    final TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);
                    gameName.setText(game.getString("name"));
                    endDatetime.setText(game.getDate("end_datetime").toString());

                    initializeItemListView();
                    setItemList(game);
                } else {
                    Log.w("error", "game retrieval failure");
                }
            }
        });
    }
    
    private void setItemList(ParseObject game) {
      final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
      final Intent i = getIntent();
      final Context context = this;
      query.getInBackground(i.getStringExtra("GameId"), new GetCallback<ParseObject>() {
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
              final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
              final ListView listView = (ListView) findViewById(R.id.listview_remainingItems);
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
    
    private void initializeItemListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final ListView itemListView = (ListView) findViewById(R.id.listview_remainingItems);
        itemListView.setAdapter(adapter);
        itemListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            final View view, int position, long id) {
                        Log.d("Values",
                                "Position is " + String.valueOf(position)
                                        + ". View is " + view.toString()
                                        + ". ID is " + String.valueOf(id));
                        
                        final String itemName = (String) parent.getItemAtPosition(position);
                        Bundle item = new Bundle();
                        item.putString("name", itemName);
                        final DialogFragment itemFoundDialogFragment = new ItemFoundDialogFragment();
                        itemFoundDialogFragment.setArguments(item);
                        itemFoundDialogFragment.show(getFragmentManager(), "itemFound");
                    }
                });

    }

    public void onFoundItemDialog(final String name) {
    	sendFoundItemToParse(name);
    }

    private void sendFoundItemToParse(final String name) {
        final ParseObject foundItem = new ParseObject("FoundItem");
        foundItem.put("game", game);
        foundItem.put("user", currentUser);
        foundItem.put("item", name);
        foundItem.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("PlayGame", "Item Found!");
                } else {
                    Log.d("PlayGame", "Error creating found item: " + e);
                }
            }
        });
    }

  
}