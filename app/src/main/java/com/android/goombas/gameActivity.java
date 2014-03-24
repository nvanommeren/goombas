package com.android.goombas;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

public class gameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_game);

        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.app.ActionBar actionBar = getActionBar();
        actionBar.hide();

        addGoomba();

        startAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_game, container, false);
            return rootView;
        }
    }

    public void addGoomba() {

        // find the view
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);

        // create a new goomba
        final Goomba goomba = new Goomba(this, 0, true);

        // hide button after it is clicked
        goomba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //when play is clicked show stop button and hide play button
            goomba.setVisibility(View.GONE);
;
            }
        });
;
        relativeLayout.addView(goomba);

    }

    public void startAnimation() {
        float scale = getResources().getDisplayMetrics().density;
        View someButton = findViewById(1);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(someButton,
                "x", -20.0f*scale, 600.0f*scale);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).after(500);
        animSet.setDuration(5000);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.start();
    }

}
