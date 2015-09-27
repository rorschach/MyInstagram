package me.rorschach.myinstagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import butterknife.InjectView;
import me.rorschach.myinstagram.Utils;
import me.rorschach.myinstagram.adapter.CommentsAdapter;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.view.SendCommentButton;

public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener {

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.f);
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator(2.f);

    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;
    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.etComment)
    AppCompatEditText etComment;
    @InjectView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private int drawingStartLocation;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupComments();
        setupSendCommentButton();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void setupComments() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            commentsAdapter.addItem();
            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

            etComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation - 100);
        llAddComment.setTranslationY(100);

        contentRoot.animate()
                .scaleY(1f)
                .setDuration(200)
                .setInterpolator(DECELERATE_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate()
                .translationY(0)
                .setInterpolator(DECELERATE_INTERPOLATOR)
                .setDuration(300)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        llAddComment.animate()
                .translationY(100)
                .setInterpolator(ACCELERATE_INTERPOLATOR)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        commentsAdapter.deleteItems();
                        contentRoot.setPivotY(drawingStartLocation - 100);
                        contentRoot.animate()
                                .scaleY(0.1f)
                                .setStartDelay(100)
                                .setDuration(200)
                                .setInterpolator(ACCELERATE_INTERPOLATOR)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        CommentsActivity.super.onBackPressed();
                                        overridePendingTransition(0, 0);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private boolean validateComment() {
        if(TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }
}
