package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.launcher3.R;

/**
 * Created by xu on 2016/8/11.
 */
public class PropertyDialog extends Dialog {
    private Context mContext;
    private String mPath;

    public PropertyDialog(Context context) {
        super(context);
        mContext = context;
    }

    public PropertyDialog(Context context, String path) {
        super(context);
        mContext = context;
        mPath = path;
    }

    public PropertyDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected PropertyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_property);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
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
