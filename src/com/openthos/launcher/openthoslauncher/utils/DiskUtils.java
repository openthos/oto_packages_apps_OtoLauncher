package com.openthos.launcher.openthoslauncher.utils;

import android.os.Environment;

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
import com.openthos.launcher.openthoslauncher.entity.CompressFormatType;

/**
 * Created by xu on 2016/8/22.
 */
public class DiskUtils {

    public static File getDesktop() {
        return new File(Environment.getExternalStorageDirectory(), "Desktop");
    }

    public static File getRecycle() {
        return new File(Environment.getExternalStorageDirectory(), "Recycle");
    }

    //command:rm
    public static void delete(File file) {
        if (file.exists()) {
            String command = "/system/xbin/rm";
            String arg = "";
            if (file.isFile()) {
                arg = "-v";
            } else if (file.isDirectory()) {
                arg = "-rv";
            }
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(new String[]{command, arg, file.getAbsolutePath()});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //command:mv
    public static void moveFile(String srcFile, String destDir) {
        String command = "/system/xbin/mv";
        String arg = "-v";
        copyOrMoveFile(command, arg, srcFile, destDir);
    }

    //command:cp
    public static void copyFile(String srcFile, String destDir) {
        String command = "/system/xbin/cp";
        String arg = "-v";
        File f = new File(srcFile);
        if (f.isDirectory()){
            arg = "-rv";
        }
        copyOrMoveFile(command, arg, srcFile, destDir);
    }

    private static void copyOrMoveFile(String command, String arg, String srcFile, String destDir) {
         try {
            File f = new File(destDir, new File(srcFile).getName());
            File destFile = f;
            if (f.exists()) {
                for (int i = 2; ; i++) {
                    File current = new File(f.getAbsolutePath() + "." + i);
                    if (!current.exists()) {
                        destFile= new File(destDir, current.getName());
                        break;
                    }
                }
            }
            Runtime.getRuntime().exec(new String[] {command, arg, srcFile,
                                                    destFile.getAbsolutePath()});
        } catch (IOException e) {
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
        return fileSizeString;
    }

    public static void rename(String path, String newname) {
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent(), newname);
        oldFile.renameTo(newFile);
    }

    // command
    public static File compress(String path, CompressFormatType type) {
        File f = new File(path);
        String command;
        String arg0;
        String arg1;
        String suffix;
        String tarPath = "";
        BufferedReader in = null;
        boolean isOk = false;
        switch (type) {
            case TAR:
                command = "/system/xbin/tar";
                arg0 = "-cvf";
                arg1 ="-C";
                suffix =".tar";
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case GZIP:
                command = "/system/xbin/tar";
                arg0 = "-zcvf";
                arg1 ="-C";
                suffix = ".tar.gz";
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case BZIP2:
                command = "/system/xbin/tar";
                arg0 = "-jcvf";
                arg1 ="-C";
                suffix = ".tar.bz2";
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case ZIP:
                command = "/system/xbin/7z";
                arg0 = "a";
                arg1 ="-w";
                suffix = ".zip";
                tarPath = path + suffix;
                try {
                    Process pro = Runtime.getRuntime().exec(
                                                  new String[]{command, arg0, tarPath, arg1, path});
                    in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    while(in.readLine() != null) {
                    }
                    isOk = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isOk) {
            return new File(tarPath);
        }
        return null;
    }

    private static boolean tarCommand(String command, String arg0, String tarPath, String arg1,
                                            String parentPath, String fileName, BufferedReader in) {
        try {
            Process pro = Runtime.getRuntime().exec(
                                  new String[]{command, arg0, tarPath, arg1, parentPath, fileName});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            while(in.readLine() != null) {
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // command
    public static File decompress(String path) {
        String command ="/usr/bin/7z";
        String arg0 = "x";
        String arg1 ="-o";
        File f = new File(path);
        try {
            Runtime.getRuntime().exec(new String[]{command, arg0,  path, arg1 + f.getParent()});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
