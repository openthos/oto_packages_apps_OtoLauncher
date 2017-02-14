package com.openthos.launcher.openthoslauncher.utils;

/**
 * Created by xu on 2016/8/12.
 */
public class OtoConsts {
    public static final int DOUBLE_CLICK_TIME = 500;
    public static final int BAR_Y = 40;
    public static final float FIX_ALPHA = 0.9f;
    public static final int FIX_PADDING = 16;
    public static final String FILEMANAGER_PACKAGE = "com.cyanogenmod.filemanager";
    public static final String OTO_FILEMANAGER_PACKAGE = "com.openthos.filemanager";
    public static final String SETTINGS_PACKAGE = "com.android.settings";
    public static final String DISPLAY_SETTINGS = "com.android.settings.DisplaySettings";
    public static final String WALLPAPER_PICKER = "com.android.settings.WallpaperPicker";
    public static final String DESKTOP_DATA = "com.android.launcher3.DesktopData";
    public static final String DESKTOP_INPUT =
                  "com.android.launcher3/com.openthos.launcher.openthoslauncher.activity.InputIME";
    public static final String INTERCEPT_ONKEYDOWN_KEYCODE = "keyCode";
    public static final String INTERCEPT_ONKEYDOWN_KEYEVENT = "event";
    //public static final String WALLPAPER_SETTINGS = "com.android.settings.WallpaperTypeSettings";
    public static final String RECYCLE_PATH = DiskUtils.getRecycle().getAbsolutePath();
    public static final String DESKTOP_PATH = DiskUtils.getDesktop().getAbsolutePath();
    //MainActivity Handler Message'what
    public static final int SAVEDATA = 0x00000001;
    public static final int SORT = 0x0000002;
    public static final int DELETE = 0x0000003;
    public static final int NEWFOLDER = 0x0000004;
    public static final int RENAME = 0x00000005;
    public static final int PROPERTY = 0x00000006;
    public static final int DELETE_REFRESH = 0x00000007;
    public static final int COMPRESS = 0x00000008;
    public static final int DELETE_DIRECT = 0x00000009;
    public static final int SHOW_FILE = 0x00000010;
    public static final int DECOMPRESS = 0x00000011;
    public static final int COPY_PASTE = 0x00000012;
    public static final int CROP_PASTE = 0x00000013;
    public static final int NEWFILE = 0x00000014;
    public static final int COPY_INFO_SHOW = 0x00000015;
    public static final int COPY_INFO_HIDE = 0x00000016;
    public static final int COPY_INFO = 0x00000017;
    public static final int CLEAN_CLIPBOARD = 0x00000018;

    public static final int SKIP_LINES = 10;
    public static final int INTERCEPT_ONKEYDOWN = 99;

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
    public static final int INDEX_NEW_FILE = 15;
    public static final int INDEX_OPEN_WITH = 16;

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
    public static final int INDEX_7Z_FILENAME = 53;

    public static final String SUFFIX_TAR = ".tar";
    public static final String SUFFIX_TAR_GZIP = ".gz";
    public static final String SUFFIX_TAR_BZIP2 = ".bz2";
    public static final String SUFFIX_ZIP = ".zip";
    public static final int FILE_NAME_LEGAL = 0;
    public static final int FILE_NAME_NULL = 1;
    public static final int FILE_NAME_ILLEGAL = 2;
    public static final int FILE_NAME_WARNING = 3;
    public static final String[] NAMESTART = {"+", "-", "."};
    public static final String[] NAMEBODY = {"@", "#", "$", "^", "&", "*", "(", ")",
                                             "[", "]", " ", "\t"};
}
