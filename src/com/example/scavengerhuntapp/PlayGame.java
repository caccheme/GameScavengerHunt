package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PlayGame extends Activity {

    ParseObject game;
    List<ParseObject> userFoundItems;
    final ParseUser currentUser = ParseUser.getCurrentUser();
    int currentScore;
    int playerScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("GameId");
        setContentView(R.layout.play_game_layout);
        getGame(gameId);
        setupButtonCallbacks(gameId);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.game_hub, menu);
//        return true;
//    }
    
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

    
        
    
    private void initializeItemListView() {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1);
      final ListView itemListView = (ListView) findViewById(R.id.listview_remainingItems);
      itemListView.setAdapter(adapter);
      
      final Date endDatetime = game.getDate("end_datetime");
      if (new Date().before(endDatetime)) {   
	      itemListView
	       .setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent,
		                final View view, int position, long id) {
		            Log.d("Dialog Values",
		                    "Position is " + String.valueOf(position)
		                            + ". View is " + view.toString()
		                            + ". ID is " + String.valueOf(id));
		            final String itemName = (String) parent
		                    .getItemAtPosition(position);
		            Bundle item = new Bundle();
		            item.putString("name", itemName);
		            final DialogFragment itemFoundDialogFragment = new ItemFoundDialogFragment();
		            itemFoundDialogFragment.setArguments(item);
		            itemFoundDialogFragment.show(getFragmentManager(), "itemFound");
		        }
	        });
      }
	  if (new Date().after(endDatetime)) { //game over bc of time
//	      do the check of scores for all players of this game here
		  getWinner();
		  itemListView
	       .setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent,
		                final View view, int position, long id) {
		            final DialogFragment gameOverDialogFragment = new GameOverDialogFragment();
		            gameOverDialogFragment.show(getFragmentManager(), "Game Over");
		        }
	        });	 
	    } 
    }
    
//    create userId array...beginning of tie game logic for when game time runs out
    private void getWinner() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GamePlayer");
        Bundle extras = getIntent().getExtras();
        final String GameId = extras.getString("GameId");
        query.whereEqualTo("game", GameId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> playerList, ParseException e) {
                if (e == null) {
                    final String[] playerIdArray = new String[playerList.size()];
                    for (int i = 0; i < playerList.size(); i++) {
                        Log.d("data", "Retrieved User: "
                                + playerList.get(i).getParseObject("user").getObjectId());
                        playerIdArray[i] = playerList.get(i).getString("objectId");
                        getPlayerScore(playerList);
// some check for biggest score and then save to 'winner'
                        game.put("winner", playerList.get(i).getParseObject("user")); //user instead of currentUser
                        game.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("Play Game",
                                            "Winner saved to game "
                                                    + game.getString("name") + "!");
                 //send winner notification to other game players here b/c game won here by most found
                                } else {
                                    Log.d("Play Game", "Error in saving winner: " + e);
                                }
                            }
                        });

                    }
                } else {
                    Log.w("Parse Error", "game retreival failure");
                }
            }
        });
                  
       
   }
  
   private void getPlayerScore(List<ParseObject> userList) { //pass parameter userId to check each player's score?
//       final JSONArray totalItems = game.getJSONArray("itemsList");
       for (int i = 0; i < userList.size(); i++) {
           final ParseQuery<ParseObject> query = ParseQuery.getQuery("FoundItem");
           query.whereEqualTo("game", game);
           query.whereEqualTo("user", userList.get(i).getString("objectId")); //event for this player
           query.include("user");
           query.findInBackground(new FindCallback<ParseObject>() {
               public void done(final List<ParseObject> foundItems, ParseException e) {
                   if (e == null) {
                       for (final ParseObject foundItem : foundItems) {
//                            map data so can group by user and then count items
//                             totalItems.length();                                                      
                             final String username = foundItem.getParseObject("user").getString("username");
                           final String item = foundItem.getString("item");
                           final Map<String, List<String>> finds = new HashMap<String, List<String>>();
                            
//                           need to add User for each different user
                           addUser(finds, username);
                           addItem(finds, username, item);
                           getScores(finds).size();  //.size(); = number of events total...need to do this query for each player and compare these            
                           Log.d("Found Scores", "scores:" +
                             getScores(finds).size() + " for user: " + username);

                          	
//                         findHighScores(getScores(finds));
                       }
                      
                   } else {
                       Log.w("Parse Error", "player username retrieval failure");
                   }
               }
           });   
       }
   }
   
//    private void findHighScores(List<Integer> scores) {
//    	final List<Integer> largest = null;
//    	final Integer max = 0;
//    	  for (final int score : scores ) {
//    	   	for(int i =0; i < scores.size(); i++) {
//    	      if(scores.get(i) > largest.get(i)) {
//    	        max = max + 1 ;
//    	      }
//    	    }
//          }
//    }

// private void findHighScorer(final Map<String, List<String>> finds, final String username, final String item) {
//	final Entry<String, List<String>> maxEntry = null;
//
//	for(Entry<String,Integer> entry : finds.entrySet()) {
//	    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
//	        maxEntry = entry;
//	    }
//	}
//
//	for (Map.Entry<String, String> entry : map.entrySet())
//	{
//	    System.out.println(entry.getKey() + "/" + entry.getValue());
//	}
//
////	Map<Integer, String> map = ...
//
//	Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
//
//	while (it.hasNext()) {
//	  Map.Entry<Integer, String> entry = it.next();
//
//	  // Remove entry if key is null or equals 0.
//	  if (entry.getKey() == null || entry.getKey() == 0) {
//	    it.remove();
//	  }
//	}
//	
//	// maxEntry should now contain the maximum,
//}
   
    private List<Integer> getScores(final Map<String, List<String>> finds) {
        final List<Integer> scores = new ArrayList<Integer>();
        for (final Map.Entry<String, List<String>> entry : finds.entrySet()) {
            scores.add(entry.getValue().size());
        }
        return scores;
    }
   
    private void addUser(final Map<String, List<String>> finds, final String name) {
        finds.put(name, new ArrayList<String>());
    }
   
    private void addItem(final Map<String, List<String>> finds, final String username, final String item) {
        finds.get(username).add(item);
        addUser(finds, username);
    }

 
    // end of time running out and look for winner tie game logic
    
    
    private void deleteAlreadyFoundItems() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("FoundItem");
        query.whereEqualTo("game", game);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                    userFoundItems = foundItems;
                    for (final String listItem : getItemListViewItems()) {
                        for (final ParseObject foundItem : userFoundItems) {
                            if (listItem.equals(foundItem.getString("item"))) {
                            	markFoundItem(listItem);
                            }
                        }
                    }
                } else {
                    Log.w("Parse Error", "player username retrieval failure");
                }
            }
        });
    }

    private ArrayList<String> getItemListViewItems() {
        final ArrayList<String> itemList = new ArrayList<String>();
        final ArrayAdapter<String> adapter = getItemAdapter();
        for (int i = 0; i < (adapter.getCount()); i++) {
            itemList.add(adapter.getItem(i));
        }
        return itemList;
    }

    private void setItemList(ParseObject game) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        final Intent i = getIntent();
        final Context context = this;
        query.getInBackground(i.getStringExtra("GameId"), new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject game, ParseException e) {
            if (e == null) {
              final Date startDatetime = game.getDate("start_datetime");
              if (new Date().after(startDatetime)) {
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
	                
	                setScore(game.getJSONArray("itemsList"));
	                deleteAlreadyFoundItems();
	             }  
              }
              
              if (new Date().before(startDatetime)) {
	              JSONArray items = game.getJSONArray("itemsList");
	              if (items != null) {
	                final List<String> itemsList = new ArrayList<String>();
	                itemsList.add("Game hasn't started yet.");	                
	                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
	                final ListView listView = (ListView) findViewById(R.id.listview_remainingItems);
	                listView.setAdapter(adapter);	                
	             }  
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
                
    private void setScore(final JSONArray totalItems) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("FoundItem");
        query.whereEqualTo("game", game);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                    userFoundItems = foundItems;
                    
                    currentScore = userFoundItems.size();
                    final TextView scoreView = (TextView) findViewById(R.id.text_score);
                    final TextView totalPointsView = (TextView) findViewById(R.id.text_totalPoints);
                    scoreView.setText(String.valueOf(currentScore));
                    totalPointsView.setText(" out of " + totalItems.length());
                } else {
                    Log.w("Parse Error", "player username retrieval failure");
                }
            }
        });
    }
    
    private ArrayAdapter<String> getItemAdapter() {
        final ListView itemListView = (ListView) findViewById(R.id.listview_remainingItems);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) itemListView
                .getAdapter();
        return adapter;
    }
    
    public void onFoundItemDialog(final String name) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereExists("winner");
        query.getInBackground(game.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject currentGame, ParseException e) {
                if (e == null) {
                    if(currentGame == null){
                    	sendFoundItemToParse(name);
                        markFoundItem(name);
                        currentScore++;
                        final TextView scoreView = (TextView) findViewById(R.id.text_score);
                        scoreView.setText(String.valueOf(currentScore));
                   
                        checkIfWinner();
                    }
                    else{
                    	launchGameAlreadyWonDialogFragment();
                    }
                } else {
                    Log.w("error", "game retrieval error");
                }
            }

        });
    }
                         
    private void checkIfWinner() {
    	final JSONArray totalItems = game.getJSONArray("itemsList");
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("FoundItem");
        query.whereEqualTo("game", game);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                    if (currentScore == totalItems.length()) {                    	     		                 
     		           launchWinnerDialogFragment();
     		           setWinnerInfo();
                    };
                } else {
                    Log.w("Parse Error", "player username retrieval failure");
                }
            }
        });
    }
    
    private void launchWinnerDialogFragment() {
    	final DialogFragment winnerDialogFragment = new WinnerDialogFragment();     		            
         winnerDialogFragment.show(getFragmentManager(), "Winner");
    }
    
    private void launchGameAlreadyWonDialogFragment() {
    	final DialogFragment gameAlreadyWonDialogFragment = new GameAlreadyWonDialogFragment();     		            
         gameAlreadyWonDialogFragment.show(getFragmentManager(), "Game Already Won");
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

    protected void markFoundItem(final String item) {
    	final ArrayAdapter<String> adapter = getItemAdapter();
        adapter.remove(item);
    	adapter.add((item + " \u2713"));
        adapter.notifyDataSetChanged();
    }
    
    private void setWinnerInfo() {
        	game.put("winner", currentUser);
            game.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Play Game",
                                "Winner Saved to game named "
                                        + game.getString("name") + "!");
// send winner notification to other game players here b/c game won here by current user
                    } else {
                        Log.d("Play Game", "Error in saving winner: " + e);
                    }
                }
            });   
    }
        
}