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

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.IconEntity;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import com.openthos.launcher.openthoslauncher.view.OpenWithDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<IconEntity> data;
    private List<Integer> selectedPositions;
    private RecycleCallBack mRecycleClick;

    private int mLastClickId = -1;
    private long mLastClickTime = 0;
    private boolean isClicked = false;
    public boolean isRename = false;

    private static final int LESS = 0;
    private static final int MORE = 1;

    private int mIndex;
    private Editable mEdit;
    private HomeViewHolder mHolder;
    public boolean mIsRenameFirst;

    public HomeAdapter(List<IconEntity> data, RecycleCallBack click) {
        this.data = data;
        this.mRecycleClick = click;
        selectedPositions = new ArrayList<>();
    }

    public void setData(List<IconEntity> data) {
        this.data = data;
    }

    public void notifyText(String commitText) {
        if (mIsRenameFirst) {
            mHolder.tv.setText(null);
        }
        mIndex = mHolder.tv.getSelectionStart();
        mEdit = mHolder.tv.getText();
        if (Intent.EXTRA_DESKTOP_ENTER.equals(commitText)) {
            confirmRename(mHolder.tv, getLastClickPos());
        } else if (Intent.EXTRA_DESKTOP_BACK.equals(commitText)) {
            if (mIndex > 0) {
                mEdit.delete(mIndex - 1, mIndex);
            }
        } else {
            mEdit.insert(mIndex, commitText);
        }
        mIsRenameFirst = false;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeViewHolder holder = new HomeViewHolder(LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.item_icon, parent, false), mRecycleClick);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.tv.setText(data.get(position).getName());
        if (data.get(position).isChecked()) {
            holder.item.setBackgroundResource(R.drawable.icon_background);
        } else if (!data.get(position).isChecked()) {
            holder.item.setBackgroundResource(R.drawable.icon_background_trans);
        }
        if (data.get(position).isBlank()) {
            holder.nullnull.setChecked(true);
        } else if (!data.get(position).isBlank()) {
            holder.nullnull.setChecked(false);
        }
        if (data.get(position).getIcon() != null) {
            holder.iv.setImageDrawable(data.get(position).getIcon());
        } else {
            holder.iv.setImageDrawable(new ColorDrawable(0));
        }
        if (isRename == true && position == getLastClickPos()) {
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
                    mIsRenameFirst = false;
                    if (isRename == false) {
                        ctrlProcess(v,event);
                    }
                    return false;
                }
            });
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
            itemView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (MenuDialog.isExistMenu() == false) {
                            MenuDialog dialog = new MenuDialog(item.getContext(), Type.BLANK, "");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                            MenuDialog.setExistMenu(true);
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (isClicked != true && getLastClickPos() != -1) {
                            setSelectedCurrent(-1);
                            notifyDataSetChanged();
                        }
                        isClicked = false;
                        if (isRename == true) {
                            isRename = false;
                            notifyDataSetChanged();
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ctrlProcess(v, event);
            return true;
        }

        private void ctrlProcess(View v, MotionEvent event) {
            if (getAdapterPosition() != -1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (selectedPositions != null) {
                            if (data.get(getAdapterPosition()).isBlank()) {
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
                    } else if (!data.get(getAdapterPosition()).isBlank()) {
                        if (MainActivity.mIsCtrlPress) {
                            boolean isSelected = false;
                            for (int i = 0; i < selectedPositions.size(); i++) {
                                if (selectedPositions.get(i) == getAdapterPosition()) {
                                    isSelected = true;
                                    data.get(selectedPositions.get(i)).setIsChecked(false);
                                    selectedPositions.remove(i);
                                    break;
                                }
                            }
                            if (!isSelected) {
                                selectedPositions.add(getAdapterPosition());
                                data.get(getAdapterPosition()).setIsChecked(true);
                            }
                        } else {
                            if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                    && (System.currentTimeMillis() - mLastClickTime)
                                                                  < OtoConsts.DOUBLE_CLICK_TIME
                                    && getLastClickPos() == getAdapterPosition()) {
                                OperateUtils.enter(item.getContext(),
                                                 data.get(getAdapterPosition()).getPath(),
                                                 data.get(getAdapterPosition()).getType());
                            } else {
                                setSelectedCurrent(getAdapterPosition());
                            }
                        }
                        mLastClickTime = System.currentTimeMillis();
                    } else {
                        setSelectedCurrent(-1);
                    }
                    notifyDataSetChanged();
                }
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
                if (selectedPositions.get(i) == getAdapterPosition()) {
                    isSelected = true;
                    break;
                }
            }
            if (isSelected) {
                Type type = data.get(getAdapterPosition()).getType();
                if (type == Type.COMPUTER || type == Type.RECYCLE) {
                    setSelectedCurrent(getAdapterPosition());
                    showDialog(event, LESS);
                } else {
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        Type types = data.get(i).getType();
                        if (types == Type.COMPUTER || types == Type.RECYCLE) {
                            data.get(i).setIsChecked(false);
                        }
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
                    type = data.get(getAdapterPosition()).getType();
                    path = data.get(getAdapterPosition()).getPath();
                    break;
                case MORE:
                    type = Type.MORE;
                    path = "";
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        path = path + Intent.EXTRA_DELETE_FILE_HEADER +
                                            data.get(selectedPositions.get(i)).getPath();
                    }
                    break;
            }
            MenuDialog dialog = new MenuDialog((MainActivity) mRecycleClick, type, path);
            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
            MenuDialog.setExistMenu(true);
        }
    }

    public void setSelectedCurrent(int current) {
        if (data != null && data.size() > 0) {
            for (int i : selectedPositions) {
                data.get(i).setIsChecked(false);
            }
        }
        selectedPositions.clear();
        if (current >= 0 && current < data.size()){
            data.get(current).setIsChecked(true);
            selectedPositions.add(current);
        }
    }

    public static void openAppBroadcast(Context context) {
        Intent openAppIntent = new Intent();
        openAppIntent.setAction(Intent.ACTION_OPEN_APPLICATION);
        context.sendBroadcast(openAppIntent);
    }

    public List<Integer> getSelectedPosList() {
        return selectedPositions;
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                        && v.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN) {
                return confirmRename((EditText) v, getLastClickPos());
            }
            return false;
        }
    };

    private boolean confirmRename(EditText v, int position) {
        IconEntity icon = data.get(position);
        String path = icon.getPath();
        String newName = String.valueOf(v.getText());
        for (IconEntity currentIcon : data) {
            if (currentIcon.getName().equals(newName)) {
                AlertDialog dialog = new AlertDialog.Builder((MainActivity) mRecycleClick)
                     .setMessage(((MainActivity) mRecycleClick).getResources().getString(
                                                        R.string.rename_fail_by_same))
                     .setNegativeButton(((MainActivity) mRecycleClick).getResources()
                                                .getString(R.string.dialog_ok),
                         new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.cancel();
                             }
                         }).create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
                v.setText(icon.getName());
                v.selectAll();
                return true;
            }
        }
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent(), newName);
        boolean isSuccess = oldFile.renameTo(newFile);
        if (!isSuccess) {
            AlertDialog dialog = new AlertDialog.Builder((MainActivity) mRecycleClick)
                 .setMessage(((MainActivity) mRecycleClick).getResources().getString(
                                                    R.string.rename_fail))
                 .setNegativeButton(((MainActivity) mRecycleClick).getResources()
                                            .getString(R.string.dialog_ok),
                     new android.content.DialogInterface.OnClickListener() {

                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.cancel();
                         }
                     }).create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
            v.setText(icon.getName());
            v.selectAll();
            return true;
        }
        icon.setName(newName);
        icon.setPath(newFile.getAbsolutePath());
        data.set(position, icon);
        notifyDataSetChanged();
        IconEntity mIcon = ((MainActivity) mRecycleClick).mDatas.get(getLastClickPos());
        mIcon.setName(newName);
        mIcon.setPath(newFile.getAbsolutePath());
        ((MainActivity)mRecycleClick).mDatas.set(getLastClickPos(), mIcon);
        v.setFocusable(false);
        v.clearFocus();
        isRename = false;
        mHolder = null;
        MainActivity.mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
        return true;
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) { // TODO: will add code, next.
            } else {
                v.setFocusable(false);
                v.clearFocus();
                isRename = false;
            }
        }
    };

    public int getLastClickPos(){
        if (selectedPositions.size() == 0) {
            return -1;
        }
        return selectedPositions.get(selectedPositions.size() - 1);
    }
}
