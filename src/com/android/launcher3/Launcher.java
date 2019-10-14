
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

/**
 * Default launcher application.
 */
public class Launcher extends Activity {

    static final int APPWIDGET_HOST_ID = 1024;
    // The Intent extra that defines whether to ignore the launch animation
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION =
            "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";

    public FrameLayout viewContainer;
    private View mLauncherView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View v = View.inflate(this,R.layout.launcher,null);
        super.setContentView(v);
        viewContainer = (FrameLayout)v.findViewById(R.id.otocontent);

        setupViews();
        mLauncherView.setBackground(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(
                "org.openthos.filemanager", "org.openthos.launcher.activity.MainActivity");
        intent.setComponent(cn);
        startActivity(intent);
        mLauncherView.setBackground(null);
    }

    private void setupViews() {
        mLauncherView = findViewById(R.id.launcher);
        mLauncherView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return Boolean.TRUE;
    }

    public void closeSystemDialogs() {
        getWindow().closeAllPanels();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
    /**
     * Receives notifications when system dialogs are to be closed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
        }
    }
}
