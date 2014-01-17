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

public class GameItemsActivity extends Activity {  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameitems);
    listCurrentItems();
    setupButtonCallbacks();
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
				   setupItemListView(itemsList);
				   }
			   }
		   else {
			   showToast(context);
			   startActivity(getIntent());
			   finish();
			   }
		   }
	   });
   }
  
  private void showToast(Context context) {
	    CharSequence text = "There was a problem with this action.";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
  }
  
  private void setupItemListView(List<String> itemsList) {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, itemsList);
      final ListView listView = (ListView) findViewById(R.id.listView1);
      listView.setAdapter(adapter);      
  }
  
  private String getUserInput(int id) {
      EditText input = (EditText) findViewById(id);
      return input.getText().toString();
  }

  
  private void setupButtonCallbacks() {
    final Button addItemButton = (Button) findViewById(R.id.manageItemsButton_addItem);
    addItemButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        final Intent i = getIntent();    
        query.getInBackground(i.getStringExtra("GameId"), new GetCallback<ParseObject>() {
          @Override
          final public void done(ParseObject game, ParseException e) {
            if (e == null) {
              final JSONArray items = game.getJSONArray("itemsList");	
              if (items != null) {
            	  addItemsToArray(items, game);
              }  
              else { 
                final JSONArray new_items = new JSONArray();
                addItemsToArray(new_items, game);
             }
            }    
            else{
              Context context = getApplicationContext();
              showToast(context);
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              startActivity(getIntent());
              finish();
            }
          }  
        });    
      } 
    }); 
    final Button doneButton = (Button) findViewById(R.id.manageItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final Intent i = getIntent();
        final String GameId = i.getStringExtra("GameId");
        Intent b = new Intent(GameItemsActivity.this, GamePlayersActivity.class);
        b.putExtra("GameId", GameId);
        GameItemsActivity.this.startActivity(b);
        finish();
      } 
    });
    final Button cancelButton = (Button) findViewById(R.id.manageItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        GameItemsActivity.this.startActivity(new Intent(GameItemsActivity.this, MainMenuActivity.class));
        finish();
      }
    });
  }    

  private void addItemsToArray(JSONArray items, ParseObject game){
      final String new_item = getUserInput(R.id.enterText); 
	  items.put(new_item); 
      game.put("itemsList", items == null ? null : items);   
      game.saveInBackground();
      startActivity(getIntent());
      finish();
  }
  
}