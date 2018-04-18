package org.openthos.launcher.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.util.AttributeSet;

public class FrameSelectView extends View {
    private Paint mPaint;
    private float mLeft, mTop, mRight, mBootom;
    //0x7f251a4b background
    //gray line
    //5.0f width

    public FrameSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint= new Paint();
        mPaint.setColor(Color.GRAY);
        //mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(0x7f);
        mPaint.setStrokeWidth(5.0f);
    }

    public void setPositionCoordinate(float left, float top, float right, float bootom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBootom = bootom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mLeft, mTop, mRight, mBootom,mPaint);
    }
}
