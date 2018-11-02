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

    final static String TAG = "!ColorSelectionDialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_color_selection, container, false);

        // set the root view to be transparent to highlight the rounded corners of the actual background
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set up all of the buttons' views
        Button playBlack = v.findViewById(R.id.black);
        Button playWhite = v.findViewById(R.id.white);
        Button playRandom = v.findViewById(R.id.random);
        Button cancel = v.findViewById(R.id.cancel);

        // set up all of the button's click functions
        playBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("StartingBlack", true);
                startActivity(intent);
                dismiss();
            }
        });

        playWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("StartingBlack", false);
                startActivity(intent);
                dismiss();
            }
        });

        playRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                double coinFlip = Math.random();
                boolean color = coinFlip <= 0.5;
                intent.putExtra("StartingBlack", color);
                startActivity(intent);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }
}
