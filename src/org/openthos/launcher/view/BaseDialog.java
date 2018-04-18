package org.openthos.launcher.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

import org.openthos.launcher.activity.MainActivity;

/**
 * Created by wang on 17-3-9.
 */

public class BaseDialog extends Dialog {
    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.setState(event.isCtrlPressed(), event.isShiftPressed());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        MainActivity.setState(event.isCtrlPressed(), event.isShiftPressed());
        return super.onKeyUp(keyCode, event);
    }
}
