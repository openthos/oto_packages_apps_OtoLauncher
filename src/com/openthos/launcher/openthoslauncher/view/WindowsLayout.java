package com.openthos.launcher.openthoslauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xu on 2016/8/11.
 */
public class WindowsLayout extends ViewGroup {

    private String TAG = "WindowsLayout";


    public WindowsLayout(Context context) {
        this(context, null);
    }

    public WindowsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : 0, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int childCount = getChildCount();
        int row = 0, column = 0;
        int contentHeight = height;

        for (int i = 0; i < childCount - 1; i++) {
            View child = getChildAt(i);
            int ct, cr, cb, cl = column * child.getMeasuredWidth();
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            ct = cHeight * row;
            cr = cl + cWidth;
            cb = ct + cHeight;
            if (cb > contentHeight) {
                column++;
                row = 0;
                i--;
            } else {
                row++;
                child.layout(cl, ct, cr, cb);
            }
        }
    }
}
