package com.nd.hilauncherdev.lib.theme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 对话框默认实现
 */
public class NdLauncherExDialogDefaultImp implements NdLauncherExDialogCallback {

	public Dialog createThemeDialog(Context ctx, int icon, CharSequence title, CharSequence message, 
			CharSequence positive, CharSequence negative, OnClickListener ok, OnClickListener cancle) {
		
		AlertDialog.Builder result = new AlertDialog.Builder(ctx);
		if (icon != -1)
			result.setIcon(icon);
		result.setTitle(title).setMessage(message).setPositiveButton(positive, ok);
		if (cancle != null){
			result.setNegativeButton(negative, cancle);
		}else{
			result.setNegativeButton(negative, new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
		}
		return result.create();
	}

}
