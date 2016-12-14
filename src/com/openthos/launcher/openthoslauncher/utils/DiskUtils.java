package com.openthos.launcher.openthoslauncher.utils;

import android.os.Environment;

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
import com.openthos.launcher.openthoslauncher.entity.CompressFormatType;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;

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
                while ((line = in.readLine()) != null) {
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
    }

    //command:mv
    public static void moveFile(String srcFile, String destDir) {
        String command = "/system/bin/mv";
        String arg = "-v";
        copyOrMoveFile(command, arg, srcFile, destDir);
    }

    //command:cp
    public static void copyFile(String srcFile, String destDir) {
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
            MainActivity.mHandler.sendEmptyMessage(OtoConsts.COPY_INFO_HIDE);
            if (command.contains("cp")) {
                if (destFile.getAbsolutePath().contains(OtoConsts.DESKTOP_PATH)) {
                    MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                            OtoConsts.SHOW_FILE, destFile.getAbsolutePath()));
                }
            } else if (command.contains("mv")) {
                if (destFile.getAbsolutePath().contains(OtoConsts.DESKTOP_PATH)) {
                    MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                            OtoConsts.SHOW_FILE, destFile.getAbsolutePath()));
                }
                if (sourceFile.getAbsolutePath().contains(OtoConsts.DESKTOP_PATH)) {
                    MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                            OtoConsts.DELETE_REFRESH, sourceFile.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            MainActivity.mHandler.sendEmptyMessage(OtoConsts.COPY_INFO_HIDE);
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
                arg1 = "-C";
                suffix = OtoConsts.SUFFIX_TAR;
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case GZIP:
                command = "/system/xbin/tar";
                arg0 = "-zcvf";
                arg1 = "-C";
                suffix = OtoConsts.SUFFIX_TAR_GZIP;
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case BZIP2:
                command = "/system/xbin/tar";
                arg0 = "-jcvf";
                arg1 = "-C";
                suffix = OtoConsts.SUFFIX_TAR_BZIP2;
                tarPath = path + suffix;
                isOk = tarCommand(command, arg0, tarPath, arg1,
                                                        f.getParent(), f.getName(), in);
                break;
            case ZIP:
                command = "/system/bin/7za";
                arg0 = "a";
                arg1 = "-w";
                suffix = OtoConsts.SUFFIX_ZIP;
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
    public static String[] decompress(String path) {
        String command = "/system/bin/7za";
        String arg0 = "x";
        String arg1 = "-o";
        File f = new File(path);
        try {
            Runtime.getRuntime().exec(new String[]{command, arg0,  path, arg1 + f.getParent()});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list(path);
    }

    public static String[] list(String file){
        String command = "system/bin/7za";
        String arg0 = "l";
        Runtime runtime = Runtime.getRuntime();
        Process pro;
        BufferedReader in = null;
        boolean isPrint = false;
        ArrayList<String> fileList= new ArrayList<>();
        try {
            pro = runtime.exec(new String[]{command, arg0, file});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (isPrint) {
                    if (line.contains("-----")) {
                        isPrint = false;
                        continue;
                    }
                    line = line.substring(OtoConsts.INDEX_7Z_FILENAME);
                    System.out.println(line);
                    if (line.contains("/")) {
                        line = line.replace(line.substring(line.indexOf("/")), "");
                        if (!fileList.contains(line)) {
                            fileList.add(line);
                        }
                    } else {
                        fileList.add(line);
                    }
                }
                if (line.contains("-----")){
                    isPrint = true;
                }
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
        return fileList.toString().substring(1, fileList.toString().length() - 1).split(", ");
    }
}
