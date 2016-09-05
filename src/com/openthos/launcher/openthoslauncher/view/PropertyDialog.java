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
    private File file;
    private ImageView titleImage;
    private TextView titleText;
    private TextView location, size, sizeOnDisk, created, modified, accessed;
    private CheckBox limit_owner_read, limit_owner_write, limit_owner_execute;
    private CheckBox limit_group_read, limit_group_write, limit_group_execute;
    private CheckBox limit_other_read, limit_other_write, limit_other_execute;
    private TextView apply, confirm, cancel;

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
        initTitle();
        initBody();
        initFoot();
    }

    private void initTitle() {
        file = new File(mPath);
        titleImage = (ImageView) findViewById(R.id.title_image);
        if (file.isDirectory()) {
            titleImage.setImageDrawable(getContext().getResources()
                                                             .getDrawable(R.drawable.ic_app_file));
        } else if (file.isFile()) {
            titleImage.setImageDrawable(getContext().getResources()
                                                             .getDrawable(R.drawable.ic_app_text));
        }
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(file.getName() + " "
                          + getContext().getResources().getString(R.string.dialog_property_title));
    }

    private void initBody() {
        location = (TextView) findViewById(R.id.location);
        size = (TextView) findViewById(R.id.size);
        sizeOnDisk = (TextView) findViewById(R.id.size_on_disk);
        created = (TextView) findViewById(R.id.created);
        modified = (TextView) findViewById(R.id.modified);
        accessed = (TextView) findViewById(R.id.accessed);

        location.setText(file.getAbsolutePath());
        size.setText(DiskUtils.formatFileSize(file.length()));
        sizeOnDisk.setText(DiskUtils.formatFileSize(file.length()));

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Process pro;
        String line = "";
        Runtime runtime = Runtime.getRuntime();
        try {
            String command = "/system/xbin/stat";
            String arg = "";
            pro = runtime.exec(new String[]{command, arg, mPath});
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(pro.getInputStream()));
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
        }
    }

    private void initLimit(String line) {
        limit_owner_read = (CheckBox) findViewById(R.id.limit_owner_read);
        limit_owner_write = (CheckBox) findViewById(R.id.limit_owner_write);
        limit_owner_execute = (CheckBox) findViewById(R.id.limit_owner_execute);
        limit_group_read = (CheckBox) findViewById(R.id.limit_group_read);
        limit_group_write = (CheckBox) findViewById(R.id.limit_group_write);
        limit_group_execute = (CheckBox) findViewById(R.id.limit_group_execute);
        limit_other_read = (CheckBox) findViewById(R.id.limit_other_read);
        limit_other_write = (CheckBox) findViewById(R.id.limit_other_write);
        limit_other_execute = (CheckBox) findViewById(R.id.limit_other_execute);
        String limit;
        if (!TextUtils.isEmpty(line)) {
            limit = line.substring(0, OtoConsts.LIMIT_LENGTH);
            if (limit.charAt(OtoConsts.LIMIT_OWNER_READ) == 'r') {
                limit_owner_read.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_WRITE) == 'w') {
                limit_owner_write.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OWNER_EXECUTE) == 'x') {
                limit_owner_execute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_READ) == 'r') {
                limit_group_read.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_WRITE) == 'w') {
                limit_group_write.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_GROUP_EXECUTE) == 'x') {
                limit_group_execute.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_READ) == 'r') {
                limit_other_read.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_WRITE) == 'w') {
                limit_other_write.setChecked(true);
            }
            if (limit.charAt(OtoConsts.LIMIT_OTHER_EXECUTE) == 'x') {
                limit_other_execute.setChecked(true);
            }
        }
        limit_owner_read.setClickable(false);
        limit_owner_write.setClickable(false);
        limit_owner_execute.setClickable(false);
        limit_group_read.setClickable(false);
        limit_group_write.setClickable(false);
        limit_group_execute.setClickable(false);
        limit_other_read.setClickable(false);
        limit_other_write.setClickable(false);
        limit_other_execute.setClickable(false);
    }

    private void initFoot() {
        apply = (TextView) findViewById(R.id.apply);
        confirm = (TextView) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.cancel);
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
