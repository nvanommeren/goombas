package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by Nikki on 21-3-14.
 */

public class Goomba extends Button {

    // field variables

    /** The start height of the goomba */
    protected int y;

    /** True if the goomba starts from the left of the screen */
    protected boolean fromLeft;

    /** The value of a goomba */
    protected int value;

    /** The height of the screen */
    protected int screenY;

    /** The width of the screen */
    protected int screenX;

    AnimatorSet animSet;

    Context myContext;

    /**
    * Contructs a goomba based on a start height and a direction.
    */
    protected Goomba(Context context) {
        super(context);

        myContext = context;

        // set image of a goomba as the background of the button
        // this.setBackgroundResource(R.drawable.up_paragoomba);
        this.setId(1);

        // calculate the height and width of the screen
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenY = size.y;
        screenX = size.x;
        // int width = display.getWidth();  // ?for API levels below 14

        // pick a random starting height, but make sure the goomba fits in the screen
        y = new Random().nextInt(screenY - 80);

        // true if goomba starts from left
        fromLeft = getRandomBoolean();

        this.value = 25;
    }

    /**
     * Set the start height of the goomba
     */
    public void setY(View btn) {
        // set starting height of the goomba
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, y, 0, 0); // left,top,right,bottom
        btn.setLayoutParams(params);
    }

    /**
     * return true if the goomba is starting from left
     */
    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public void startAnimation() {

        // scaling for converting dpi to pixels
        float scale = getResources().getDisplayMetrics().density;

        // pick random starting height for the goomba
        setY(this);



        ObjectAnimator anim1;

        if (fromLeft) {
             anim1 = ObjectAnimator.ofFloat(this,
                    "x", -100.0f * scale, screenX * scale);

            this.setBackgroundResource(R.drawable.flying_goomba);
        }
        else {
            anim1 = ObjectAnimator.ofFloat(this,
                    "x", screenX * scale + 100.0f, -100.0f * scale);

            this.setBackgroundResource(R.drawable.flying_goomba_right);
        }

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) this.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        animSet = new AnimatorSet();
        animSet.play(anim1);
        animSet.setDuration(5000);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.start();
    }

    public void shot() {

        // change picture to dead goomba
        this.setBackgroundResource(R.drawable.dead_goomba);

        // make sure the user can not "shoot" the goomba again
        this.setEnabled(false);

        // stop the movement of the goomba
        animSet.cancel();

        // scaling for converting dpi to pixelsfloat scale
        float scale = getResources().getDisplayMetrics().density;

        // make the goomba fade out
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        ObjectAnimator fall = ObjectAnimator.ofFloat(this, "y", y, y + 80.0f * scale);
        AnimatorSet animSet2 = new AnimatorSet();
        animSet2.play(fadeOut).with(fall);
        animSet2.setDuration(1000);
        animSet2.start();

    }

    public int getValue() {
        return value;
    }

}