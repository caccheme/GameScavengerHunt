package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditGameItems extends Activity {

//    private List<ParseObject> gameItems = new ArrayList<ParseObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editgameitems_layout);
        setupButtonCallbacks();
        listCurrentItems(); 
//        getGame();
    }
//
//    private void getGame() {
//        Bundle extras = getIntent().getExtras();
//        String gameId = extras.getString("GameId");
//        Log.d("Game Info", "Game View ID is " + gameId);
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//        query.getInBackground(gameId, new GetCallback<ParseObject>() {
//            public void done(ParseObject game, ParseException e) {
//                if (e == null) {
//                    Log.d("Game Info", "Game name is " + game.getString("name"));
//                    listCurrentItems();
////                    initializeListViews(game);
//                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
//                    gameName.setText(game.getString("name"));
//
////                    setItemList(game);
//                } else {
//                    Log.w("error", "game retreival failure");
////                    initializeListViews(game);
//
//                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
//                    gameName.setText(game.getString("name"));
//
////                    setItemList(game);
//                }
//            }
//        });
//    }

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
              finish();
              startActivity(getIntent()); 
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
                    finish();
                    startActivity(getIntent()); 
                  }  
                  else { 
                    JSONArray new_items = new JSONArray();
                    new_items.put(new_item);
                    game.put("itemsList", new_items);
                    game.saveInBackground();
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
//        final Button saveButton = (Button) findViewById(R.id.button_save); 
//        saveButton.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            finish();
//            final Intent i = getIntent();
//            final String GameId = i.getStringExtra("GameId");
//            Intent b = new Intent(EditGameItems.this, ViewGame.class);
//            b.putExtra("GameId", GameId);
//            EditGameItems.this.startActivity(b);
//            launchGameView(game.getObjectId());
//          } 
//        });
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
    
    
    
    
    
    
    
//    private void initializeListViews(ParseObject game) {
//	    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//	            android.R.layout.simple_list_item_1);
//	    final ListView itemListView = (ListView) findViewById(R.id.listview_items);
//	    itemListView.setAdapter(adapter);
//	    itemListView
//	            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//	
//	                @Override
//	                public void onItemClick(AdapterView<?> parent,
//	                        final View view, int position, long id) {
//	                    final String item = (String) parent
//	                            .getItemAtPosition(position);
//	
//	                    adapter.remove(item);
//	                    adapter.notifyDataSetChanged();
//	                    view.setAlpha(1);
//	                }
//	            });
//    }
//    
//    will need query to call from json array instead of join table in order to get them on the view
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

//    private void addToListView(String item, ArrayAdapter<String> adapter) {
//        adapter.add(item);
//        adapter.notifyDataSetChanged();
//    }
//
//    private ArrayAdapter<String> getItemAdapter() {
//        final ListView itemListView = (ListView) findViewById(R.id.listview_items);
//        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) itemListView
//                .getAdapter();
//        return adapter;
//    }
//
//  private void setupButtonCallbacks() {
//  final Button saveGameButton = (Button) findViewById(R.id.button_save);
//  saveGameButton.setOnClickListener(new OnClickListener() {
//      public void onClick(View v) {
//          Bundle extras = getIntent().getExtras();
//          final String gameId = extras.getString("gameId");
//          ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//          query.getInBackground(gameId, new GetCallback<ParseObject>() {
//              public void done(final ParseObject game, ParseException e) {
//                  if (e == null) {
//                      game.saveInBackground(new SaveCallback() {
//                          public void done(ParseException e) {
//                              if (e == null) {
//                                  Log.d("Game Update", "Game Updated!");
//                                  updateGameItems(getItemList(), game);
//                                  showToast("Game Updated!");
//                                  launchGameView(game.getObjectId());
//                              } else {
//                                  Log.d("Game Creation", "Error creating game: "
//                                          + e);
//                              }
//                          }
//                      });
//                  }
//              }
//          });
//
//      }
//  });
//  final Button addItemButton = (Button) findViewById(R.id.button_addItem);
//  addItemButton.setOnClickListener(new OnClickListener() {
//      public void onClick(View v) {
////      	make json array button to add items and show....
//          showItemAddDialog();
//      }
//    });
//  }

//	private void launchGameView(String gameId) {
//	  Intent intent = new Intent(EditGameItems.this, ViewGame.class);
//	  intent.putExtra("GameId", gameId);
//	  Log.d("GameId", "game id is " + gameId);
//	  startActivity(intent);
//	}
//
//  private void showToast(String message) {
//    ScavengerHuntApplication.getInstance()
//          .showToast(EditGameItems.this, message);
//  }
//
//  private void clearParseObjects(List<ParseObject> parseList) {
//   for (ParseObject item : parseList) {
//      item.deleteInBackground();
//   }
//  }
//
//
//	private void updateGameItems(ArrayList<String> itemList, ParseObject game) {
//	  clearParseObjects(gameItems);
//	  for (int i = 0; i < itemList.size(); i++) {
//	      final String item = itemList.get(i);
//	      Log.d("Item", item);
//	      final ParseObject gameItem = new ParseObject("GameItem");
//	      gameItem.saveInBackground(new SaveCallback() {
//	          public void done(ParseException e) {
//	              if (e == null) {
//	                  Log.d("Save", "Item Saved");
//	              } else {
//	                  Log.d("Save", "Error saving gameItem: " + e);
//	              }
//	          }
//	
//	      });
//	  }
//	}
//
//
////make json array method to replace the below    
//	private ArrayList<String> getItemList() {
//	  ArrayList<String> itemList = new ArrayList<String>();
//	  ArrayAdapter<String> adapter = getItemAdapter();
//	  for (int i = 0; i < (adapter.getCount()); i++) {
//	      itemList.add(adapter.getItem(i));
//	  }
//	  return itemList;
//	}
//
//	private void showItemAddDialog() {
//	  final DialogFragment itemAddDialogFragment = new ItemAddDialogFragment();
//	  itemAddDialogFragment.show(getFragmentManager(), "editGameItems");
//	}
//
//	private void addItemToList(String item, ArrayAdapter<String> adapter) {
//	  adapter.add(item);
//	  adapter.notifyDataSetChanged();
//	}
//	
//	public void onFinishItemDialog(String item) {
//	  addItemToList(item, getItemAdapter());
//	}

    
}