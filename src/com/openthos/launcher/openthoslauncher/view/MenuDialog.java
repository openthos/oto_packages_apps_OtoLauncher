package com.openthos.launcher.openthoslauncher.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.OtoConsts;

/**
 * Created by xu on 2016/8/11.
 */
public class MenuDialog extends Dialog {
    private ListView listView;
    private Context context;
    private Type type;

    public MenuDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MenuDialog(Context context, Type type) {
        super(context);
        this.context = context;
        this.type = type;
    }

    public MenuDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }


    protected MenuDialog(Context context, boolean cancelable,
                         DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_menu);
        getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.color.transparent));
        String[] s = {};
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        switch (type) {
            case computer:
                s = context.getResources().getStringArray(R.array.menu_computer);
                break;
            case recycle:
                s = context.getResources().getStringArray(R.array.menu_recycle);
                break;
            case directory:
                s = context.getResources().getStringArray(R.array.menu_file);
                break;
            case file:
                s = context.getResources().getStringArray(R.array.menu_file);
                break;
            case blank:
                s = context.getResources().getStringArray(R.array.menu_blank);
                break;

        }

        for (int i = 0; i < s.length; i++) {
            View mv = View.inflate(context, R.layout.item_menu, null);
            TextView tv = (TextView) mv.findViewById(R.id.text);
            tv.setText(s[i]);
            ll.addView(mv);
        }
    }

    public void showDialog(int x, int y, int height, int width) {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay();
        if (x > (d.getWidth() - dialogWindow.getAttributes().width))
        {
            lp.x = d.getWidth() - dialogWindow.getAttributes().width;
        } else {
            lp.x = x;
        }
        if (y > (d.getHeight() - dialogWindow.getAttributes().height)) {
            lp.y = d.getHeight() - dialogWindow.getAttributes().height;
        } else {
            lp.y = y - OtoConsts.FIX_Y;
        }
        lp.alpha = OtoConsts.FIX_ALPHA;
        dialogWindow.setAttributes(lp);
    }
}
