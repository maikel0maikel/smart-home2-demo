package com.nd.hilauncherdev.appmarket;

import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageActivity;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

/**
 * 专题详情
 * 
 * @author zhuchenghua
 * 
 */
public class AppMarketSubjectDetailActivity extends HiActivity {
	private Context mContext;
	private Handler mHandler = new Handler();

	/** 标题 */
	private TextView container_title;

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

	/** 头部条 */
	private View mListHeaderView;

	/**
	 * ListView头部显示的网页
	 */
	private WebView mWebView;

	/**
	 * 总供多少个软件的头View
	 */
	private View mAppCountView;

	/**
	 * 软件个数的View
	 */
	private TextView totalCountView;

	/** 底部下载进度条 */
	private AppMarketDownloadBar ll_download_bar;

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

	/** 列表adapter */
	private BaseAdapter mAdapter;

	private boolean mIsListScrolling = false;

	/***
	 * 网页加载是否出错
	 */
	private boolean mIsWebLoadingError = false;

	/**
	 * 专题数据加载是否出错
	 */
	private boolean mIsSubDetailLoadError = false;

	/**
	 * 数据界面是否已经显示了
	 */
	private boolean mIsDataViewShown = false;

	/**
	 * 功能的主要辅助类，提供获取数据，反馈下载统计等功能
	 */
	private AppMarketUtil mAppMarketUtil;

	/**
	 * 加载数据的任务
	 */
	private AsyncTask<String, Integer, Integer> mDataLoadTask;

	/**
	 * 详情项
	 */
	private AppMarketSubjectDetailItem mDetailItem;

	/**
	 * 专题详情数据URL
	 */
	private String mSubDetailUrl = null;

	/**
	 * 软件安装监听
	 */
	private BroadcastReceiver mNewAppInstallReceiver;

	private int mClientType;

	/** 记录WebView的滚动坐标Y */
	private int mWebViewScrollY;

	/**
	 * 下载服务
	 */
	private DownloadServerServiceConnection mDownloadService = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_market_subject_activity);
		mContext = this;

		// 获取专题数据的Url
		mSubDetailUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		mClientType = getIntent().getIntExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED);
		mAppMarketUtil = new AppMarketUtil(mContext, mClientType);

		// 绑定下载服务
		mDownloadService = new DownloadServerServiceConnection(mContext);
		mDownloadService.bindDownloadService(new DownloadServiceBindCallBack());

		initView();
		initListener();

		// 统计界面打开次数
		AppMarketUtil.executeThread(new Runnable() {

			@Override
			public void run() {

				switch (mClientType) {

				// 一键装机
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED:
					HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_NEED_SUBJECT_DETAIL);
					break;

				// 热门游戏
				case OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY:
					HiAnalytics.submitEvent(mContext, AnalyticsConstant.EVENT_ONE_KEY_PHONE_PLAY_SUBJECT_DETAIL);
					break;
				}
			}
		});

	}

	private void initView() {
		rl_data_main = (RelativeLayout) findViewById(R.id.rl_data_main);
		lv_main_list = (ListView) findViewById(R.id.lv_main_list);
		im_into_download = (ImageView) findViewById(R.id.im_into_download);
		app_running_back_btn = (ImageView) findViewById(R.id.app_running_back_btn);
		container_title = (TextView) findViewById(R.id.container_title);
		// 底部下载进度条
		ll_download_bar = (AppMarketDownloadBar) findViewById(R.id.ll_download_bar);

		ll_download_bar.setActionCallBack(new CommonCallBack<Void>() {

			@Override
			public void invoke(Void... args) {
				im_into_download.invalidate();
			}
		});

		mLoadingView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.LOADING_DATA_INFO_VIEW);
		mNoDataView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.SEARCH_NO_DATA_VIEW);
		mNetworkErrorView = ViewFactory.getNomalErrInfoView(mContext, rl_data_main, ViewFactory.NET_BREAK_VIEW);

		mLoadingView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		mNetworkErrorView.setVisibility(View.GONE);

		// 初始化头部View
		mListHeaderView = (LinearLayout) getLayoutInflater().inflate(R.layout.app_market_subject_header_view, null);
		mWebView = (WebView) mListHeaderView.findViewById(R.id.web_view);
		mAppCountView = mListHeaderView.findViewById(R.id.ll_split);
		totalCountView = (TextView) mListHeaderView.findViewById(R.id.tv_app_count);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLightTouchEnabled(true);
		// 设置浏览器加载状态处理
		mWebView.setWebViewClient(new WebViewClient() {

			private int retryCount = 0;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (!mIsWebLoadingError || (mIsWebLoadingError && retryCount == 2))
					showDataFace();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				mIsWebLoadingError = true;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mDetailItem.isSingleApp()) {
					AppMarketItem item = mDetailItem.getAppList().get(0);
					if (item.getDownloadState() == DownloadState.STATE_CANCLE || item.getDownloadState() == DownloadState.STATE_NONE) {
						AppMarketUtil.startDownload(mContext, mDetailItem.getAppList().get(0));
						im_into_download.invalidate();

						// 显示下载条
						showDownloadBar();
					}
				}
				return true;
			}

		});

		// 正在加数据的界面先显示出来
		mLoadingView.setVisibility(View.VISIBLE);

		// 添加ListView的头部
		lv_main_list.addHeaderView(mListHeaderView, null, false);

		mAdapter = new AppListAdapter();
		lv_main_list.setAdapter(mAdapter);
		lv_main_list.setOnScrollListener(new ListViewScrollListener());

	}

	/**
	 * 初始化事件监听
	 */
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

		// 下载全部软件
		totalCountView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDownloadAllAppDialog();
			}
		});

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (TextUtils.isEmpty(mSubDetailUrl)) {
			mLoadingView.setVisibility(View.GONE);
			mNoDataView.setVisibility(View.VISIBLE);
			return;
		}
		if (mDataLoadTask != null && mDataLoadTask.getStatus() == AsyncTask.Status.RUNNING)
			mDataLoadTask.cancel(true);

		mDataLoadTask = new DataLoadTask().execute(mSubDetailUrl);
	}

	/**
	 * 展示数据界面
	 */
	private synchronized void showDataFace() {
		if (mIsDataViewShown)
			return;

		mIsDataViewShown = true;

		if (mIsWebLoadingError) {

			lv_main_list.removeHeaderView(mListHeaderView);
			// 单专题情况下载，网页加载失败，则当是数据加载失败
			if (mDetailItem.isSingleApp()) {
				mLoadingView.setVisibility(View.GONE);
				mNoDataView.setVisibility(View.VISIBLE);
				return;
			}

		} else {

			// 单应用的专题，不显示全部下载的按钮,并将网页View添加到数据主显区，不添加到ListView的头部,ListView也不显示
			if (mDetailItem.isSingleApp()) {
				mAppCountView.setVisibility(View.GONE);
				lv_main_list.removeHeaderView(mListHeaderView);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				mListHeaderView.setLayoutParams(params);
				rl_data_main.addView(mListHeaderView);
				// 显示下载条
				showDownloadBar();
			} else {
				// 不是单应用专题，显示全部下载按钮
				mAppCountView.setVisibility(View.VISIBLE);
				String tip = String.format(getString(R.string.app_market_download_all_tip), mItems.size());
				totalCountView.setText(tip);
				lv_main_list.setVisibility(View.VISIBLE);
			}
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
	 * 显示下载条
	 */
	private void showDownloadBar() {
		AppMarketItem item = mDetailItem.getAppList().get(0);
		if (item.getDownloadState() != DownloadState.STATE_CANCLE && item.getDownloadState() != DownloadState.STATE_NONE) {
			ll_download_bar.init(item);
			ll_download_bar.setVisibility(View.VISIBLE);
			ll_download_bar.bringToFront();
		}
	}

	/**
	 * 显示 下载助手的对话框
	 */
	private void showDownloadAllAppDialog() {
		String confirm = mContext.getString(R.string.app_market_download_all_confirm);

		CommonDialog alertd = ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), String.format(confirm, mDetailItem.getAppList().size()),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						List<AppMarketItem> appList = mDetailItem.getAppList();
						for (AppMarketItem item : appList) {
							if (item.getDownloadState() == DownloadState.STATE_CANCLE || item.getDownloadState() == DownloadState.STATE_NONE)
								AppMarketUtil.startDownload(mContext, item);
						}
						mAdapter.notifyDataSetChanged();
						im_into_download.invalidate();
					}
				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		alertd.show();
	}

	/**
	 * 退出界时清理所有需要清理的数据
	 */
	private void clearAllDataOnExit() {

		try {
			if (mDataLoadTask != null)
				mDataLoadTask.cancel(true);
			if (mNewAppInstallReceiver != null)
				mContext.unregisterReceiver(mNewAppInstallReceiver);
			mNewAppInstallReceiver = null;

			// 中止加载图标的线程池
			AppMarketUtil.clearThreads();

			// 清除图标缓存
			AppMarketUtil.clearIconCache();

			AppMarketUtil.maxCount = 0;

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

		try {
			// 注册软件安装广播监听，有新软件安装，要过滤掉
			if (mNewAppInstallReceiver == null) {
				mNewAppInstallReceiver = new NewAppInstallReceiver();
				IntentFilter itFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
				itFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				itFilter.addDataScheme("package");
				mContext.registerReceiver(mNewAppInstallReceiver, itFilter);
			}

		} catch (Exception e) {
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearAllDataOnExit();// 清理数据
		try {
			mWebView.stopLoading();
			mWebView.freeMemory();
			mWebView.destroy();
		} catch (Exception e) {
		}

		mIsWebLoadingError = false;
	}

	/**
	 * -内部类--------------------------------------------------------------------
	 * ---------------------------------
	 */

	private class AppListAdapter extends BaseAdapter implements View.OnClickListener {

		private String resDownloadCount;

		private CommonCallBack<Void> mIconLoaderCallBack;

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
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mItems.get(position).getId();
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

		private class Holder {

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

		@Override
		protected Integer doInBackground(String... params) {
			String url = params[0];
			try {
				if (!TelephoneUtil.isNetworkAvailable(mContext))
					throw new IOException("No availabled connection!");
				mDetailItem = mAppMarketUtil.getSubjectDetailFromServer(url);
				// 没有软件，表示加载失败
				if (mDetailItem == null || mDetailItem.getAppList() == null || mDetailItem.getAppList().size() == 0)
					return EVENT_ERROR;

				return EVENT_LOAD_DATA_SUCCESS;
			} catch (Exception e) {
				Log.e(Global.TAG, "load apps failed:" + e.toString());
				return getExceptionType(e);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mDetailItem != null && mDetailItem.getAppList() != null) {
				mDetailItem.getAppList().clear();
				mDetailItem.setAppList(null);
			}
		}

		@Override
		protected void onPostExecute(Integer result) {

			switch (result) {

			case EVENT_LOAD_DATA_SUCCESS:
				mIsSubDetailLoadError = false;
				// 非单应用专题才显示列表
				if (!mDetailItem.isSingleApp()) {
					mItems.addAll(mDetailItem.getAppList());
					mAdapter.notifyDataSetChanged();
				} else {
					// 单专题 ，下载图标
					AppMarketItem item = mDetailItem.getAppList().get(0);
					initItemState(item);
					AppMarketUtil.loadIconInThread(item.getPackageName(), item.getIconFilePath(), item.getIconUrl(), null);
				}
				break;
			case EVENT_NET_WORK_ERROR:
				mIsSubDetailLoadError = true;
				break;
			case EVENT_ERROR:
				mIsSubDetailLoadError = true;

				break;
			}

			// 加载失败
			if (mIsSubDetailLoadError) {
				container_title.setText("");
				mNoDataView.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
			} else {

				// 设置专题名称
				container_title.setText(mDetailItem.getSubjectTitle());

				// 加载专题网页页面
				if (mDetailItem.getSubjectWebUrl() != null) {
					try {
						mWebView.loadUrl(mDetailItem.getSubjectWebUrl());
					} catch (Exception e) {
						mIsWebLoadingError = true;
						showDataFace();
						Log.w(Global.TAG, "AppMarketSubjectDetailActivity onPostExecute failed:" + e.toString());
					}

				} else {
					mIsWebLoadingError = true;
					showDataFace();
				}

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
	 * 新应用安装监听
	 */
	private class NewAppInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context content, Intent intent) {
			// 单应用专题下的新应用安装监听
			if (null == intent || mDetailItem == null || !mDetailItem.isSingleApp())
				return;
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName == null)
				return;

			AppMarketItem item = mDetailItem.getAppList().get(0);

			if (ApkTools.isTheSameApp(mContext, packageName, item.getApkFilePath())) {
				initItemState(item);
				ll_download_bar.refrashState();
			}

		}// end onReceiver

	}// end class NewAppInstallReceiver

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
						initData();
					} else {

						// 绑定失败，当是数据加载失败了，因为后续的操作无法正常进行
						mLoadingView.setVisibility(View.GONE);
						mNoDataView.setVisibility(View.VISIBLE);
					}
				}
			});

		}

	}// end DownloadServiceBindCallBack
}
