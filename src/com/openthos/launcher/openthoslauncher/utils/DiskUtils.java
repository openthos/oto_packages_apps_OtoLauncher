package com.openthos.launcher.openthoslauncher.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

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
        copyFolder(srcDirName, destDirName);
        deleteFolder(srcDirName);
        return;
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

    public static void copyFolder(String srcDir, String destDir) {
        File srcFile = new File(srcDir);
        String name = srcFile.getName();
        File destFile = new File(destDir + File.separator + name);
        if (!destFile.exists()) {
            destFile.mkdir();
        }
        if (srcFile.exists() && destFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            String src = srcDir;
            String dest = destFile.getAbsolutePath();
            for (File temp : files) {
                if (temp.isDirectory()) {
                    String tempSrc = src + File.separator + temp.getName();
                    String tempDest = dest + File.separator + temp.getName();
                    File tempFile = new File(tempDest);
                    tempFile.mkdir();
                    if (temp.listFiles().length != 0) {
                        copyFolder(tempSrc, tempDest);
                    }
                } else {
                    String tempPath = src + File.separator + temp.getName();
                    copyFile(tempPath, dest);
                }
            }
        }
    }

    public static void copyFile(String srcPath, String destDirPath) {
        File srcfile = new File(srcPath);
        File destDir = new File(destDirPath);
        InputStream is = null;
        OutputStream os = null;
        int ret = 0;
        if (srcfile.exists() && destDir.exists() && destDir.isDirectory()) {
            try {
                is = new FileInputStream(srcfile);
                String destFile = destDirPath + File.separator + srcfile.getName();
                os = new FileOutputStream(new File(destFile));
                byte[] buffer = new byte[1024];
                while ((ret = is.read(buffer)) != -1) {
                    os.write(buffer, 0, ret);
                }
                os.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public static void deleteFolder(String dirPath) {
        File folder = new File(dirPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String tempFilePath = dirPath + File.separator + file.getName();
                deleteFolder(tempFilePath);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }
}
