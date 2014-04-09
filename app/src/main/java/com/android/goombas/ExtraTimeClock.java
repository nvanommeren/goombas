/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the Clock which appears in the screen and gives the user
 * extra time to play the game when it is clicked, extends the
 * Button class.
 *
 ***********************************************************************/

package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Random;


public class ExtraTimeClock extends Button {

    /** The width of the screen */
    int screenX;

    /** The height of the screen */
    int screenY;

    Context myContext;

    protected ExtraTimeClock(Context context) {
        super(context);

        this.myContext = context;

        // set background image of the clock
        this.setBackgroundResource(R.drawable.clock_small);

        // get the size of the display
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenX = screenSize.x;
        screenY = screenSize.y;

        // Pick a random x position for the clock, but make sure the clock fits in the screen
        int startX = new Random().nextInt(screenX - 80);

        // Add parameters for the size of the clock
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(52, 64);
        params.setMargins(startX,-100,0,0);
        this.setLayoutParams(params);


    }

    /**
     * Set an animation for moving the clock down the screen in 4 seconds.
     */
    public void setAnimation() {

        // start outside the screen and move down until the clock is out of sight
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(this, "y", -100, screenY + 100);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(moveDown);

        // Set duration to 4000 milliseconds
        animSet.setDuration(4000);

        animSet.start();

    }
}
