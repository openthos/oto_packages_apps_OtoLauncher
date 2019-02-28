package org.openthos.launcher.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.launcher3.R;
import org.openthos.launcher.activity.MainActivity;
import org.openthos.launcher.entity.Type;
import org.openthos.launcher.utils.FileUtils;
import org.openthos.launcher.utils.OtoConsts;
import org.openthos.launcher.view.OpenWithDialog;
/**
 * Created by Wang Zhixu on 2016/12/29.
 */
public class OperateUtils {
    public static void openAppBroadcast(Context context) {
        Intent openAppIntent = new Intent();
        openAppIntent.setAction(OtoConsts.ACTION_OPEN_APPLICATION);
        context.sendBroadcast(openAppIntent);
    }

    public static void enter(Context context, String path, Type type) {
        switch (type) {
            case COMPUTER:
            case RECYCLE:
            case DIRECTORY:
                PackageManager packageManager = context.getPackageManager();
                try {
                    Intent intent = packageManager.getLaunchIntentForPackage(
                                             OtoConsts.OTO_FILEMANAGER_PACKAGE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtra(OtoConsts.EXTRA_DESKTOP_PATH_TAG, path);
                    context.startActivity(intent);
                } catch (NullPointerException e) {
                }
                break;
            case FILE:
                String fileType = FileUtils.getMIMEType(new File(path));
                List<ResolveInfo> resolveInfoList = new ArrayList<>();
                PackageManager manager = context.getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (android.os.Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(context,
                            "org.openthos.support.fileprovider", new File(path)), fileType);
                } else {
                    intent.setDataAndType(Uri.fromFile(new File(path)), fileType);
                }
                resolveInfoList = manager.queryIntentActivities(intent,
                                                                 PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfoList.size() > 0) {
                    intent.putExtra("package", "com.android.launcher3");
                    context.startActivity(intent);
                } else {
                    OpenWithDialog openWithDialog = new OpenWithDialog(context, path);
                    openWithDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    openWithDialog.showDialog();
                }
                break;
        }
        openAppBroadcast(context);
    }

    public static void exec(String[] commands) {
        Process pro;
        BufferedReader in = null;
        try {
            pro = Runtime.getRuntime().exec(commands);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                continue;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showBaseAlertDialog(Context context, int messageId,
                                                       DialogInterface.OnClickListener click) {
        AlertDialog dialog = new AlertDialog.Builder(context)
             .setMessage(context.getResources().getString(messageId))
             .setPositiveButton(context.getResources().getString(R.string.dialog_delete_yes), click)
             .setNegativeButton(context.getResources().getString(R.string.dialog_delete_no),
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 }).create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnKeyListener(new BaseKeyEvent());
        dialog.show();
    }

    public static void showChooseAlertDialog(Context context, int messageId,
                       DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog dialog = new AlertDialog.Builder(context)
             .setMessage(context.getResources().getString(messageId))
             .setPositiveButton(context.getResources().getString(R.string.dialog_delete_yes), ok)
             .setNegativeButton(context.getResources().getString(R.string.dialog_delete_no), cancel)
             .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnKeyListener(new BaseKeyEvent());
        dialog.show();
    }

    public static void showSimpleAlertDialog(Context context, int messageId,
                                                       DialogInterface.OnClickListener click) {
        AlertDialog dialog = new AlertDialog.Builder(context)
             .setMessage(context.getResources().getString(messageId))
             .setPositiveButton(context.getResources().getString(R.string.dialog_delete_yes), click)
             .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public static class BaseKeyEvent implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            MainActivity.setState(event.isCtrlPressed(), event.isShiftPressed());
            return false;
        }
    }
}
