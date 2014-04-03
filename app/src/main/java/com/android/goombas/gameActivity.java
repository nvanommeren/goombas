package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class gameActivity extends ActionBarActivity {

    private int points;

    private SharedPreferences prefs;

    // file name for the shared preferences
    private final String PREFS_NAME = "goombasPrefs";

    // class used to handle all database actions
    private DBAdapter db;

    private ImageView target;

    private ImageView bullet;

    private RelativeLayout relativeLayout;

    private RelativeLayout.LayoutParams targetParams;

    private int targetSize;

    private int numberOfBullets;

    private boolean hasBullets;

    private MediaPlayer mpReload;

    private MediaPlayer mpGoomba;

    private MediaPlayer mpEmpty;

    private MediaPlayer mpBackground;

    private CountDownTimer timer;

    private boolean music;


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

        // get Shared Preferences object
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        // add a timer in the top right corner
        final TextView textView = (TextView) findViewById(R.id.textView);
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf(millisUntilFinished / 1000));

                if ((millisUntilFinished/1000) % 1 == 0) {

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

                mpBackground.stop();

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

        // create a sound for reloading the bullets
        mpReload = MediaPlayer.create(getApplicationContext(), R.raw.gun_cocking_01);

        // create a sound for shooting a goomba
        mpGoomba = MediaPlayer.create(getApplicationContext(), R.raw.smw_stomp);

        // create a sound for shooting when the gun is empty
        mpEmpty = MediaPlayer.create(getApplicationContext(), R.raw.gun_empty);

        mpBackground = MediaPlayer.create(getApplicationContext(), R.raw.super_mario_bros);
        mpBackground.setVolume(0.0f, 0.8f);
        mpBackground.start();

        // load the music button
        loadMusicButton();

        // start a new gamePlay
        gamePlay game = new gamePlay();

        // add target to the view
        target = new ImageView(this);
        // target.setBackgroundResource(R.drawable.target_small);
        relativeLayout = (RelativeLayout)findViewById(R.id.layout1);
        targetSize = 80;

        // set targetparams and make sure the targets starts outside the view
        targetParams = new RelativeLayout.LayoutParams(targetSize, targetSize);
        targetParams.setMargins(-100, -100, 0, 0);
        target.setLayoutParams(targetParams);
        relativeLayout.addView(target);

        loadBullets(relativeLayout);

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
        // final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);

        // create a new goomba
        final Goomba goomba = new Goomba(this);



        // add goomba to view
        relativeLayout.addView(goomba);

        // final int height = goomba.getStartHeight();

        // final TextView showValue = addShowValue(goomba.getValue(), height);

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
                targetParams.setMargins(x,y , 0, 0);
                target.setLayoutParams(targetParams);

                // add target to the view
                relativeLayout.addView(target);

                // only shoot goomba's when user has bullets
                if (hasBullets) {

                    // add parameters for the value and set the text to the value
                    final TextView showValue = new TextView(getBaseContext());
                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                             RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    int correction = (int) (0.5 * goomba.getHeight());
                    params.setMargins(x + correction , y , 0, 0);
                    showValue.setLayoutParams(params);
                    showValue.setTextSize(30);
                    showValue.setTextColor(Color.parseColor("#FF0000"));
                    showValue.setText(String.valueOf(goomba.getValue()));

                    // show the value of the shot goomba
                    relativeLayout.addView(showValue);

                    // function for a shot goomba
                    goomba.shot();

                    // make the value rise and fade out
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(showValue, "alpha", 1.0f, 0.0f);
                    // ObjectAnimator rise = ObjectAnimator.ofFloat(showValue, "y", height , height - 40);
                    ObjectAnimator rise = ObjectAnimator.ofFloat(showValue, "y", y , y - 40);
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.play(fadeOut).with(rise);
                    animSet.setDuration(1200);
                    animSet.start();

                    if (music) {
                        // play a sound when the goomba is hit
                        mpGoomba.start();
                    }

                }

                // remove one bullet from the view
                ImageView usedBullet = (ImageView) findViewById(numberOfBullets - 1);
                relativeLayout.removeView(usedBullet);

                // update number of bullets
                numberOfBullets --;

                if (numberOfBullets == 0) {
                    target.setBackgroundResource(R.drawable.target_empty);
                    hasBullets = false;
                    addReloadButton();
                }

                // update number of scored points
                points += goomba.getValue();
                final TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView2.setText(String.valueOf(points));

            }
        });

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

    public TextView addShowValue(int value, int height) {

        // create textbox to show the number of points
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                 RelativeLayout.LayoutParams.WRAP_CONTENT,
                 RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(80, height, 0, 0);
        final TextView showValue = new TextView(getBaseContext());


        showValue.setLayoutParams(params);

        showValue.setText(String.valueOf(value));
        showValue.setTextColor(R.drawable.font);
        showValue.setTextSize(35);
        showValue.setTypeface(null, Typeface.BOLD);


        // RelativeLayout layout =(RelativeLayout)findViewById(R.id.layout1);
        relativeLayout.addView(showValue);
        showValue.setVisibility(View.INVISIBLE);

        return showValue;

    }

    /**
     *  Handle touches on the screen
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
                int correction = (int) (0.5 * targetSize);

                // set target to the coordinates of the touch
                targetParams.setMargins(x - correction,y - correction, 0, 0);
                target.setLayoutParams(targetParams);

                // add target to the view
                relativeLayout.addView(target);

                ImageView usedBullet = (ImageView) findViewById(numberOfBullets - 1);
                relativeLayout.removeView(usedBullet);

                numberOfBullets --;

                if (numberOfBullets == 0) {
                    hasBullets = false;
                    target.setBackgroundResource(R.drawable.target_empty);
                    // relativeLayout.addView(target);
                    addReloadButton();
                }

                // finger touches the screen
                break;

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                break;

            case MotionEvent.ACTION_UP:
                // finger leaves the screen
                break;
        }

        // tell the system that we handled the event and no further processing is required
        return true;
    }

    public void loadBullets(View view) {

        numberOfBullets = 10;


        if (music) {
            // play a sound when the user reload the buttons
            mpReload.start();
        }

        // place the number of bullets in the view
        for (int i = 0; i < numberOfBullets; i++) {
            // add bullets to the right bottom corner of the screen
            bullet = new ImageView(this);
            bullet.setBackgroundResource(R.drawable.bullet_bill);
            RelativeLayout.LayoutParams bulletParams = new RelativeLayout.LayoutParams(20, 70);

            // set bullet in the bottom right corner
            bulletParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            bulletParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bulletParams.setMargins(0,0,i*25,0);

            bullet.setId(i);
            bullet.setLayoutParams(bulletParams);
            relativeLayout.addView(bullet);
        }

        hasBullets = true;
        target.setBackgroundResource(R.drawable.target_small);

    }

    public void addReloadButton() {

        if (music) {

            // play a sound when there are no bullets left
            mpEmpty.start();
        }

        // vibrate for 0,5 second
        Vibrator v = (Vibrator) this.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        // show reload button
        final Button reloadButton = new Button(getBaseContext());
        reloadButton.setText("Reload");

        // put reload button in the bottom right of the screen
        final RelativeLayout.LayoutParams reloadParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        reloadParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        reloadParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        reloadParams.setMargins(0,0,10,10);
        reloadButton.setLayoutParams(reloadParams);


        // make the reloadbutton green
        reloadButton.setBackgroundResource(R.drawable.rounded_button);

        // enable the default sound of the button
        reloadButton.setSoundEffectsEnabled(false);

        // add button to the view
        relativeLayout.addView(reloadButton);

        // when the reload button is clicked, call load bullets
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBullets(v);

                relativeLayout.removeView(reloadButton);
            }
        });

    }

    public void loadMusicButton() {

        // Get the value of music, music on (true) by default
        music = prefs.getBoolean("music", true);

        Button button = (Button) findViewById(R.id.music);

        // set button to music on or off
        if (music) {
            button.setBackgroundResource(R.drawable.music_on);

        }
        else {
            button.setBackgroundResource(R.drawable.music_off);

            // set volume of the background music to zero
            mpBackground.setVolume(0.0f, 0.0f);
        }

    }

    public void toggleMusic(View view) {

        // change the value of music
        music = !music;

        // open the Shared Preferences editor and save the new value of music
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("music", music);
        editor.commit();

        // find the clicked music button
        Button button = (Button) findViewById(R.id.music);

        // change the image of the music button
        if (music) {
            button.setBackgroundResource(R.drawable.music_on);

            // set volume of the background music
            mpBackground.setVolume(0.0f, 0.8f);
        }
        else {
            button.setBackgroundResource(R.drawable.music_off);

            // set volume of the background music to zero
            mpBackground.setVolume(0.0f, 0.0f);
        }
    }


    /**
     *  Finish activity when game is send to the background
     */
    @Override
    protected void onStop() {
        super.onStop();

        // stop the background music
        mpBackground.stop();

        // stop the timer
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        // finish the activity
        finish();
    }

}
