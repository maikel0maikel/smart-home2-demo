package com.nd.hilauncherdev.lib.theme;

import android.content.Context;

public interface NdLauncherExDownActionCallback{
	
	/**
	 * 资源首次下载回调(断点续传不调用)
	 * ----实现此方法时,为防止ANR请在内部开启Thread处理统计。
	 * @param ctx
	 * @param itemType  
	 * 			ThemeItem.ITEM_TYPE_SKIN 表示皮肤
	 * 			ThemeItem.ITEM_TYPE_THEME 表示主题
	 * 			ThemeItem.ITEM_TYPE_LAUNCHER 表示91桌面
	 * @param resID 服务端资源id统一为主题id+itemType
	 */
	public void firstDown(Context ctx, int itemType, String resID);
}