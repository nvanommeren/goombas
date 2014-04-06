/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for a "Shoot the Goomba's" game.
 *
 ***********************************************************************/

package com.android.goombas;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Properties;


public class GamePlay {

    private final Context myContext;

    private SharedPreferences prefs;

    private int points;
    private int numberOfBullets;
    private boolean music;

    /** MediaPlayers for the sounds */
    private MediaPlayer mpReload;
    private MediaPlayer mpGoomba;
    private MediaPlayer mpEmpty;
    private MediaPlayer mpBackground;

    /**
     * Construct the variables for the shoot the goomba's game
     */
    protected GamePlay(Context context, SharedPreferences preferences) {

        this.myContext = context;

        this.prefs = preferences;

        // set scored points to zero
        this.points = 0;

        // get file with the configuration properties
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(myContext);
        Properties prop = assetsPropertyReader.getProperties("config.properties");

        // set number of bullets as defined in the config file
        this.numberOfBullets = Integer.parseInt(prop.getProperty("numberOfBullets"));

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
     * Play reload sound and reset the number of bullets.
     */
    public void playReload() {

        // get file with the configuration properties
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(myContext);
        Properties prop = assetsPropertyReader.getProperties("config.properties");

        // set number of bullets as defined in the config file
        numberOfBullets = Integer.parseInt(prop.getProperty("numberOfBullets"));

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

