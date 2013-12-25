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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ViewGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String gameInfoId = extras.getString("gameInfoId");
        getGame();
        setContentView(R.layout.viewgame_layout);
        setupButtonCallbacks(gameInfoId);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.viewgame_menu, menu);
//        return true;
//    }

    private void setupButtonCallbacks(final String gameInfoId) {
        final Button editGameButton = (Button) findViewById(R.id.button_editGame);
        editGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(ViewGame.this, EditGame.class);
              intent.putExtra("gameInfoId", gameInfoId);
              Log.d("GameInfoId", "game id is " + gameInfoId);
              startActivity(intent);
            }
        });
    }

    private void setItemList(ParseObject gameInfo) {
//      I have "itemsList" column with JSONarray of items for each gameInfo record, so I pull from that array
//      rather than from join table like GamePlayer to get playerlist above
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
              ListView listView = (ListView) findViewById(R.id.listview_gameItems);
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

    private void getGame() {
        Bundle extras = getIntent().getExtras();
        String gameInfoId = extras.getString("gameInfoId");
        Log.d("Game Info", "Game View ID is " + gameInfoId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        query.getInBackground(gameInfoId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject gameInfo, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + gameInfo.getString("gameName"));

//                    setupUsernameListView();
                    
                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
                    TextView startDatetime = (TextView) findViewById(R.id.text_startDatetime);
                    TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);

                    gameName.setText(gameInfo.getString("gameName"));
                    startDatetime.setText(gameInfo.getString("start_datetime"));
                    endDatetime.setText(gameInfo.getString("end_datetime"));
                    setItemList(gameInfo);                  
//   call playerlist here ...                 setPlayerList(gameInfo);
                    
                } else {
                    Log.w("error", "failed to retrieve game");
                }
            }
        });
    }
   
    
}