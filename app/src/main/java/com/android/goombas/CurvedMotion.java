package com.android.goombas;

/**
 * Created by Nikki on 1-4-14.
 */
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.animation.ObjectAnimator;
        import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewTreeObserver;
        import android.view.animation.DecelerateInterpolator;
        import android.widget.Button;
        import android.widget.RelativeLayout;
        import android.widget.RelativeLayout.LayoutParams;

/**
 * This app shows how to move a view in a curved path between two endpoints.
 * The real work is done by PathEvaluator, which interpolates along a path
 * using Bezier control and anchor points in the path.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the androiddevelopers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class CurvedMotion extends Activity {

    private static final DecelerateInterpolator sDecelerateInterpolator =
            new DecelerateInterpolator();
    boolean mTop = true;
    boolean mLeft = true;
    Button specialGoomba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curved_motion);

        // add special goomba to the screen
        specialGoomba = new Button(this);
        specialGoomba.setBackgroundResource(R.drawable.special_goomba);

        // set size of the special goomba
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);
        specialGoomba.setLayoutParams(params);

        // add special goomba to the view
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        layout.addView(specialGoomba);




            // if user clicks on the special goomba
            specialGoomba.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    // Capture current location of button
                    final int oldLeft = specialGoomba.getLeft();
                    final int oldTop = specialGoomba.getTop();

                    // Change layout parameters of button to move it
                    moveButton();

                    // Add OnPreDrawListener to catch button after layout but before drawing
                    specialGoomba.getViewTreeObserver().addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                public boolean onPreDraw() {
                                    specialGoomba.getViewTreeObserver().removeOnPreDrawListener(this);

                                    // Capture new location
                                    int left = specialGoomba.getLeft();
                                    int top = specialGoomba.getTop();
                                    int deltaX = left - oldLeft;
                                    int deltaY = top - oldTop;

                                    // Set up path to new location using a B�zier spline curve
                                    AnimatorPath path = new AnimatorPath();
                                    path.moveTo(-deltaX, -deltaY);
                                    path.curveTo(-(deltaX/2), -deltaY, 0, -deltaY/2, 0, 0);

                                    // Set up the animation
                                    final ObjectAnimator anim = ObjectAnimator.ofObject(
                                            CurvedMotion.this, "buttonLoc",
                                            new PathEvaluator(), path.getPoints().toArray());
                                    anim.setInterpolator(sDecelerateInterpolator);
                                    anim.start();
                                    return true;
                                }
                            });


                }
            });
        }


    /**
     * Toggles button location on click between top-left and bottom-right
     */
    private void moveButton() {
        LayoutParams params = (LayoutParams) specialGoomba.getLayoutParams();
        if (mTop && mLeft) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        else if (mTop) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        else if (mLeft) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        else {
            // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            // params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        specialGoomba.setLayoutParams(params);
        mTop = !mTop;
        mLeft = !mLeft;
    }

    /**
     * We need this setter to translate between the information the animator
     * produces (a new "PathPoint" describing the current animated location)
     * and the information that the button requires (an xy location). The
     * setter will be called by the ObjectAnimator given the 'buttonLoc'
     * property string.
     */
    public void setButtonLoc(PathPoint newLoc) {
        specialGoomba.setTranslationX(newLoc.mX);
        specialGoomba.setTranslationY(newLoc.mY);
    }

}