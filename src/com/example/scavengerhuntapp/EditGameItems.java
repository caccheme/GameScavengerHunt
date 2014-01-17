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
import android.widget.AdapterView;
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
              final JSONArray items = game.getJSONArray("itemsList"); 
              if (items != null) {        
                List<String> firstItemsList = new ArrayList<String>();
                final int length = items.length() ;
                
                for(int i = 0; i < length; i++){
                  try{             
                    firstItemsList.add(items.getString(i));
                  }
                  catch (Exception exc) {
                    Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
                  }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, firstItemsList); 
                ListView listView = (ListView) findViewById(R.id.listview_items);
                listView.setAdapter(adapter);

 //  delete item from JSON Array ItemsList when clicked....still thinking on this and will finish with more time (1/17/14)                
//                final List<String> list = firstItemsList ; //new ArrayList<String>();
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//                	  final String item = (String) parent.getItemAtPosition(position);
//                	    for(int i = 0; i < length; i++){
//                            try{
//	                             list.remove(items.getString(i));
//	                    	     list.remove(item);
//	                    
//                            }
//                            catch (Exception exc) {
//                            	Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
//                            }
//                         }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list); 
//                        ListView listView = (ListView) findViewById(R.id.listview_items);
//                        listView.setAdapter(adapter);
////                	    final ArrayAdapter<String> adapter = getItemAdapter();
//                	    adapter.remove(item);
//                        adapter.notifyDataSetChanged();
//                	 }
//                  });
              }
              
            } else {
              Toast.makeText(context, 
            		  "There was a problem.", Toast.LENGTH_SHORT).show();
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