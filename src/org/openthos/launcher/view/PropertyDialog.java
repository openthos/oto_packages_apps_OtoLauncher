package org.openthos.launcher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.android.launcher3.R;
import org.openthos.launcher.utils.DiskUtils;
import org.openthos.launcher.utils.FileUtils;
import org.openthos.launcher.utils.OperateUtils;
import org.openthos.launcher.utils.OtoConsts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xu on 2016/8/11.
 */
public class PropertyDialog extends BaseDialog implements View.OnClickListener,
                                               CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private String mPath;
    private TextView mSize;
    private TextView mSizeOnDisk;
    private CheckBox mLimitOwnerRead;
    private CheckBox mLimitOwnerWrite;
    private CheckBox mLimitOwnerExecute;
    private CheckBox mLimitGroupRead;
    private CheckBox mLimitGroupWrite;
    private CheckBox mLimitGroupExecute;
    private CheckBox mLimitOtherRead;
    private CheckBox mLimitOtherWrite;
    private CheckBox mLimitOtherExecute;
    private int[][] mValues = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    private boolean mIsChange = false;
    private DialogInterface.OnClickListener mCommitClick = null;
    private DialogInterface.OnClickListener mCancelClick = null;

    public PropertyDialog(Context context) {
        super(context);
        mContext = context;
    }

    public PropertyDialog(Context context, String path) {
        super(context);
        mContext = context;
        mPath = path;
    }

    public PropertyDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected PropertyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.dialog_property);
        File file = new File(mPath);
        initTitle(file);
        initBody(file);
        initFoot();
    }

    private void initTitle(File file) {
        ImageView titleImage = (ImageView) findViewById(R.id.title_image);
        if (file.isDirectory()) {
            titleImage.setImageDrawable(getContext().getResources()
                                                             .getDrawable(R.drawable.ic_directory));
        } else if (file.isFile()) {
            titleImage.setImageDrawable(FileUtils.getFileIcon(mPath, mContext));
        }
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(file.getName() + " "
                          + getContext().getResources().getString(R.string.dialog_property_title));
    }

    private void initBody(File file) {
        TextView location = (TextView) findViewById(R.id.location);
        mSize = (TextView) findViewById(R.id.size);
        mSizeOnDisk = (TextView) findViewById(R.id.size_on_disk);
        TextView created = (TextView) findViewById(R.id.created);
        TextView modified = (TextView) findViewById(R.id.modified);
        TextView accessed = (TextView) findViewById(R.id.accessed);
        if (new File(mPath).isFile()) {
            mSize.setText(DiskUtils.formatFileSize(file.length()));
            mSizeOnDisk.setText(DiskUtils.formatFileSize(file.length()));
        } else {
            mSize.setText(mContext.getString(R.string.dialog_count));
            mSizeOnDisk.setText(mContext.getString(R.string.dialog_count));
            GetFolderSize task = new GetFolderSize();
            task.execute(mPath);
        }
        location.setText(file.getAbsolutePath());

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Process pro;
        String line = "";
        Runtime runtime = Runtime.getRuntime();
        BufferedReader in = null;
        try {
            String command = "/system/xbin/stat";
            String arg = "";
            pro = runtime.exec(new String[]{command, arg, mPath});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            while ((line = in.readLine()) != null) {
                if (line.contains("Access") && line.contains("Uid")) {
                    String limit = line.substring(OtoConsts.INDEX_LIMIT_BEGIN,
                                                  OtoConsts.INDEX_LIMIT_END);
                    initLimit(limit);
                }else if (line.contains("Access")) {
                    String accessTime = line.substring(OtoConsts.INDEX_TIME_BEGIN,
                                                       OtoConsts.INDEX_TIME_END);
                    //android.util.Log.i("wwwww","!"+accessTime+"!");
                    //Date dateTmp = dateFormat.parse(accessTime);
                    accessed.setText(accessTime);
                }else if (line.contains("Modify")) {
                    String modifyTime = line.substring(OtoConsts.INDEX_TIME_BEGIN,
                                                       OtoConsts.INDEX_TIME_END);
                    //Date dateTmp = dateFormat.parse(modifyTime);
                    modified.setText(modifyTime);
                }else if (line.contains("Change")) {
                    String changeTime = line.substring(OtoConsts.INDEX_TIME_BEGIN,
                                                       OtoConsts.INDEX_TIME_END);
                    //Date dateTmp = dateFormat.parse(changeTime);
                    created.setText(changeTime);
                }
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void initLimit(String line) {
        mLimitOwnerRead = (CheckBox) findViewById(R.id.limit_owner_read);
        mLimitOwnerWrite = (CheckBox) findViewById(R.id.limit_owner_write);
        mLimitOwnerExecute = (CheckBox) findViewById(R.id.limit_owner_execute);
        mLimitGroupRead = (CheckBox) findViewById(R.id.limit_group_read);
        mLimitGroupWrite = (CheckBox) findViewById(R.id.limit_group_write);
        mLimitGroupExecute = (CheckBox) findViewById(R.id.limit_group_execute);
        mLimitOtherRead = (CheckBox) findViewById(R.id.limit_other_read);
        mLimitOtherWrite = (CheckBox) findViewById(R.id.limit_other_write);
        mLimitOtherExecute = (CheckBox) findViewById(R.id.limit_other_execute);
        mLimitOwnerRead.setOnCheckedChangeListener(this);
        mLimitOwnerWrite.setOnCheckedChangeListener(this);
        mLimitOwnerExecute.setOnCheckedChangeListener(this);
        mLimitGroupRead.setOnCheckedChangeListener(this);
        mLimitGroupWrite.setOnCheckedChangeListener(this);
        mLimitGroupExecute.setOnCheckedChangeListener(this);
        mLimitOtherRead.setOnCheckedChangeListener(this);
        mLimitOtherWrite.setOnCheckedChangeListener(this);
        mLimitOtherExecute.setOnCheckedChangeListener(this);
        mLimitOwnerRead.setOnClickListener(this);
        mLimitOwnerWrite.setOnClickListener(this);
        mLimitOwnerExecute.setOnClickListener(this);
        mLimitGroupRead.setOnClickListener(this);
        mLimitGroupWrite.setOnClickListener(this);
        mLimitGroupExecute.setOnClickListener(this);
        mLimitOtherRead.setOnClickListener(this);
        mLimitOtherWrite.setOnClickListener(this);
        mLimitOtherExecute.setOnClickListener(this);
        String limit;
        if (!TextUtils.isEmpty(line)) {
            limit = line.substring(0, OtoConsts.LIMIT_LENGTH);
            if (limit.charAt(OtoConsts.LIMIT_OWNER_READ) == 'r') {
                mLimitOwnerRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_WRITE) == 'w') {
                mLimitOwnerWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_EXECUTE) == 'x') {
                mLimitOwnerExecute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_READ) == 'r') {
                mLimitGroupRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_WRITE) == 'w') {
                mLimitGroupWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_EXECUTE) == 'x') {
                mLimitGroupExecute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_READ) == 'r') {
                mLimitOtherRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_WRITE) == 'w') {
                mLimitOtherWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_EXECUTE) == 'x') {
                mLimitOtherExecute.setChecked(true);
            }
        }
    }

    private void initFoot() {
        mCommitClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                commitLimit();
                dialog.cancel();
            }
        };
        mCancelClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        TextView apply = (TextView) findViewById(R.id.apply);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.apply:
                        if (mIsChange) {
                            OperateUtils.showChooseAlertDialog(mContext, R.string.confirm_limit,
                                                                     mCommitClick, mCancelClick);
                        }
                        break;
                    case R.id.confirm:
                        if (mIsChange) {
                            OperateUtils.showChooseAlertDialog(mContext, R.string.confirm_limit,
                                                                     mCommitClick, mCancelClick);
                        }
                        dismiss();
                        break;
                    case R.id.cancel:
                        dismiss();
                        break;
                }
            }
        };
        apply.setOnClickListener(click);
        confirm.setOnClickListener(click);
        cancel.setOnClickListener(click);
    }

    private void commitLimit() {
        String chmod = "";
        for (int[] type : mValues) {
            int result = 0;
            for (int i : type) {
                result = result + i;
            }
            chmod = chmod + result;
        }
        BufferedReader in = null;
        String line = "";
        try {
            Process pro = Runtime.getRuntime().exec(
                    new String[]{"su", "-c", "chmod " + chmod + " " + mPath.replace(" ", "\\ ")});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            while ((line = in.readLine()) != null) {
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.limit_owner_read:
                if (isChecked) {
                    mValues[0][0] = 4;
                } else {
                    mValues[0][0] = 0;
                }
                break;
            case R.id.limit_owner_write:
                if (isChecked) {
                    mValues[0][1] = 2;
                } else {
                    mValues[0][1] = 0;
                }
                break;
            case R.id.limit_owner_execute:
                if (isChecked) {
                    mValues[0][2] = 1;
                } else {
                    mValues[0][2] = 0;
                }
                break;
            case R.id.limit_group_read:
                if (isChecked) {
                    mValues[1][0] = 4;
                } else {
                    mValues[1][0] = 0;
                }
                break;
            case R.id.limit_group_write:
                if (isChecked) {
                    mValues[1][1] = 2;
                } else {
                    mValues[1][1] = 0;
                }
                break;
            case R.id.limit_group_execute:
                if (isChecked) {
                    mValues[1][2] = 1;
                } else {
                    mValues[1][2] = 0;
                }
                break;
            case R.id.limit_other_read:
                if (isChecked) {
                    mValues[2][0] = 4;
                } else {
                    mValues[2][0] = 0;
                }
                break;
            case R.id.limit_other_write:
                if (isChecked) {
                    mValues[2][1] = 2;
                } else {
                    mValues[2][1] = 0;
                }
                break;
            case R.id.limit_other_execute:
                if (isChecked) {
                    mValues[2][2] = 1;
                } else {
                    mValues[2][2] = 0;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        mIsChange = true;
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    private class GetFolderSize extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            File f = new File(params[0]);
            BufferedReader in = null;
            String line = "";
            String result = "";
            if (f.exists() && f.isDirectory()) {
                try {
                    Process pro = Runtime.getRuntime().exec(
                                              new String[]{"du", "-h", f.getAbsolutePath()});
                    in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    while ((line = in.readLine()) != null) {
                        if (line != null) {
                            result = line;
                        }
                    }
                } catch (IOException e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            String size = "";
            if (!TextUtils.isEmpty(result)) {
                size = result.split("/")[0].trim();
            }
            return size;
        }

        @Override
        protected void onPostExecute(String size) {
            mSize.setText(size);
            mSizeOnDisk.setText(size);
        }
    }
}
