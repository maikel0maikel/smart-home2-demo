package com.nd.hilauncherdev.appmarket;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ApkInstaller;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageActivity;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

/**
 * 支持下载进度的布局
 * @author zhuchenghua
 *
 */
public class AppMarketDownloadingButton extends LinearLayout {

	private Context mContext = getContext();
	
	private final String TAG="AppMarketDownloadingButton";
	private AppMarketItem mItem;
	/**
	 * 下载状态监听
	 */
	private BroadcastReceiver mDownloadReceiver;
	/**
	 * 软件安装监听
	 */
	private BroadcastReceiver mNewAppInstallReceiver;
	
	/**
	 * 正在安装监听
	 */
	private BroadcastReceiver mSilentInstallReceiver;
	
	private ImageView im_download;
	private TextView tv_download_state;
	
	public AppMarketDownloadingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketDownloadingButton(Context context) {
		super(context);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		im_download=(ImageView)findViewById(R.id.im_download);
		tv_download_state=(TextView)findViewById(R.id.tv_download_state);
	}
	
	/**
	 * 设置下载状态
	 */
	public void initState()
	{
		if(mItem==null)
			return;
		
		int state=mItem.getDownloadState();
		
		if(state!=DownloadState.STATE_CANCLE && state!=DownloadState.STATE_NONE){
			im_download.setVisibility(View.GONE);
			tv_download_state.setVisibility(View.VISIBLE);
		}else{
			im_download.setVisibility(View.VISIBLE);
			tv_download_state.setVisibility(View.GONE);
		}
		
		setClickable(true);
		String stateStr=null;
		switch (mItem.getDownloadState()) {
		//正在下载
		case DownloadState.STATE_DOWNLOADING:
			stateStr=mItem.getDownloadProccess()+"%";
			break;
		//暂停
		case DownloadState.STATE_PAUSE:
			stateStr=mContext.getString(R.string.app_market_app_download_pause);
			break;
		//等待中
		case DownloadState.STATE_WAITING:
			stateStr=mContext.getString(R.string.app_market_app_download_wait);
			break;
			
		//已下载
		case DownloadState.STATE_FINISHED:
			stateStr=mContext.getString(R.string.common_button_install);
			break;
		//已安装
		case DownloadState.STATE_INSTALLED:
			stateStr=mContext.getString(R.string.app_market_app_installed);
			break;
			
		//取消和未下载
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_NONE:
			
			break;
		//正在安装 
		case ApkInstaller.INSTALL_STATE_INSTALLING:
			stateStr=mContext.getString(R.string.app_market_installing);
			setClickable(false);
			break;
		}	
		
		if(stateStr!=null)
			tv_download_state.setText(stateStr);
	}
	
	
	/**
	 * 注册广播监听
	 */
	private void registReceiver()
	{
		try {
			//注册软件安装广播监听，有新软件安装，要过滤掉
			if(mNewAppInstallReceiver==null)
			{
				mNewAppInstallReceiver=new NewAppInstallReceiver();
				IntentFilter itFilter=new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
				itFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				itFilter.addDataScheme("package");
				mContext.registerReceiver(mNewAppInstallReceiver, itFilter);
			}
			
			//注册下载状态监听
			if(mDownloadReceiver==null)
			{
				IntentFilter iFilter2=new IntentFilter(DownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
				mDownloadReceiver=new DownloadReceiver();
				mContext.registerReceiver(mDownloadReceiver, iFilter2);
			}
			
			//注册静默安装监听
			if(mSilentInstallReceiver==null)
			{
				IntentFilter iFilter3=new IntentFilter(ApkInstaller.RECEIVER_APP_SILENT_INSTALL);
				mSilentInstallReceiver=new SilentInstallReceiver();
				mContext.registerReceiver(mSilentInstallReceiver, iFilter3);
			}
			
		} catch (Exception e) {
		}
		
		
		
	}
	
	/**
	 * 注销广播监听
	 */
	private void unRegistReceiver()
	{
		
		try {
			if(mDownloadReceiver!=null)
				mContext.unregisterReceiver(mDownloadReceiver);
			mDownloadReceiver=null;
			
			if(mNewAppInstallReceiver!=null)
				mContext.unregisterReceiver(mNewAppInstallReceiver);
			mNewAppInstallReceiver=null;
			
			if(mSilentInstallReceiver!=null)
				mContext.unregisterReceiver(mSilentInstallReceiver);
			mSilentInstallReceiver=null;
		} catch (Exception e) {
		}
		
	}
	
	@Override
	public void setTag(Object tag) {
		super.setTag(tag);
		if((tag!=null) && (tag instanceof AppMarketItem)){
			mItem=(AppMarketItem)tag;
			initState();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		//注册广播监听
		registReceiver();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		//注销广播监听
		unRegistReceiver();
	}

	public void onClick() {
		//AppMarketItem item=(AppMarketItem)getTag();
		if(mItem==null) return;
		int state=mItem.getDownloadState();
		switch (state) {
		
		//正在下载
		case DownloadState.STATE_DOWNLOADING:
		//暂停
		case DownloadState.STATE_PAUSE:
		//等待中
		case DownloadState.STATE_WAITING:
			//正在下载、暂停、等待下，跳转到下载管理 界面
			Intent dlMgrIntent=new Intent(mContext,DownloadManageActivity.class);
			dlMgrIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(dlMgrIntent);
			break;
		//已下载 
		case DownloadState.STATE_FINISHED:
			File file=new File(mItem.getApkFilePath());
			if(!file.exists()){ //文件不存，改状态为未下载的
				mItem.setDownloadState(DownloadState.STATE_NONE);
				MessageUtils.makeShortToast(mContext, mContext.getString(R.string.file_manager_file_not_exist_tips));
			}else
				ApkTools.installApplication(mContext, file);
			
			break;
			
		//已安装
		case DownloadState.STATE_INSTALLED:
			AppMarketUtil.showOpenAppTipDialog(mItem,mContext);
			break;
			
		//取消和未下载
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_NONE:
			AppMarketUtil.startDownload(mContext,mItem);
			initState();
			break;
		}
		
	}//end onClick
	
	
	/**
	 * 下载状态监听
	 */
	private class DownloadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String key=intent.getStringExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION);
				
				if(key!=null && key.equals(mItem.getKey()))
				{
					AppMarketUtil.setDownloadState(mContext, mItem);
					//刷新状态
					initState();
				}
			} catch (Exception e) {
				
				Log.w(TAG, "DownloadReceiver expose error!",e);
			}
				
		}
		
	}//end DownloadReceiver

	/**
	 * 新应用安装监听
	 */
	private class NewAppInstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context content, Intent intent) {
			
			final String packageName = intent.getData().getSchemeSpecificPart();
			
			if(packageName==null)
				return;
			
			if(packageName.equals(mItem.getPackageName()))
			{
				AppMarketUtil.setDownloadState(mContext, mItem);
				//刷新状态
				initState();
			}
			
		}//end onReceiver
		
	
	}//end class NewAppInstallReceiver
	
	/**
	 * 静默安装的监听器
	 */
	private class SilentInstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
				
			if(mItem==null)
				return;
			String packageName=intent.getStringExtra(ApkInstaller.EXTRA_APP_INSTALL_PACAKGE_NAME);
			if(TextUtils.isEmpty(packageName))
				return;
			
			try {
				
				if(packageName.equals(mItem.getPackageName()))
				{
					AppMarketUtil.setDownloadState(mContext, mItem);
					initState();
					
				}//end if
				
			} catch (Exception e) {
				
				Log.w(TAG, "SilentInstallReceiver expose error!",e);
			}
			
		}//end onReceive
		
	}//end SilentInstallReceiver
	
}
