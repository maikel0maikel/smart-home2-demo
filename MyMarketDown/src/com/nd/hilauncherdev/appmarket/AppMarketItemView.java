package com.nd.hilauncherdev.appmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

/**
 * 单元格项的View
 * @author zhuchenghua
 *
 */
public class AppMarketItemView extends RelativeLayout {

	private final String BG_COLOR_SELECTED="#a9e1f5";
	
	private AppMarketSlidingView mSlidingView;
	private View im_selected;
	private View im_unselected;
	
	/**
	 * 是否可选择
	 */
	private boolean mIsForSelectedState=false;
	
	/**
	 * 是否处在选中状态
	 */
	private boolean mSelected=false;
	
	/**
	 * 下载状态
	 */
	private int mDownloadState=DownloadState.STATE_CANCLE;
	
	private AppMarketItem mItem;
	
	private Context mContext;
	
	/**
	 * 当前进度
	 */
	private int mCurrentProcess=0;
	
	/**
	 * 下载进度监听
	 */
	private DownloadReceiver mDownloadReceiver;

	public AppMarketItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
	}

	public AppMarketItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}

	public AppMarketItemView(Context context) {
		super(context);
		mContext=context;
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		im_selected=findViewById(R.id.im_selected);
		im_unselected=findViewById(R.id.im_unselected);
	}

	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		registerDownloadReceiver();
		invalidate();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		unRegisterDownloadReceiver();
	}

	/**
	 * 初始化
	 * @param item
	 */
	public void init(AppMarketItem item,AppMarketSlidingView slidingView)
	{
		mSlidingView=slidingView;
		mItem=item;
		
		AppMarketItemIconView iconView=(AppMarketItemIconView)findViewById(R.id.im_icon);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		TextView tv_download_number=(TextView)findViewById(R.id.tv_download_number);
		
		//小屏的（width<=320的屏）标题只显示一行
		/*if(!ScreenUtil.getInstance().isLargeScreen())
			tv_title.setMaxLines(1);*/
		
		iconView.setAppMarketItem(mItem);
		tv_title.setText(mItem.getTitle());
		
		//下载次数
		String downloadCount=mContext.getString(R.string.app_market_detail_download_count_unit);
		downloadCount=String.format(downloadCount, mItem.getDownloadNumber());
		tv_download_number.setText(downloadCount);
		
		//初始化下载状态
		ApkDownloadInfo dlInfo=null;
		//ApkDownloadInfo dlInfo=AppMarketUtil.getDownloadSate(mItem.getKey(), mSlidingView.getDownloadTasks());
		if(dlInfo!=null)
		{
			mDownloadState=dlInfo.getState();
			//当前下载进度
			mCurrentProcess=dlInfo.progress;
		}
		
		//初始化状态
		initState();
		//注册正在下载的广播监听
		registerDownloadReceiver();	
	}

	public void reInitState()
	{
		if(mItem==null) return;
		
		//初始化下载状态
		ApkDownloadInfo dlInfo=null;
		//ApkDownloadInfo dlInfo=AppMarketUtil.getDownloadSate(mItem.getKey(),mSlidingView.getDownloadTasks());
		if(dlInfo!=null)
		{
			mDownloadState=dlInfo.getState();
			//当前下载进度
			mCurrentProcess=dlInfo.progress;
		}else
			mDownloadState=DownloadState.STATE_NONE;
		
		initState();
	}
	
	/**
	 * 初始化界面状态，如已安装，已下载，暂停等
	 */
	private void initState()
	{
		if(mItem==null) return;
		
		TextView tv_size=(TextView)findViewById(R.id.tv_size);
		String stateStr=mItem.getSize();
		
		if(AndroidPackageUtils.isPkgInstalled(mContext, mItem.getPackageName()))
			mDownloadState=DownloadState.STATE_INSTALLED;
		switch (mDownloadState) {
		//正在下载
		case DownloadState.STATE_DOWNLOADING:
			stateStr=mCurrentProcess+"%";
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
			stateStr=mContext.getString(R.string.app_market_app_downloaded);
			break;
		//已安装
		case DownloadState.STATE_INSTALLED:
			stateStr=mContext.getString(R.string.app_market_app_installed);
			break;
		//取消和未下载
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_NONE:
			
			break;
		}
		
		//非普通状态下，背景高亮显示
		if(mDownloadState!=DownloadState.STATE_CANCLE && mDownloadState!=DownloadState.STATE_NONE){
			setBackgroundColor(Color.parseColor(BG_COLOR_SELECTED));
			im_selected.setVisibility(View.INVISIBLE);
			im_unselected.setVisibility(View.INVISIBLE);
		}else{
			setBackgroundResource(R.drawable.drawer_selector);
			//setBackgroundColor(Color.TRANSPARENT);
			//是否是可选状态
			if(mIsForSelectedState){
				
				//是否是选中态
				if(mSelected)
				{
					mSlidingView.addDownloadView(mItem.getApkUrl(), this);
					im_unselected.setVisibility(View.INVISIBLE);
					im_selected.setVisibility(View.VISIBLE);
					
				}else{
					
					mSlidingView.removeDownloadView(mItem.getApkUrl());
					im_selected.setVisibility(View.INVISIBLE);
					im_unselected.setVisibility(View.VISIBLE);
				}
				
			}else{
				
				im_selected.setVisibility(View.INVISIBLE);
				im_unselected.setVisibility(View.INVISIBLE);
			}
			
		}
		
		
		
		
		
		tv_size.setText(stateStr);
	}
	
	/**
	 * 注册下载监听
	 */
	private void registerDownloadReceiver()
	{
		if(mDownloadReceiver!=null || getTag() == null)
			return;
		IntentFilter iFilter=new IntentFilter(DownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
		mDownloadReceiver=new DownloadReceiver();
		mContext.registerReceiver(mDownloadReceiver, iFilter);
	}
	
	/**
	 * 注销下载监听
	 */
	private void unRegisterDownloadReceiver()
	{
		if(mDownloadReceiver!=null)
			mContext.unregisterReceiver(mDownloadReceiver);
		mDownloadReceiver=null;
	}
	
	/**
	 * 开始下载，切换状态
	 */
	public void startDownload()
	{
		AppMarketUtil.startDownload(mContext,mItem);
		mDownloadState=mItem.getDownloadState();
		mSelected=false;
		initState();
		
		//去掉打勾
		im_selected.setVisibility(View.GONE);
		
	}
	
	/**
	 * 已安装情况下，显示是否立即运行的对话框提示
	 */
	/*private void showOpenAppTipDialog()
	{
		CommonDialog alertd=ViewFactory.getAlertDialog(mContext, 
				mContext.getString(R.string.common_tip), 
				mContext.getString(R.string.app_market_app_installed_run_tip), 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						AndroidPackageUtils.runApplication(mContext, mItem.getPackageName());
						dialog.dismiss();
					}
				}, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		alertd.show(); 
	}*/
	
	/**
	 * 单元格点击事件
	 * @param item
	 * @return
	 */
	public boolean onClick()
	{
		/*if(mDownloadState!=DownloadState.STATE_NONE && mDownloadState!=DownloadState.STATE_CANCLE){
			
			//已下载的，直接安装
			if(mDownloadState==DownloadState.STATE_FINISHED)
			{
				File file=new File(mItem.getApkFilePath());
				if(!file.exists()){ //文件不存，改状态为未下载的
					mDownloadState=DownloadState.STATE_NONE;
					MessageUtils.makeShortToast(mContext, mContext.getString(R.string.file_manager_file_not_exist_tips));
					initState();
				}else
					ApkTools.installApplication(mContext, file);
				
			}else if(mDownloadState==DownloadState.STATE_INSTALLED){ //已安装，提示是否立刻打开
				showOpenAppTipDialog();
			}else{
				//正在下载、暂停、等待下，跳转到下载管理 界面
				Intent dlMgrIntent=new Intent(mContext,DownloadManageActivity.class);
				dlMgrIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(dlMgrIntent);
			}
			
			return false;
		}*/
		
		//非常规状态下，不可再选
		if(mDownloadState!=DownloadState.STATE_NONE && mDownloadState!=DownloadState.STATE_CANCLE){
			Intent intent=new Intent(mContext,AppMarketAppDetailActivity.class);
			intent.putExtra(AppMarketConstants.EXTRA_APP_MARKET_ITEM, mItem);
			mContext.startActivity(intent);
			return false;
		}else{
			
			//非可选择状态，进入详情页
			if(!mIsForSelectedState)
			{
				Intent intent=new Intent(mContext,AppMarketAppDetailActivity.class);
				intent.putExtra(AppMarketConstants.EXTRA_APP_MARKET_ITEM, mItem);
				mContext.startActivity(intent);
				return false;
			}
		}
		
		
		//改变被选中项的背景，及显示打勾
		if(mSlidingView.getViewInDownloadByUrl(mItem.getApkUrl())!=null){
			setItemSelected(false);
		}else{
			setItemSelected(true);
		}
		
		return true;
	}
	
	/**
	 * 设置是否打开可选择状态
	 * @param isForSelect
	 */
	public void setForSelect(boolean isForSelect)
	{
		//正在下载、已安装、已下载、正在下载、等待、暂停状态，不给选择
		/*if(mDownloadState!=DownloadState.STATE_NONE && mDownloadState!=DownloadState.STATE_CANCLE)
			return;*/
		
		mIsForSelectedState=isForSelect;
		initState();
	}
	
	/**
	 * 选中/清除选中
	 */
	public void setItemSelected(boolean selected)
	{
		
		if(mSlidingView==null)
			return;
		mSelected=selected;
		initState();
		
	}
	
	/**
	 * 下载状态监听
	 */
	private class DownloadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mItem == null)
				return ;
			String key=intent.getStringExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION);
			if(key!=null && key.equals(mItem.getKey()))
			{
				mDownloadState=intent.getIntExtra(DownloadBroadcastExtra.EXTRA_STATE, DownloadState.STATE_NONE);
				if(mDownloadState==DownloadState.STATE_DOWNLOADING) //下正下载，更新进度
					mCurrentProcess=intent.getIntExtra(DownloadBroadcastExtra.EXTRA_PROGRESS, 0);
				initState();
			}
			
				
		}
		
	}//end DownloadReceiver
	
}
