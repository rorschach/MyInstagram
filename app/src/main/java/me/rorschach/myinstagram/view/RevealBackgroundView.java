package me.rorschach.myinstagram.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import timber.log.Timber;

/**
 * Created by hl810 on 15-8-30.
 */
public class RevealBackgroundView extends View {

    private static final String TAG = "RevealBackgroundView";

    public static final int STATE_NOT_START = 0;
    public static final int STATE_ANIMATE_IN = 1;
    public static final int STATE_ANIMATE_IN_FINISHED = 2;
    public static final int STATE_ANIMATE_OUT = 3;
    public static final int STATE_ANIMATE_OUT_FINISHED = 4;

    private int state = STATE_NOT_START;

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    private int startLocationX;
    private int startLocationY;
    private int currentRadius;

    public ObjectAnimator revealAnimator;

    private OnStateChangeListener onStateChangeListener;

    private Paint fillPaint;

    public RevealBackgroundView(Context context) {
        super(context);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_ANIMATE_OUT_FINISHED) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        } else {
            canvas.drawCircle(startLocationX, startLocationY, currentRadius, fillPaint);
        }
    }

    public void setCurrentRadius(int radius) {
        this.currentRadius = radius;
        invalidate();
    }

    public void startFromLocation(int[] tapLocationOnScreen) {
        Timber.d(TAG, "startFromLocation");
        changeState(STATE_ANIMATE_IN);
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        revealAnimator = ObjectAnimator
                .ofInt(this, "currentRadius", 0, getWidth() + getHeight())
                .setDuration(200);
        revealAnimator.setInterpolator(ACCELERATE_INTERPOLATOR);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_ANIMATE_IN_FINISHED);
            }
        });
        revealAnimator.start();
        Timber.d(TAG, "start animation");
    }

    public void endFromLocation(int[] tapLocationOnScreen) {
        Timber.d(TAG, "endFromLocation");
        changeState(STATE_ANIMATE_OUT);

        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        revealAnimator = ObjectAnimator
                .ofInt(this, "currentRadius", getWidth() + getHeight(), 0)
                .setDuration(600);
        revealAnimator.setStartDelay(200);
        revealAnimator.setInterpolator(ACCELERATE_INTERPOLATOR);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_ANIMATE_OUT_FINISHED);
            }
        });
        revealAnimator.start();
        Timber.d(TAG, "start animation");
    }

    public void setToFinishedFrame() {
        changeState(STATE_ANIMATE_IN_FINISHED);
        invalidate();
    }

    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }

    private void changeState(int state) {
        if (this.state == state) {
            return;
        }

        this.state = state;
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(state);
        }

    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public static interface OnStateChangeListener {
        public void onStateChange(int state);
    }
}
