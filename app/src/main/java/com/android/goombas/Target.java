/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the target, extends the ImageView.
 *
 ***********************************************************************/

package com.android.goombas;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Target extends ImageView {

    protected int size;

    /** Parameters for the target */
    protected RelativeLayout.LayoutParams targetParams;

    protected Target(Context context) {
        super(context);

        // Define the size of the target
        this.size = 80;

        // Set parameters for the size of the target
        this.targetParams = new RelativeLayout.LayoutParams(size, size);
        this.setLayoutParams(targetParams);

        }

    /**
     * Sets parameters for the location of the target to x and y.
     */
    public void setTargetParams(int x, int y) {

        // Set parameters for the location of the target
        targetParams.setMargins(x, y, 0, 0);
        this.setLayoutParams(targetParams);

    }

    /**
     * Returns size of the target
     */
    public int getSize() {
        return size;
    }
}
