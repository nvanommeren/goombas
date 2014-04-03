package com.android.goombas;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     *  Hide status bar when activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.app.ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    /**
     * Called when the user clicks the Start Game button
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, gameActivity.class);

        startActivity(intent);
    }

    /**
     * Called when the user clicks the High Scores button
     */
    public void highScores(View view) {
        Intent intent = new Intent(this, scoreActivity.class);

        // the user is directed from main to score page
        intent.putExtra("directedFrom", "main");

        startActivity(intent);
    }

}
