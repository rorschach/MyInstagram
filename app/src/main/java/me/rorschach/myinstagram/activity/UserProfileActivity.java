package me.rorschach.myinstagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.Utils;
import me.rorschach.myinstagram.adapter.UserProfileAdapter;
import me.rorschach.myinstagram.utils.CircleTransformation;
import me.rorschach.myinstagram.view.RevealBackgroundView;
import timber.log.Timber;

/**
 * Created by hl810 on 15-8-30.
 */
public class UserProfileActivity extends BaseDrawerActivity implements
        RevealBackgroundView.OnStateChangeListener {

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @InjectView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @InjectView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @InjectView(R.id.vUserDetails)
    View vUserDetails;
    @InjectView(R.id.btnFollow)
    Button btnFollow;
    @InjectView(R.id.vUserStats)
    View vUserStats;
    @InjectView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    @InjectView(R.id.fabCreate)
    FloatingActionButton fabCreate;

    private float toolBarLocation;
    private static final String TAG = "UserProfileActivity";

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    public static final String ARG_TOOLBAR_LOCATION = "toolBar_location";

    private boolean pendingIntroAnimation;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private int[] startingLocation;

    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    @OnClick(R.id.fabCreate)
    public void clickFab() {
        Toast.makeText(this, "Take Photos", Toast.LENGTH_SHORT).show();
    }

    public static void startUserProfileFromLocation(
            int[] startingLocation, float toolBarLocation, Activity activity) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(ARG_TOOLBAR_LOCATION, toolBarLocation);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = getString(R.string.user_profile_photo);

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);

        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            toolBarLocation = getIntent().getFloatExtra(ARG_TOOLBAR_LOCATION, 0.0f);
            startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }


    @Override
    public void onStateChange(int state) {
        Timber.d(TAG, state);
        if (RevealBackgroundView.STATE_ANIMATE_IN_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else if (RevealBackgroundView.STATE_ANIMATE_OUT == state) {
            Timber.d(TAG, state + ":::");
            animateBack();
        } else if (RevealBackgroundView.STATE_ANIMATE_OUT_FINISHED == state) {
            UserProfileActivity.this.getWindow().getDecorView().setVisibility(View.INVISIBLE);
            UserProfileActivity.this.finish();
            overridePendingTransition(0, 0);
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(300)
                .setInterpolator(DECELERATE_INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0);

        vUserProfileRoot.animate().
                translationY(0).
                setDuration(300).
                setInterpolator(DECELERATE_INTERPOLATOR);
        ivUserProfilePhoto.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(100)
                .setInterpolator(DECELERATE_INTERPOLATOR);
        vUserDetails.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(200)
                .setInterpolator(DECELERATE_INTERPOLATOR);
        vUserStats.animate()
                .alpha(1)
                .setDuration(200)
                .setStartDelay(400)
                .setInterpolator(DECELERATE_INTERPOLATOR)
                .start();
    }


    @Override
    public void onBackPressed() {
        vRevealBackground.endFromLocation(startingLocation);
    }

    private void animateBack() {
        AnimatorSet animationSet = new AnimatorSet();

        ObjectAnimator photoAnimator = ObjectAnimator.ofFloat(
                rvUserProfile, "Y", 0, rvUserProfile.getHeight() * 2);
        photoAnimator.setDuration(200);
        photoAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator tabsAnimator = ObjectAnimator.ofFloat(
                tlUserProfileTabs, "Y", 0, -tlUserProfileTabs.getHeight()-vUserProfileRoot.getHeight());
        tabsAnimator.setDuration(400);
        tabsAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator rootAnimator = ObjectAnimator.ofFloat(
                vUserProfileRoot, "Y", 0, -vUserProfileRoot.getHeight());
        rootAnimator.setDuration(400);
        rootAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator avatarAnimator = ObjectAnimator.ofFloat(
                ivUserProfilePhoto, "Y", 0, -ivUserProfilePhoto.getHeight());
        avatarAnimator.setDuration(300);
        avatarAnimator.setStartDelay(100);
        avatarAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator detailAnimator = ObjectAnimator.ofFloat(
                vUserDetails, "Y", 0, -vUserDetails.getHeight());
        detailAnimator.setDuration(300);
        detailAnimator.setStartDelay(200);
        detailAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator statsAnimator = ObjectAnimator.ofFloat(
                vUserStats, "alpha", 1 , 0);
        statsAnimator.setDuration(200);
        statsAnimator.setStartDelay(400);
        statsAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        animationSet.play(photoAnimator);
        animationSet.play(tabsAnimator)
                .with(rootAnimator).with(avatarAnimator).with(detailAnimator).with(statsAnimator)
                .after(photoAnimator);
        animationSet.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        int fabSize = Utils.dpToPx(56);
        fabCreate.setTranslationY(2 * fabSize);

        getToolbar().setTranslationY(toolBarLocation);
        getIvLogo().setTranslationY(toolBarLocation);
        getInboxMenuItem().getActionView().setTranslationY(toolBarLocation);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);

        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);

        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                }).start();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(OVERSHOOT_INTERPOLATOR)
                .setDuration(ANIM_DURATION_FAB)
                .setStartDelay(300)
                .start();
    }

}
