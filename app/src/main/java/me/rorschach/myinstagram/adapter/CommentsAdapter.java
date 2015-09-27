package me.rorschach.myinstagram.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.utils.RoundedTransformation;

import static android.provider.Settings.System.AIRPLANE_MODE_ON;

/**
 * Created by hl810 on 15-8-26.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.f);

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private CommentViewHolder commentViewHolder;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public CommentsAdapter(Context context) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.btn_fab_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false);
        commentViewHolder = new CommentViewHolder(view);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        switch (position % 3) {
            case 0:
                commentViewHolder.tvComment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
                break;
            case 1:
                commentViewHolder.tvComment.setText("Cupcake ipsum dolor sit amet bear claw.");
                break;
            case 2:
                commentViewHolder.tvComment.setText("Hello Instagram.");
                break;
        }

        Picasso.with(context)
                .load(R.drawable.ic_launcher)
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(new RoundedTransformation())
                .into(commentViewHolder.ivUserAvatar);
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.ivUserAvatar)
        ImageView ivUserAvatar;
        @InjectView(R.id.tvComment)
        TextView tvComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(DECELERATE_INTERPOLATOR)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }


    public void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public void deleteItems() {
        itemsCount = 0;
        notifyDataSetChanged();
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }


}
