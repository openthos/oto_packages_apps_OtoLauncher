package com.openthos.launcher.openthoslauncher.utils;

import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;

/**
 * Created by xu on 2016/8/22.
 */
public class DiskUtils {

    public static File getDesktop() {
        File dir = Environment.getExternalStorageDirectory();
        return new File(dir, "Desktop");
    }

    public static File getRecycle() {
        File dir = Environment.getExternalStorageDirectory();
        return new File(dir, "Recycle");
    }

    public static void delete(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                } else {
                    for (File f : childFile) {
                        delete(f);
                        file.delete();
                    }
                }
            }
        } else {
            return;
        }
    }

    public static void moveDirectory(String srcDirName, String destDirName) {
        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return;
        }
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile()) {
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            } else if (sourceFile.isDirectory()) {
                moveDirectory(sourceFile.getAbsolutePath(), destDir.getAbsolutePath()
                                                 + File.separator + sourceFile.getName());
            }
        }
        srcDir.delete();
        return;
    }

    public static boolean moveFile(String srcFileName, String destDirName) {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }

        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
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
        return fileSizeString;
    }
}
