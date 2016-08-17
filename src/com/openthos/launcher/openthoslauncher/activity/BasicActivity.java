package com.openthos.launcher.openthoslauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.openthos.launcher.openthoslauncher.R;

/**
 * Created by xu on 2016/8/4.
 */
public class BasicActivity extends Activity {

    public FrameLayout viewContainer;
    public ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v=View.inflate(this,R.layout.activity_background,null);
        super.setContentView(v);

        viewContainer = (FrameLayout)v.findViewById(R.id.content);
        backgroundImage= (ImageView)v.findViewById(R.id.background);
        backgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.system));
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {

        viewContainer.removeAllViews();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                     ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        viewContainer.addView(view, layoutParams);
    }
}
