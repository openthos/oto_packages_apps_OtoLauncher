package com.openthos.launcher.openthoslauncher.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.entity.Consts;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<HashMap<String, Object>> data;

    private RecycleCallBack mRecycleClick;

    public int pos = -1;
    private int mLastClickId = -1;
    private long mLastClickTime = 0;

    public HomeAdapter(List<HashMap<String, Object>> data, RecycleCallBack click) {
        this.data = data;
        this.mRecycleClick = click;
    }

    public void setData(List<HashMap<String, Object>> data) {
        this.data = data;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeViewHolder holder = new HomeViewHolder(LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.item_icon, parent, false), mRecycleClick);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.tv.setText(data.get(position).get("name").toString());
        if ((Boolean) data.get(position).get("isChecked")) {
            holder.item.setBackgroundResource(R.drawable.icon_background);
        } else if (!(Boolean) data.get(position).get("isChecked")) {
            holder.item.setBackgroundResource(R.drawable.icon_background_trans);
        }
        if ((Boolean) data.get(position).get("null")) {
            holder.nullnull.setChecked(true);
        } else if (!(Boolean) data.get(position).get("null")) {
            holder.nullnull.setChecked(false);
        }
        if ((int) data.get(position).get("icon") != -1) {
            holder.iv.setImageDrawable(holder.item.getResources().getDrawable(
                                                         (int) data.get(position).get("icon")));
        } else {
            holder.iv.setImageDrawable(new ColorDrawable(0));
        }
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        RelativeLayout item;
        ImageView iv;
        TextView tv;
        CheckBox checkBox;
        CheckBox nullnull;
        private RecycleCallBack mClick;

        public HomeViewHolder(View view, RecycleCallBack click) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            iv = (ImageView) view.findViewById(R.id.icon);
            tv = (TextView) view.findViewById(R.id.texts);
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                MenuDialog dialog = new MenuDialog(item.getContext(),
                                                (Type) data.get(getAdapterPosition()).get("type"));
                dialog.showDialog((int) event.getRawX(), (int) event.getRawY(), 0, 0);
                LayoutInflater mLayoutInflater = ((Activity) item.getContext()).getLayoutInflater();
            }
            if ((Boolean) data.get(getAdapterPosition()).get("null") != true) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if ((Math.abs(System.currentTimeMillis() - mLastClickTime)
                        < Consts.DOUBLE_CLICK_TIME) && (mLastClickId == getAdapterPosition())) {
                       PackageManager packageManager = item.getContext().getPackageManager();
                       Intent intent = packageManager.getLaunchIntentForPackage(
                                                                    "com.cyanogenmod.filemanager");
                       item.getContext().startActivity(intent);
                    } else {
                        if (null != mClick) {
                            mClick.itemOnClick(getAdapterPosition(), v);
                        }
                        if ((Boolean) data.get(getAdapterPosition()).get("isChecked") == false) {
                            if (pos != -1 && pos != getAdapterPosition()
                                && (Boolean) data.get(pos).get("isChecked") == true) {
                                data.get(pos).put("isChecked",
                                                  !(Boolean) data.get(pos).get("isChecked"));
                            }
                            data.get(getAdapterPosition()).put("isChecked",
                                     !(Boolean) data.get(getAdapterPosition()).get("isChecked"));
                            if (pos != getAdapterPosition()) {
                                pos = getAdapterPosition();
                            } else {
                                pos = -1;
                            }
                            notifyDataSetChanged();
                        }
                        mLastClickTime = System.currentTimeMillis();
                        mLastClickId = pos;
                    }
                }
            }
            return false;
        }
    }
}
