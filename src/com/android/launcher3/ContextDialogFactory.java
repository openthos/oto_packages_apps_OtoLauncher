package com.android.launcher3;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

/**
 * Created by Mingkai on 2016/6/15.
 */
public class ContextDialogFactory {
    public static final int DIALOG_FOR_PC = 0;
    public static final int DIALOG_FOR_TRASH = 1;
    public static final int DIALOG_FOR_NORMAL = 2;
    public static final int DIALOG_FOR_DESKTOP = 3;
    private static ContextDialog mContextDialog;

    public static class ContextDialog extends Dialog implements View.OnClickListener {
        private View targetView;
        private FrameLayout mContainer;
        private int type;
        private Context context;

        public ContextDialog(Context context, int type) {
            super(context);
            this.type = type;
            this.context = context;
        }

        public ContextDialog(Context context) {
            this(context, 0);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Window window = getWindow();
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            window.requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_container);
            mContainer = (FrameLayout) findViewById(R.id.dialog_container);
            LinearLayout linearLayout = getCurrentView();
            mContainer.removeAllViews();
            mContainer.addView(linearLayout);
        }

        private LinearLayout getCurrentView() {
            LinearLayout mLinearLayout = null;
            if (type == ContextDialogFactory.DIALOG_FOR_DESKTOP) {
                mLinearLayout = (LinearLayout) getLayoutInflater()
                                                   .inflate(R.layout.dialog_list_desktop, null);
            } else if (type == ContextDialogFactory.DIALOG_FOR_NORMAL) {
                mLinearLayout = (LinearLayout) getLayoutInflater()
                                                   .inflate(R.layout.dialog_list_normal, null);
            } else if (type == ContextDialogFactory.DIALOG_FOR_TRASH) {
                mLinearLayout = (LinearLayout) getLayoutInflater()
                                                   .inflate(R.layout.dialog_list_trash, null);
            } else {//type == ContextDialogFactory.DIALOG_FOR_PC
                mLinearLayout = (LinearLayout) getLayoutInflater()
                                                   .inflate(R.layout.dialog_list_pc, null);
            }
            for (int i = 0; i < mLinearLayout.getChildCount(); ++i) {
                mLinearLayout.getChildAt(i).setOnClickListener(this);
            }
            return mLinearLayout;
        }

        public void setPoint(float[] point) {
            Window mDialogWindow = this.getWindow();
            mDialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
            WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
            attrs.x = (int) (point[0]);
            attrs.y = (int) (point[1]);
        }

        public void setType(int type) {
            this.type = type;
        }

        //listener and the functions
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.dialog_open_pc:
                case R.id.dialog_open_normal:
                case R.id.dialog_open_trash:
                    openApp(v);
                    break;
                case R.id.dialog_open_with:
                    openWith(v);
                    break;
                case R.id.dialog_compress:
                    compress(v);
                    break;
                case R.id.dialog_uncompress:
                    uncompress(v);
                    break;
                case R.id.dialog_about:
                    showAboutComputer();
                    break;
                case R.id.dialog_new_folder:
                    newFolder();
                    break;
                case R.id.dialog_copy:
                    copy(v);break;
                case R.id.dialog_cut:
                    cut(v);
                    break;
                case R.id.dialog_del:
                    delete(v);
                    break;
                case R.id.dialog_paste:
                    paste();
                    break;
                case R.id.dialog_empty:
                    emptyTrash(v);
                    break;
                case R.id.dialog_rename:
                    rename(v);
                    break;
                case R.id.dialog_property:
                    property(v);
                    break;
                case R.id.dialog_display_settings:
                    displaySettings();
                    break;
                case R.id.dialog_change_pix:
                    changeBackground();
                    break;
                case R.id.dialog_sort:
                    sort();
                    break;
                default:
                    this.dismiss();
            }
            this.dismiss();
        }

        private boolean openApp(View v) {
            ((Launcher) context).onClick(targetView);
            return false;
        }

        private boolean showAboutComputer() {
            return true;
        }

        private boolean emptyTrash(View v) {

            return true;
        }

        private boolean openWith(View v) {
            return true;
        }

        private boolean compress(View v) {
            return true;
        }

        private boolean uncompress(View v) {
            return true;
        }

        private boolean cut(View v) {
            return true;
        }

        private boolean copy(View v) {
            return true;
        }

        private boolean paste() {
            return true;
        }

        private boolean delete(View v) {
            return true;
        }

        private boolean rename(View v) {
            return true;
        }

        private boolean sort() {
            return true;
        }

        private boolean property(View v) {
            return true;
        }

        private boolean newFolder() {
            return true;
        }

        private boolean displaySettings() {
            return true;
        }

        private boolean changeBackground() {
            return true;
        }

        public void setTargetView(View targetView) {
            this.targetView = targetView;
        }
    }

    public static ContextDialog newPCContextDialog(Context context, View v, float[] point) {
        return newContextDialog(context, v, point, ContextDialogFactory.DIALOG_FOR_PC);
    }

    public static ContextDialog newNormalContextDialog(Context context, View v, float[] point) {
        return newContextDialog(context, v, point,ContextDialogFactory.DIALOG_FOR_NORMAL);
    }

    public static ContextDialog newTrashContextDialog(Context context, View v, float[] point) {
        return newContextDialog(context, v, point, ContextDialogFactory.DIALOG_FOR_TRASH);
    }

    public static ContextDialog newDesktopContextDialog(Context context, View v, float[] point) {
        return newContextDialog(context, v, point, ContextDialogFactory.DIALOG_FOR_DESKTOP);
    }

    private static ContextDialog newContextDialog(Context context, View v,
                                                  float[] point, int resId) {
        checkDialog();
        mContextDialog = new ContextDialog(context);
        mContextDialog.setType(resId);
        mContextDialog.setTargetView(v);
        mContextDialog.setPoint(point);
        return mContextDialog;
    }

    private static void checkDialog() {
        if (mContextDialog != null && mContextDialog.isShowing()) {
            mContextDialog.dismiss();
            mContextDialog = null;
        }
    }
}
