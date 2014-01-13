package com.example.scavengerhuntapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class GameOverDialogFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.gameTimedOut)
               .setPositiveButton("Return to Games List", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Intent intent = new Intent(getActivity(), InvitedGames.class);
                       startActivity(intent);
                   }
               });
        return builder.create();
    }
    
}