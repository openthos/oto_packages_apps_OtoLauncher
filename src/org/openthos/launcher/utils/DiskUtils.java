package org.openthos.launcher.utils;

import android.os.Environment;
import android.content.ContentResolver;
import android.content.ContentValues;

import android.os.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.openthos.launcher.entity.CompressFormatType;
import org.openthos.launcher.utils.OtoConsts;
import org.openthos.launcher.activity.MainActivity;

/**
 * Created by xu on 2016/8/22.
 */
public class DiskUtils {
    private static final String COMMAND_7ZA = "/system/bin/7za";

    public static File getDesktop() {
        return new File(Environment.getExternalStorageDirectory(), "Desktop");
    }

    public static File getRecycle() {
        return new File(Environment.getExternalStorageDirectory(), "Recycle");
    }

    //command:rm
    public static void delete(File file) {
        if (file.exists()) {
            String command = "/system/bin/rm";
            String arg = "";
            if (file.isFile()) {
                arg = "-v";
            } else if (file.isDirectory()) {
                arg = "-rv";
            }
            BufferedReader in = null;
            Runtime runtime = Runtime.getRuntime();
            try {
                Process pro = runtime.exec(new String[]{command, arg, file.getAbsolutePath()});
                in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                String line;
                MainActivity.mHandler.removeMessages(OtoConsts.COPY_INFO_HIDE);
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.DELETE_INFO_SHOW);
                while ((line = in.readLine()) != null) {
                }
                MainActivity.mHandler.sendEmptyMessageDelayed(OtoConsts.COPY_INFO_HIDE, 1000);
            } catch (IOException e) {
                MainActivity.mHandler.sendEmptyMessageDelayed(OtoConsts.COPY_INFO_HIDE, 1000);
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
    }

    //command:mv
    public static void moveFile(String srcFile, String destDir) {
        if (new File(srcFile).getParent().equals(destDir)) {
            return;
        }
        String command = "/system/bin/mv";
        String arg = "-v";
        copyOrMoveFile(command, arg, srcFile, destDir);
    }

    //command:cp
    public static void copyFile(String srcFile, String destDir) {
        if (srcFile.equals(destDir)) {
            return;
        }
        String command = "/system/bin/cp";
        String arg = "-v";
        File f = new File(srcFile);
        if (f.isDirectory()){
            arg = "-rv";
        }
        copyOrMoveFile(command, arg, srcFile, destDir);
    }

    private static void copyOrMoveFile(String command, String arg, String srcFile, String destDir) {
        Process pro;
        File f = new File(destDir, new File(srcFile).getName());
        File destFile = f;
        File sourceFile = new File(srcFile);
        BufferedReader in = null;
        try {
            if (sourceFile.isDirectory()) {
                if (f.exists()) {
                    for (int i = 2; ; i++) {
                        File current = new File(f.getAbsolutePath() + "." + i);
                        if (!current.exists()) {
                            destFile = new File(destDir, current.getName());
                            break;
                        }
                    }
                }
            } else if (sourceFile.isFile()) {
                if (f.exists()) {
                    String suffix;
                    if (f.getAbsolutePath().contains(".")) {
                        suffix = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."),
                                f.getAbsolutePath().length());
                    } else {
                        suffix = "";
                    }
                    for (int i = 2; ; i++) {
                        File current = new File(f.getAbsolutePath().replace(suffix, "") + "."
                                + i + suffix);
                        if (!current.exists()) {
                            destFile = new File(destDir, current.getName());
                            break;
                        }
                    }
                }
            }
            pro = Runtime.getRuntime().exec(new String[]{command, arg, srcFile,
                    destFile.getAbsolutePath()});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            MainActivity.mHandler.removeMessages(OtoConsts.COPY_INFO_HIDE);
            MainActivity.mHandler.sendEmptyMessage(OtoConsts.COPY_INFO_SHOW);
            int i = 0;
            while ((line = in.readLine()) != null) {
                if (i == 0) {
                    MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                            OtoConsts.COPY_INFO, line));
                    i = OtoConsts.SKIP_LINES;
                } else {
                    i--;
                }
            }
            MainActivity.mHandler.sendEmptyMessageDelayed(OtoConsts.COPY_INFO_HIDE, 1000);
            if (command.contains("mv")) {
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.CLEAN_CLIPBOARD);
            }
        } catch (IOException e) {
            MainActivity.mHandler.sendEmptyMessageDelayed(OtoConsts.COPY_INFO_HIDE, 1000);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (destDir.equals(OtoConsts.RECYCLE_PATH)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("source", OtoConsts.DESKTOP_PATH);
            contentValues.put("filename", destFile.getName());
            MainActivity.getResolver().insert(MainActivity.getUri(), contentValues);
        }
    }

    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < OtoConsts.SIZE_KB) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < OtoConsts.SIZE_MB) {
            fileSizeString = df.format((double) fileSize / OtoConsts.SIZE_KB) + "K";
        } else if (fileSize < OtoConsts.SIZE_GB) {
            fileSizeString = df.format((double) fileSize / OtoConsts.SIZE_MB) + "M";
        } else if (fileSize < OtoConsts.SIZE_TB){
            fileSizeString = df.format((double) fileSize / OtoConsts.SIZE_GB) + "G";
        }else {
            fileSizeString = df.format((double) fileSize / OtoConsts.SIZE_TB) + "T";
        }
        if (fileSizeString.equals(".00B")) {
            fileSizeString = "0.00B";
        }
        return fileSizeString;
    }

    public static void rename(String path, String newname) {
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent(), newname);
        oldFile.renameTo(newFile);
    }
}
