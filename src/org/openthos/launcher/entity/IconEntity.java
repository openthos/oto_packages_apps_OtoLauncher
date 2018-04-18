package org.openthos.launcher.entity;

import android.graphics.drawable.Drawable;
import android.view.View;
import org.openthos.launcher.entity.Type;
/**
 * Created by xu on 2017/01/22.
 */
public class IconEntity {
    private String mName;
    private String mPath;
    private Drawable mIcon;
    private int mIconRes;
    private boolean mIsBlank;
    private Type mType;
    private View mView;
    private long mLastModified;

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

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(long lastModified) {
        mLastModified = lastModified;
    }
}
