package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.launcher3.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by xu on 2016/11/2.
 */
public class CompressDialog extends Dialog {
    private Context mContext;
    private String mPath;

    public CompressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CompressDialog(Context context, String path) {
        super(context);
        mContext = context;
        mPath = path;
    }

    public CompressDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected CompressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_compress);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
        initFoot();
    }

    private void initFoot() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        confirm.setOnClickListener(click);
        cancel.setOnClickListener(click);
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

}
