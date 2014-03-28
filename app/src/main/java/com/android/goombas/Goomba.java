package com.android.goombas;

import android.animation.Animator;
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
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Nikki on 21-3-14.
 */

public class Goomba extends ImageButton {

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

    protected int flyDuriation;

    protected int size;

    AnimatorSet animSet;

    Context myContext;

    /**
    * Contructs a goomba based on a start height and a direction.
    */
    protected Goomba(Context context) {
        super(context);

        myContext = context;

        this.setId(1);

        // calculate the height and width of the screen
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenY = screenSize.y;
        screenX = screenSize.x;
        // int width = display.getWidth();  // ?for API levels below 14



        // pick random the sort of the goomba verplaatsen naar gameActivity
        int index = new Random().nextInt(3);

        // goomba far away
        if (index == 0) {
            this.flyDuriation = 4000;
            this.value = 25;
            this.size = 60;
        }
        // goomba middle far away
        else if (index == 1) {
            this.flyDuriation = 5000;
            this.value = 10;
            this.size = 90;
        }
        // goomba close
        else {
            this.flyDuriation = 6000;
            this.value = 5;
            this.size = 120;
        }

        // pick a random starting height, but make sure the goomba fits in the screen
        y = new Random().nextInt(screenY - size);

        // true if goomba starts from left
        fromLeft = new Random().nextBoolean();


    }

    /**
     * Set the start height of the goomba
     */
    public void setY(View btn) {
        // set starting height of the goomba
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
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
        ObjectAnimator anim2;



        // when goomba flies from left to right
        if (fromLeft) {
            anim1 = ObjectAnimator.ofFloat(this,
                    "x", -size, screenX); // ??scale
            // anim2 = ObjectAnimator.ofFloat(pointsView,
            //         "x", -size, screenX);
            this.setBackgroundResource(R.drawable.flying_goomba);
        }

        // when goomba flies from right to left
        else {
            anim1 = ObjectAnimator.ofFloat(this,
                    "x", screenX , -size); // ??scale
            // anim2 = ObjectAnimator.ofFloat(pointsView,
//                    "x", screenX , -size);

            this.setBackgroundResource(R.drawable.flying_goomba_right);

        }

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) this.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        animSet = new AnimatorSet();
        animSet.play(anim1); //with(anim2)
        animSet.setDuration(flyDuriation);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.start();

        // ?? werkt niet hide the goomba when the animation is finished
        if (!animSet.isRunning()) {
            this.setEnabled(false);
            this.setVisibility(GONE);
        }

    }

    public void shot() {

        // change picture to dead goomba
        this.setBackgroundResource(R.drawable.dead_goomba);

        // make sure the user can not "shoot" the goomba again
        this.setEnabled(false);

        // make value of the goomba visible to the user


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

    public int getSize() {
        return size;
    }

}