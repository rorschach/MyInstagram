package me.rorschach.myinstagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.Utils;
import me.rorschach.myinstagram.adapter.FeedAdapter;
import me.rorschach.myinstagram.view.FeedContextMenu;
import me.rorschach.myinstagram.view.FeedContextMenuManager;


public class MainActivity extends BaseDrawerActivity
        implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.content)
    CoordinatorLayout clContent;
    @InjectView(R.id.fabCreate)
    FloatingActionButton fabCreate;
    @InjectView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private float toolBarLocation;

    private OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setupFeed();

        toolBarLocation = Utils.dpToPx(56);
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(10, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.fabCreate)
    public void clickFab() {
        Toast.makeText(this, "Take Photos", Toast.LENGTH_SHORT).show();
    }

    private void setupFeed() {
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(manager);
        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void startIntroAnimation() {
        int fabSize = Utils.dpToPx(56);
        fabCreate.setTranslationY(2 * fabSize);

        getToolbar().setTranslationY(-toolBarLocation);
        getIvLogo().setTranslationY(-toolBarLocation);
        getInboxMenuItem().getActionView().setTranslationY(-toolBarLocation);

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
        feedAdapter.updateItems(10, false);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startLocation = new int[2];
        v.getLocationOnScreen(startLocation);
        startLocation[0] += v.getWidth() / 2;
        toolBarLocation = rvFeed.getTranslationY();
        Log.d("toolBarLocation", toolBarLocation + ".");
        UserProfileActivity.startUserProfileFromLocation(startLocation, toolBarLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }
}
