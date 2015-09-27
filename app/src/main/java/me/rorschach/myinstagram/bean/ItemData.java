package me.rorschach.myinstagram.bean;

import android.widget.ImageView;

/**
 * Created by hl810 on 15-8-27.
 */
public class ItemData {

    public ImageView avatar;
    public String comment;

    public ItemData(ImageView avatar, String comment) {
        this.avatar = avatar;
        this.comment = comment;
    }
}
