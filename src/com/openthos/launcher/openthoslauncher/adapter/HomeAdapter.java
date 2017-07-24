package com.openthos.launcher.openthoslauncher.adapter;

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
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.RenameUtils;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.IconEntity;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import com.openthos.launcher.openthoslauncher.view.OpenWithDialog;

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

    private int mIndex;
    private Editable mEdit;
    private HomeViewHolder mHolder;
    public boolean mIsRenameFirst;
    private boolean mIsRenameRefresh = false;

    public HomeAdapter(List<IconEntity> datas, RecycleCallBack click) {
        mDatas = datas;
        mRecycleClick = click;
        selectedPositions = new ArrayList<>();
    }

    public void notifyText(String commitText) {
        if (mIsRenameFirst) {
            mHolder.tv.requestFocus();
            if (commitText.equals(RenameUtils.LEFT)) {
                mHolder.tv.setSelection(0);
            } else if (commitText.equals(RenameUtils.RIGHT)) {
                mHolder.tv.setSelection(mHolder.tv.getText().length());
            } else if (!(commitText.equals(RenameUtils.ENTER)
                       || commitText.equals(RenameUtils.HOME)
                       || commitText.equals(RenameUtils.END))) {
                mHolder.tv.setText(null);
            }
        }
        mIndex = mHolder.tv.getSelectionStart();
        mEdit = mHolder.tv.getText();
        switch (commitText) {
            case RenameUtils.ENTER:
                mIsRenameRefresh = true;
                mHolder.tv.setFocusable(false);
                mHolder.tv.clearFocus();
                mHolder = null;
                isRename = false;
                break;
            case RenameUtils.DELETE:
                if (mIndex > 0) {
                    mEdit.delete(mIndex - 1, mIndex);
                }
                break;
            case RenameUtils.BACKSPACE:
                if (mIndex < mEdit.length()) {
                    mEdit.delete(mIndex, mIndex + 1);
                }
                break;
            case RenameUtils.HOME:
                mHolder.tv.setSelection(0);
                break;
            case RenameUtils.END:
                mHolder.tv.setSelection(mEdit.length());
                break;
            case RenameUtils.LEFT:
                if ((mIndex -1) >= 0) {
                    mHolder.tv.setSelection(mIndex - 1);
                }
                break;
            case RenameUtils.RIGHT:
                if ((mIndex + 1) <= mEdit.length()) {
                    mHolder.tv.setSelection(mIndex + 1);
                }
                break;
            default:
                mEdit.insert(mIndex, commitText);
                break;
        }
        mIsRenameFirst = false;
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
        if (mDatas.get(position).isChecked()) {
            holder.item.setSelected(true);
        } else {
            holder.item.setSelected(false);
        }
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
        if (isRename == true && position == mRenamePos) {
            holder.tv.setFocusable(true);
            holder.tv.setFocusableInTouchMode(true);
            holder.tv.requestFocus();
            mHolder = holder;
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
        private boolean mIsSelected = false;

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
                        isClicked = true;
                        mIsRenameFirst = false;
                        if (!isRename || getLastClickPos() != getAdapterPosition()) {
                            ctrlProcess(v,event);
                        }
                    }
                    return false;
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
            }
            return true;
        }

        private void ctrlProcess(View v, MotionEvent event) {
            ((MainActivity) mRecycleClick).setIsSelected(true);
            if (isRename) {
                mIsRenameRefresh = false;
                isRename = false;
            }
            if (getAdapterPosition() != -1) {
                ((MainActivity) mRecycleClick).setPressInfo(event.getEventTime(),
                        mDatas.get(getAdapterPosition()).getType(),
                        mDatas.get(getAdapterPosition()).getPath(),
                        (int) event.getRawX(), (int) event.getRawY());
                if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    ((MainActivity) mRecycleClick).removeCallbacks();
                    if (selectedPositions != null) {
                        if (mDatas.get(getAdapterPosition()).isBlank()) {
                            setSelectedCurrent(-1);
                            showDialog(event, LESS);
                        } else {
                            if (selectedPositions.size() > 1) {
                                showMoreDialog(event);
                            } else {
                                showLessDialog(event);
                            }
                        }
                    }
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
                                        mIsSelected = true;
                                        break;
                                    }
                                }
                                if (!mIsSelected) {
                                    selectedPositions.add(mDatas.get(i));
                                    mDatas.get(i).setIsChecked(true);
                                    mIsSelected = false;
                                }
                            }
                        }
                    } else {
                        mShiftPos = getAdapterPosition();
                        if (MainActivity.mIsCtrlPress) {
                            for (int i = 0; i < selectedPositions.size(); i++) {
                                if (mDatas.indexOf(selectedPositions.get(i))
                                                                         == getAdapterPosition()) {
                                    mIsSelected = true;
                                    selectedPositions.get(i).setIsChecked(false);
                                    selectedPositions.remove(i);
                                    break;
                                }
                            }
                            if (!mIsSelected) {
                                selectedPositions.add(mDatas.get(getAdapterPosition()));
                                mDatas.get(getAdapterPosition()).setIsChecked(true);
                                mIsSelected = false;
                            }
                        } else {
                            if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                    && (System.currentTimeMillis() - mLastClickTime)
                                                                  < OtoConsts.DOUBLE_CLICK_TIME
                                    && getLastClickPos() == getAdapterPosition()) {
                                ((MainActivity) mRecycleClick).removeCallbacks();
                                OperateUtils.enter(item.getContext(),
                                                 mDatas.get(getAdapterPosition()).getPath(),
                                                 mDatas.get(getAdapterPosition()).getType());
                            } else {
                                setSelectedCurrent(getAdapterPosition());
                            }
                        }
                        mLastClickTime = System.currentTimeMillis();
                    }
                } else {
                    if (!MainActivity.mIsCtrlPress && !MainActivity.mIsShiftPress) {
                        isClicked = false;
                        ((MainActivity) mRecycleClick).setIsSelected(false);
                        ((MainActivity) mRecycleClick).setLocation(event.getRawX(), event.getRawY());
                        setSelectedCurrent(-1);
                    }
                }
                notifyDataSetChanged();
            }
        }

        private void showLessDialog(MotionEvent event) {
            setSelectedCurrent(getAdapterPosition());
            showDialog(event, LESS);
            notifyDataSetChanged();
        }

        private void showMoreDialog(MotionEvent event) {
            boolean isSelected = false;
            for (int i = 0; i < selectedPositions.size(); i++) {
                if (selectedPositions.get(i) == mDatas.get(getAdapterPosition())) {
                    isSelected = true;
                    break;
                }
            }
            if (isSelected) {
                Type type = mDatas.get(getAdapterPosition()).getType();
                if (type == Type.COMPUTER || type == Type.RECYCLE) {
                    setSelectedCurrent(getAdapterPosition());
                    showDialog(event, LESS);
                } else {
                    IconEntity computerTemp = null;
                    IconEntity recycleTemp = null;
                    for (IconEntity icon : selectedPositions) {
                        if (icon.getType() == Type.COMPUTER) {
                            icon.setIsChecked(false);
                            computerTemp = icon;
                        } else if (icon.getType() == Type.RECYCLE) {
                            icon.setIsChecked(false);
                            recycleTemp = icon;
                        }
                    }
                    if (computerTemp != null) {
                        selectedPositions.remove(computerTemp);
                    }
                    if (recycleTemp != null) {
                        selectedPositions.remove(recycleTemp);
                    }
                    showDialog(event, MORE);
                }
            } else {
                showLessDialog(event);
                return;
            }
            notifyDataSetChanged();
        }

        private void showDialog(MotionEvent event, int selectedNum) {
            Type type = null;
            String path = null;
            switch (selectedNum) {
                case LESS:
                    type = mDatas.get(getAdapterPosition()).getType();
                    path = mDatas.get(getAdapterPosition()).getPath();
                    break;
                case MORE:
                    type = Type.MORE;
                    path = "";
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        path = path + OtoConsts.EXTRA_DELETE_FILE_HEADER
                                                              + selectedPositions.get(i).getPath();
                    }
                    break;
            }
            MenuDialog dialog = new MenuDialog((MainActivity) mRecycleClick, type, path);
            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
        }
    }

    public void setSelectedCurrent(int current) {
        if (mDatas != null && mDatas.size() > 0) {
            for (IconEntity icon : selectedPositions) {
                if (icon.isChecked()) {
                    icon.getView().setSelected(false);
                    icon.setIsChecked(false);
                }
            }
        }
        selectedPositions.clear();
        if (current >= 0 && current < mDatas.size()){
            mDatas.get(current).getView().setSelected(true);
            mDatas.get(current).setIsChecked(true);
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
                mIsRenameRefresh =true;
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
        if (mIsRenameRefresh) {
            notifyItemChanged(mRenamePos);
            mIsRenameRefresh = false;
        }
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
}
