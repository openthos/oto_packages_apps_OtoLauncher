package com.openthos.launcher.openthoslauncher.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.launcher3.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xu on 2016/12/06.
 */
public class CopyInfoDialog extends Dialog {
    private Activity mContext;
    private TextView mTextMessage;
    private TextView mTextTitle;
    private static CopyInfoDialog dialog = null;

    public CopyInfoDialog(Activity context) {
        super(context);
        mContext = context;
    }

    public static CopyInfoDialog getInstance(Activity activity) {
        if (dialog == null) {
            return new CopyInfoDialog(activity);
        } else {
            return dialog;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(mContext, R.layout.dialog_copy_info, null);
        setContentView(v);
        mTextMessage = (TextView) v.findViewById(R.id.text_message);
        mTextTitle = (TextView) v.findViewById(R.id.text_title);
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    public void changeMsg(final String s) {
        if ("main".equals(Thread.currentThread().getName())) {
            mTextMessage.setText(s);
        } else {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextMessage.setText(s);
                }
            });
        }
    }

    public void changeTitle(final String s) {
        mTextTitle.setText(s);
    }
}
