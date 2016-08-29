package com.openthos.launcher.openthoslauncher.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.LauncherWallpaperPickerActivity;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

import java.io.File;
/**
 * Created by xu on 2016/8/11.
 */
public class MenuDialog extends Dialog {
    private Context context;
    private Type type;
    private String path;

    public MenuDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MenuDialog(Context context, Type type, String path) {
        super(context);
        this.context = context;
        this.type = type;
        this.path = path;
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
        android.widget.Toast.makeText(getContext(),path,android.widget.Toast.LENGTH_LONG).show();
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
            tv.setTag(s[i]);
            tv.setOnHoverListener(hoverListener);
            tv.setOnClickListener(clickListener);
            ll.addView(mv);
        }

    }

    public void showDialog(int x, int y) {
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
        if (y > (d.getHeight() - dialogWindow.getAttributes().height-OtoConsts.BAR_Y)) {
            lp.y = y - dialogWindow.getAttributes().height;
            //lp.y = d.getHeight() - dialogWindow.getAttributes().height;
        } else {
            lp.y = y - OtoConsts.FIX_Y;
        }
        lp.alpha = OtoConsts.FIX_ALPHA;
        dialogWindow.setAttributes(lp);
    }

    View.OnHoverListener hoverListener = new View.OnHoverListener() {

        public boolean onHover(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    v.setBackgroundResource(R.color.item_hover_background);
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    v.setBackgroundResource(R.color.transparent);
                    break;
            }
            return false;
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = (String) v.getTag();
            String[] all_menu = context.getResources().getStringArray(R.array.all_menu);
            if (text.equals(all_menu[OtoConsts.INDEX_OPEN])) {
                //open
                switch (type) {
                    case computer:
                    case recycle:
                    case directory:
                        PackageManager packageManager = getContext().getPackageManager();
                        Intent openDir = packageManager.getLaunchIntentForPackage(
                                                              OtoConsts.FILEMANAGER_PACKAGE);
                        Bundle bundle = new Bundle();
                        bundle.putString("path",path);
                        openDir.putExtras(bundle);
                        getContext().startActivity(openDir);
                        break;
                    case file:
                        Intent openFile = new Intent();
                        openFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        openFile.setAction(Intent.ACTION_VIEW);
                        String fileType = FileUtils.getMIMEType(new File(path));
                        openFile.setDataAndType(Uri.fromFile(new File(path)), fileType);
                        getContext().startActivity(openFile);
                        break;
                }
            } else if (text.equals(all_menu[OtoConsts.INDEX_ABOUT_COMPUTER])) {
                //about_computer
                PackageManager packageManager = getContext().getPackageManager();
                Intent about = packageManager.getLaunchIntentForPackage(
                                                             OtoConsts.SETTINGS_PACKAGE);
                about.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(about);
            } else if (text.equals(all_menu[OtoConsts.INDEX_COMPRESS])) {
                //compress
            } else if (text.equals(all_menu[OtoConsts.INDEX_DECOMPRESSION])) {
                //decompression
            } else if (text.equals(all_menu[OtoConsts.INDEX_CROP])) {
                //crop
            } else if (text.equals(all_menu[OtoConsts.INDEX_COPY])) {
                //copy
            } else if (text.equals(all_menu[OtoConsts.INDEX_PASTE])) {
                //paste
            } else if (text.equals(all_menu[OtoConsts.INDEX_SORT])) {
                //sort
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.SORT);
            } else if (text.equals(all_menu[OtoConsts.INDEX_NEW_FOLDER])) {
                //new_folder
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.NEWFOLDER);
            }else if (text.equals(all_menu[OtoConsts.INDEX_DISPLAY_SETTINGS])) {
                //display_settings
                Intent display = new Intent();
                ComponentName compDisplay = new ComponentName(OtoConsts.SETTINGS_PACKAGE,
                                                              OtoConsts.DISPLAY_SETTINGS);
                display.setComponent(compDisplay);
                display.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(display);
            } else if (text.equals(all_menu[OtoConsts.INDEX_CHANGE_WALLPAPER])) {
                //change_wallpaper
                Intent wallpaper = new Intent(getContext(),LauncherWallpaperPickerActivity.class);
                //ComponentName compWall = new ComponentName(OtoConsts.SETTINGS_PACKAGE,
                //                                             OtoConsts.WALLPAPER_SETTINGS);
                //wallpaper.setComponent(compWall);
                wallpaper.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(wallpaper);
            } else if (text.equals(all_menu[OtoConsts.INDEX_DELETE])) {
                //delete
                new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getResources().getString(
                                                              R.string.dialog_delete_text))
                       .setPositiveButton(getContext().getResources().getString(
                                                              R.string.dialog_delete_yes),
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                DiskUtils.moveDirectory(path,
                                                              OtoConsts.RECYCLE_PATH);
                                                Message deleteFile = new Message();
                                                deleteFile.obj = path;
                                                deleteFile.what = OtoConsts.DELETE;
                                                MainActivity.mHandler.sendMessage(deleteFile);
                                            }
                                        }.start();
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(getContext().getResources().getString(
                                                             R.string.dialog_delete_no),
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
            } else if (text.equals(all_menu[OtoConsts.INDEX_RENAME])) {
                //rename
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.RENAME);
            } else if (text.equals(all_menu[OtoConsts.INDEX_CLEAN_RECYCLE])) {
                //clean_recycle
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        File recycle = new File(path);
                        DiskUtils.delete(recycle);
                        if (!recycle.exists()) {
                            recycle.mkdir();
                        }
                    }
                }.start();
            } else if (text.equals(all_menu[OtoConsts.INDEX_PROPERTY])) {
                //property
                Message property = new Message();
                property.obj = path;
                property.what = OtoConsts.PROPERTY;
                MainActivity.mHandler.sendMessage(property);

            }
            dismiss();
        }
    };
}
