package com.newtechsys.othello.Activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.newtechsys.othello.Fragments.ColorSelectionDialog;
import com.newtechsys.othello.R;

/*
    Created by Eric Ortiz
    Student Number is 102-39-903
    Created for November 12, 2018
    Submitted for CSC 475 - Assignment #3: Othello

    This app is an Othello game that allows a user to play a match of Othello with another human
    locally, or against an AI that utilizes the Mini-Max algorithm for computing moves
 */

public class MainMenuActivity extends AppCompatActivity {
    /***********
     * GLOBALS
     ***********/

    // tag the activity for debug statements
    final static String TAG = "!MainMenuActivity";

    // fragment manager for handling dialogs
    FragmentManager fm = getSupportFragmentManager();

    // variable for special messages
    int settingsCounter = 0;

    /**********************
     * ACTIVITY LIFECYCLE
     **********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this method (and activity) is called whenever the app is first opened - it presents the
        // user with two options of playing and a third option for settings

        // create the activity and set up its layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // set up the main menu buttons' views
        Button playHuman = findViewById(R.id.play_human);
        Button playAI = findViewById(R.id.play_ai);
        final Button settings = findViewById(R.id.settings);


        /* OnClick Listeners for Menu Buttons */
        playHuman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // if the user wants to play against another human, display the options to them
                DialogFragment dialogFragment = ColorSelectionDialog.newInstance(false);
                dialogFragment.show(fm, "show");
            }
        });

        playAI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // if the user wants to play against an AI, display the options to them
                DialogFragment dialogFragment = ColorSelectionDialog.newInstance(true);
                dialogFragment.show(fm, "show");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // have some fun with the user if they insist on going to the settings page (which
                // doesn't exist)
                if (settingsCounter == 0)
                    Toast.makeText(getApplication(), R.string.not_implemented, Toast.LENGTH_LONG).show();
                else if (settingsCounter == 1)
                    Toast.makeText(getApplication(), R.string.no_really, Toast.LENGTH_LONG).show();
                else if (settingsCounter == 2)
                    Toast.makeText(getApplication(), R.string.stop_tapping, Toast.LENGTH_LONG).show();
                else if (settingsCounter == 3)
                    Toast.makeText(getApplication(), R.string.one_more, Toast.LENGTH_LONG).show();
                else {
                    finishAndRemoveTask();
                }

                settingsCounter++;
            }
        });
    }
}
