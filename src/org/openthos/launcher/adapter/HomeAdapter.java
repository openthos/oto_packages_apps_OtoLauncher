package org.openthos.launcher.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Window;
import android.view.WindowManager;
import android.text.Editable;
import android.text.TextUtils;

import com.android.launcher3.R;
import org.openthos.launcher.activity.MainActivity;
import org.openthos.launcher.utils.OtoConsts;
import org.openthos.launcher.utils.OperateUtils;
import org.openthos.launcher.utils.DiskUtils;
import org.openthos.launcher.utils.FileUtils;
import org.openthos.launcher.utils.RenameUtils;
import org.openthos.launcher.entity.Type;
import org.openthos.launcher.entity.IconEntity;
import org.openthos.launcher.view.MenuDialog;
import org.openthos.launcher.view.OpenWithDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<IconEntity> mDatas;
    private List<IconEntity> selectedPositions;
    private RecycleCallBack mRecycleClick;

    private int mLastClickId = -1;
    private long mLastClickTime = 0;
    public boolean isClicked = false;
    public boolean isRename = false;
    public int mRenamePos = -1;
    public int mShiftPos = -1;

    private static final int LESS = 0;
    private static final int MORE = 1;

    private Editable mEdit;
    private HomeViewHolder mHolder;

    public HomeAdapter(List<IconEntity> datas, RecycleCallBack click) {
        mDatas = datas;
        mRecycleClick = click;
        selectedPositions = new ArrayList<>();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_icon, parent, false);
        HomeViewHolder holder = new HomeViewHolder(view, mRecycleClick);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (view.getMeasuredHeight() != ((MainActivity) mRecycleClick).getItemHeight()) {
            ((MainActivity) mRecycleClick).setItemHeight(view.getMeasuredHeight());
        }
        return holder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position).getName());
        mDatas.get(position).setView(holder.item);
        if (mDatas.get(position).isBlank()) {
            holder.nullnull.setChecked(true);
        } else if (!mDatas.get(position).isBlank()) {
            holder.nullnull.setChecked(false);
        }
        if (mDatas.get(position).getIcon() != null) {
            holder.iv.setImageDrawable(mDatas.get(position).getIcon());
        } else {
            holder.iv.setImageDrawable(new ColorDrawable(0));
        }
        if (isRename == true && position == mRenamePos && !holder.tv.hasFocus()) {
            holder.tv.setFocusable(true);
            holder.tv.setFocusableInTouchMode(true);
            holder.tv.requestFocus();
            mHolder = holder;
            holder.tv.selectAll();
            if (mDatas.get(position).getType() == Type.FILE) {
                int index = holder.tv.getText().toString().lastIndexOf(".");
                if (index > 0) {
                    holder.tv.setSelection(0, index);
                }
            }
        } else {
            holder.tv.setFocusable(false);
            holder.tv.clearFocus();
        }
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        RelativeLayout item;
        ImageView iv;
        EditText tv;
        CheckBox checkBox;
        CheckBox nullnull;
        private RecycleCallBack mClick;
        private boolean mIsContains = false;

        public HomeViewHolder(View view, RecycleCallBack click) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            iv = (ImageView) view.findViewById(R.id.icon);
            tv = (EditText) view.findViewById(R.id.texts);
            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnKeyListener(keyListener);
            tv.setOnFocusChangeListener(focusChangeListener);
            tv.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!isRename || getLastClickPos() != getAdapterPosition()) {
                            ctrlProcess(v,event);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!((MainActivity) mRecycleClick).isLongMenuShow()) {
                            ((MainActivity) mRecycleClick).removeLongPressCallbacks();
                            ((MainActivity) mRecycleClick).mRefreshWithoutHot = false;
                        }
                        isClicked = false;
                    }
                    return true;
                }
            });
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ctrlProcess(v, event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!((MainActivity) mRecycleClick).isLongMenuShow()) {
                    ((MainActivity) mRecycleClick).removeLongPressCallbacks();
                    ((MainActivity) mRecycleClick).mRefreshWithoutHot = false;
                    if (isClicked && !MainActivity.mIsShiftPress && !MainActivity.mIsCtrlPress) {
                        setSelectedCurrent(getAdapterPosition());
                    }
                    isClicked = false;
                }
            }
            return true;
        }

        private void ctrlProcess(View v, MotionEvent event) {
            ((MainActivity) mRecycleClick).mRefreshWithoutHot = true;
            if (isRename) {
                mHolder.tv.setFocusable(false);
                mHolder.tv.clearFocus();
                mHolder = null;
                isRename = false;
            }
            if (getAdapterPosition() != -1) {
                if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    checkSelectedIcons();
                    showDialog((int) event.getRawX(), (int) event.getRawY());
                } else if (!mDatas.get(getAdapterPosition()).isBlank()) {
                    isClicked = true;
                    if (MainActivity.mIsShiftPress) {
                        if (selectedPositions.size() == 0) {
                            mShiftPos = -1;
                        }
                        if (mShiftPos == -1) {
                            mShiftPos = getAdapterPosition();
                        }
                        setSelectedCurrent(mShiftPos);
                        if (mShiftPos != getAdapterPosition()) {
                            for (int i = Math.min(mShiftPos, getAdapterPosition());
                                       i <= Math.max(mShiftPos, getAdapterPosition()); i++) {
                                if (i == mShiftPos || mDatas.get(i).isBlank()) {
                                    continue;
                                }
                                for (int j = 0; j < selectedPositions.size(); j++) {
                                    if (selectedPositions.get(j) == mDatas.get(i)) {
                                        mIsContains = true;
                                        break;
                                    }
                                }
                                if (!mIsContains) {
                                    selectedPositions.add(mDatas.get(i));
                                    if (mDatas.get(i).getView() != null) {
                                        mDatas.get(i).getView().setSelected(true);
                                    }
                                    mIsContains = false;
                                }
                            }
                        }
                    } else {
                        mShiftPos = getAdapterPosition();
                        if (MainActivity.mIsCtrlPress) {
                            for (int i = 0; i < selectedPositions.size(); i++) {
                                if (mDatas.indexOf(selectedPositions.get(i))
                                                                         == getAdapterPosition()) {
                                    mIsContains = true;
                                    if (selectedPositions.get(i).getView() != null) {
                                        selectedPositions.get(i).getView().setSelected(false);
                                    }
                                    selectedPositions.remove(i);
                                    break;
                                }
                            }
                            if (!mIsContains) {
                                selectedPositions.add(mDatas.get(getAdapterPosition()));
                                if (mDatas.get(getAdapterPosition()).getView() != null) {
                                    mDatas.get(getAdapterPosition()).getView().setSelected(true);
                                }
                                mIsContains = false;
                            }
                            mIsContains = false;
                        } else {
                            if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                    && (System.currentTimeMillis() - mLastClickTime)
                                                                  < OtoConsts.DOUBLE_CLICK_TIME
                                    && getLastClickPos() == getAdapterPosition()) {
                                ((MainActivity) mRecycleClick).removeLongPressCallbacks();
                                OperateUtils.enter(item.getContext(),
                                                 mDatas.get(getAdapterPosition()).getPath(),
                                                 mDatas.get(getAdapterPosition()).getType());
                            } else {
                                checkSelectedIcons();
                                ((MainActivity) mRecycleClick).setPressInfo(event.getEventTime(),
                                        (int) event.getRawX(), (int) event.getRawY());
                            }
                        }
                        mLastClickTime = System.currentTimeMillis();
                    }
                } else {
                    if (!MainActivity.mIsCtrlPress && !MainActivity.mIsShiftPress) {
                        checkSelectedIcons();
                        ((MainActivity) mRecycleClick).setPressInfo(event.getEventTime(),
                                (int) event.getRawX(), (int) event.getRawY());
                        isClicked = false;
                        ((MainActivity) mRecycleClick).setLocation(event.getRawX(), event.getRawY());
                        setSelectedCurrent(-1);
                    }
                }
            }
        }

        public void checkSelectedIcons() {
            if (mDatas.get(getAdapterPosition()).isBlank()) {
                setSelectedCurrent(-1);
            } else {
                if (selectedPositions.contains(mDatas.get(getAdapterPosition()))) {
                    IconEntity currentIcon = mDatas.get(getAdapterPosition());
                    Type type = mDatas.get(getAdapterPosition()).getType();
                    if (currentIcon.getType() == Type.COMPUTER
                            || currentIcon.getType() == Type.RECYCLE) {
                        setSelectedCurrent(getAdapterPosition());
                    } else {
                        IconEntity computerTemp = null;
                        IconEntity recycleTemp = null;
                        for (IconEntity icon : selectedPositions) {
                            if (icon.getType() == Type.COMPUTER) {
                                if (icon.getView() != null) {
                                    icon.getView().setSelected(false);
                                }
                                computerTemp = icon;
                            } else if (icon.getType() == Type.RECYCLE) {
                                if (icon.getView() != null) {
                                    icon.getView().setSelected(false);
                                }
                                recycleTemp = icon;
                            }
                        }
                        if (computerTemp != null) {
                            selectedPositions.remove(computerTemp);
                        }
                        if (recycleTemp != null) {
                            selectedPositions.remove(recycleTemp);
                        }
                        selectedPositions.remove(currentIcon);
                        selectedPositions.add(currentIcon);
                    }
                } else {
                    setSelectedCurrent(getAdapterPosition());
                }
            }
        }
    }

    public void showDialog(int x, int y) {
        Type type = Type.BLANK;
        String path = "";
        if (selectedPositions.size() == 1) {
            type = mDatas.get(getLastClickPos()).getType();
            path = mDatas.get(getLastClickPos()).getPath();
        } else if (selectedPositions.size() > 1) {
            type = Type.MORE;
            for (int i = 0; i < selectedPositions.size(); i++) {
                path = path + OtoConsts.EXTRA_DELETE_FILE_HEADER
                        + selectedPositions.get(i).getPath();
            }
        }
        MenuDialog dialog = new MenuDialog((MainActivity) mRecycleClick, type, path);
        dialog.showDialog(x, y);
    }

    public void setSelectedCurrent(int current) {
        if (mDatas != null && mDatas.size() > 0) {
            for (IconEntity icon : selectedPositions) {
                if (icon.getView() != null) {
                    icon.getView().setSelected(false);
                }
            }
        }
        selectedPositions.clear();
        if (current != -1 && mDatas.get(current).getView() == null){
            return;
        }
        if (current >= 0 && current < mDatas.size()){
            mDatas.get(current).getView().setSelected(true);
            selectedPositions.add(mDatas.get(current));
        } else {
            mShiftPos = -1;
        }
    }

    public static void openAppBroadcast(Context context) {
        Intent openAppIntent = new Intent();
        openAppIntent.setAction(OtoConsts.ACTION_OPEN_APPLICATION);
        context.sendBroadcast(openAppIntent);
    }

    public List<IconEntity> getSelectedPosList() {
        return selectedPositions;
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                        && v.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN) {
                v.setFocusable(false);
                v.clearFocus();
                mHolder = null;
                isRename = false;
                return true;
            }
            return false;
        }
    };

    private boolean confirmRename(final EditText v, final int position) {
        final IconEntity icon = mDatas.get(position);
        String path = icon.getPath();
        String newName = String.valueOf(v.getText());
        for (IconEntity currentIcon : mDatas) {
            if ((currentIcon.getType() == Type.FILE || currentIcon.getType() == Type.DIRECTORY)
                    && icon != currentIcon) {
                if (currentIcon.getName().equals(newName)) {
                    DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    };
                    OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                               R.string.rename_fail_by_same, click);
                    v.setText(icon.getName());
                    v.selectAll();
                    return false;
                }
            }
        }
        switch (isValidFileName(newName)) {
            case OtoConsts.FILE_NAME_LEGAL:
                return rename(v, position);
            case OtoConsts.FILE_NAME_WARNING:
                DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        rename(v, position);
                        notifyItemChanged(position);
                        dialog.cancel();
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        notifyItemChanged(position);
                        dialog.cancel();
                    }
                };
                OperateUtils.showChooseAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_warning, okClick, cancel);
                return true;
            case OtoConsts.FILE_NAME_NULL:
                DialogInterface.OnClickListener nullClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(position);
                        dialog.cancel();
                    }
                };
                OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_not_null, nullClick);
                break;
            case OtoConsts.FILE_NAME_ILLEGAL:
                DialogInterface.OnClickListener illClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(position);
                        dialog.cancel();
                    }
                };
                OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                   R.string.file_name_illegal, illClick);
                break;
        }
        return false;
    }

    private boolean rename(EditText v, int position) {
        IconEntity icon = mDatas.get(position);
        final String path = icon.getPath();
        final String newName = String.valueOf(v.getText());
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent(), newName);
        boolean isSuccess = oldFile.renameTo(newFile);
        if (!isSuccess) {
            DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
            OperateUtils.showSimpleAlertDialog((MainActivity) mRecycleClick,
                                                       R.string.rename_fail, click);
            v.setText(icon.getName());
            return false;
        }
        icon.setName(newName);
        icon.setPath(newFile.getAbsolutePath());
        if (icon.getType() == Type.FILE) {
            icon.setIcon(FileUtils.getFileIcon(newFile.getAbsolutePath(),
                                                       (MainActivity) mRecycleClick));
        }
        mDatas.set(position, icon);
        MainActivity.mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
        return true;
    }

    private int isValidFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return OtoConsts.FILE_NAME_NULL;
        } else {
            if (fileName.indexOf("/") != -1) {
                return OtoConsts.FILE_NAME_ILLEGAL;
            }
            if (!Pattern.compile("[^@#\\$\\^&*\\(\\)\\[\\]]*").matcher(fileName).matches()) {
                return OtoConsts.FILE_NAME_WARNING;
            }
            return OtoConsts.FILE_NAME_LEGAL;
        }
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                confirmRename((EditText) v, mRenamePos);
            }
        }
    };

    public int getLastClickPos(){
        if (selectedPositions.size() == 0) {
            return -1;
        }
        return mDatas.indexOf(selectedPositions.get(selectedPositions.size() - 1));
    }

    public void notifyText(String commitText) {
        mEdit = mHolder.tv.getText();
        int start = mHolder.tv.getSelectionStart();
        int end = mHolder.tv.getSelectionEnd();
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        if (end < start) {
            start = start + end;
            end = start - end;
            start = start - end;
        }
        switch (commitText) {
            case RenameUtils.ENTER:
                mHolder.tv.setFocusable(false);
                mHolder.tv.clearFocus();
                mHolder = null;
                isRename = false;
                break;
            case RenameUtils.BACKSPACE:
                if (start != end) {
                    mEdit.delete(start, end);
                } else if (start > 0) {
                    mEdit.delete(start - 1, start);
                }
                break;
            case RenameUtils.DELETE:
                if (start != end) {
                    mEdit.delete(start, end);
                } else if (start < mEdit.length()) {
                    mEdit.delete(start, start + 1);
                }
                break;
            case RenameUtils.HOME:
                mHolder.tv.setSelection(0);
                break;
            case RenameUtils.END:
                mHolder.tv.setSelection(mEdit.length());
                break;
            case RenameUtils.LEFT:
                if (start != end) {
                    mHolder.tv.setSelection(start);
                } else if (start > 0) {
                    mHolder.tv.setSelection(start - 1);
                }
                break;
            case RenameUtils.RIGHT:
                if (start != end) {
                    mHolder.tv.setSelection(end);
                } else if (start < mEdit.length()) {
                        mHolder.tv.setSelection(start + 1);
                }
                break;
            default:
                if (MainActivity.mIsCtrlPress) {
                    switch (commitText.toLowerCase()) {
                        case "a":
                            mHolder.tv.selectAll();
                            break;
                        case "c":
                            commitText = mEdit.toString().substring(start, end);
                            if (!TextUtils.isEmpty(commitText)) {
                                MainActivity.mClipboardManager.setText(commitText);
                            }
                            break;
                        case "x":
                            mHolder.tv.setSelection(end);
                            commitText = mEdit.toString().substring(start, end);
                            if (!TextUtils.isEmpty(commitText)) {
                                MainActivity.mClipboardManager.setText(commitText);
                                mEdit.delete(start, end);
                            }
                            break;
                        case "v":
                            mHolder.tv.setSelection(end);
                            CharSequence charSequence = MainActivity.mClipboardManager.getText();
                            if (charSequence != null) {
                                mEdit.replace(start, end,
                                    charSequence.toString().replaceAll("\r|\n|\r\n", ""));
                            }
                            break;
                    }
                } else {
                    mHolder.tv.setSelection(end);
                    mEdit.replace(start, end, commitText);
                }
                break;
        }
    }
}
