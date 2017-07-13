package com.openthos.launcher.openthoslauncher.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.ClipboardManager;

import com.android.launcher3.R;
import com.android.launcher3.LauncherWallpaperPickerActivity;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.adapter.HomeAdapter;

import java.io.File;
/**
 * Created by xu on 2016/8/11.
 */
public class MenuDialog extends BaseDialog {
    private Context context;
    private Type type;
    private String path;
    private int dialogHeight = 0;
    private int dialogWidth = 144;
    private int itemHeight = 0;
    private String sourcePath = "";

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
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.dialog_menu);
        getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.color.transparent));
        String[] s = {};
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        switch (type) {
            case COMPUTER:
                s = context.getResources().getStringArray(R.array.menu_computer);
                break;
            case RECYCLE:
                s = context.getResources().getStringArray(R.array.menu_recycle);
                break;
            case DIRECTORY:
                s = context.getResources().getStringArray(R.array.menu_file);
                break;
            case FILE:
                s = context.getResources().getStringArray(R.array.menu_file);
                break;
            case BLANK:
                s = context.getResources().getStringArray(R.array.menu_blank);
                break;
            case MORE:
                s = context.getResources().getStringArray(R.array.menu_more);
                break;
        }

        ClipboardManager cm
                = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            sourcePath = (String) cm.getText();
        } catch (ClassCastException e) {
            sourcePath = "";
        }
        if (sourcePath == null) {
            sourcePath = "";
        }
        View tempView = View.inflate(context, R.layout.item_menu, null);
        TextView tempTv = (TextView) tempView.findViewById(R.id.text);
        for (int i = 0; i < s.length; i++) {
           tempTv.setText(s[i]);
           tempView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
           if (tempView.getMeasuredWidth() > dialogWidth) {
                dialogWidth = tempView.getMeasuredWidth();
           }
        }
        itemHeight = tempView.getMeasuredHeight();
        for (int i = 0; i < s.length; i++) {
            View mv;
            if (s[i].equals(context.getResources().getString(R.string.open_with))
                                                                       && type == Type.DIRECTORY) {
                continue;
            }
            String endPath = path.toLowerCase();
            if (type == Type.MORE) {
                mv = View.inflate(context, R.layout.item_menu, null);
            } else if ((s[i].equals(context.getResources().getString(R.string.decompression))
                        && !(endPath.endsWith(OtoConsts.SUFFIX_TAR)
                         || endPath.endsWith(OtoConsts.SUFFIX_TAR_BZIP2)
                         || endPath.endsWith(OtoConsts.SUFFIX_TAR_GZIP)
                         || endPath.endsWith(OtoConsts.SUFFIX_ZIP)
                         || endPath.endsWith(OtoConsts.SUFFIX_RAR)
                         || endPath.endsWith(OtoConsts.SUFFIX_7Z)))
                || (s[i].equals(context.getResources().getString(R.string.compress))
                        && (endPath.endsWith(OtoConsts.SUFFIX_TAR_GZIP)
                          || endPath.endsWith(OtoConsts.SUFFIX_ZIP)
                          || endPath.endsWith(OtoConsts.SUFFIX_TAR_BZIP2)
                          || endPath.endsWith(OtoConsts.SUFFIX_RAR)
                          || endPath.endsWith(OtoConsts.SUFFIX_7Z)))
                || (s[i].equals(context.getResources().getString(R.string.paste))
                        && !((sourcePath.startsWith(OtoConsts.EXTRA_FILE_HEADER))
                               || (sourcePath.startsWith(OtoConsts.EXTRA_CROP_FILE_HEADER))))) {
                continue;
                //mv = View.inflate(context, R.layout.item_menu_unable, null);
            } else {
                mv = View.inflate(context, R.layout.item_menu, null);
            }
            TextView tv = (TextView) mv.findViewById(R.id.text);
            tv.setText(s[i]);
            tv.setTag(s[i]);
            tv.setOnHoverListener(hoverListener);
            tv.setOnClickListener(clickListener);
            tv.getLayoutParams().width = dialogWidth;
            ll.addView(mv);
            dialogHeight += itemHeight;
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
        if (y > (d.getHeight() - dialogHeight - OtoConsts.BAR_Y)) {
            lp.y = y - dialogHeight - OtoConsts.FIX_PADDING;
        } else {
            lp.y = y;
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
                OperateUtils.enter(getContext(), path,type);
            } else if (text.equals(all_menu[OtoConsts.INDEX_ABOUT_COMPUTER])) {
                //about_computer
                Intent wallpaper =  new Intent();
                ComponentName compWallpaper = new ComponentName(OtoConsts.SETTINGS_PACKAGE,
                                                              OtoConsts.DEVICES_INFO);
                wallpaper.setComponent(compWallpaper);
                wallpaper.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(wallpaper);
            } else if (text.equals(all_menu[OtoConsts.INDEX_COMPRESS])) {
                //compress
                path = path.contains(OtoConsts.EXTRA_DELETE_FILE_HEADER) ?
                        path : (OtoConsts.EXTRA_DELETE_FILE_HEADER + path);
                Intent intent = new Intent(OtoConsts.COMPRESS_FILES);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(OtoConsts.COMPRESS_PATH_TAG, path);
                context.startActivity(intent);
            } else if (text.equals(all_menu[OtoConsts.INDEX_DECOMPRESSION])) {
                //decompression
                Intent intent = new Intent(OtoConsts.DECOMPRESS_FILE);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(OtoConsts.COMPRESS_PATH_TAG, path);
                context.startActivity(intent);
            } else if (text.equals(all_menu[OtoConsts.INDEX_CROP])) {
                //crop
                ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                    .setText(path.contains(OtoConsts.EXTRA_DELETE_FILE_HEADER) ?
                        path.replace(OtoConsts.EXTRA_DELETE_FILE_HEADER, OtoConsts.EXTRA_CROP_FILE_HEADER)
                            : OtoConsts.EXTRA_CROP_FILE_HEADER + path);
            } else if (text.equals(all_menu[OtoConsts.INDEX_COPY])) {
                //copy
                ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                    .setText(path.contains(OtoConsts.EXTRA_DELETE_FILE_HEADER) ?
                        path.replace(OtoConsts.EXTRA_DELETE_FILE_HEADER, OtoConsts.EXTRA_FILE_HEADER)
                            : OtoConsts.EXTRA_FILE_HEADER + path);
            } else if (text.equals(all_menu[OtoConsts.INDEX_PASTE])) {
                //paste
                Message paste = new Message();
                if (sourcePath.startsWith(OtoConsts.EXTRA_FILE_HEADER)) {
                    paste.what = OtoConsts.COPY_PASTE;
                    //paste.obj = sourcePath.replace(Intent.EXTRA_FILE_HEADER, "");
                } else if (sourcePath.startsWith(OtoConsts.EXTRA_CROP_FILE_HEADER)) {
                    paste.what = OtoConsts.CROP_PASTE;
                    //paste.obj = sourcePath.replace(Intent.EXTRA_CROP_FILE_HEADER, "");
                }
                paste.obj = sourcePath;
                MainActivity.mHandler.sendMessage(paste);
            } else if (text.equals(all_menu[OtoConsts.INDEX_SORT])) {
                //sort
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.SORT);
            } else if (text.equals(all_menu[OtoConsts.INDEX_NEW_FOLDER])) {
                //new_folder
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.NEWFOLDER);
            } else if (text.equals(all_menu[OtoConsts.INDEX_NEW_FILE])) {
                //new_file
                NewFileDialog newFileDialog = new NewFileDialog(context);
                newFileDialog.showDialog();
            } else if (text.equals(all_menu[OtoConsts.INDEX_DISPLAY_SETTINGS])) {
                //display_settings
                Intent display = new Intent();
                ComponentName compDisplay = new ComponentName(OtoConsts.SETTINGS_PACKAGE,
                                                              OtoConsts.DISPLAY_SETTINGS);
                display.setComponent(compDisplay);
                display.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(display);
            } else if (text.equals(all_menu[OtoConsts.INDEX_CHANGE_WALLPAPER])) {
                //change_wallpaper
                Intent wallpaper =  new Intent();
                ComponentName compWallpaper = new ComponentName(OtoConsts.OTO_FILEMANAGER_PACKAGE,
                                                              OtoConsts.WALLPAPER_PICKER);
                wallpaper.setComponent(compWallpaper);
                wallpaper.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(wallpaper);
            } else if (text.equals(all_menu[OtoConsts.INDEX_DELETE])) {
                //delete
                Message deleteFile = new Message();
                deleteFile.obj = path.contains(OtoConsts.EXTRA_DELETE_FILE_HEADER) ?
                                     path : OtoConsts.EXTRA_DELETE_FILE_HEADER + path;
                deleteFile.what = OtoConsts.DELETE;
                MainActivity.mHandler.sendMessage(deleteFile);
            } else if (text.equals(all_menu[OtoConsts.INDEX_RENAME])) {
                //rename
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.RENAME);
            } else if (text.equals(all_menu[OtoConsts.INDEX_CLEAN_RECYCLE])) {
                //clean_recycle
                DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                File recycle = new File(path);
                                DiskUtils.delete(recycle);
                                if (!recycle.exists()) {
                                    recycle.mkdir();
                                }
                                MainActivity.getResolver().delete(MainActivity.getUri(), null, null);
                            }
                        }.start();
                    }
                };
                OperateUtils.showBaseAlertDialog(context, R.string.dialog_clean_recycle, click);
            } else if (text.equals(all_menu[OtoConsts.INDEX_PROPERTY])) {
                //property
                Message property = new Message();
                property.obj = path;
                property.what = OtoConsts.PROPERTY;
                MainActivity.mHandler.sendMessage(property);
            } else if (text.equals(all_menu[OtoConsts.INDEX_OPEN_WITH])) {
                OpenWithDialog openWithDialog = new OpenWithDialog(context, path);
                openWithDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                openWithDialog.showDialog();
            }
            dismiss();
        }
    };
}
