package com.android.goombas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TableLayout;
import android.widget.TextView;

public class scoreActivity extends ActionBarActivity {

    // class used to handle all database actions
    private DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.app.ActionBar actionBar = getActionBar();
        actionBar.hide();

        // get points of last played score from intent
        Intent intent = getIntent();

        // if the user goes to this page from the home page
        if (intent.getStringExtra("directedFrom").equals("main")) {

            // hide the text "You got ... points"
            TextView textView = (TextView) findViewById(R.id.textView);
            TextView textView2 = (TextView) findViewById(R.id.textView2);
            TextView textView3 = (TextView) findViewById(R.id.textView3);
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);


            // show the text "High Scores"
            TextView textView4 = (TextView) findViewById(R.id.textView4);
            textView4.setVisibility(View.VISIBLE);
        }
        else {
            String points = intent.getStringExtra("points");

            // send number of points to the view
            TextView textView2 = (TextView) findViewById(R.id.textView2);
            textView2.setText(points);
        }


        // show table with 5 high scores
        showData();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // when the user selects the main menu
        if (id == R.id.activity_main) {

            // start an intent to go to the main activity
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);

            return true;
        }
        // when the user selects to play a new game
        else if (id == R.id.activity_game) {

            // start an intent to go to the game activity
            Intent intent = new Intent(getBaseContext(), gameActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the high scores of chosen length in a table to the user
     */
    public void showData()
    {

        // instantiate a DatabaseHelper and open it
        db = new DBAdapter(this);
        db.open();

        // find the high scores in the database
        Cursor cursor = db.findScores();
        int count = cursor.getCount();

        // if there are values in the database to show, fill the table
        if (count != 0) {

            cursor.moveToFirst();
            int i = 1;

            // put the name of the player in the table
            do {

                String noID = "n" + i;
                String nameID = "name" + i;
                int resID = getResources().getIdentifier(noID, "id", getPackageName());
                int resID2 = getResources().getIdentifier(nameID, "id", getPackageName());
                TextView textViewNumber = (TextView) findViewById(resID);
                TextView textViewName = (TextView) findViewById(resID2);

                String foundName = db.getName(cursor);
                textViewNumber.setText(String.valueOf(i));
                textViewName.setText(foundName);
                i++;
            } while(cursor.moveToNext());

            cursor.moveToFirst();
            i = 1;

            // put the number of points in the table
            do {

                String pointsID = "points" + i;
                int resID = getResources().getIdentifier(pointsID, "id", getPackageName());
                TextView textViewPoints = (TextView) findViewById(resID);

                String foundMistakes = db.getPoints(cursor);
                textViewPoints.setText(foundMistakes);
                i++;
            } while(cursor.moveToNext());
        }
        else {

            // hide high score table when there is nothing to show
            TextView textView = (TextView) findViewById(R.id.textView2);
            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
            textView.setVisibility(View.INVISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     *  Finish activity when game is send to the background
     */
    @Override
    protected void onStop() {
        super.onStop();

        finish();
    }

}
