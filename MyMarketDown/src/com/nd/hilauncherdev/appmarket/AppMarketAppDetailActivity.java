package com.nd.hilauncherdev.appmarket;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.CommonSlidingViewData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.ApkInstaller;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.kitset.util.StringUtil;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.myphone.util.TabContainerUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.activity.DownloadManageActivity;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

public class AppMarketAppDetailActivity extends Activity {
	private ImageView app_running_back_btn;
	private TextView container_title;
	private ImageView im_into_download;
	private AppMarketItemIconView im_icon;
	private TextView tv_app_title;
	private TextView tv_download_count;
	private TextView tv_version_name;
	private TextView tv_download;
	private ImageView im_detail_expand;
	private TextView tv_detail_descrption;
	private LinearLayout ll_main;
	private View sv_detail_container;
	private TextView tv_download_bottom;
	private AppMarketDetailSecurityView ll_security;

	private AppMarketDetailPreviewSlidingView mSlidingView;
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
	private List<ICommonDataItem> mPreviewImageItems;
	/**
	 * 单元格宽度
	 */
	private int cellWidth;
	/**
	 * 单元格高度
	 */
	private int cellHeight;

	/**
	 * 行数，默认1行
	 */
	private int mRowCount = 1;
	/**
	 * 列数，默认2列
	 */
	private int mColCount = 2;

	/**
	 * 详情对象
	 */
	private AppMarketDetailItem mDetailItem;

	/**
	 * 列表对象
	 */
	private AppMarketItem mAppItem;

	/**
	 * 详情页的URL
	 */
	private String mDetailUrl;

	/**
	 * 下载状态
	 */
	private int mDownloadState = DownloadState.STATE_CANCLE;
	/**
	 * 当前进度
	 */
	private int mCurrentProcess = 0;

	/**
	 * 下载进度监听
	 */
	private DownloadReceiver mDownloadReceiver;

	/**
	 * 软件安装监听
	 */
	private BroadcastReceiver mNewAppInstallReceiver;

	/**
	 * 软件静默安装监听
	 */
	private BroadcastReceiver mSilentInstallReceiver;

	/**
	 * 加载详情数据的任务
	 */
	private AsyncTask<Void, Integer, Integer> mDataLoadTask;

	/**
	 * 简介最少行数
	 */
	private final int TEXT_MIN_LINE_COUNT = 10;

	private final int EVENT_NET_WORK_ERROR = 10000;
	private final int EVENT_LOAD_DATA_SUCCESS = 10001;
	private final int EVENT_ERROR = -1;

	/**
	 * 文字是否展开
	 */
	private boolean mIsExpand = false;

	/**
	 * 星级图标View的集合
	 */
	private View[] mStarViewArray = null;

	private int mClientType;

	private AppMarketUtil mAppMarketUtil;

	/**
	 * 下载服务
	 */
	private DownloadServerServiceConnection mDownloadService = null;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TabContainerUtil.fullscreen(this);// 隐藏标题条
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_market_app_detail_activity);
		mContext = this;
		mAppItem = (AppMarketItem) getIntent().getSerializableExtra(AppMarketConstants.EXTRA_APP_MARKET_ITEM);
		if (mAppItem == null || StringUtil.isEmpty(mAppItem.getDetailUrl())) {
			finish();
			return;
		}

		// 下载状态监听
		IntentFilter iFilter = new IntentFilter(DownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
		mDownloadReceiver = new DownloadReceiver();
		mContext.registerReceiver(mDownloadReceiver, iFilter);

		// 新应用安装监听
		mNewAppInstallReceiver = new NewAppInstallReceiver();
		IntentFilter itFilter = new IntentFilter();
		itFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		itFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		itFilter.addDataScheme("package");
		registerReceiver(mNewAppInstallReceiver, itFilter);

		// 静默安装监听
		mSilentInstallReceiver = new SilentInstallReceiver();
		IntentFilter itFilter2 = new IntentFilter();
		itFilter2.addAction(ApkInstaller.RECEIVER_APP_SILENT_INSTALL);
		registerReceiver(mSilentInstallReceiver, itFilter2);

		mDetailUrl = mAppItem.getDetailUrl();
		mClientType = getIntent().getIntExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED);
		mAppMarketUtil = new AppMarketUtil(mContext, mClientType);

		// 绑定下载服务
		mDownloadService = new DownloadServerServiceConnection(mContext);
		mDownloadService.bindDownloadService(new DownloadServiceBindCallBack());

		initView();
		// loadDataFromServer();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		app_running_back_btn = (ImageView) findViewById(R.id.app_running_back_btn);
		container_title = (TextView) findViewById(R.id.container_title);
		im_into_download = (ImageView) findViewById(R.id.im_into_download);
		im_icon = (AppMarketItemIconView) findViewById(R.id.im_icon);
		tv_app_title = (TextView) findViewById(R.id.tv_app_title);
		tv_download_count = (TextView) findViewById(R.id.tv_download_count);
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		tv_download = (TextView) findViewById(R.id.tv_download);
		im_detail_expand = (ImageView) findViewById(R.id.im_detail_expand);
		tv_detail_descrption = (TextView) findViewById(R.id.tv_detail_descrption);
		sv_detail_container = findViewById(R.id.sv_detail_container);
		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		mSlidingView = (AppMarketDetailPreviewSlidingView) findViewById(R.id.sliding_view);
		tv_download_bottom = (TextView) findViewById(R.id.tv_download_bottom);
		ll_security = (AppMarketDetailSecurityView) findViewById(R.id.ll_security);

		mLoadingView = ViewFactory.getNomalErrInfoView(mContext, ll_main, ViewFactory.LOADING_DATA_INFO_VIEW);
		mNoDataView = ViewFactory.getNomalErrInfoView(mContext, ll_main, ViewFactory.SEARCH_NO_DATA_VIEW);
		mNetworkErrorView = ViewFactory.getNomalErrInfoView(mContext, ll_main, ViewFactory.NET_BREAK_VIEW);

		container_title.setText(mAppItem.getTitle());

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		mLoadingView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		mNetworkErrorView.setVisibility(View.GONE);

		mLoadingView.setLayoutParams(params);
		mNoDataView.setLayoutParams(params);
		mNetworkErrorView.setLayoutParams(params);

		mSlidingView.setEndlessScrolling(false);
		mSlidingView.setOnItemClickListener(new CommonSlidingView.OnCommonSlidingViewClickListener() {

			@Override
			public void onItemClick(View v, int positionInData, int positionInScreen, int screen, ICommonData data) {
				Intent intent = new Intent(mContext, AppMarketLargeImagePreviewActivity.class);
				intent.putExtra(AppMarketConstants.EXTRA_APP_MARKET_DETAIL_ITEM, mDetailItem);
				intent.putExtra(AppMarketConstants.EXTRA_APP_MARKET_POSITION, positionInData);
				startActivity(intent);
			}

		});

		// 初始化星级View集合
		mStarViewArray = new View[] { findViewById(R.id.im_star1), findViewById(R.id.im_star2), findViewById(R.id.im_star3), findViewById(R.id.im_star4), findViewById(R.id.im_star5) };

		// 初始化按钮
		initBtn();

		// 初始化参数
		initParameter();

	}

	/**
	 * 初始化按钮事件
	 */
	private void initBtn() {
		app_running_back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				return;
			}
		});

		// 设置文字展开点击事件
		DetailTextExpandClickListener detailTextExpandClickListener = new DetailTextExpandClickListener();
		tv_detail_descrption.setOnClickListener(detailTextExpandClickListener);
		im_detail_expand.setOnClickListener(detailTextExpandClickListener);

		// 进入下载管理界面
		im_into_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DownloadManageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		// 上面的下载按钮
		tv_download.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadSoft();
			}
		});

		// 底部下载按钮
		tv_download_bottom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadSoft();
			}
		});
	}

	/**
	 * 初始化参数
	 */
	private void initParameter() {
		int screenWidth = ScreenUtil.getCurrentScreenWidth(mContext);

		// 计算单元格高宽
		cellWidth = screenWidth;
		cellHeight = ScreenUtil.dip2px(mContext, 240);

		mSlidingViewData = new CommonSlidingViewData(cellWidth, cellHeight, mColCount, mRowCount, new ArrayList<ICommonDataItem>());
		mPreviewImageItems = mSlidingViewData.getDataList();
		mPreviewImageItems.clear();
		ArrayList<ICommonData> datas = new ArrayList<ICommonData>();
		datas.add(mSlidingViewData);
		mSlidingView.setList(datas);

	}

	/**
	 * 下载数据
	 */
	private void loadDataFromServer() {
		if (mDataLoadTask != null && mDataLoadTask.getStatus() == AsyncTask.Status.RUNNING)
			mDataLoadTask.cancel(true);

		mDataLoadTask = new DataLoadTask().execute();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (mDetailItem != null) {
			sv_detail_container.setVisibility(View.VISIBLE);

			// 初始化所有状态
			initDownloadState();

			// 标题名称
			container_title.setText(mDetailItem.getTitle());
			tv_app_title.setText(mDetailItem.getTitle());

			// 设置下载次数
			String downloadCount = mContext.getString(R.string.app_market_detail_download_count_unit);
			downloadCount = String.format(downloadCount, mDetailItem.getDownloadNumber());
			tv_download_count.setText(downloadCount);
			// 设置星级
			setStar(mDetailItem.getStar());
			// 设置版本、大小、语言数据
			String versionInfo = mContext.getString(R.string.app_market_detail_version);
			versionInfo = String.format(versionInfo, mDetailItem.getVersionName(), mDetailItem.getSize(), mDetailItem.getLanguage());
			tv_version_name.setText(versionInfo);

			if (mDetailItem.getSafeInfo() != null) {
				ll_security.init(mDetailItem.getSafeInfo());
			} else {
				View securityBar = ll_security.findViewById(R.id.ll_security_bar);
				securityBar.setVisibility(View.GONE);
			}

			// 详情描述
			tv_detail_descrption.setText(mDetailItem.getDescription());

			// 设置预览图界面
			List<String> imageUrlList = mDetailItem.getPreviewImageUrlList();
			if (imageUrlList == null || imageUrlList.size() == 0)
				mSlidingView.setVisibility(View.GONE);
			else {

				for (int i = 0; i < imageUrlList.size(); i++) {
					AppMarketDetailPreviewImageItem preItem = new AppMarketDetailPreviewImageItem();
					preItem.setResId(mDetailItem.getResId());
					preItem.setImageUrl(imageUrlList.get(i));
					mPreviewImageItems.add(preItem);
				}

			}
			im_icon.setAppMarketItem(mAppItem);
			mSlidingView.reLayout();
			mSlidingView.invalidate();
		}
	}

	/**
	 * 设置星级
	 * 
	 * @param star
	 */
	private void setStar(int star) {
		if (star > mStarViewArray.length)
			star = mStarViewArray.length;
		for (int i = 0; i < star; i++) {
			ImageView starView = (ImageView) mStarViewArray[i];
			starView.setImageResource(R.drawable.theme_shop_v2_theme_detail_comment_rating_1);
		}
	}

	/**
	 * 初始化界面状态，如已安装，已下载，暂停等
	 */
	public void initDownloadState() {
		int textColorInt = Color.parseColor("#EEEEED");
		String stateStr = null;

		tv_download.setEnabled(true);
		tv_download_bottom.setEnabled(true);
		mDownloadState = mAppItem.getDownloadState();
		mCurrentProcess = mAppItem.getDownloadProccess();

		switch (mDownloadState) {
		// 正在下载
		case DownloadState.STATE_DOWNLOADING:
			stateStr = mContext.getString(R.string.theme_shop_theme_download_now) + ":" + mCurrentProcess + "%";
			break;
		// 暂停
		case DownloadState.STATE_PAUSE:
			stateStr = mContext.getString(R.string.app_market_app_download_pause);
			break;
		// 等待中
		case DownloadState.STATE_WAITING:
			stateStr = mContext.getString(R.string.app_market_app_download_wait);
			break;

		// 已下载
		case DownloadState.STATE_FINISHED:
			stateStr = mContext.getString(R.string.common_button_install);
			break;
		// 已安装
		case DownloadState.STATE_INSTALLED:
			stateStr = mContext.getString(R.string.app_market_app_installed);
			break;

		// 取消和未下载
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_NONE:
			stateStr = mContext.getString(R.string.theme_shop_theme_downloading);
			textColorInt = Color.WHITE;
			break;
		// 正在安装
		case ApkInstaller.INSTALL_STATE_INSTALLING:
			stateStr = mContext.getString(R.string.app_market_installing);
			// 禁用按钮
			tv_download.setEnabled(false);
			tv_download_bottom.setEnabled(false);
			break;
		}

		// 上面的下载按钮
		tv_download.setTextColor(textColorInt);
		tv_download.setText(stateStr);
		// 底部的下载按钮
		tv_download_bottom.setTextColor(textColorInt);
		tv_download_bottom.setText(stateStr);
	}

	/**
	 * 已安装情况下，显示是否立即运行的对话框提示
	 */
	private void showOpenAppTipDialog() {
		CommonDialog alertd = ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), mContext.getString(R.string.app_market_app_installed_run_tip),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						AndroidPackageUtils.runApplication(mContext, mAppItem.getPackageName());
						dialog.dismiss();
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
	 * 下载软件
	 */
	private void downloadSoft() {
		/*
		 * if(true){ ApkTools.installApplication(mContext, new
		 * File("/sdcard/test.apk")); return; }
		 */
		switch (mDownloadState) {

		// 正在下载
		case DownloadState.STATE_DOWNLOADING:
			// 暂停
		case DownloadState.STATE_PAUSE:
			// 等待中
		case DownloadState.STATE_WAITING:
			// 正在下载、暂停、等待下，跳转到下载管理 界面
			Intent dlMgrIntent = new Intent(mContext, DownloadManageActivity.class);
			dlMgrIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(dlMgrIntent);
			break;
		// 已下载
		case DownloadState.STATE_FINISHED:
			File file = new File(mAppItem.getApkFilePath());
			if (!file.exists()) { // 文件不存，改状态为未下载的
				mDownloadState = DownloadState.STATE_NONE;
				MessageUtils.makeShortToast(mContext, mContext.getString(R.string.file_manager_file_not_exist_tips));
				initDownloadState();
			} else
				ApkTools.installApplication(mContext, file);

			break;

		// 已安装
		case DownloadState.STATE_INSTALLED:
			showOpenAppTipDialog();
			break;

		// 取消和未下载
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_NONE:
			if (!nonSafe())
				startDownloadApp();

			break;
		}
	}

	/**
	 * 下载软件
	 */
	private void startDownloadApp() {
		AppMarketUtil.startDownload(mContext, mAppItem);
		mDownloadState = mAppItem.getDownloadState();
		// 刷新右上角下载图标的状态
		im_into_download.invalidate();
		initDownloadState();
	}

	/**
	 * 安全检测
	 * 
	 * @return
	 */
	private boolean nonSafe() {
		AppMarketAppSafeInfo safeInfo = mDetailItem.getSafeInfo();
		// 病毒项
		AppMarketAppSafeInfo.SafeItem scanItem = safeInfo.getmScanProvider();
		// 内嵌广告项
		AppMarketAppSafeInfo.SafeItem adItem = safeInfo.getmAdvertisement();
		// 隐私项
		AppMarketAppSafeInfo.SafeItem privacyItem = safeInfo.getmPrivacy();
		StringBuffer content = new StringBuffer();
		if (scanItem != null && scanItem.state != 0 && scanItem.state != 1)
			content.append(scanItem.title).append(",");
		if (adItem != null && adItem.state != 0 && adItem.state != 1)
			content.append(adItem.title).append(",");
		if (privacyItem != null && privacyItem.state != 0 && privacyItem.state != 1)
			content.append(privacyItem.title).append(",");

		if (content.length() == 0)
			return false;
		else {
			content.deleteCharAt(content.length() - 1);
			String dialogContent = String.format(mContext.getString(R.string.app_market_non_safe_content_tip), content.toString());
			CommonDialog alertd = ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), dialogContent, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					startDownloadApp();
					dialog.dismiss();
				}
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			alertd.show();

			return true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSlidingView != null)
			mSlidingView.invalidate();
		if (mDownloadService.isBind()) {
			AppMarketUtil.setDownloadState(mContext, mAppItem);
			initDownloadState();
			im_into_download.invalidate();
		}

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	private void unRegistReceiver(BroadcastReceiver receiver) {
		try {
			if (receiver != null)
				unregisterReceiver(receiver);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {

		// 注销下载状态监听
		unRegistReceiver(mDownloadReceiver);
		// 注销新应用安装的广播监听
		unRegistReceiver(mNewAppInstallReceiver);
		// 注销应用静默安装的广播监听
		unRegistReceiver(mSilentInstallReceiver);

		try {
			if (mSlidingView != null)
				mSlidingView.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	/** -内部类----------------------------------------------------------------- */

	/**
	 * 获取应用列表的任务
	 */
	private class DataLoadTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			mLoadingView.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				if (!TelephoneUtil.isNetworkAvailable(mContext))
					throw new IOException("No availabled connection!");
				// mDetailUrl="http://bbx2.sj.91.com/soft/phone/detail.aspx?act=226&resId=4009634&title=%e5%a4%a9%e7%8c%ab%ef%bc%88%e6%b7%98%e5%ae%9d%e5%95%86%e5%9f%8e%ef%bc%89&pact=251&placeId=222258149&resType=1&mt=4&sv=3.5&pid=6&osv=4.0.3&iv=2&pos=2";
				// 加载详情数据
				mDetailItem = mAppMarketUtil.getAppDetail(mDetailUrl);

				return EVENT_LOAD_DATA_SUCCESS;
			} catch (Exception e) {
				Log.e(Global.TAG, "load apps failed:" + e.toString());
				return getExceptionType(e);
			}
		}

		@Override
		protected void onCancelled() {
			mDetailItem = null;
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {

			mLoadingView.setVisibility(View.GONE);

			switch (result) {

			case EVENT_LOAD_DATA_SUCCESS:
				if (mDetailItem != null) {
					sv_detail_container.setVisibility(View.VISIBLE);
					AppMarketUtil.setDownloadState(mContext, mAppItem);
					initData();
				} else
					mNoDataView.setVisibility(View.VISIBLE);

				break;
			case EVENT_NET_WORK_ERROR:
				mNetworkErrorView.setVisibility(View.VISIBLE);
				break;
			case EVENT_ERROR:
				mNoDataView.setVisibility(View.VISIBLE);
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
	 * 文字展开收缩点击事件
	 */
	private class DetailTextExpandClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int lineCount = tv_detail_descrption.getLineCount();
			if (lineCount <= TEXT_MIN_LINE_COUNT) {
				im_detail_expand.setVisibility(View.GONE);
				return;
			}
			if (!mIsExpand)
				tv_detail_descrption.setMaxLines(Integer.MAX_VALUE);
			else
				tv_detail_descrption.setMaxLines(TEXT_MIN_LINE_COUNT);
			mIsExpand = !mIsExpand;
			if (mIsExpand) {
				im_detail_expand.setImageResource(R.drawable.detail_shrink);
				tv_download_bottom.setVisibility(View.VISIBLE);
			} else {
				im_detail_expand.setImageResource(R.drawable.detail_expand);
				tv_download_bottom.setVisibility(View.INVISIBLE);
			}
		}

	}// end class DetailTextExpandClickListener

	/**
	 * 下载状态监听
	 */
	private class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mAppItem == null)
				return;
			String key = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION);
			if (key != null && key.equals(mAppItem.getKey())) {
				AppMarketUtil.setDownloadState(mContext, mAppItem);
				// 重刷下载状态
				initDownloadState();
			}

		}

	}// end DownloadReceiver

	/**
	 * 新应用安装监听
	 */
	private class NewAppInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context content, Intent intent) {
			if (null == intent || mAppItem == null)
				return;
			final String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName != null && packageName.equals(mAppItem.getPackageName())) {
				AppMarketUtil.setDownloadState(mContext, mAppItem);
				initDownloadState();
			}

		}

	}// end NewAppInstallReceiver

	/**
	 * 静默安装的监听器
	 */
	private class SilentInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getStringExtra(ApkInstaller.EXTRA_APP_INSTALL_PACAKGE_NAME);
			if (!TextUtils.isEmpty(packageName) && packageName.equals(mAppItem.getPackageName())) {
				AppMarketUtil.setDownloadState(mContext, mAppItem);
				initDownloadState();
			}
		}

	}// end SilentInstallReceiver

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
						loadDataFromServer();

					} else {
						mLoadingView.setVisibility(View.GONE);
						mNoDataView.setVisibility(View.VISIBLE);
						// 绑定失败，当是数据加载失败了，因为后续的操作无法正常进行
					}
				}
			});

		}

	}// end DownloadServiceBindCallBack
}
