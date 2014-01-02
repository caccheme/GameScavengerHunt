package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditGamePlayers extends Activity {

    private List<ParseObject> gamePlayers = new ArrayList<ParseObject>();
    private List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgameplayers_layout);
        setupButtonCallbacks();
        getGame();
    }

    private void setupButtonCallbacks() {
        final Button saveGameButton = (Button) findViewById(R.id.button_save);
        saveGameButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                final String GameId = extras.getString("GameId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
                query.getInBackground(GameId, new GetCallback<ParseObject>() {
                    public void done(final ParseObject game, ParseException e) {
                        if (e == null) {
                          game.saveInBackground(new SaveCallback() {
                             public void done(ParseException e) {
                                    if (e == null) {
                                    	updateGamePlayers(getChosenPlayerList(), game);
                                    	Log.d("Game Update", "Game Updated!");
                                        ScavengerHuntApplication.getInstance().showToast(EditGamePlayers.this, "Game Updated!");
                                         Intent newintent = new Intent(EditGamePlayers.this, ViewGame.class);
                                         newintent.putExtra("GameId", GameId);
                                         Log.d("GameId", "Game id is " + GameId);
                                         startActivity(newintent); 
                                         
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

    private void updateGamePlayers(List<ParseUser> chosenPlayerList, ParseObject game) {    
    	for (ParseObject item : gamePlayers) {
            item.deleteInBackground(); }

        for (int i = 0; i < chosenPlayerList.size(); i++) {
            ParseUser user = chosenPlayerList.get(i);
            Log.d("Player", user.toString());
            final Intent intent = getIntent();
            ParseObject gamePlayer = new ParseObject("GamePlayer");
            gamePlayer.put("user", user);
            gamePlayer.put("game", intent.getStringExtra("GameId"));
            gamePlayer.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Save", "gamePlayer data saved!");
                    } else {
                        Log.d("Save", "Error saving gamePlayer: " + e);
                    }
                }

            });
        }
    }

    private List<ParseUser> getChosenPlayerList() {
        final List<ParseUser> chosenPlayers = new ArrayList<ParseUser>();
        final ListView playerListView = (ListView) findViewById(R.id.listview_players);
        final SparseBooleanArray checkedItems = playerListView
                .getCheckedItemPositions();
        if (checkedItems != null) {
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.valueAt(i)) {
                    chosenPlayers.add(playerList.get(checkedItems.keyAt(i)));
                }
            }
        }
        return chosenPlayers;
    }
   
    private void getGamePlayers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GamePlayer");
        Bundle extras = getIntent().getExtras();
        final String GameId = extras.getString("GameId");
        query.whereEqualTo("GameId", GameId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override   
        	public void done(List<ParseObject> playerList, ParseException e) {
                if (e == null) {
                    Log.d("User List", "Retrieved " + playerList.size()
                            + " player(s)");
                    getPlayers(playerList);
                    gamePlayers = playerList;
                } else {
                    Log.w("Parse Error", "game retreival failure");
                }
            }
        });
    }

    private void getPlayers(List<ParseObject> playerList) {
        final ArrayList<ParseUser> players = new ArrayList<ParseUser>();
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).getParseObject("userId")
                    .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Log.d("Parse User",
                                        "Retrieved User "
                                                + user.getString("username"));
                                players.add(user);
                            } else {
                                Log.w("Parse Error", "game retreival failure");
                            }
                        }
                    });
        }
        setParseUserList(players);
    }
    
    private void setParseUserList(final ArrayList<ParseUser> gamePlayerList) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.selectKeys(Arrays.asList("username"));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    String[] usernameList = new String[userList.size()];
                    int[] gamePlayerPositions = new int[gamePlayerList.size()];
                    Log.d("User List", "Retrieved " + userList.size());
                    for (int i = 0; i < userList.size(); i++) {
                        Log.d("data", "Retrieved User: "
                                + userList.get(i).getString("username"));
                        usernameList[i] = userList.get(i).getString("username");
                    }
                    playerList = userList;

                    for (int a = 0; a < gamePlayerList.size(); a++) {
                        for (int i = 0; i < userList.size(); i++) {
                            String userId = userList.get(i).getObjectId();
                            String playerId = gamePlayerList.get(a)
                                    .getObjectId();
                            Log.d("user comparison", gamePlayerList.get(a)
                                    .getString("username")
                                    + " : "
                                    + userList.get(i).getString("username"));

                            if (userId.equals(playerId)) {
                                Log.d("user comparison", "match found");
                                gamePlayerPositions[a] = i;
                                continue;
                            }
                        }
                    }
                    setupUsernameListView(usernameList, gamePlayerPositions);
                } else {
                    Log.w("error", "game retreival failure");
                }
            }
        });

    }

    private void setupUsernameListView(String[] usernameList,
            int[] gamePlayerPositions) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, usernameList);
        final ListView playerListView = (ListView) findViewById(R.id.listview_players);
        playerListView.setAdapter(adapter);
        playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for (int position : gamePlayerPositions) {
            playerListView.setItemChecked(position, true);
        }
    }

    private void getGame() {
        Bundle extras = getIntent().getExtras();
        String gameId = extras.getString("GameId");
        Log.d("Game Info", "Game View ID is " + gameId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("name"));
                    getGamePlayers();

                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
                    gameName.setText(game.getString("name"));

                } else {
                    Log.w("error", "game retreival failure");
                    getGamePlayers();
                }
            }
        });
    }

}