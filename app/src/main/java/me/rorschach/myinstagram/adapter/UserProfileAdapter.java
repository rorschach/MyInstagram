package me.rorschach.myinstagram.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.Utils;
import timber.log.Timber;

/**
 * Created by hl810 on 15-9-2.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    private final Context context;
    private final int cellSize;

    private final List<String> photos;

    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    public UserProfileAdapter(Context context) {
        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 3;
        this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_photo, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPhoto((PhotoViewHolder) holder, position);
    }

    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.with(context)
                .load(photos.get(position))
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {

                    @Override
                    public void onSuccess() {
                        animateUserPhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (lastAnimatedItem < position) {
            lastAnimatedItem = position;
        }
    }

    private void animateUserPhoto(PhotoViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getAdapterPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getAdapterPosition() * 30;

            viewHolder.flRoot.setScaleX(0);
            viewHolder.flRoot.setScaleY(0);
            viewHolder.flRoot.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(200)
                    .setInterpolator(DECELERATE_INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();

            Log.d("AdapterAnimation", String.valueOf(viewHolder.getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.flRoot)
        FrameLayout flRoot;
        @InjectView(R.id.ivPhoto)
        ImageView ivPhoto;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

}
