package com.nd.hilauncherdev.appmarket;

import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.basecontent.HiActivity;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.kitset.Analytics.AnalyticsConstant;
import com.nd.hilauncherdev.kitset.Analytics.HiAnalytics;
import com.nd.hilauncherdev.kitset.Analytics.OtherAnalytics;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.ApkInstaller;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.kitset.util.SystemUtil;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.RecommendApps;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageActivity;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadDBManager;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;

/***
 * 应用/游戏专题主界面
 * 
 * @author zhuchenghua
 * 
 */
public class AppMarketSubjectActivity extends HiActivity {

	public static final String TAG="AppMarketSubjectActivity";
	
	private Handler mHandler = new Handler();

	private Context mContext;
	/** 标题 */
	private TextView container_title;

	/** 点我 */
	private View btn_hit_me;

	/***
	 * 数据显示区的View
	 */
	private RelativeLayout rl_data_main;

	/**
	 * 数据显示列表
	 */
	private ListView lv_main_list;

	/** 进入下载管理界面 按钮 */
	private ImageView im_into_download;

	/** 退出activity的按钮 */
	private ImageView app_running_back_btn;

	/** 精品分隔区 */
	private View rl_split;

	/**
	 * ListView头部显示的网页
	 */
	private WebView mWebView;

	/**
	 * 精品推荐的分隔View
	 */
	private View mListHeaderView;

	/**
	 * 精品推荐上的hot图标
	 */
	private View im_prime_recmd_hot;
	
	/**
	 * 数据加载框
	 */
	private View wait_layout;
	
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

	private final int EVENT_NET_WORK_ERROR = 10000;
	private final int EVENT_LOAD_DATA_SUCCESS = 10001;
	private final int EVENT_ERROR = -1;

	/**
	 * 应用列表集合
	 */
	private List<AppMarketItem> mItems = new ArrayList<AppMarketItem>();
	
	/**编辑推荐的数据集合*/
	private List<AppMarketItem> mEditorRecItems = new ArrayList<AppMarketItem>();
	
	/**精品推荐的数据集合*/
	private List<AppMarketItem> mPrimeRecItems = new ArrayList<AppMarketItem>();

	/** 列表adapter */
	private BaseAdapter mAdapter;

	private boolean mIsListScrolling = false;

	/***
	 * 网页加载是否出错
	 */
	private boolean mIsWebLoadError = false;

	/**
	 * 网页是否正在加载
	 */
	private boolean mIsWebLoading = false;

	/**
	 * 软件是否正在加载
	 */
	private boolean mIsAppListLoading = false;

	/**
	 * 软件加载出错
	 */
	private boolean mIsAppListLoadError = false;

	/**
	 * 数据界面是否已经显示了
	 */
	private boolean mIsDataViewShown = false;

	/**
	 * 功能的主要辅助类，提供获取数据，反馈下载统计等功能
	 */
	private AppMarketUtil mAppMarketUtil;
	/**
	 * 客户端类型：一键装机、一键玩机
	 */
	private int mClientType = OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED;

	/**
	 * 加载编辑推荐数据的任务
	 */
	private AsyncTask<String, Integer, Integer> mDataLoadTask;
	
	/**
	 * 加载精品推荐数据的任务
	 */
	private AsyncTask<String, Integer, Integer> mPrimeDataLoadTask;
	

	/** 记录WebView的滚动坐标Y */
	private int mWebViewScrollY;

	/**
	 * 进入手机助手的应用页的extra参数值
	 */
	private final String ASSIT_APP_EXTRA_TYPE_APP = "11";

	/**
	 * 进入手机助手的游戏页的extra参数值
	 */
	private final String ASSIT_APP_EXTRA_TYPE_GAME = "12";

	/**
	 * 进入手机助手的分页参数KEY
	 */
	private final String ASSIT_APP_ACTION_TYPE_EXTRA_KEY = "act_id";

	/**
	 * 下载管理类
	 */
	private DownloadServerServiceConnection mDownloadService;

	/**
	 * 编辑推荐标签
	 */
	private CheckedTextView tv_editor_recmd;
	
	/**
	 * 精品推荐标签
	 */
	private CheckedTextView tv_prime_recmd;
	
	/**
	 * 标签索引--编辑推荐
	 */
	private final int TAB_EDITOR_REC=0;
	
	/**
	 * 标签索引--精品推荐
	 */
	private final int TAB_PRIME_REC=1;
	
	/**
	 * 当前选中的标签
	 */
	private int mCurTab=TAB_EDITOR_REC;
	
	/**
	 * 图标是否显示点的标记
	 */
	private boolean mIsDrawingNewMask=false;
	
	private long webViewLoadBegin=0;
	
	private long listDataLoadBegin=0;
	
	// ------------------------------方法区-------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_market_subject_activity);
		mContext = this;
		mAdapter = new AppListAdapter();
		mClientType = getIntent().getIntExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED);
		mIsDrawingNewMask=getIntent().getBooleanExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_DRAWING_NEW, false);
		mAppMarketUtil = new AppMarketUtil(mContext, mClientType);

		
		AppMarketUtil.maxCount = 50;

		initView();
		initListener();

		// 创建所需的目录
		AppMarketUtil.createBaseDir();
		// 绑定下载服务
		mDownloadService = new DownloadServerServiceConnection(mContext);
		mDownloadService.bindDownloadService(new DownloadServiceBindCallBack());

		// 统计界面打开次数
		AppMarketUtil.executeThread(new Runnable() {

			@Override
			public void run() {

				boolean success = false;

				switch (mClientType) {

				// 一键装机
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED:
					success = OtherAnalytics.submitAppNecessaryOpen(mContext);
					if (!success) // 实时统计失败，采用通用平台的方式统计
						HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_NEED_OTHER_ANALYTIC_FAILED);
					break;

				// 热门游戏
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY:
					success = OtherAnalytics.submitAppGameOpen(mContext);
					if (!success) // 实时统计失败，采用通用平台的方式统计
						HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_PLAY_OTHER_ANALYTIC_FAILED);
					break;
				}
			}
		});
		
	}

	private void initView() {
		btn_hit_me = findViewById(R.id.btn_hit_me);
		rl_data_main = (RelativeLayout) findViewById(R.id.rl_data_main);
		lv_main_list = (ListView) findViewById(R.id.lv_main_list);
		im_into_download = (ImageView) findViewById(R.id.im_into_download);
		mListHeaderView = getLayoutInflater().inflate(R.layout.app_market_subject_header_view, null);
		app_running_back_btn = (ImageView) findViewById(R.id.app_running_back_btn);
		container_title = (TextView) findViewById(R.id.container_title);
		im_prime_recmd_hot=mListHeaderView.findViewById(R.id.im_prime_recmd_hot);
		
		tv_editor_recmd=(CheckedTextView)mListHeaderView.findViewById(R.id.tv_editor_recmd);
		tv_prime_recmd=(CheckedTextView)mListHeaderView.findViewById(R.id.tv_prime_recmd);
		wait_layout=findViewById(R.id.wait_layout);

		// 浏览器组件
		mWebView = (WebView) mListHeaderView.findViewById(R.id.web_view);
		rl_split = mListHeaderView.findViewById(R.id.rl_split);

		mLoadingView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.LOADING_DATA_INFO_VIEW);
		mNoDataView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.SEARCH_NO_DATA_VIEW);
		mNetworkErrorView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.NET_BREAK_VIEW);

		// 不的同客户端类型，标题不一样，如应用杂志，热门游戏
		if (mClientType == OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			container_title.setText(R.string.app_market_one_key);
		else if (mClientType == OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			container_title.setText(R.string.app_market_one_key_play);

		mLoadingView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		mNetworkErrorView.setVisibility(View.GONE);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLightTouchEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(true);
		// 设置浏览器加载状态处理
		mWebView.setWebViewClient(new WebViewClient() {

			private int retryCount = 0;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				retryCount++;
				if(webViewLoadBegin==0)
					webViewLoadBegin=System.currentTimeMillis();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mIsWebLoading = false;
				if (!mIsWebLoadError || (mIsWebLoadError && retryCount == 2)) {
					Log.d(TAG, "WebView load time span:"+(System.currentTimeMillis()-webViewLoadBegin));
					showDataFace();
					mWebView.getSettings().setBlockNetworkImage(false);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				mIsWebLoadError = true;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && mIsDataViewShown) {
					Class<? extends Activity> cls = null;
					// 不是进入专题详情，就是进入往期专题列表界面的
					if (url.indexOf(AppMarketUtil.SUBJECT_DETAIL_FLAG) == -1) {
						cls = AppMarketSubjectLaterActivity.class;

					} else {
						// 进入专题详情
						cls = AppMarketSubjectDetailActivity.class;
					}

					// 进入专题详情
					Intent intent = new Intent(mContext, cls);
					intent.putExtra(Intent.EXTRA_TEXT, url);
					intent.putExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, mClientType);
					startActivity(intent);
				}

				return true;
			}

		});

		btn_hit_me.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ApkInstaller.showSilentInstallTipDialog(mContext);
				btn_hit_me.setVisibility(View.GONE);
			}
		});

		tv_editor_recmd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResTab(TAB_EDITOR_REC);
			}
		});
		
		tv_prime_recmd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				im_prime_recmd_hot.setVisibility(View.GONE);
				setResTab(TAB_PRIME_REC);
			}
		});
		
		if (TelephoneUtil.hasRootPermission() && !DownloadDBManager.isSilentInstallTiped(mContext) && !DownloadDBManager.isSilentInstallable(mContext)) {
			btn_hit_me.setVisibility(View.VISIBLE);
		}

		//显示hot图片
		if(mIsDrawingNewMask)
		{
			im_prime_recmd_hot.setVisibility(View.VISIBLE);
		}
		
		// 正在加数据的界面先显示出来
		mLoadingView.setVisibility(View.VISIBLE);
		// 添加ListView的头部
		mWebView.setVisibility(View.INVISIBLE);
		lv_main_list.addHeaderView(mListHeaderView, null, false);
		lv_main_list.setAdapter(mAdapter);
		lv_main_list.setOnScrollListener(new ListViewScrollListener());

	}

	private void initListener() {

		// 进入下载管理界面
		im_into_download.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DownloadManageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			}
		});

		// 退出按钮
		app_running_back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 列表项单击事件
		lv_main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// 有HeaderView的情况下，下标会下移一个，所以要减去HeaderView的个数
				position -= lv_main_list.getHeaderViewsCount();

				AppMarketItem item = mItems.get(position);
				Intent intent = new Intent(mContext, AppMarketAppDetailActivity.class);
				intent.putExtra(AppMarketConstants.EXTRA_APP_MARKET_ITEM, item);
				intent.putExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, mClientType);
				mContext.startActivity(intent);
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mIsWebLoading = true;
		mIsAppListLoading = true;

		// 加载网页
		try {
			mWebView.loadUrl(mAppMarketUtil.getMainSubjectUrl());
			//mWebView.loadUrl("http://www.baidu.com");
		} catch (Exception e) {
			mIsWebLoadError = true;
			showDataFace();
			Log.w(Global.TAG, "AppMarketSubjectActivity.initData load web failed:" + e.toString());
		}

		if (mDataLoadTask != null && mDataLoadTask.getStatus() == AsyncTask.Status.RUNNING)
			mDataLoadTask.cancel(true);

		// 获取一键装机与一键玩机的列表地址
		String url_no_page = mAppMarketUtil.getTodayHotUrl();
		if (mClientType == OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			url_no_page = mAppMarketUtil.getTodayHotUrl();
		else if (mClientType == OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			url_no_page = mAppMarketUtil.getGameUrl();

		// 加载编辑推荐的应用
		mDataLoadTask = new DataLoadTask().execute(url_no_page);
	}

	private synchronized void showDataFace() {
		/*if (mIsWebLoading)
			return;*/
		mIsDataViewShown = true;

		// 网页加载失败，从ListView头部移除掉
		if (mIsWebLoadError)
			lv_main_list.removeHeaderView(mListHeaderView);

		// 软件列表加载失败，但网页加载成功，则将网页View添加到主数据区，不添加到ListView的头部
		if (mIsAppListLoadError && !mIsWebLoadError) {
			rl_data_main.removeView(mListHeaderView);
			lv_main_list.removeHeaderView(mListHeaderView);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			mListHeaderView.setLayoutParams(params);
			rl_data_main.addView(mListHeaderView);
			mWebView.setVisibility(View.VISIBLE);
		} else if(!mIsWebLoadError && mIsAppListLoading){
			//网页加载成功，软件列表正在加载，则先显示网页
			rl_split.setVisibility(View.INVISIBLE);
			lv_main_list.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.VISIBLE);
		}else if (mIsAppListLoadError && mIsWebLoadError) {
			// 网页与软件列表都加载失败,显示无数据界面
			mNoDataView.setVisibility(View.VISIBLE);
		} else if (!mIsAppListLoadError && !mIsWebLoadError) {
			// 网页与软件列表都加载成功,把"精品推荐"分隔线显示出来
			rl_split.setVisibility(View.VISIBLE);
			lv_main_list.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.VISIBLE);
		} else if (!mIsAppListLoadError && mIsWebLoadError) {
			// 软件列表加载成功，网页加载失败，显示软件列表
			rl_split.setVisibility(View.VISIBLE);
			lv_main_list.setVisibility(View.VISIBLE);
		}

		mLoadingView.setVisibility(View.GONE);
	}

	/**
	 * 设置数据项的状态
	 * 
	 * @param item
	 */
	private void initItemState(AppMarketItem item) {
		AppMarketUtil.setDownloadState(mContext, item);
	}

	/**
	 * 选中标签
	 * @param tabIndex 标签索引
	 */
	private void setResTab(int tabIndex)
	{
		if(mCurTab==tabIndex)
			return;
		
		switch (tabIndex) {
		
		case TAB_EDITOR_REC:
			mItems.clear();
			mItems.addAll(mEditorRecItems);
			mAdapter.notifyDataSetChanged();
			changeTabStyle(TAB_EDITOR_REC);
			break;
			
		case TAB_PRIME_REC:
			if(mPrimeRecItems.size()>0){
				mItems.clear();
				mItems.addAll(mPrimeRecItems);
				mAdapter.notifyDataSetChanged();
				changeTabStyle(TAB_PRIME_REC);
				
			}else{
				
				if (mPrimeDataLoadTask != null && mPrimeDataLoadTask.getStatus() == AsyncTask.Status.RUNNING)
					mPrimeDataLoadTask.cancel(true);
				
				String url_no_page = mAppMarketUtil.getPrimeRecAppUrl();
				// 加载推荐的应用
				mPrimeDataLoadTask = new PrimeRecDataLoadTask().execute(url_no_page);
			}
			
			
			break;
		}
	}
	
	/**
	 * 切换标签风格
	 * @param tabIndex
	 */
	private void changeTabStyle(int tabIndex)
	{
		switch (tabIndex) {
		case TAB_EDITOR_REC:
			tv_editor_recmd.setChecked(true);
			tv_prime_recmd.setChecked(false);
			break;
		case TAB_PRIME_REC:
			tv_editor_recmd.setChecked(false);
			tv_prime_recmd.setChecked(true);
			break;
		}
		
		mCurTab=tabIndex;
	}
	
	/**
	 * 显示 下载助手的对话框
	 */
	private void showInstallAssitAppDialog() {
		final StringBuffer title = new StringBuffer(mContext.getString(R.string.common_button_download)).append(mContext.getString(R.string.app_market_app_assit));
		CommonDialog alertd = ViewFactory.getAlertDialog(mContext, title, mContext.getString(R.string.app_market_app_no_assit_tip), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				downloadAssitApp();
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		alertd.show();

	}// end showInstallAssitAppDialog

	/**
	 * 下载91手机助手
	 */
	private void downloadAssitApp() {
		ThreadUtil.executeMore(new Runnable() {

			@Override
			public void run() {
				// 在线获取91助手的下载地址
				String downloadUrl = OtherAnalytics.get91AssistAppDownloadUrl(mContext);
				// 未获取到采用默认地址
				if (StringUtil.isEmpty(downloadUrl))
					downloadUrl = RecommendApps.ASSIT_APP_DOWNLOAD_URL;

				ApkDownloadInfo dlInfo = new ApkDownloadInfo(downloadUrl, downloadUrl);
				String fileName=FileUtil.getFileName(downloadUrl, true);
				if(!fileName.endsWith(".apk"))
					fileName=RecommendApps.PANDASPACE_PCK + ".apk";
				dlInfo.apkFile = fileName;
				dlInfo.downloadDir = AppMarketUtil.PACKAGE_DOWNLOAD_DIR;
				dlInfo.appName = mContext.getString(R.string.app_market_app_assit);
				dlInfo.iconPath = "drawable:logo_91assist";
				mDownloadService.addDownloadTask(dlInfo);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// 刷新右上角下载图标的状态
						im_into_download.invalidate();
					}
				});

			}
		});

	}

	/**
	 * ListView底部添加下载更多选项
	 */
	private void addDownloadMoreItem() {
		if (mItems == null || mItems.size() == 0)
			return;

		LinearLayout footView = new LinearLayout(mContext);
		AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
		footView.setLayoutParams(footParams);
		footView.setPadding(0, 20, 0, 20);
		footView.setGravity(Gravity.CENTER);
		footView.setClickable(true);
		footView.setBackgroundResource(R.drawable.myphone_click_item_blue);

		TextView textMore = new TextView(mContext);
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textMore.setText(R.string.launcher_edit_show_more);
		textMore.setGravity(Gravity.CENTER);
		textMore.setTextColor(Color.BLACK);
		textMore.setTextSize(16);
		textMore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.app_market_more, 0, 0, 0);
		textMore.setCompoundDrawablePadding(10);
		textMore.setLayoutParams(textParams);

		footView.addView(textMore);

		footView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 已安装了'91助手',直接打开
				if (AndroidPackageUtils.isPkgInstalled(mContext, RecommendApps.PANDASPACE_PCK)) {
					PackageManager pm = getPackageManager();
					Intent intent = pm.getLaunchIntentForPackage(RecommendApps.PANDASPACE_PCK);
					switch (mClientType) {

					// 一键装机
					case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED:
						intent.putExtra(ASSIT_APP_ACTION_TYPE_EXTRA_KEY, ASSIT_APP_EXTRA_TYPE_APP);
						break;
					// 热门游戏
					case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY:
						intent.putExtra(ASSIT_APP_ACTION_TYPE_EXTRA_KEY, ASSIT_APP_EXTRA_TYPE_GAME);
						break;
					}
					// startActivity(intent);
					SystemUtil.startActivity(AppMarketSubjectActivity.this, intent);
					// AndroidPackageUtils.runApplication(mContext,
					// assit_app_package);
				} else {
					showInstallAssitAppDialog();// 显示下载'91助手'提示
				}
			}
		});

		lv_main_list.addFooterView(footView);

	}// end addDownloadMoreItem

	/**
	 * 退出界时清理所有需要清理的数据
	 */
	private void clearAllDataOnExit() {

		try {

			if (mDataLoadTask != null)
				mDataLoadTask.cancel(true);
			
			if (mPrimeDataLoadTask != null)
				mPrimeDataLoadTask.cancel(true);

			// 中止加载图标的线程池
			AppMarketUtil.clearThreads();

			// 清除图标缓存
			AppMarketUtil.clearIconCache();

			AppMarketUtil.maxCount = 0;

			mItems.clear();
			mEditorRecItems.clear();
			mPrimeRecItems.clear();
			mItems=null;
			mEditorRecItems=null;
			mPrimeRecItems=null;
			mAppMarketUtil = null;

		} catch (Exception e) {
			Log.w(Global.TAG, "AppMarketSubjectActivity clearAllDataOnExit:" + e.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// WebView滚动到之前记录的位置
		if (mWebView != null)
			mWebView.scrollTo(0, mWebViewScrollY);

		// 刷新列表项状态
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (!mIsListScrolling)
								mAdapter.notifyDataSetChanged();
							im_into_download.invalidate();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	protected void onPause() {

		// 记录WebView的滚动位置
		if (mWebView != null)
			mWebViewScrollY = mWebView.getScrollY();
		super.onPause();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		lv_main_list.setVisibility(View.GONE);
		
		clearAllDataOnExit();// 清理数据
		try {
			// 清理webview缓存
			mWebView.stopLoading();
			mWebView.freeMemory();
			mWebView.destroy();
		} catch (Exception e) {
		}

		mIsWebLoadError = false;
	}

	// -内部类-----------------------------------------------------------------

	private class AppListAdapter extends BaseAdapter implements View.OnClickListener {

		private String resDownloadCount;

		/** 图标加载线程回调接口 */
		private CommonCallBack<Void> mIconLoaderCallBack;

		/** Holder对象集合 */
		public Set<Holder> holderSet = new HashSet<Holder>();

		public AppListAdapter() {
			mIconLoaderCallBack = new CommonCallBack<Void>() {

				@Override
				public void invoke(Void... args) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (!mIsListScrolling)
								notifyDataSetChanged();
						}
					});

				}
			};
		}

		@Override
		public int getCount() {
			return mItems==null?0:mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems==null?null:mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mItems==null?0:mItems.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			AppMarketItem item = mItems.get(position);
			initItemState(item);
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = getLayoutInflater().inflate(R.layout.app_market_app_list_item, null);
				holder.im_icon = (ImageView) convertView.findViewById(R.id.im_icon);
				holder.tv_app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
				holder.ll_star = convertView.findViewById(R.id.ll_star);
				holder.tv_download_count = (TextView) convertView.findViewById(R.id.tv_download_count);
				holder.tv_version_name = (TextView) convertView.findViewById(R.id.tv_version_name);
				holder.ll_download = (AppMarketDownloadingButton) convertView.findViewById(R.id.ll_download);

				// 初始化星级View集合
				holder.starViewArray = new View[] { holder.ll_star.findViewById(R.id.im_star1), holder.ll_star.findViewById(R.id.im_star2), holder.ll_star.findViewById(R.id.im_star3),
						holder.ll_star.findViewById(R.id.im_star4), holder.ll_star.findViewById(R.id.im_star5) };

				convertView.setTag(holder);
				holderSet.add(holder);

			} else
				holder = (Holder) convertView.getTag();

			Bitmap icon = AppMarketUtil.getIconFromCache(item.getPackageName());
			if (icon == null || icon.isRecycled()) {
				holder.im_icon.setImageResource(R.drawable.app_market_default_icon);
				if (!mIsListScrolling)
					AppMarketUtil.loadIconInThread(item.getPackageName(), item.getIconFilePath(), item.getIconUrl(), mIconLoaderCallBack);
			} else
				holder.im_icon.setImageBitmap(icon);

			holder.tv_app_title.setText(item.getTitle());

			// 下载次数
			if (resDownloadCount == null)
				resDownloadCount = mContext.getString(R.string.app_market_detail_download_count_unit);
			String downloadCount = String.format(resDownloadCount, item.getDownloadNumber());
			holder.tv_download_count.setText(downloadCount);

			// 设置星级
			setStar(holder.starViewArray, item.getStar());

			// 版本号，尺寸
			String versionInfo = mContext.getString(R.string.app_market_detail_version);
			versionInfo = String.format(versionInfo, item.getVersionName(), item.getSize(), "");
			holder.tv_version_name.setText(versionInfo);

			holder.ll_download.setTag(item);
			holder.ll_download.setOnClickListener(this);

			// 设置下载状态
			// initSate(item, holder);

			return convertView;
		}

		/**
		 * 设置星级
		 * 
		 * @param star
		 */
		private void setStar(View viewArray[], int star) {
			if (star > viewArray.length)
				star = viewArray.length;
			for (int i = 0; i < viewArray.length; i++) {
				ImageView starView = (ImageView) viewArray[i];
				if (i < star)
					starView.setImageResource(R.drawable.theme_shop_v2_theme_detail_comment_rating_1);
				else
					starView.setImageResource(R.drawable.theme_shop_v2_theme_detail_comment_rating_2);
			}
		}

		public class Holder {

			public ImageView im_icon;
			public TextView tv_app_title;
			public View ll_star;
			public TextView tv_download_count;
			public TextView tv_version_name;
			public AppMarketDownloadingButton ll_download;

			/**
			 * 星级图标View的集合
			 */
			public View[] starViewArray = null;
		}

		@Override
		public void onClick(View v) {
			if (v instanceof AppMarketDownloadingButton) {
				((AppMarketDownloadingButton) v).onClick();
				im_into_download.invalidate();
			}

		}// end onClick

	}// end class AppListAdapter

	/**
	 * 获取应用列表的任务
	 */
	private class DataLoadTask extends AsyncTask<String, Integer, Integer> {

		private List<AppMarketItem> tempList;

		@Override
		protected Integer doInBackground(String... params) {
			String url = params[0];
			try {
				if (!TelephoneUtil.isNetworkAvailable(mContext))
					throw new IOException("No availabled connection!");
				listDataLoadBegin=System.currentTimeMillis();
				tempList = mAppMarketUtil.getAppsFromServer(null, url, 1);
				Log.d(TAG, "list data load time span:"+(System.currentTimeMillis()-listDataLoadBegin));
				// batchInitItemState(tempList);
				return EVENT_LOAD_DATA_SUCCESS;
			} catch (Exception e) {
				Log.e(Global.TAG, "load editor recommend apps failed:" + e.toString());
				return getExceptionType(e);
			}
		}

		@Override
		protected void onCancelled() {
			if (tempList != null)
				tempList.clear();
			tempList = null;
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			mIsAppListLoading = false;

			switch (result) {

			case EVENT_LOAD_DATA_SUCCESS:
				if (tempList != null && tempList.size() > 0) {
					mEditorRecItems.addAll(tempList);
					mItems.clear();
					mItems.addAll(mEditorRecItems);
					mAdapter.notifyDataSetChanged();
					addDownloadMoreItem();
					mIsAppListLoadError = false;
				} else {
					mIsAppListLoadError = true;
				}

				break;
			case EVENT_NET_WORK_ERROR:
				mIsAppListLoadError = true;
				break;
			case EVENT_ERROR:
				mIsAppListLoadError = true;

				break;
			}
			
			// 显示数据界面
			showDataFace();
		}

		/**
		 * 检验异常类型
		 * 
		 * @param e
		 * @return
		 */
		private int getExceptionType(Exception e) {
			if ((e instanceof MalformedURLException) || (e instanceof IOException) || (e instanceof SocketTimeoutException) || (e instanceof EOFException) || (e instanceof SocketException)
					|| (e instanceof java.net.ProtocolException) || (e instanceof org.apache.http.ProtocolException))
				return EVENT_NET_WORK_ERROR;
			else
				return EVENT_ERROR;
		}

	}// end DataLoadTask;
	
	/**
	 * 获取精品推荐列表的任务
	 */
	private class PrimeRecDataLoadTask extends AsyncTask<String, Integer, Integer> {

		private List<AppMarketItem> tempList;

		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			wait_layout.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(String... params) {
			String url = params[0];
			try {
				if (!TelephoneUtil.isNetworkAvailable(mContext))
					throw new IOException("No availabled connection!");
				tempList = mAppMarketUtil.getAppsFromServer(null, url, 1);
				// batchInitItemState(tempList);
				return EVENT_LOAD_DATA_SUCCESS;
			} catch (Exception e) {
				Log.e(Global.TAG, "load prime recommend apps failed:" + e.toString());
				return getExceptionType(e);
			}
		}

		@Override
		protected void onCancelled() {
			if (tempList != null)
				tempList.clear();
			tempList = null;
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			wait_layout.setVisibility(View.GONE);
			switch (result) {

			case EVENT_LOAD_DATA_SUCCESS:
				if (tempList != null && tempList.size() > 0) {
					mPrimeRecItems.addAll(tempList);
					mItems.clear();
					mItems.addAll(mPrimeRecItems);
					mAdapter.notifyDataSetChanged();
					changeTabStyle(TAB_PRIME_REC);
					break;
				}
				
			case EVENT_NET_WORK_ERROR:
			case EVENT_ERROR:
				MessageUtils.makeShortToast(mContext, R.string.app_market_load_data_failed);
				break;
			}
		}

		/**
		 * 检验异常类型
		 * 
		 * @param e
		 * @return
		 */
		private int getExceptionType(Exception e) {
			if ((e instanceof MalformedURLException) || (e instanceof IOException) || (e instanceof SocketTimeoutException) || (e instanceof EOFException) || (e instanceof SocketException)
					|| (e instanceof java.net.ProtocolException) || (e instanceof org.apache.http.ProtocolException))
				return EVENT_NET_WORK_ERROR;
			else
				return EVENT_ERROR;
		}

	}// end DataLoadTask;

	/**
	 * ListView滚动监听
	 */
	private class ListViewScrollListener implements AbsListView.OnScrollListener {

		private int scrollType = -1;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (scrollType == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || scrollType == -1)
				mIsListScrolling = false;
			else
				mIsListScrolling = true;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			scrollType = scrollState;
			if (scrollType == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				mIsListScrolling = false;
				mAdapter.notifyDataSetChanged();
			}
		}

	}// end class ListViewScrollListener

	/**
	 * 绑定下载服务的回调
	 */
	private class DownloadServiceBindCallBack implements CommonCallBack<Boolean> {

		@Override
		public void invoke(final Boolean... arg) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					boolean bindSuccess = false;
					if (arg != null && arg.length > 0)
						bindSuccess = arg[0].booleanValue();
					// Log.d(Global.TAG, "bind download service:"+bindSuccess);
					if (bindSuccess) {
						im_into_download.invalidate();
						initData();
					} else {

						// 绑定失败，当是数据加载失败了，因为后续的操作无法正常进行
						mIsAppListLoadError = true;
						mIsWebLoadError = true;
						showDataFace();
					}
				}
			});

		}

	}// end DownloadServiceBindCallBack
	
	
}
