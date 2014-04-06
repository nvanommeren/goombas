/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for a Goomba, the object you need to click in order to get
 * points, extends the Image Button class.
 *
 ***********************************************************************/

package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.Properties;
import java.util.Random;

public class Goomba extends ImageButton {

    /** The height of the screen */
    protected int screenY;

    /** The width of the screen */
    protected int screenX;

    /** The value of a goomba */
    protected int value;

    protected int flyDuriation;

    protected int size;

    /** The start height of the goomba */
    protected int y;

    /** True if the goomba starts from the left of the screen */
    protected boolean fromLeft;

    /** Animator Set for the movement of the Goomba */
    protected AnimatorSet animSet;

    protected Context myContext;

    /**
    * Contructs a Goomba based on a start height and a direction.
    */
    protected Goomba(Context context, int index) {
        super(context);

        myContext = context;

        // Calculate the height and width of the screen
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenY = screenSize.y;
        screenX = screenSize.x;
        // int width = display.getWidth();  // ?for API levels below 14

        // Get file with the configuration properties
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        Properties prop = assetsPropertyReader.getProperties("config.properties");

        // Get the values, fly durations and sizes as defined in the config file
        final String[] values = prop.getProperty("value").split(",");
        final String[] flyDurations = prop.getProperty("flyDuration").split(",");
        final String[] sizes = prop.getProperty("size").split(",");

        // Set the value, fly duration and size of the Goomba
        this.value = Integer.parseInt(values[index]);
        this.flyDuriation = Integer.parseInt(flyDurations[index]);
        this.size = Integer.parseInt(sizes[index]);

        // Pick a random starting height, but make sure the Goomba fits in the screen
        y = new Random().nextInt(screenY - size);

        // True if Goomba starts from left
        fromLeft = new Random().nextBoolean();

        // Enable the default sound of the button
        this.setSoundEffectsEnabled(false);


    }

    /**
     * Sets the starting height of the Goomba
     */
    private void setY(View goomba) {

        // Add parameters for the starting height to the Goomba
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.setMargins(0, y, 0, 0); // left,top,right,bottom
        goomba.setLayoutParams(params);

    }

    /**
     * Starts the movement of the Goomba.
     */
    public void startAnimation() {

        // Pick random starting height for the Goomba
        setY(this);

        ObjectAnimator anim1;

        // When Goomba moves from left to right
        if (fromLeft) {
            anim1 = ObjectAnimator.ofFloat(this,
                    "x", -size, screenX);
            this.setBackgroundResource(R.drawable.flying_goomba);
        }

        // When Goomba moves from right to left
        else {
            anim1 = ObjectAnimator.ofFloat(this,
                    "x", screenX , -size);

            this.setBackgroundResource(R.drawable.flying_goomba_right);

        }

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) this.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        animSet = new AnimatorSet();
        animSet.play(anim1);
        animSet.setDuration(flyDuriation);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.start();

    }

    /**
     * Called when a Goomba is clicked, changes the picture of the Goomba and enables
     * the user to click the Goomba again.
     */
    public void shot() {

        // Change picture to dead Goomba
        this.setBackgroundResource(R.drawable.dead_goomba);

        // Make sure the user can not "shoot" the Goomba again
        this.setEnabled(false);

        // Stop the movement of the Goomba
        animSet.cancel();

        // Scaling for converting dpi to pixelsfloat scale
        float scale = getResources().getDisplayMetrics().density;

        // Make the Goomba fade out and move downwards
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        ObjectAnimator fall = ObjectAnimator.ofFloat(this, "y", y, y + 80.0f * scale);
        AnimatorSet animSet2 = new AnimatorSet();
        animSet2.play(fadeOut).with(fall);
        animSet2.setDuration(1000);
        animSet2.start();

    }

    /**
     * Returns the value of the Goomba.
     */
    public int getValue() {
        return value;
    }


}