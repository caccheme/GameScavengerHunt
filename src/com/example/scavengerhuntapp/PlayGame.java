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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PlayGame extends Activity {
  final List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String GameId = extras.getString("GameId");
        getGame();
        setContentView(R.layout.play_game);
        setupButtonCallbacks(GameId);
    }

    private void setupButtonCallbacks(final String GameId) {
        final Button updateItemsFoundButton = (Button) findViewById(R.id.button_update);
        updateItemsFoundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//    update the FoundItem table with game info, player info and item info
//            	keep that item 'checked' and show the PlayGame view upon update
            }
         });

        final Button menuButton = (Button) findViewById(R.id.button_back);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            PlayGame.this.startActivity(new Intent(PlayGame.this, MainMenuActivity.class));
            finish();
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
              final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, itemsList);
              final ListView listView = (ListView) findViewById(R.id.listview_gameItems);
              listView.setAdapter(adapter);
              listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
        String GameId = extras.getString("GameId");
        Log.d("Game Info", "Game View ID is " + GameId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(GameId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("name"));
                    
                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
                    TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);

                    gameName.setText(game.getString("name"));                   
                    endDatetime.setText(game.getDate("end_datetime").toString());
                    setItemList(game);
                } 
                
                else {
                    Log.w("error", "failed to retrieve game");
                }
            }
        });
    }
  
}