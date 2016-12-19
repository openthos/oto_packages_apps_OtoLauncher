package com.openthos.launcher.openthoslauncher.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

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
public class PropertyDialog extends Dialog {
    private Context mContext;
    private String mPath;

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
        setContentView(R.layout.dialog_property);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
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
        TextView size = (TextView) findViewById(R.id.size);
        TextView sizeOnDisk = (TextView) findViewById(R.id.size_on_disk);
        TextView created = (TextView) findViewById(R.id.created);
        TextView modified = (TextView) findViewById(R.id.modified);
        TextView accessed = (TextView) findViewById(R.id.accessed);

        location.setText(file.getAbsolutePath());
        size.setText(DiskUtils.formatFileSize(file.length()));
        sizeOnDisk.setText(DiskUtils.formatFileSize(file.length()));

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
        CheckBox limitOwnerRead = (CheckBox) findViewById(R.id.limit_owner_read);
        CheckBox limitOwnerWrite = (CheckBox) findViewById(R.id.limit_owner_write);
        CheckBox limitOwnerExecute = (CheckBox) findViewById(R.id.limit_owner_execute);
        CheckBox limitGroupRead = (CheckBox) findViewById(R.id.limit_group_read);
        CheckBox limitGroupWrite = (CheckBox) findViewById(R.id.limit_group_write);
        CheckBox limitGroupExecute = (CheckBox) findViewById(R.id.limit_group_execute);
        CheckBox limitOtherRead = (CheckBox) findViewById(R.id.limit_other_read);
        CheckBox limitOtherWrite = (CheckBox) findViewById(R.id.limit_other_write);
        CheckBox limitOtherExecute = (CheckBox) findViewById(R.id.limit_other_execute);
        String limit;
        if (!TextUtils.isEmpty(line)) {
            limit = line.substring(0, OtoConsts.LIMIT_LENGTH);
            if (limit.charAt(OtoConsts.LIMIT_OWNER_READ) == 'r') {
                limitOwnerRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_WRITE) == 'w') {
                limitOwnerWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_EXECUTE) == 'x') {
                limitOwnerExecute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_READ) == 'r') {
                limitGroupRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_WRITE) == 'w') {
                limitGroupWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_EXECUTE) == 'x') {
                limitGroupExecute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_READ) == 'r') {
                limitOtherRead.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_WRITE) == 'w') {
                limitOtherWrite.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_EXECUTE) == 'x') {
                limitOtherExecute.setChecked(true);
            }
        }
        limitOwnerRead.setClickable(false);
        limitOwnerWrite.setClickable(false);
        limitOwnerExecute.setClickable(false);
        limitGroupRead.setClickable(false);
        limitGroupWrite.setClickable(false);
        limitGroupExecute.setClickable(false);
        limitOtherRead.setClickable(false);
        limitOtherWrite.setClickable(false);
        limitOtherExecute.setClickable(false);
    }

    private void initFoot() {
        TextView apply = (TextView) findViewById(R.id.apply);
        TextView confirm = (TextView) findViewById(R.id.confirm);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        apply.setOnClickListener(click);
        confirm.setOnClickListener(click);
        cancel.setOnClickListener(click);
    }

    public void showDialog() {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.0f;
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

}
