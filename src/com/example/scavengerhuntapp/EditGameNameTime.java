package com.example.scavengerhuntapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class EditGameNameTime extends Activity {
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgamenametime_layout);
//        setupButtonCallbacks();
        getGame();
    }

    @Override
    public void onResume() {
        super.onResume();
    }   
    
    private void getGame() {
      Bundle extras = getIntent().getExtras();
      String gameId = extras.getString("gameId");
      Log.d("Game Info", "Game View ID is " + gameId);

      ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
      query.getInBackground(gameId, new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject game, ParseException e) {
              if (e == null) {
                  Log.d("Game Info", "Game name is " + game.getString("gameName"));
        
                  TextView gameName = (TextView) findViewById(R.id.edit_gameName);
                  TextView startDate = (TextView) findViewById(R.id.editStartDate);
                  TextView endDate = (TextView) findViewById(R.id.editEndDate);
//will set up date time so fills each field individually after I set up date picker....to do for future
                  gameName.setText(game.getString("gameName"));
                  startDate.setText(game.getString("start_datetime"));
                  endDate.setText(game.getString("end_datetime"));

              } 
              else {
                  Log.w("error", "game retreival failure");

                  TextView gameName = (TextView) findViewById(R.id.text_gameName);
                  TextView startDatetime = (TextView) findViewById(R.id.text_startDatetime);
                  TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);

                  gameName.setText(game.getString("gameName"));
                  startDatetime.setText(game.getString("start_datetime"));
                  endDatetime.setText(game.getString("end_datetime"));

              }
          }
      });
    }


//  button callback methods for edit button below...need to add for edit items and edit players buttons to the below    
//  will do all the button callbacks once I get the views of each edit game set up
//  private void setupButtonCallbacks() {
//      final Button updateGameButton = (Button) findViewById(R.id.button_update);
//
//      updateGameButton.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              doUpdateGame();
//          }
//      });
//  }

//  private void doUpdateGame() {
//  Bundle extras = getIntent().getExtras();
//  final String gameId = extras.getString("gameId");
//  ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//  final ParseUser currentUser = ParseUser.getCurrentUser();
//  query.getInBackground(gameId, new GetCallback<ParseObject>() {
//      @Override
//      public void done(final ParseObject game, ParseException e) {
//          if (e == null) {
//              game.put("gameName", getGameName());
////              game.put("start_datetime", getStartDateTime());
////              game.put("end_datetime", getEndDateTime());
//              game.saveInBackground(new SaveCallback() {
//                  @Override
//                  public void done(ParseException e) {
//                      if (e == null) {
//                          Log.d("Game Update", "Game Updated!");
//                          updateGamePlayers(getChosenPlayerList(), game);
//                           showToast("Game Updated!");
//                           launchGameView(game.getObjectId());
//                      } else {
//                          Log.d("Game Creation", "Error creating game: "
//                                  + e);
//                      }
//                  }
//              });
//          }
//      }
//  });
//}

//    private String getGameName() {
//      return getUserInput(R.id.edit_gameName);
//    }
//    
//    private String getUserInput(int id) {
//      EditText input = (EditText) findViewById(id);
//      return input.getText().toString();
//    }
// 
//  private void launchGameView(String gameId) {
//    Intent intent = new Intent(EditGameNameTime.this, ViewGame.class);
//    intent.putExtra("gameId", gameId);
//    Log.d("GameId", "game id is " + gameId);
//    startActivity(intent);
//  }    

    
}