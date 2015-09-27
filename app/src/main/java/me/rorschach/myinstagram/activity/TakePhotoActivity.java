package me.rorschach.myinstagram.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rorschach.myinstagram.R;
import me.rorschach.myinstagram.view.RevealBackgroundView;
import me.rorschach.myinstagram.Utils;

/**
 * Created by hl810 on 15-9-23.
 */
public class TakePhotoActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener {


    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.vPhotoRoot)
    View vTakePhotoRoot;
    @InjectView(R.id.vShutter)
    View vShutter;
    @InjectView(R.id.ivTakenPhoto)
    ImageView ivTakenPhoto;
    @InjectView(R.id.vUpperPanel)
    ViewSwitcher vUpperPanel;
    @InjectView(R.id.vLowerPanel)
    ViewSwitcher vLowerPanel;
    @InjectView(R.id.cameraView)
    CameraView cameraView;
    @InjectView(R.id.rvFilters)
    RecyclerView rvFilters;
    @InjectView(R.id.btnTakePhoto)
    Button btnTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ButterKnife.inject(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    private void setupRevealBackground() {

    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_ANIMATE_OUT_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
//            startIntroAnimation();
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }
}
