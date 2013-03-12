package com.nd.hilauncherdev.lib.theme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public interface NdLauncherExDialogCallback {
	/**
	 * 
	 * 通用对话框
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param view 视图
	 * @param positive 确定按钮文字
	 * @param negative 取消按钮文字
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 */
     public Dialog createThemeDialog(Context ctx, int icon, CharSequence title, CharSequence message, CharSequence positive, CharSequence negative, final OnClickListener ok, final OnClickListener cancle);
}
