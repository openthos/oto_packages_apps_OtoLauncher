package org.openthos.launcher.adapter;

import android.view.View;

/**
 * Created by xu on 2016/8/8.
 */
public interface RecycleCallBack {
    void itemOnClick(int position,View view);
    void onMove(int from,int to);
}
