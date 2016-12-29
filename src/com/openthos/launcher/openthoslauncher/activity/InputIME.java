package com.openthos.launcher.openthoslauncher.activity;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.os.Bundle;
import android.os.Message;

import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

public class InputIME extends InputMethodService{

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Message msg = MainActivity.mHandler.obtainMessage();
        msg.what = OtoConsts.INTERCEPT_ONKEYDOWN;
        Bundle bundle = new Bundle();
        bundle.putInt(OtoConsts.INTERCEPT_ONKEYDOWN_KEYCODE, keyCode);
        bundle.putParcelable(OtoConsts.INTERCEPT_ONKEYDOWN_KEYEVENT, event);
        msg.setData(bundle);
        msg.sendToTarget();
        return true;
    }
}
