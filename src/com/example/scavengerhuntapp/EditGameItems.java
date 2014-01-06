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
import com.parse.SaveCallback;

public class EditGameItems extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgameitems_layout);
        setupButtonCallbacks();
        listCurrentItems(); 
    }

    private void listCurrentItems(){
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList); 
                ListView listView = (ListView) findViewById(R.id.listview_items);
                listView.setAdapter(adapter);  
              }
            }
            else {
              CharSequence text = "There was a problem. Please hold.";
              int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
              startActivity(getIntent());
              finish();
            }
          }
        });  
      }

    private void setupButtonCallbacks() {
        final EditText userInput = (EditText) findViewById(R.id.enterText);
        final Button addItemButton = (Button) findViewById(R.id.button_addItem);
        addItemButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
            final Intent i = getIntent();    
            query.getInBackground(i.getStringExtra("GameId"), new GetCallback<ParseObject>() {
              @Override
              public void done(ParseObject game, ParseException e) {
                if (e == null) {
                  final String new_item = userInput.getText().toString().trim(); 
                  JSONArray items = game.getJSONArray("itemsList"); 
                  if (items != null) {
                    items.put(new_item); 
                    game.put("itemsList", items);   
                    game.saveInBackground();
                    startActivity(getIntent());
                    finish();
                  }  
                  else { 
                    JSONArray new_items = new JSONArray();
                    new_items.put(new_item);
                    game.put("itemsList", new_items);
                    game.saveInBackground();                    
                    startActivity(getIntent());
                    finish();
                 }
                }    
                else{
                  Context context = getApplicationContext();
                  CharSequence text = "Item didn't save, try again.";
                  int duration = Toast.LENGTH_SHORT;                     
                  Toast.makeText(context, text, duration).show();
                  Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));                 
                  startActivity(getIntent());
                  finish();
                }
              }  
            });    
          } 
        }); 
        final Button saveGameButton = (Button) findViewById(R.id.button_save);
        saveGameButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                final String gameId = extras.getString("GameId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
                query.getInBackground(gameId, new GetCallback<ParseObject>() {
                    public void done(final ParseObject game, ParseException e) {
                        if (e == null) {
                            game.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("Game Update", "Game Updated!");
                                         launchGameView(game.getObjectId());
                                    } else {
                                        Log.d("Game Creation", "Error creating game: "
                                                + e);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

      }    
    
	private void launchGameView(String gameId) {
	  Intent intent = new Intent(EditGameItems.this, ViewGame.class);
	  intent.putExtra("GameId", gameId);
	  Log.d("GameId", "game id is " + gameId);
	  startActivity(intent);
	}
    
}