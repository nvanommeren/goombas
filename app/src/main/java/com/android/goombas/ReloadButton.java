/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Class for the Reload Button, called when the user is out of bullets
 * in the bottom right corner of the screen.
 *
 ***********************************************************************/

package com.android.goombas;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ReloadButton extends Button {

    Context myContext;

    protected ReloadButton(Context context) {
        super(context);

        myContext = context;

    }

    public void onAppearance(GamePlay game) {

        // play a sound when there are no bullets left
        game.playEmpty();

        // vibrate for 0,5 second
        Vibrator v = (Vibrator) this.myContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public void setReloadParams() {

        this.setText("Reload");

        // put reload button in the bottom right of the screen
        final RelativeLayout.LayoutParams reloadParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        reloadParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        reloadParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        reloadParams.setMargins(0,0,10,10);

        this.setLayoutParams(reloadParams);

        // make the reloadbutton green
        this.setBackgroundResource(R.drawable.rounded_button);

        // enable the default sound of the button
        this.setSoundEffectsEnabled(false);

    }
}
