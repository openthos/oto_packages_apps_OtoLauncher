package org.openthos.launcher.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.content.FileProvider;

import com.android.launcher3.R;
import org.openthos.launcher.utils.FileUtils;
import org.openthos.launcher.utils.OtoConsts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenWithDialog extends BaseDialog implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private TextView mTextView;
    private List<ResolveInfo> mResolveList;
    private ResolveAdapter mResolveAdapter;
    private PackageManager mPackageManager;
    private String mFilePath;
    private String mFileType;

    public OpenWithDialog(Context mContext, String mFilePath) {
        super(mContext);
        this.mContext = mContext;
        this.mFilePath = mFilePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.open_with_dialog);
        initView();
        initListener();
    }

    private void initListener() {
        mListView.setOnItemClickListener(this);
    }

    private void initView() {
        mPackageManager = mContext.getPackageManager();
        mListView = (ListView) findViewById(R.id.lv_open_with);
        mTextView = (TextView) findViewById(R.id.tv_no_open_with);
        initList();
        if (mResolveList.size() > 0) {
            mResolveAdapter = new ResolveAdapter();
            mListView.setAdapter(mResolveAdapter);
            mListView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            if (mResolveList.size() > OtoConsts.LIMIT_FILES_NUM) {
                ViewGroup.LayoutParams params = mListView.getLayoutParams();
                params.height = OtoConsts.LIMIT_FILES_HEIGHT;
                mListView.setLayoutParams(params);
            }
            mResolveAdapter.notifyDataSetChanged();
        } else {
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initList() {
        mResolveList = new ArrayList<>();
        File file = new File(mFilePath);
        if (file.exists()) {
            mFileType = FileUtils.getMIMEType(new File(mFilePath));
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(mFilePath)), mFileType);
            mResolveList = mPackageManager.queryIntentActivities(intent,
                                        PackageManager.MATCH_DEFAULT_ONLY);
        }
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String packageName = mResolveList.get(i).activityInfo.packageName;
        String className = mResolveList.get(i).activityInfo.name;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(FileProvider.getUriForFile(mContext,
                    "org.openthos.support.fileprovider", new File(mFilePath)), mFileType);
        } else {
            intent.setDataAndType(Uri.fromFile(new File(mFilePath)), mFileType);
        }
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        mContext.startActivity(intent);
        dismiss();
    }

    private class ResolveAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mResolveList.size();
        }

        @Override
        public Object getItem(int i) {
            return mResolveList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            View convertView = view;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).
                                  inflate(R.layout.list_item, viewGroup, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.text.setText(mResolveList.get(i).loadLabel(mPackageManager));
            holder.image.setImageDrawable(mResolveList.get(i).loadIcon(mPackageManager));
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView text;
        public ImageView image;

        public ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.tv_list_item);
            image = (ImageView) view.findViewById(R.id.iv_list_item);
        }
    }
}
