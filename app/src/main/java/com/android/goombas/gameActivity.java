package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class gameActivity extends ActionBarActivity {

    private int points;

    // file name for the shared preferences
    private final String PREFS_NAME = "goombasPrefs";

    // class used to handle all database actions
    private DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_game);

        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.app.ActionBar actionBar = getActionBar();
        actionBar.hide();

        // add a timer in the top right corner
        final TextView textView = (TextView) findViewById(R.id.textView);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf(millisUntilFinished / 1000));

                if ((millisUntilFinished/1000) % 0.5 == 0) {

                    int index = new Random().nextInt(3) + 1;

                    // add between 1 and 3 goomba's at the same time
                    for (int i = 0; i < index; i++ ) {
                        addGoomba();
                    }
                }
            }

            public void onFinish() {

                // get a new DBAdapter and open/create the database
                db = new DBAdapter(getBaseContext());
                db.open();

                // check if last played score is a high score
                if (db.checkHighScore(points)) {
                    insertName();
                }
                else {
                    // go to the score page
                    Intent intent = new Intent(getBaseContext(), scoreActivity.class);
                    intent.putExtra("points", String.valueOf(points));
                    intent.putExtra("directedFrom", "game");

                    startActivity(intent);

                    finish();
                }
            }
        }.start();

        // start a new gamePlay
        gamePlay game = new gamePlay();

        // add number of point in the top left corner
        points = game.getPoints();
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(String.valueOf(points));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void addGoomba() {

        // find the view
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);

        // create a new goomba
        final Goomba goomba = new Goomba(this);

        // hide goomba after it is clicked
        goomba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //when play is clicked show stop button and hide play button
                goomba.shot();

                // update number of scored points
                points += goomba.getValue();
                final TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView2.setText(String.valueOf(points));

                // points += goomba.getValue();

            }
        });

        // add goomba to view
        relativeLayout.addView(goomba);

        // start the animation of the goomba
        goomba.startAnimation();

    }

    /**
     *  Finish activity when user hits the back button
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *  Asks user for input of the name to add to the High Score table
     */
    public void insertName() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New High Score!");
        alert.setMessage("Your Name: ");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        // get the SharedPreferences object and place last inputted name in user input box
        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String name = prefs.getString("name", "Unknown");
        input.setText(name);

        // when the user clicks the "ok" button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            // make sure user input is not too long
            String insertName = input.getText().toString();
            if (insertName.length() > 7) {
                insertName = insertName.substring(0, 8);
            }

            // check for empty input
            if (insertName.equals("") ) {
                insertName = name;
            }

            // create an editor for the SharedPreferences and change name in the user input
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", insertName);
            editor.commit();

            db.insertScore(insertName, points);

            // start a new intent to show the highscores
            Intent intent2 = new Intent(getBaseContext(), scoreActivity.class);

            intent2.putExtra("points", String.valueOf(points));
            intent2.putExtra("directedFrom", "game");
            startActivity(intent2);

            finish();

            }
        });

        // prevent the user to cancel the alert by hitting the back button
        alert.setCancelable(false);

        alert.show();
    }

}
