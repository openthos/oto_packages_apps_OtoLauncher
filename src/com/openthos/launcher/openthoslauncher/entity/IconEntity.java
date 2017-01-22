package com.openthos.launcher.openthoslauncher.entity;

import android.graphics.drawable.Drawable;
import com.openthos.launcher.openthoslauncher.entity.Type;
/**
 * Created by xu on 2017/01/22.
 */
public class IconEntity {
    private String mName;
    private String mPath;
    private boolean mIsChecked;
    private Drawable mIcon;
    private int mIconRes;
    private boolean mIsBlank;
    private Type mType;

    public IconEntity() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public void setIconRes(int iconRes) {
        mIconRes = iconRes;
    }

    public boolean isBlank() {
        return mIsBlank;
    }

    public void setIsBlank(boolean isBlank) {
        mIsBlank = isBlank;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }
}
