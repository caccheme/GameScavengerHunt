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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList); 
            ListView listView = (ListView) findViewById(R.id.listView1);
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
              final String new_item = userInput.getText().toString().trim();
//              Is there a reason you're not using the getUserInput function you defined for this?
              final JSONArray items = game.getJSONArray("itemsList"); 
              if (items != null) {
                items.put(new_item); 
                game.put("itemsList", items);   
                game.saveInBackground();
                startActivity(getIntent());
                finish();
              }  
              else { 
                final JSONArray new_items = new JSONArray();
                new_items.put(new_item);
                game.put("itemsList", new_items);
                game.saveInBackground();
                startActivity(getIntent());
                finish();
//                If code repeats in both branches of a conditional, move that code outside 
//                the conditional. There's really only one conditional 
//                part -- whether you use an existing array or make a new one.
//                The simplest way to set new_items to a if else value is to use 
//                the ternary operator. See 
//                http://urbanhonking.com/ideasfordozens/2006/03/01/learns_to_use_the_ternary_oper/
             }
            }    
            else{
              Context context = getApplicationContext();
              CharSequence text = "Item didn't save, try again.";
              final int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
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
        Intent i = new Intent(GameItemsActivity.this, MainMenuActivity.class);
        GameItemsActivity.this.startActivity(i);
        finish();
      }
    });
  }    

}
