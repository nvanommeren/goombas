/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the image of a single bullet in the bottom right corner
 * of the screen, extends the Image View class.
 *
 ***********************************************************************/


package com.android.goombas;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Bullet extends ImageView {

    protected Bullet(Context context) {
        super(context);

    }

    public void setBulletParameters(int i) {

        this.setBackgroundResource(R.drawable.bullet_bill);
        RelativeLayout.LayoutParams bulletParams = new RelativeLayout.LayoutParams(20, 70);

        // set bullet in the bottom right corner
        bulletParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bulletParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bulletParams.setMargins(0,0,i*25,0);

        this.setId(i);
        this.setLayoutParams(bulletParams);
    }
}
