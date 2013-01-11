package com.nd.hilauncherdev.lib.theme.down;


import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;
import com.nd.hilauncherdev.lib.theme.util.SUtil;
import com.nd.hilauncherdev.lib.theme.util.TelephoneUtil;

/**
 * 用户主题下载的管理类
 */
public final class DownloadTask {

	 private final static String TAG = "ThemeShopV2DownloadManager";
	
	private Context ctx;
	
	private ThemeItem mTheme;
	
	public static HashMap<String ,String> hashMap = new HashMap<String,String>();
	
	/**
	 * 下载主题
	 */
	public void downloadTheme( Context ctx, ThemeItem theme ) {
		this.ctx = ctx;
		mTheme = theme;
		
		if (!SUtil.isSdPresent()) {
			Toast.makeText(ctx, R.string.ndtheme_sdcard_unfound_msg,
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if( null == mTheme ) {
			Toast.makeText(ctx, R.string.ndtheme_theme_fetch_loading,
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if ( mTheme.getItemType()!=ThemeItem.ITEM_TYPE_LAUNCHER ) {
			if(mTheme.getDownloadUrl().indexOf("&imei=")==-1)
				mTheme.setDownloadUrl(mTheme.getDownloadUrl()+"&imei="+TelephoneUtil.getIMEI(ctx));
		}
		startChineseThemeDownloadService();
	}
	
	
	/**
	 * 下载主题
	 */
	private void startChineseThemeDownloadService() {
		if (!hasDownloaded()) {
			downloadChinseTheme();
		}
	}
	
	/**
	 * 下载国内主题
	 */
	private void downloadChinseTheme() {		
		// 如果已经在下载列表中，不再响应
		String downloadUrl = mTheme.getDownloadUrl();
		if (DownloadService.inDownList( downloadUrl )) {
			HiLauncherThemeGlobal.ddpost(HiLauncherThemeGlobal.R(R.string.ndtheme_txt_downloading));
			return;
		} else {
			Log.d(TAG, "new apt " +downloadUrl );
		}		
		
		downloadThemeFromServer();
	}

	/**
	 * 检查本地是否下载过此主题
	 * @return
	 */
	private boolean hasDownloaded() {
		
		try{
			DowningTaskItem newDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(mTheme.getId());
		
			if (newDowningTaskItem!=null){ 
				if (newDowningTaskItem.state==DowningTaskItem.DownState_Finish){
		    		ThemeLauncherExAPI.showThemeApplyActivity(ctx, newDowningTaskItem);
		    		return true;
				}
				if(newDowningTaskItem.state==DowningTaskItem.DownState_Downing){
					HiLauncherThemeGlobal.ddpost(HiLauncherThemeGlobal.R(R.string.ndtheme_txt_downloading));
					return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * 正式从服务端下载主题
	 */
	private void downloadThemeFromServer() {
		String path = HiLauncherThemeGlobal.url2path(mTheme.getLargePostersUrl(),	HiLauncherThemeGlobal.CACHES_HOME_MARKET);		
		hashMap.put( mTheme.getDownloadUrl(), path );
		
		Intent intent = new Intent(ctx, DownloadService.class);
		intent.putExtra("mTheme", mTheme);
		ctx.startService(intent);		
		HiLauncherThemeGlobal.dpost(ctx, HiLauncherThemeGlobal.R(R.string.ndtheme_txt_start_download_theme));
	}
	
}
