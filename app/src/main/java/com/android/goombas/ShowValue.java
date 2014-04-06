/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the value shown when you hit a Goomba.
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

    protected int x;
    protected int y;

    protected ShowValue(Context context, int x, int y) {
        super(context);

        this.x = x;
        this.y = y;

    }


    public void setValueParams(Goomba goomba) {

        // set parameters for the value of the shot goomba
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int correction = (int) (0.5 * goomba.getHeight());
        params.setMargins(x + correction , y , 0, 0);
        this.setLayoutParams(params);
        this.setTextSize(30);
        this.setTextColor(Color.parseColor("#FF0000"));
        this.setText(String.valueOf(goomba.getValue()));
    }

    public void valueAnimation() {

        // make the value rise and fade out
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        ObjectAnimator rise = ObjectAnimator.ofFloat(this, "y", y , y - 40);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(fadeOut).with(rise);
        animSet.setDuration(1200);
        animSet.start();
    }
}
