package com.newtechsys.othello.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.newtechsys.othello.Classes.Board;
import com.newtechsys.othello.R;

public class GameActivity extends AppCompatActivity {

    Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
