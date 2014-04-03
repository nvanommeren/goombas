package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by Nikki on 23-3-14.
 */
public class gamePlay {

    private final Context myContext;

    private SharedPreferences prefs;

    private int points;
    private int numberOfBullets;
    private boolean music;

    // MediaPlayers for the sounds
    private MediaPlayer mpReload;
    private MediaPlayer mpGoomba;
    private MediaPlayer mpEmpty;
    private MediaPlayer mpBackground;

    /**
     * Construct the variables for the shoot the goomba's game
     */
    protected gamePlay(Context context, SharedPreferences preferences) {

        this.myContext = context;

        this.prefs = preferences;

        // set scored points to zero
        this.points = 0;

        // set number of bullets
        this.numberOfBullets = 10;

        // Get the value of music, music on (true) by default
        this.music = prefs.getBoolean("music", true);

        // create a sound for reloading the bullets
        mpReload = MediaPlayer.create(myContext, R.raw.gun_cocking_01);

        // create a sound for shooting a goomba
        mpGoomba = MediaPlayer.create(myContext, R.raw.smw_stomp);

        // create a sound for shooting when the gun is empty
        mpEmpty = MediaPlayer.create(myContext, R.raw.gun_empty);

        // create a background sound and start it
        mpBackground = MediaPlayer.create(myContext, R.raw.super_mario_bros);
        mpBackground.setVolume(0.0f, 0.8f);
        mpBackground.start();

    }

    /**
     * Switches the state of the music.
     */
    public void switchMusic(Button button) {
        // change the value of music
        music = !music;

        // open the Shared Preferences editor and save the new value of music
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("music", music);
        editor.commit();


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
     * Play empty bullet sound.
     */
    public void playEmpty() {

        if (music) {

            // play a sound when there are no bullets left
            mpEmpty.start();
        }
    }

    /**
     * Play shot Goomba sound.
     */
    public void playShotGoomba() {

        if (music) {
            // play a sound when the goomba is hit
            mpGoomba.start();
        }
    }

    /**
     * Play reload sound.
     */
    public void playReload() {

        numberOfBullets = 10;

        if (music) {
            // play a sound when the user reload the buttons
            mpReload.start();
        }

     }

    /**
     * Set the volume of the background music to zero.
     */
    public void muteBackground() {

        mpBackground.setVolume(0.0f, 0.0f);
    }

    /**
     * Stop the background music
     */
    public void stopMusic() {
        mpBackground.stop();
    }

    /**
     * Update the number of scored points
     */
    public void updatePoints(Goomba goomba) {
        // update number of scored points
        points += goomba.getValue();
    }

    /**
     * Update the number of bullets
     */
    public void updateBullets(ImageView target) {

        // update number of bullets
        numberOfBullets --;

        if (numberOfBullets == 0) {
            target.setBackgroundResource(R.drawable.target_empty);
        }
    }


    /**
     * Set parameters for adding a single bullet
     */
    public ImageView addBullet(ImageView bullet, int i) {
        bullet.setBackgroundResource(R.drawable.bullet_bill);
        RelativeLayout.LayoutParams bulletParams = new RelativeLayout.LayoutParams(20, 70);

        // set bullet in the bottom right corner
        bulletParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bulletParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bulletParams.setMargins(0,0,i*25,0);

        bullet.setId(i);
        bullet.setLayoutParams(bulletParams);

        return bullet;
    }


    /**
     * Set parameters for the reload button
     */
    public Button setReloadButton() {

        // play a sound when there are no bullets left
        playEmpty();

        // vibrate for 0,5 second
        Vibrator v = (Vibrator) this.myContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        // show reload button
        final Button reloadButton = new Button(myContext);
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

        return reloadButton;

    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public boolean getMusicValue() {
        return music;
    }

    public int getPoints() {
        return points;
    }

}

