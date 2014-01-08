package com.example.scavengerhuntapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ItemFoundDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String itemName = getArguments().getString("name");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Did you find the item: " + "\""+ itemName + "\"" + "?")
                .setPositiveButton(R.string.dialog_found,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((PlayGame) getActivity()).onFoundItemDialog(itemName);
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ItemFoundDialogFragment.this.getDialog()
                                        .cancel();
                            }
                        });
        return builder.create();
    }

}