/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the value shown when you hit a Goomba or click an extra time
 * clock.
 *
 ***********************************************************************/

package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowValue extends TextView {

    /** X-coordinate of the Show Value */
    protected int x;

    /** Y-coordinate of the Show Value */
    protected int y;

    protected ShowValue(Context context, int x, int y) {
        super(context);

        this.x = x;
        this.y = y;

    }

    /**
     * Sets the parameters for the location of the Show Value and adds the value,
     * depending on the value of the clicked object.
     */
    public void setValueParams(int size, String value) {

        // Sets parameters for the location of the Show Value
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int correction = (int) (0.5 * size);
        params.setMargins(x + correction , y , 0, 0);
        this.setLayoutParams(params);

        // Sets the text size and text color
        this.setTextSize(25);
        this.setTextColor(Color.parseColor("#FF0000"));

        // Adds the value of the shot Goomba
        this.setText(value);
    }

    /**
     * Adds an animation to the show value.
     */
    public void valueAnimation() {

        // Make the ShowValue rise and fade out
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        ObjectAnimator rise = ObjectAnimator.ofFloat(this, "y", y , y - 40);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(fadeOut).with(rise);

        // Sets the duration and starts the animation
        animSet.setDuration(1200);
        animSet.start();
    }
}
