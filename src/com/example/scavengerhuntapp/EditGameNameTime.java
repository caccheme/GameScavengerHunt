package com.example.scavengerhuntapp;

//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
//import android.app.DialogFragment;
//import android.util.SparseBooleanArray;
//import android.view.Menu;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import com.parse.FindCallback;
//import com.parse.ParseUser;

public class EditGameNameTime extends Activity {

//    private List<ParseObject> gamePlayers = new ArrayList<ParseObject>();
//    private List<ParseObject> gameItems = new ArrayList<ParseObject>();
//    private List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgamenametime_layout);
        setupButtonCallbacks();
        getGame();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.edit_game, menu);
//        return true;
//    }

    public void onResume() {
        super.onResume();
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
                    
                    EditText gameName = (EditText) findViewById(R.id.edit_gameName);
                    
                    gameName.setText(game.getString("name"));
                    populateDateTimeFields(game);            
                } else {
                    Log.w("error", "game retreival failure");
                    setGameInfo(game);
                }
            }
        });
    }

    public void setGameInfo(ParseObject game) {
//        initializeListViews(game);
        
 
 
//        setItemList(game);
    }

    private void setupButtonCallbacks() {
        final Button updateGameButton = (Button) findViewById(R.id.button_update);
//        final Button addItemButton = (Button) findViewById(R.id.button_editAddItem);
        updateGameButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doUpdateGame();
            }
        });
        

//        addItemButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                showItemAddDialog();
//            }
//        });
    }

    private void doUpdateGame() {
        Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("gameId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");

        // Retrieve the object by id
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(final ParseObject game, ParseException e) {
                if (e == null) {
                    game.put("name", getGameName());
                    game.put("start_datetime", getStartDateTime());
                    game.put("end_datetime", getEndDateTime());
                    game.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("Game Update", "Game Updated!");
//                                updateGamePlayers(getChosenPlayerList(), game);
//                                updateGameItems(getItemList(), game);
                                 showToast("Game Updated!");
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

    private void launchGameView(String gameId) {
        Intent intent = new Intent(EditGameNameTime.this, ViewGame.class);
        intent.putExtra("gameId", gameId);
        Log.d("GameId", "game id is " + gameId);
        startActivity(intent);
    }
    
//    private void clearParseObjects(List<ParseObject> parseList) {
//        for (ParseObject item : parseList) {
//            item.deleteInBackground();
//        }
//    }

//    private void updateGameItems(ArrayList<String> itemList, ParseObject game) {
//        clearParseObjects(gameItems);
//        for (int i = 0; i < itemList.size(); i++) {
//            final String item = itemList.get(i);
//            Log.d("Item", item);
//            final ParseObject gameItem = new ParseObject("GameItem");
//            gameItem.put("name", item);
//            gameItem.put("game", game);
//            gameItem.saveInBackground(new SaveCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d("Save", "Item Saved");
//                    } else {
//                        Log.d("Save", "Error saving gameItem: " + e);
//                    }
//                }
//
//            });
//        }
//    }

//    private ArrayList<String> getItemList() {
//        ArrayList<String> itemList = new ArrayList<String>();
//        ArrayAdapter<String> adapter = getItemAdapter();
//        for (int i = 0; i < (adapter.getCount()); i++) {
//            itemList.add(adapter.getItem(i));
//        }
//        return itemList;
//
//    }

//    private void updateGamePlayers(List<ParseUser> chosenPlayerList,
//            ParseObject game) {
//        clearParseObjects(gamePlayers);
//        for (int i = 0; i < chosenPlayerList.size(); i++) {
//            ParseUser user = chosenPlayerList.get(i);
//            Log.d("Player", user.toString());
//            ParseObject gamePlayer = new ParseObject("GamePlayer");
//            gamePlayer.put("user", user);
//            gamePlayer.put("game", game);
//            gamePlayer.saveInBackground(new SaveCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d("Save", "gamePlayer data saved!");
//                    } else {
//                        Log.d("Save", "Error saving gamePlayer: " + e);
//                    }
//                }
//
//            });
//        }
//    }

    private void showToast(String message) {
        ScavengerHuntApplication.getInstance()
                .showToast(EditGameNameTime.this, message);
    }

//    private List<ParseUser> getChosenPlayerList() {
//        final List<ParseUser> chosenPlayers = new ArrayList<ParseUser>();
//        final ListView playerListView = (ListView) findViewById(R.id.listview_players);
//        final SparseBooleanArray checkedItems = playerListView
//                .getCheckedItemPositions();
//        if (checkedItems != null) {
//            for (int i = 0; i < checkedItems.size(); i++) {
//                if (checkedItems.valueAt(i)) {
//                    chosenPlayers.add(playerList.get(checkedItems.keyAt(i)));
//                }
//            }
//        }
//        return chosenPlayers;
//    }

    private String getGameName() {
        return getUserInput(R.id.edit_gameName);
    }

    private Date getStartDateTime() {
        return convertToDateTime(getUserInput(R.id.editStartDate) + " "
                + getUserInput(R.id.editStartTime));
    }

    private Date getEndDateTime() {
        return convertToDateTime(getUserInput(R.id.editEndDate) + " "
                + getUserInput(R.id.editEndTime));
    }

    private String getUserInput(int id) {
        EditText input = (EditText) findViewById(id);
        return input.getText().toString();
    }

    private static Date convertToDateTime(String dateString) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yyyy h:mm a", Locale.US);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public void showStartDatePickerDialog(View v) {
        final DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "startDatePicker");
    }

    public void showStartTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "startTimePicker");
    }

    public void showEndDatePickerDialog(View v) {
        final DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "endDatePicker");
    }

    public void showEndTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "endTimePicker");
    }

//    private void showItemAddDialog() {
//        final DialogFragment itemAddDialogFragment = new ItemAddDialogFragment();
//        itemAddDialogFragment.show(getFragmentManager(), "editGameItems");
//    }

//    private void addItemToList(String item, ArrayAdapter<String> adapter) {
//        adapter.add(item);
//        adapter.notifyDataSetChanged();
//    }

//    public void onFinishItemDialog(String item) {
//        addItemToList(item, getItemAdapter());
//    }

    private void populateDateTimeFields(ParseObject game) {
        EditText startDateView = (EditText) findViewById(R.id.editStartDate);
        EditText startTimeView = (EditText) findViewById(R.id.editStartTime);
        EditText endDateView = (EditText) findViewById(R.id.editEndDate);
        EditText endTimeView = (EditText) findViewById(R.id.editEndTime);

        startDateView.setText(getGameDate(game.getDate("start_datetime")));
        endDateView.setText(getGameDate(game.getDate("end_datetime")));
        startTimeView.setText(getGameTime(game.getDate("start_datetime")));
        endTimeView.setText(getGameTime(game.getDate("end_datetime")));
    }

    private String getGameDate(Date date) {
        return convertDatetimeToDate(date);

    }

    private String getGameTime(Date date) {
        return convertDatetimeToTime(date);
    }

    private String convertDatetimeToTime(Date dateTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a",
                Locale.US);
        String convertedTime = new String();
        convertedTime = dateFormat.format(dateTime);
        return convertedTime;
    }

    private String convertDatetimeToDate(Date dateTime) {

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy",
                Locale.US);

        String convertedDate = new String();
        convertedDate = dateFormat.format(dateTime);
        return convertedDate;
    }

//    private void initializeListViews(ParseObject game) {
//        setupPlayerListView(game);
//        setupItemListView();
//    }

//    private void setupPlayerListView(ParseObject game) {
//        getGamePlayers(game);
//
//    }

//    private void getGamePlayers(ParseObject game) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("GamePlayer");
//        query.whereEqualTo("game", game);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> playerList, ParseException e) {
//                if (e == null) {
//                    Log.d("User List", "Retrieved " + playerList.size()
//                            + " player(s)");
//                    getPlayers(playerList);
//                    gamePlayers = playerList;
//                } else {
//                    Log.w("Parse Error", "game retreival failure");
//                }
//            }
//        });
//    }

//    private void getPlayers(List<ParseObject> playerList) {
//        final ArrayList<ParseUser> players = new ArrayList<ParseUser>();
//        for (int i = 0; i < playerList.size(); i++) {
//            playerList.get(i).getParseObject("user")
//                    .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
//                        public void done(ParseUser user, ParseException e) {
//                            if (e == null) {
//                                Log.d("Parse User",
//                                        "Retrieved User "
//                                                + user.getString("username"));
//                                players.add(user);
//                            } else {
//                                Log.w("Parse Error", "game retreival failure");
//                            }
//                        }
//                    });
//        }
//        setParseUserList(players);
//    }

//    private void setParseUserList(final ArrayList<ParseUser> gamePlayerList) {
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.selectKeys(Arrays.asList("username"));
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> userList, ParseException e) {
//                if (e == null) {
//                    String[] usernameList = new String[userList.size()];
//                    int[] gamePlayerPositions = new int[gamePlayerList.size()];
//                    Log.d("User List", "Retrieved " + userList.size());
//                    for (int i = 0; i < userList.size(); i++) {
//                        Log.d("data", "Retrieved User: "
//                                + userList.get(i).getString("username"));
//                        usernameList[i] = userList.get(i).getString("username");
//                    }
//                    playerList = userList;
//
//                    for (int a = 0; a < gamePlayerList.size(); a++) {
//                        for (int i = 0; i < userList.size(); i++) {
//                            String userId = userList.get(i).getObjectId();
//                            String playerId = gamePlayerList.get(a)
//                                    .getObjectId();
//                            Log.d("user comparison", gamePlayerList.get(a)
//                                    .getString("username")
//                                    + " : "
//                                    + userList.get(i).getString("username"));
//
//                            if (userId.equals(playerId)) {
//                                Log.d("user comparison", "match found");
//                                gamePlayerPositions[a] = i;
//                                continue;
//                            }
//                        }
//                    }
//                    setupUsernameListView(usernameList, gamePlayerPositions);
//                } else {
//                    Log.w("error", "game retreival failure");
//                }
//            }
//        });
//
//    }

//    private void setupUsernameListView(String[] usernameList,
//            int[] gamePlayerPositions) {
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_multiple_choice, usernameList);
//        final ListView playerListView = (ListView) findViewById(R.id.listview_players);
//        playerListView.setAdapter(adapter);
//        playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        for (int position : gamePlayerPositions) {
//            playerListView.setItemChecked(position, true);
//        }
//    }

//    private void setItemList(ParseObject game) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItem");
//        query.whereEqualTo("game", game);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> itemList, ParseException e) {
//                if (e == null) {
//                    Log.d("Parse Item", "Retrieved " + itemList.size()
//                            + " item(s)");
//                    for (int i = 0; i < itemList.size(); i++) {
//                        addToListView(itemList.get(i).getString("name"),
//                                getItemAdapter());
//                    }
//                    gameItems = itemList;
//                } else {
//                    Log.w("Parse Error", "game retreival failure");
//                }
//            }
//        });
//    }

//    private void setupItemListView() {
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1);
//        final ListView itemListView = (ListView) findViewById(R.id.listview_items);
//        itemListView.setAdapter(adapter);
//        itemListView
//                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent,
//                            final View view, int position, long id) {
//                        final String item = (String) parent
//                                .getItemAtPosition(position);
//
//                        adapter.remove(item);
//                        adapter.notifyDataSetChanged();
//                        view.setAlpha(1);
//                    }
//                });
//
//    }

//    private void addToListView(String item, ArrayAdapter<String> adapter) {
//        adapter.add(item);
//        adapter.notifyDataSetChanged();
//    }

//    private ArrayAdapter<String> getItemAdapter() {
//        final ListView itemListView = (ListView) findViewById(R.id.listview_items);
//        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) itemListView
//                .getAdapter();
//        return adapter;
//    }

//    private void getGame() {
//        Bundle extras = getIntent().getExtras();
//        String gameId = extras.getString("gameId");
//        Log.d("Game Info", "Game View ID is " + gameId);
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//        query.getInBackground(gameId, new GetCallback<ParseObject>() {
//            public void done(ParseObject game, ParseException e) {
//                if (e == null) {
//                    Log.d("Game Info", "Game name is " + game.getString("name"));
//                    setGameInfo(game);
//                } else {
//                    Log.w("error", "game retreival failure");
//                    setGameInfo(game);
//                }
//            }
//        });
//    }

}