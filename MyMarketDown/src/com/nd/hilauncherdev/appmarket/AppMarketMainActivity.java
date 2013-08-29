package com.nd.hilauncherdev.appmarket;

import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.basecontent.HiActivity;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.CommonSlidingViewData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.kitset.Analytics.AnalyticsConstant;
import com.nd.hilauncherdev.kitset.Analytics.HiAnalytics;
import com.nd.hilauncherdev.kitset.Analytics.OtherAnalytics;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.kitset.util.SystemUtil;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.util.TabContainerUtil;
import com.nd.hilauncherdev.myphone.util.commonsliding.CommonLightbar;
import com.nd.hilauncherdev.plugin.RecommendApps;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageActivity;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.myphone.util.commonsliding.CommonSlidingView;

public class AppMarketMainActivity extends HiActivity {

	private AppMarketSlidingView mSlidingView;
	private CommonLightbar lightBar;
	private LinearLayout wp_main_layout;
	private TextView container_title;
	
	
	/**进入下载管理界面 按钮*/
	private ImageView im_into_download;
	
	/**批量选择按钮*/
	private TextView tv_open_batch_select;
	/**操作条，默认是隐藏*/
	private View ll_oper_bar;
	/**关闭批量选择按钮*/
	private View ll_close_batch_select;
	/**清空选中 按钮*/
	private View ll_clear_select;
	/**一键安装 按钮*/
	private View ll_start_download;
	/**一键安装 文本框*/
	private TextView tv_start_download;
	
	
	/***
	 * 正在加载。。的视图
	 */
	private View mLoadingView;
	/**
	 * 无数据的视图
	 */
	private View mNoDataView;
	/**
	 * 网络故障的视图
	 */
	private View mNetworkErrorView;
	
	/**
	 * 滑动体中数据的封装
	 */
	private CommonSlidingViewData mSlidingViewData;
	private Context mContext;
	/**
	 * 数据类集合，当前只有一个类别
	 */
	private List<ICommonDataItem> mItems;
	/**
	 * 单元格宽度
	 */
	private int cellWidth;
	/**
	 * 单元格高度
	 */
	private int cellHeight;
	private AsyncTask<String, Integer, Integer> mDataLoadTask;
	
//	private final String FONT_COLOR_ONKEY_INSTALL_ENABLE="#cdf2ff";
//	private final String FONT_COLOR_ONKEY_INSTALL_DISABLE="#d3d3d3";
	
	private final int EVENT_NET_WORK_ERROR=10000;
	private final int EVENT_LOAD_DATA_SUCCESS=10001;
	private final int EVENT_ERROR=-1;
	
	private String str_onkey_install;
	
	/**
	 * 指示灯选中状态图片
	 */
	private Drawable mLightChecked;
	/**
	 * 指示灯普通状态图片
	 */
	private Drawable mLightNormal;
	
	/**
	 * 行数，默认3行
	 */
	private int mRowCount=3;
	/**
	 * 列数，默认4列
	 */
	private int mColCount=4;
	/**
	 * 软件总数目，最后一个"打开91助手"项不算在内
	 */
	private int mMaxCount;
	/**
	 * 最多页数，目前暂定3屏
	 */
	private int mMaxPageCount=3;
	
	/**
	 * 功能的主要辅助类，提供获取数据，反馈下载统计等功能
	 */
	private AppMarketUtil mAppMarketUtil;
	
	/**
	 * 软件安装监听
	 */
	private BroadcastReceiver mNewAppInstallReceiver;
	
	/**
	 * 当前是否处于可选状态
	 */
	private boolean mIsForSelected=false;
	
	/**
	 * 显示更多的数据项,没有实际的数据，只是为了暂一个位置
	 */
	private AppMarketItem mMoreItem;
	
	/**
	 * 进入手机助手的应用页的extra参数值
	 */
	private final String ASSIT_APP_EXTRA_TYPE_APP="11";
	
	/**
	 * 进入手机助手的游戏页的extra参数值
	 */
	private final String ASSIT_APP_EXTRA_TYPE_GAME="12";
	
	/**
	 * 进入手机助手的分页参数KEY
	 */
	private final String ASSIT_APP_ACTION_TYPE_EXTRA_KEY="act_id";
	
	/**
	 * 客户端类型：一键装机、一键玩机
	 */
	private int mClientType=OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED;
	
	private Handler mHandler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabContainerUtil.fullscreen(this);//隐藏标题条
		
		setContentView(R.layout.app_market_activity);
		
		mContext=this;
		
		str_onkey_install=getString(R.string.theme_shop_theme_downloading);
		Resources res = mContext.getResources();
		mLightChecked = res.getDrawable(R.drawable.drawer_lightbar_checked);
		mLightNormal = res.getDrawable(R.drawable.drawer_lightbar_normal);
		
		mClientType=getIntent().getIntExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED);
		mAppMarketUtil=new AppMarketUtil(mContext,mClientType);
		
		//初始化界面
		initView();
		//初始化布局参数
		initParameter();
		//创建所需的目录
		AppMarketUtil.createBaseDir();
		//初始化数据
		initData();
		
		//统计界面打开次数
		ThreadUtil.executeMore(new Runnable() {
			
			@Override
			public void run() {
				
				boolean success=false;
				
				switch (mClientType) {
				
				//一键装机
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED:
					success=OtherAnalytics.submitAppNecessaryOpen(mContext);
					if(!success) //实时统计失败，采用通用平台的方式统计
						HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_NEED_OTHER_ANALYTIC_FAILED);
					break;
					
				//热门游戏
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY:
					success=OtherAnalytics.submitAppGameOpen(mContext);
					if(!success) //实时统计失败，采用通用平台的方式统计
						HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_PLAY_OTHER_ANALYTIC_FAILED);
					break;
				}
			}
		});
		
	}//end onCreate
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		mSlidingView=(AppMarketSlidingView) findViewById(R.id.sliding_view);
		wp_main_layout=(LinearLayout)findViewById(R.id.wp_main_layout);
		lightBar=(CommonLightbar) findViewById(R.id.lightbar);
		ll_oper_bar=findViewById(R.id.ll_oper_bar);
		tv_open_batch_select=(TextView)findViewById(R.id.tv_open_batch_select);
		ll_start_download=findViewById(R.id.ll_start_download);
		ll_close_batch_select=findViewById(R.id.ll_close_batch_select);
		ll_clear_select=findViewById(R.id.ll_clear_select);
		tv_start_download=(TextView)findViewById(R.id.tv_start_download);
		
		mLoadingView=ViewFactory.getNomalErrInfoView(mContext, wp_main_layout, ViewFactory.LOADING_DATA_INFO_VIEW);
		mNoDataView=ViewFactory.getNomalErrInfoView(mContext, wp_main_layout, ViewFactory.SEARCH_NO_DATA_VIEW);
		mNetworkErrorView=ViewFactory.getNomalErrInfoView(mContext, wp_main_layout, ViewFactory.NET_BREAK_VIEW);
		
		im_into_download=(ImageView)findViewById(R.id.im_into_download);
		container_title=(TextView)findViewById(R.id.container_title);
		
		//不的同客户端类型，标题不一样，如一键装机，一键玩机
		if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			container_title.setText(R.string.app_market_one_key);
		else if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			container_title.setText(R.string.app_market_one_key_play);
		
		mLoadingView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		mNetworkErrorView.setVisibility(View.GONE);
		
		//初始化指示灯
		lightBar.setNormalLighter(mLightNormal);
		lightBar.setSelectedLighter(mLightChecked);
		mSlidingView.setCommonLightbar(lightBar);
		//关闭循环滑动
		mSlidingView.setEndlessScrolling(false);
		lightBar.update(0);
		mSlidingView.setOnItemClickListener(new ItemClickListener());
		
		//初始化按钮事件
		initBtn();
	}
	

	/**
	 * 初始化参数
	 */
	private void initParameter()
	{
		int topHeight=wp_main_layout.getMeasuredHeight();
		int bottomHeight=ll_oper_bar.getMeasuredHeight();
		int screenHeight=ScreenUtil.getCurrentScreenHeight(mContext);
		int screenWidth=ScreenUtil.getCurrentScreenWidth(mContext);
		
		//计算行列数
		cellWidth=screenWidth/4;
		cellHeight=(screenHeight-topHeight-bottomHeight)/4;
		//特大屏的放4行,其他小类型屏放3行
		if(ScreenUtil.getInstance().isExLardgeScreen())
			mRowCount=4;
			
		mMaxCount=mRowCount*mColCount*mMaxPageCount-1;
		AppMarketUtil.maxCount=mMaxCount;
		
		mSlidingViewData=new CommonSlidingViewData(cellWidth, cellHeight, mColCount, mRowCount, new ArrayList<ICommonDataItem>());
		mItems=mSlidingViewData.getDataList();
		mItems.clear();
		ArrayList<ICommonData> datas = new ArrayList<ICommonData>();
		datas.add(mSlidingViewData);
		mSlidingView.setList(datas);
		
		//显示更多的项
		mMoreItem=new AppMarketItem();
		
	}
	
	/**
	 * 初始化按钮
	 */
	private void initBtn() {

		/**返回按钮*/
		findViewById(R.id.app_running_back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		//批量选择按钮
		tv_open_batch_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mSlidingView==null || mItems==null || mItems.size()==0)return;
				mIsForSelected=true;
				mSlidingView.setForSelect(mIsForSelected);
				tv_open_batch_select.setVisibility(View.GONE);
				ll_oper_bar.setVisibility(View.VISIBLE);
			}
		});
		
		//关闭批量选择按钮
		ll_close_batch_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mSlidingView==null)return;
				//清空所有已选项
				mSlidingView.setAllSelect(false);
				//关闭可选状态
				mIsForSelected=false;
				mSlidingView.setForSelect(mIsForSelected);
				//改变一键下载按钮状态
				changeDownLoadBtnState();
				tv_open_batch_select.setVisibility(View.VISIBLE);
				ll_oper_bar.setVisibility(View.GONE);
			}
		});
		
		//清空选中项
		ll_clear_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//清空所有已选项
				mSlidingView.setAllSelect(false);
				//改变一键下载按钮状态
				changeDownLoadBtnState();
			}
		});
		
		// 一键下载
		ll_start_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mItems!=null && mItems.size()>0 && mSlidingView.getDownloadViewMap().size()==0)
					MessageUtils.makeShortToast(mContext, R.string.app_market_install_tip);
				else{
					batchDownloadApp();
				}
			}
		});
		
		//进入下载管理界面
		im_into_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext,DownloadManageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}// end initBtn();
	
	/**
	 * 初始化数据
	 */
	private void initData()
	{
		mSlidingView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.VISIBLE);
		loadData();
	}
	
	/**
	 * 加载数据
	 */
	private void loadData()
	{
		if(mDataLoadTask!=null && mDataLoadTask.getStatus() == AsyncTask.Status.RUNNING)
			mDataLoadTask.cancel(true);
		
		//获取一键装机与一键玩机的列表地址
		String url_no_page=mAppMarketUtil.getTodayHotUrl();
		if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			url_no_page=mAppMarketUtil.getTodayHotUrl();
		else if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			url_no_page=mAppMarketUtil.getGameUrl();
		mDataLoadTask=new DataLoadTask().execute(url_no_page);
	}
	
	/**
	 * 显示 下载助手的对话框
	 */
	private void showInstallAssitAppDialog()
	{
		final StringBuffer title = new StringBuffer(mContext.getString(R.string.common_button_download))
			.append(mContext.getString(R.string.app_market_app_assit));
		CommonDialog alertd=ViewFactory.getAlertDialog(mContext, 
										title, 
										mContext.getString(R.string.app_market_app_no_assit_tip), 
										new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int arg1) {
												downloadAssitApp();
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
	}
	
	/**
	 * 下载91手机助手
	 */
	private void downloadAssitApp()
	{
		ThreadUtil.executeMore(new Runnable() {
			
			@Override
			public void run() {
				//在线获取91助手的下载地址
				String downloadUrl=OtherAnalytics.get91AssistAppDownloadUrl(mContext);
				//未获取到采用默认地址
				if(StringUtil.isEmpty(downloadUrl))
					downloadUrl = RecommendApps.ASSIT_APP_DOWNLOAD_URL;
				
				ApkDownloadInfo dlInfo=new ApkDownloadInfo(downloadUrl,downloadUrl);
				dlInfo.apkFile=RecommendApps.PANDASPACE_PCK+".apk";
				dlInfo.downloadDir=AppMarketUtil.PACKAGE_DOWNLOAD_DIR;
				dlInfo.appName=mContext.getString(R.string.app_market_app_assit);
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						//刷新右上角下载图标的状态
						im_into_download.invalidate();
					}
				});
				
			}
		});
		
	}
	
	/**
	 * 开始批量下载
	 */
	private void batchDownloadApp()
	{
		HashMap<String, View> dlViewMap=mSlidingView.getDownloadViewMap();
		Set<Entry<String, View>> set=dlViewMap.entrySet();
		for(Entry<String, View> entry:set)
		{
			AppMarketItemView itemView=(AppMarketItemView)entry.getValue();
			itemView.startDownload();
		}
		dlViewMap.clear();
		//改变一键下载的按钮状态
		changeDownLoadBtnState();
		//刷新下载管理图标，显示处在下载管理队列的个数
		im_into_download.invalidate();
	}
	
	/**
	 * 改变一键下载按钮状态
	 */
	private void changeDownLoadBtnState()
	{
		//int bottomLayoutbg=R.drawable.myphone_common_bottom_bg;
		if(mSlidingView.getDownloadViewMap().size()>0){
			tv_start_download.setText(str_onkey_install+"("+mSlidingView.getDownloadViewMap().size()+")");
			//bottomLayoutbg=R.drawable.myphone_common_edit_bottom_bg_selector;
		}else{
			tv_start_download.setText(str_onkey_install);
			//bottomLayoutbg=R.drawable.myphone_common_bottom_bg;
		}
		//ll_bottom_layout.setBackgroundResource(bottomLayoutbg);
	}
	
	/**
	 * 退出界时清理所有需要清理的数据
	 */
	private void clearAllDataOnExit()
	{
		
		try {
			//注销新应用安装的广播监听
			if(mNewAppInstallReceiver!=null)
				unregisterReceiver(mNewAppInstallReceiver);
			//移除划动体中的所有子View
			mSlidingView.removeAllViews();
			mSlidingView.setVisibility(View.GONE);
			lightBar.setVisibility(View.GONE);
			
			//释放指示灯图片
			mLightNormal.setCallback(null);
			mLightChecked.setCallback(null);
			
			
			//中断获取数据的任务
			if(mDataLoadTask!=null)
				mDataLoadTask.cancel(true);
			
			//中止加载图标的线程池
			AppMarketUtil.clearThreads();
			
			//清除图标缓存
			AppMarketUtil.clearIconCache();
			
			//重置可显示的最多数据项数目
			AppMarketUtil.maxCount=0;
			
			mAppMarketUtil=null;
			
		} catch (Exception e) {
			Log.w(Global.TAG, "AppMarketMainActivity clearAllDataOnExit:"+e.toString());
		}
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		//注册软件安装广播监听，有新软件安装，要过滤掉
		if(mNewAppInstallReceiver==null)
		{
			mNewAppInstallReceiver=new NewAppInstallReceiver();
			IntentFilter itFilter=new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
			itFilter.addDataScheme("package");
			registerReceiver(mNewAppInstallReceiver, itFilter);
		}
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mSlidingView!=null && mSlidingView.getChildCount() > 0)
		{
			mSlidingView.refreshDownloadState();
		}
		im_into_download.invalidate();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){ //按回退键
			if(mIsForSelected)
			{
				if(mSlidingView==null)return false;
				
				//清空所有已选项
				mSlidingView.setAllSelect(false);
				//关闭可选状态
				mIsForSelected=false;
				mSlidingView.setForSelect(mIsForSelected);
				//改变一键下载按钮状态
				changeDownLoadBtnState();
				tv_open_batch_select.setVisibility(View.VISIBLE);
				ll_oper_bar.setVisibility(View.GONE);
				
				return false;
			}
				
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		clearAllDataOnExit();//清理数据
		super.onDestroy();
	}
	
	/**
	 * <p>更新安装后的状态</p>
	 * 
	 * <p>date: 2012-9-25 下午03:28:36
	 * @author pdw
	 * @param packageName
	 */
	private void updateState(String packageName) {
		final AppMarketSlidingView slidingView = mSlidingView ;
		final int layoutCount = slidingView.getChildCount();
		for (int i = 0 ; i < layoutCount ; i++) {
			final ViewGroup layout = (ViewGroup) slidingView.getChildAt(i);
			final int childCount = layout.getChildCount() ;
			for (int j = 0 ; j < childCount ; j++) {
				final View child = layout.getChildAt(j);
				if (child instanceof AppMarketItemView) {
					final AppMarketItemView itemView = (AppMarketItemView) child ;
					if (itemView.getTag() instanceof AppMarketItem) {
						final AppMarketItem item = (AppMarketItem) itemView.getTag();
						if (packageName.equals(item.getPackageName())) {
							itemView.reInitState();
						}
					}
				}
			}
		}
	}//end updateState
	
	
/**-内部类-----------------------------------------------------------------*/


	/**
	 * 获取应用列表的任务
	 */
	private class DataLoadTask extends AsyncTask<String, Integer, Integer>{

		private List<AppMarketItem> tempList;
		
		@Override
		protected Integer doInBackground(String... params) {
			String url=params[0];
			try {
				if(!TelephoneUtil.isNetworkAvailable(mContext))
					throw new IOException("No availabled connection!");
				tempList=mAppMarketUtil.getAppsFromServer(null,url,1);
				return EVENT_LOAD_DATA_SUCCESS;
			} catch (Exception e) {
				Log.e(Global.TAG, "load apps failed:"+e.toString());
				return getExceptionType(e);
			}
		}

		@Override
		protected void onCancelled() {
			if(tempList!=null)
				tempList.clear();
			tempList=null;
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			
			case EVENT_LOAD_DATA_SUCCESS:
				mItems.clear();
				if(tempList!=null && tempList.size()>0){
					mItems.addAll(tempList);
					mItems.add(mMoreItem);//在最后面添加一个"进入91助手"的数据项
					mSlidingView.setVisibility(View.VISIBLE);
					//mSlidingView.reLayout();
				}else
					mNoDataView.setVisibility(View.VISIBLE);
				
				break;
			case EVENT_NET_WORK_ERROR:
				mNetworkErrorView.setVisibility(View.VISIBLE);
				break;
			case EVENT_ERROR:
				mNoDataView.setVisibility(View.VISIBLE);
				break;
			}
			mLoadingView.setVisibility(View.GONE);
		}
		
		/**
		 * 检验异常类型
		 * @param e
		 * @return
		 */
		private int getExceptionType(Exception e)
		{
			if((e instanceof MalformedURLException)
					||(e instanceof IOException)
					||(e instanceof SocketTimeoutException)
					||(e instanceof EOFException)
					||(e instanceof SocketException)
					||(e instanceof java.net.ProtocolException)
					||(e instanceof org.apache.http.ProtocolException))
				return EVENT_NET_WORK_ERROR;
			else
				return EVENT_ERROR;
		}
		
		
	}//end DataLoadTask;
	
	
	/**
	 * 单元项选中事件
	 */
	private class ItemClickListener implements CommonSlidingView.OnCommonSlidingViewClickListener{

		@Override
		public void onItemClick(View v, int positionInData,
				int positionInScreen, int screen, ICommonData data) {
			//点击的是显示更多的项
			if(v.getTag() == null)
			{
				//已安装了'91助手',直接打开
				if(AndroidPackageUtils.isPkgInstalled(mContext, RecommendApps.PANDASPACE_PCK))
				{
					PackageManager pm=getPackageManager();
					Intent intent=pm.getLaunchIntentForPackage(RecommendApps.PANDASPACE_PCK);
					switch (mClientType) {
					
					//一键装机
					case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED:
						intent.putExtra(ASSIT_APP_ACTION_TYPE_EXTRA_KEY, ASSIT_APP_EXTRA_TYPE_APP);
						break;
					//热门游戏
					case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY:
						intent.putExtra(ASSIT_APP_ACTION_TYPE_EXTRA_KEY, ASSIT_APP_EXTRA_TYPE_GAME);
						break;
					}
//					startActivity(intent);
					SystemUtil.startActivity(AppMarketMainActivity.this, intent);
					//AndroidPackageUtils.runApplication(mContext, assit_app_package);
				}else{
					showInstallAssitAppDialog();//显示下载'91助手'提示
				}
				
				return ;
			}
			
			AppMarketItemView itemView=(AppMarketItemView)v;
			if(!itemView.onClick()) //有改变单元格的状态
				return ;
			
			//改底部操作条的样式
			changeDownLoadBtnState();
		}
		
	}//end class ItemClickListener
	
	/**
	 * 新应用安装监听
	 */
	private class NewAppInstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context content, Intent intent) {
			if (null == intent || mItems == null || mSlidingView == null)
				return;
			final String action = intent.getAction();
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (Intent.ACTION_PACKAGE_ADDED.equals(action) && packageName!=null)
			{
				updateState(packageName);
			}
		}
	
	}//end class NewAppInstallReceiver
	
	
}//end activity








	










