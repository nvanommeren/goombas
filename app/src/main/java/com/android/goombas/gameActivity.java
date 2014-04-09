/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Game Activity class for the Shoot the Goomba's game, handles the
 * page where the game is played.
 *
 ***********************************************************************/

package com.android.goombas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class gameActivity extends ActionBarActivity {

    /** File name for the Shared Preferences object */
    private final String PREFS_NAME = "goombasPrefs";

    private SharedPreferences prefs;

    private RelativeLayout relativeLayout;

    private CountDownTimer timer;

    private long milliSecondsLeft;

    private GamePlay game;

    private Target target;

    /** Class used to handle all database actions */
    private DBAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.app.ActionBar actionBar = getActionBar();
        actionBar.hide();

        // get Shared Preferences object
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // start a new gamePlay
        game = new GamePlay(this, prefs);

        // set a count down timer
        setTimer(game.getGameTime());

        // load the music button
        loadMusicButton();

        relativeLayout = (RelativeLayout)findViewById(R.id.layout1);

        // add target to the view
        target = new Target(this);
        target.setTargetParams(-100, -100);
        relativeLayout.addView(target);

        // add bullets to the view
        loadBullets(relativeLayout);

        // add number of point in the top left corner
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(String.valueOf(game.getPoints()));

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
        // when the user selects the main menu
        if (id == R.id.activity_main) {

            // start an intent to go to the main activity
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets a timer which counts down from 60 seconds and calls the addGoomba function every
     * second. Finishes the game when the time is up.
     */
    private void setTimer(long millisFromStart) {

        // add a timer in the textView in the top right corner
        final TextView textView = (TextView) findViewById(R.id.textView);

        // count down from 60 seconds
        timer = new CountDownTimer(millisFromStart, 1000) {

            // indices for the random numbers
            int index, index2;

            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf(millisUntilFinished / 1000));

                // called every second
                if ((millisUntilFinished/1000) % 1 == 0) {

                    // random number between 1 and 3
                    index = new Random().nextInt(3) + 1;

                    // add between 1 and 3 goomba's at the same time
                    for (int i = 0; i < index; i++ ) {
                        addGoomba();

                    }

                    // Save the number of seconds left in a variable
                    milliSecondsLeft = millisUntilFinished;

                    // random number between 0 and 24
                    index2 = new Random().nextInt(25);

                    // add a extra time clock when the random number is 0
                    if (index2 == 0) {
                        addClock();
                    }
                }

            }

            // when the time is up
            public void onFinish() {

                // stop the background music
                game.stopMusic();

                // get a new DBAdapter and open/create the database
                db = new DBAdapter(getBaseContext());
                db.open();

                // check if last played score is a high score
                if (db.checkHighScore(game.getPoints())) {
                    insertName();
                }
                else {
                    // go to the score page
                    Intent intent = new Intent(getBaseContext(), scoreActivity.class);
                    intent.putExtra("points", String.valueOf(game.getPoints()));
                    intent.putExtra("directedFrom", "game");

                    startActivity(intent);

                    finish();
                }
            }
        }.start();

    }


    /**
     * Adds a new Goomba to the view and sets the target to the coordinates of the
     * goomba when it is shot.
     */
    private void addGoomba() {

        // pick random the sort of the goomba
        int index = new Random().nextInt(3);

        // create a new goomba
        final Goomba goomba = new Goomba(this, index);

        // add goomba to view
        relativeLayout.addView(goomba);

        // start the animation of the goomba
        goomba.startAnimation();

        // when the goomba is shot
        goomba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get coordinates of the shot goomba
                int x = Math.round(goomba.getX());
                int y = Math.round(goomba.getY());

                // remove previous target
                relativeLayout.removeView(target);

                // set target to the coordinates of the shot goomba
                target.setTargetParams(x, y);

                // add target to the view
                relativeLayout.addView(target);

                // only shoot goomba's when user has bullets
                if (game.getNumberOfBullets() > 0) {

                    // add a new show value to show the value of the shot goomba
                    ShowValue showV = new ShowValue(getBaseContext(), x, y);

                    int size = goomba.getHeight();
                    String value = String.valueOf(goomba.getValue());

                    // add parameters to the show value
                    showV.setValueParams(size, value);

                    // add animation to the show value
                    showV.valueAnimation();

                    // add the value of the shot goomba to the view
                    relativeLayout.addView(showV);


                    // function for a shot goomba
                    goomba.shot();

                    // play the sound of a shot goomba
                    game.playShotGoomba();

                }

                // remove one bullet from the view
                ImageView usedBullet = (ImageView) findViewById(game.getNumberOfBullets() - 1);
                relativeLayout.removeView(usedBullet);

                game.updateBullets(target);

                if (game.getNumberOfBullets() == 0) {

                    addReloadButton();
                }

                // update the number of scored points in the game
                game.updatePoints(goomba);

                final TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView2.setText(String.valueOf(game.getPoints()));

            }
        });

    }

    /**
     * Adds a new extraTimeClock to the view, when the user clicks it, the user
     * gets 5 extra seconds play time.
     */
    private void addClock() {

        // Add an clock to the view
        final ExtraTimeClock clock = new ExtraTimeClock(getBaseContext());
        relativeLayout.addView(clock);

        // Set the animation for the clock
        clock.setAnimation();

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // remove the clock from the view
                relativeLayout.removeView(clock);

                // stop the old timer
                timer.cancel();

                // set a new timer with current time plus 5 seconds
                setTimer(milliSecondsLeft + 5000);

                // get coordinates of the clicked clock
                int x = Math.round(clock.getX());
                int y = Math.round(clock.getY());

                // add a new show value to show the value of the clicked clock
                ShowValue showV = new ShowValue(getBaseContext(), x, y);

                // set parameters of the show value
                showV.setValueParams(80, "5 sec!");

                // set target to the coordinates of the clicked clock
                target.setTargetParams(x, y);

                // add animation to the show value
                showV.valueAnimation();

                // add the value of the shot goomba to the view
                relativeLayout.addView(showV);
            }
        });
    }

    /**
     *  Asks user for name input to add to the High Score table.
     */
    private void insertName() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New High Score!");
        alert.setMessage("Your Name: ");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        // place last inputted name in user input box
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

            db.insertScore(insertName, game.getPoints());

            // start a new intent to show the highscores
            Intent intent2 = new Intent(getBaseContext(), scoreActivity.class);

            intent2.putExtra("points", String.valueOf(game.getPoints()));
            intent2.putExtra("directedFrom", "game");
            startActivity(intent2);

            finish();

            }
        });

        // prevent the user to cancel the alert by hitting the back button
        alert.setCancelable(false);

        alert.show();
    }

    /**
     *  Handle touches on the screen.
     */
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:

                // remove previous target
                relativeLayout.removeView(target);

                // get coordinates of the touch
                int x = (int)event.getX();
                int y = (int)event.getY();

                // correct so the target is in the middle of the touch
                int correction = (int) (0.5 * target.getSize());

                // set target to the coordinates of the touch
                target.setTargetParams(x - correction, y - correction);

                // add target to the view
                relativeLayout.addView(target);

                ImageView usedBullet = (ImageView) findViewById(game.getNumberOfBullets() - 1);
                relativeLayout.removeView(usedBullet);

                game.updateBullets(target);

                if (game.getNumberOfBullets() == 0) {
                    addReloadButton();
                }

                // finger touches the screen
                break;

        }

        // tell the system that we handled the event and no further processing is required
        return true;
    }


    /**
     * Called when the user clicks the "Reload" button
     */
    public void loadBullets(View view) {

        game.playReload();

        int numberOfBullets = game.getNumberOfBullets();

        // place the number of bullets in the view
        for (int i = 0; i < numberOfBullets; i++) {

            // add bullets to the right bottom corner of the screen
            Bullet bullet = new Bullet(this);

            // set settings for the bullet
            bullet.setBulletParameters(i);

            relativeLayout.addView(bullet);
        }

        target.setBackgroundResource(R.drawable.target_small);

    }


    /**
     * Loads the right background image for the music button
     */
    public void loadMusicButton() {

        Button button = (Button) findViewById(R.id.music);

        // set button to music on or off
        if (game.getMusicValue()) {
            button.setBackgroundResource(R.drawable.music_on);

        }
        else {
            button.setBackgroundResource(R.drawable.music_off);

            // set volume of the background music to zero
            game.muteBackground();
        }

    }

    /**
     * Called when the user clicks the music button. Changes the state of music.
     */
    public void toggleMusic(View view) {

        // find the clicked music button
        Button button = (Button) findViewById(R.id.music);

        game.switchMusic(button);


    }

    /**
     * Add Reload button to the view.
     */
    private void addReloadButton() {

        final ReloadButton reload = new ReloadButton(this);

        reload.setReloadParams();

        reload.onAppearance(game);

        // add button to the view
        relativeLayout.addView(reload);

        // when the reload button is clicked, call load bullets
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBullets(v);

                relativeLayout.removeView(reload);
            }
        });
    }

    /**
     *  Finish activity when game is send to the background.
     */
    @Override
    protected void onStop() {
        super.onStop();

        // stop the background music
        game.stopMusic();

        // stop the timer
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        // finish the activity
        finish();
    }

}
