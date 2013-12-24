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
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject gameInfo, ParseException e) {
        if (e == null) {
          JSONArray items = gameInfo.getJSONArray("itemsList"); 
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
            ListView listView = (ListView) findViewById(R.id.listView1);
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
  
  private void setupButtonCallbacks() {
    final EditText userInput = (EditText) findViewById(R.id.enterText);
    final Button addItemButton = (Button) findViewById(R.id.manageItemsButton_addItem);
    addItemButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        final Intent i = getIntent();    
        query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject gameInfo, ParseException e) {
            if (e == null) {
              final String new_item = userInput.getText().toString().trim(); 
              JSONArray items = gameInfo.getJSONArray("itemsList"); 
              if (items != null) {
                items.put(new_item); 
                gameInfo.put("itemsList", items);   
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent()); 
              }  
              else { 
                JSONArray new_items = new JSONArray();
                new_items.put(new_item);
                gameInfo.put("itemsList", new_items);
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent());
             }
            }    
            else{
              Context context = getApplicationContext();
              CharSequence text = "Item didn't save, try again.";
              int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              finish();
              startActivity(getIntent());
            }
          }  
        });    
      } 
    }); 
    final Button doneButton = (Button) findViewById(R.id.manageItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        final Intent i = getIntent();
        final String gameInfoId = i.getStringExtra("gameInfoId");
        Intent b = new Intent(GameItemsActivity.this, GamePlayersActivity.class);
        b.putExtra("gameInfoId", gameInfoId);
        GameItemsActivity.this.startActivity(b);
      } 
    });
    final Button cancelButton = (Button) findViewById(R.id.manageItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        Intent i = new Intent(GameItemsActivity.this, MainMenuActivity.class);
        GameItemsActivity.this.startActivity(i);
      }
    });
  }    

}
