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

public class MainMenuActivity extends AppCompatActivity {

    final static String TAG = "!MainMenuActivity";

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // set up the main menu buttons' views
        Button playHuman = findViewById(R.id.play_human);
        Button playAI = findViewById(R.id.play_ai);
        Button settings = findViewById(R.id.settings);

        // create the dialog fragment
        final DialogFragment dialogFragment = new ColorSelectionDialog();

        // set up the button click listeners
        playHuman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialogFragment.show(fm, "show");
            }
        });

        playAI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplication(), R.string.not_implemented, Toast.LENGTH_LONG).show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplication(), R.string.not_implemented, Toast.LENGTH_LONG).show();
            }
        });
    }
}
