package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.KeyEvent;

import com.openthos.launcher.openthoslauncher.activity.MainActivity;

/**
 * Created by wang on 17-3-9.
 */

public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    public BaseDialog(@NonNull Context context, boolean cancelable,
                      @Nullable OnCancelListener cancelListener) {
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
