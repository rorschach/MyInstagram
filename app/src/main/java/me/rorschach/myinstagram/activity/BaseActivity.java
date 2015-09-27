package me.rorschach.myinstagram.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import me.rorschach.myinstagram.R;

/**
 * Created by hl810 on 15-8-30.
 */
public class BaseActivity extends AppCompatActivity {

    @Optional
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Optional
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;

    private MenuItem inboxMenuItem;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        injectViews();
    }

    protected void injectViews() {
        ButterKnife.inject(this);
        setupToolbar();
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        inboxMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Downloading", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MenuItem getInboxMenuItem() {
        return inboxMenuItem;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }
}
