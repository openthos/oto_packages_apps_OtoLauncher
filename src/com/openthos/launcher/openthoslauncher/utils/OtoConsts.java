package com.openthos.launcher.openthoslauncher.utils;

/**
 * Created by xu on 2016/8/12.
 */
public class OtoConsts {
    public static final int MAX_ICON = 50;
    public static final int DOUBLE_CLICK_TIME = 500;
    public static final int MAX_LINE = 6;
    public static final int FIX_Y = 0;
    public static final int BAR_Y = 40;
    public static final float FIX_ALPHA = 0.9f;
    //openthos:"com.emindsoft.openthos"
    //system:"com.cyanogenmod.filemanager"
    public static final String FILEMANAGER_PACKAGE = "com.cyanogenmod.filemanager";
    public static final String SETTINGS_PACKAGE = "com.android.settings";
    public static final String DISPLAY_SETTINGS = "com.android.settings.DisplaySettings";
    //public static final String WALLPAPER_SETTINGS = "com.android.settings.WallpaperTypeSettings";
    public static final String RECYCLE_PATH = DiskUtils.getRecycle().getAbsolutePath();
    public static final String DESKTOP_PATH = DiskUtils.getDesktop().getAbsolutePath();
    //MainActivity Handler Message'what
    public static final int REFRESH = 0x0000001;
    public static final int SORT = 0x0000002;
    public static final int DELETE = 0x0000003;
    public static final int NEWFOLDER = 0x0000004;
    public static final int RENAME = 0x00000005;
    public static final int PROPERTY = 0x00000006;

    public static final int INDEX_OPEN = 0;
    public static final int INDEX_ABOUT_COMPUTER = 1;
    public static final int INDEX_COMPRESS = 2;
    public static final int INDEX_DECOMPRESSION = 3;
    public static final int INDEX_CROP = 4;
    public static final int INDEX_COPY = 5;
    public static final int INDEX_PASTE = 6;
    public static final int INDEX_SORT = 7;
    public static final int INDEX_NEW_FOLDER = 8;
    public static final int INDEX_DISPLAY_SETTINGS = 9;
    public static final int INDEX_CHANGE_WALLPAPER = 10;
    public static final int INDEX_DELETE = 11;
    public static final int INDEX_RENAME = 12;
    public static final int INDEX_CLEAN_RECYCLE = 13;
    public static final int INDEX_PROPERTY = 14;

    public static final int LIMIT_LENGTH = 10;
    public static final int LIMIT_OWNER_READ = 1;
    public static final int LIMIT_OWNER_WRITE = 2;
    public static final int LIMIT_OWNER_EXECUTE = 3;
    public static final int LIMIT_GROUP_READ = 4;
    public static final int LIMIT_GROUP_WRITE = 5;
    public static final int LIMIT_GROUP_EXECUTE = 6;
    public static final int LIMIT_OTHER_READ = 7;
    public static final int LIMIT_OTHER_WRITE = 8;
    public static final int LIMIT_OTHER_EXECUTE = 9;

    public static final long SIZE_KB = 1024L;
    public static final long SIZE_MB = 1024L*1024L;
    public static final long SIZE_GB = 1024L*1024L*1024L;
    public static final long SIZE_TB = 1024L*1024L*1024L*1024L;

    public static final int INDEX_LIMIT_BEGIN = 14;
    public static final int INDEX_LIMIT_END = 24;
    public static final int INDEX_TIME_BEGIN = 8;
    public static final int INDEX_TIME_END = 27;

}
