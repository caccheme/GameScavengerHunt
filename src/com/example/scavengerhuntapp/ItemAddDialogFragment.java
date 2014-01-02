package com.example.scavengerhuntapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ItemAddDialogFragment extends DialogFragment {
    private EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_item, null);
        mEditText = (EditText) view.findViewById(R.id.editNewItem);

        builder.setView(view)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (getTag() == "editGameItems") {
//                                    ((EditGame) getActivity()).onFinishItemDialog(mEditText
//                                            .getText().toString());
                                }
                                else if (getTag() == "createGameItems") {
//                                    ((NewGameActivity) getActivity()).onFinishItemDialog(mEditText
//                                            .getText().toString());
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ItemAddDialogFragment.this.getDialog().cancel();
                            }
                        });
        return builder.create();
    }
}