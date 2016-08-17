package com.openthos.launcher.openthoslauncher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openthos.launcher.openthoslauncher.R;

/**
 * Created by xu on 2016/8/11.
 */
public class MenuAdapter extends BaseAdapter {
    Context context;
    String[] list;

    public MenuAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item=View.inflate(context, R.layout.item_menu,null);
        TextView tv= (TextView) item.findViewById(R.id.text);
        tv.setText(list[position]);
        return null;
    }
}
