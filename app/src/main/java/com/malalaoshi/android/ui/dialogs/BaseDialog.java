package com.malalaoshi.android.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.malalaoshi.android.R;

/**
 * Created by donald on 2017/6/29.
 */

public abstract class BaseDialog {

    private static final int COMMON_DIALOG_STYLE = R.style.dialog_common_style;
    private final Display mDisplay;
    private final Dialog mDialog;
    protected Context mContext;

    public BaseDialog(Context context) {
        mContext = context;
        if (getDialogStyleId() == 0)
            mDialog = new Dialog(context, COMMON_DIALOG_STYLE);
        else
            mDialog = new Dialog(context, getDialogStyleId());
        View view = getView();
        mDialog.setContentView(view);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = (int) (mDisplay.getWidth() * 0.85);
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    protected abstract View getView();

    protected abstract int getDialogStyleId();

    public void show(){
        mDialog.show();
    }
    public void dismiss(){
        mDialog.dismiss();
    }
    public boolean isShowing(){
        return mDialog.isShowing();
    }
    public void setCancelable(boolean flag){
        mDialog.setCancelable(flag);
    }

}
