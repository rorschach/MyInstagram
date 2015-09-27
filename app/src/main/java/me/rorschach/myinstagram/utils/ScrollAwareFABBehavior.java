package me.rorschach.myinstagram.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import me.rorschach.myinstagram.Utils;

/**
 * Created by hl810 on 15-8-25.
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();
    private boolean mIsAnimatingOut = false;

    public ScrollAwareFABBehavior() {
        super();
    }

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                        nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            animateIn(child);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to
    // hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {
        ViewCompat.animate(button)
                .translationY(2 * Utils.dpToPx(56))
                .setDuration(200)
                .setInterpolator(ACCELERATE_INTERPOLATOR)
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }

    // Same animation that FloatingActionButton.Behavior
    // uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        ViewCompat.animate(button)
                .translationY(0)
                .setDuration(400)
                .setStartDelay(300)
                .setInterpolator(OVERSHOOT_INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                })
                .start();
    }
}
