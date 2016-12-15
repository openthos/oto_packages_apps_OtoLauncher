package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.entity.CompressFormatType;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

import java.io.File;
import java.io.IOException;

/**
 * Created by xu on 2016/11/2.
 */
public class CompressDialog extends Dialog {
    private Context mContext;
    private String mPath;
    private CompressFormatType mFormatType = CompressFormatType.ZIP;

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
        initBody();
        initFoot();
    }

    private void initBody() {
        RadioButton tar = (RadioButton) findViewById(R.id.tar);
        RadioButton tar_gz = (RadioButton) findViewById(R.id.tar_gz);
        RadioButton tar_bz2 = (RadioButton) findViewById(R.id.tar_bz2);
        RadioButton zip = (RadioButton) findViewById(R.id.zip);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tar:
                        mFormatType = CompressFormatType.TAR;
                        break;
                    case R.id.tar_gz:
                        mFormatType = CompressFormatType.GZIP;
                        break;
                    case R.id.tar_bz2:
                        mFormatType = CompressFormatType.BZIP2;
                        break;
                    case R.id.zip:
                        mFormatType = CompressFormatType.ZIP;
                        break;
                }
            }
        };
        tar.setOnClickListener(click);
        tar_gz.setOnClickListener(click);
        tar_bz2.setOnClickListener(click);
        zip.setOnClickListener(click);
        if (mPath.endsWith(OtoConsts.SUFFIX_TAR)) {
            tar.setVisibility(View.GONE);
            zip.setVisibility(View.GONE);
            tar_gz.performClick();
        } else {
            tar_gz.setVisibility(View.GONE);
            tar_bz2.setVisibility(View.GONE);
        }
    }

    private void initFoot() {
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.confirm:
                        new Thread() {
                            public void run() {
                                File f = DiskUtils.compress(mPath, mFormatType);
                                Message msg = new Message();
                                msg.obj = f.getAbsolutePath();
                                msg.what = OtoConsts.SHOW_FILE;
                                MainActivity.mHandler.sendMessage(msg);
                            }
                        }.start();
                        break;
                    case R.id.cancel:
                        break;
                }
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
