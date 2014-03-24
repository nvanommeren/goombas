package com.android.goombas;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Nikki on 21-3-14.
 */

public class Goomba extends Button {

    // field variables

    /** The start height of the goomba */
    protected float	y;

    /** True if the goomba starts from the left of the screen */
    protected boolean fromLeft;

     /**
     * Contructs a goomba based on a start height and a direction.
     */
    protected Goomba(Context context, int y, boolean fromLeft) {
        super(context);

        // set image of a goomba as the background of the button
        this.setBackgroundResource(R.drawable.moving_goomba);
        this.setId(1);

        this.y = y;
        this.fromLeft = fromLeft;
    }

}