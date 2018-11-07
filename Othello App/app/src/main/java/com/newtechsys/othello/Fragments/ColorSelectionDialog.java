package com.newtechsys.othello.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.newtechsys.othello.Activities.GameActivity;
import com.newtechsys.othello.R;

/*********************************
 Created by eortiz on 10/26/2018.
 **********************************/
public class ColorSelectionDialog extends android.support.v4.app.DialogFragment {
    /***********
     * GLOBALS
     ***********/

    final static String TAG = "!ColorSelectionDialog";

    /**********************
     * ACTIVITY LIFECYCLE
     **********************/

    /* Pseudo-Constructor */
    public static ColorSelectionDialog newInstance(boolean aiOn) {
        // this method serves as an initializer for the dialog fragment so that we can pass an
        // argument to it - in this case whether or not the AI is turned on

        // create a new dialog fragment to return
        ColorSelectionDialog dialog = new ColorSelectionDialog();

        // add the passed in argument to the dialog fragment
        Bundle args = new Bundle();
        args.putBoolean("againstComputer", aiOn);
        dialog.setArguments(args);

        return dialog;
    }

    /* Create the Dialog's Layout */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // this method creates the layout for the dialog and the functionality of its buttons

        // create the dialog's view
        View v = inflater.inflate(R.layout.fragment_color_selection, container, false);

        // set the root view to be transparent to highlight the rounded corners of the actual background
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // determine if the user wants to play against the computer
        final boolean againstComputer = getArguments().getBoolean("againstComputer");

        // set up all of the buttons' views and their click functions
        Button playBlack = v.findViewById(R.id.black);
        Button playWhite = v.findViewById(R.id.white);
        Button playRandom = v.findViewById(R.id.random);
        Button cancel = v.findViewById(R.id.cancel);

        // player 1 starts black
        playBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("StartingBlack", true);
                intent.putExtra("AgainstComputer", againstComputer);
                startActivity(intent);
                dismiss();
            }
        });

        // player 1 starts white
        playWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("StartingBlack", false);
                intent.putExtra("AgainstComputer", againstComputer);
                startActivity(intent);
                dismiss();
            }
        });

        // player 1 starts a random color
        playRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                double coinFlip = Math.random();
                boolean color = coinFlip <= 0.5;
                intent.putExtra("StartingBlack", color);
                intent.putExtra("AgainstComputer", againstComputer);
                startActivity(intent);
                dismiss();
            }
        });

        // dismiss the dialog fragment
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }
}
