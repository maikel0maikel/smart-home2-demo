package com.nd.hilauncherdev.appmarket;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.kitset.util.ApkInstaller;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

/**
 * 下载进度条布局
 * 
 * @author zhuchenghua
 * 
 */
public class AppMarketDownloadBar extends LinearLayout implements View.OnClickListener {

	private Context mContext = getContext();
	
	private final String TAG = "AppMarketDownloadBar";

	/** 下载进度显示区 */
	private View rl_download_progress_area;

	/** 按钮区 */
	private View ll_download_btn_area;

	/** 下载进度文字View */
	private TextView tv_process;

	/** 下载状态文字View */
	private TextView tv_download_tip;

	/** 下载进度条 */
	private ProgressBar pb_proccess;

	/** 暂停按钮 */
	private ImageView im_pause;

	/** 取消按钮 */
	private ImageView im_cancel;

	/**
	 * 下载状态监听
	 */
	private DownloadReceiver mDownloadReceiver;

	/**
	 * 正在安装监听
	 */
	private BroadcastReceiver mSilentInstallReceiver;

	private AppMarketItem mItem;

	private TextView tipView;

	/** 操作回调 */
	private CommonCallBack<Void> mActionCallBack;

	private DownloadServerServiceConnection mDownloadServcie;

	public AppMarketDownloadBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketDownloadBar(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		rl_download_progress_area = findViewById(R.id.rl_download_progress_area);
		ll_download_btn_area = findViewById(R.id.ll_download_btn_area);
		// 下载进度文字View
		tv_process = (TextView) findViewById(R.id.tv_process);
		// 下载状态文字View
		tv_download_tip = (TextView) findViewById(R.id.tv_download_tip);
		// 下载进度条
		pb_proccess = (ProgressBar) findViewById(R.id.pb_proccess);
		/** 暂停按钮 */
		im_pause = (ImageView) findViewById(R.id.im_pause);
		/** 取消按钮 */
		im_cancel = (ImageView) findViewById(R.id.im_cancel);

		mDownloadServcie = new DownloadServerServiceConnection(mContext);

		initListener();
	}

	/**
	 * 初始化按钮监听
	 */
	private void initListener() {
		im_pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mItem == null)
					return;

				if (mItem.getDownloadState() == DownloadState.STATE_DOWNLOADING || mItem.getDownloadState() == DownloadState.STATE_WAITING) {
					// 正在下载状态，点击暂停
					boolean b = mDownloadServcie.pause(mItem.getKey());
					if (b) {
						mItem.setDownloadState(DownloadState.STATE_PAUSE);
						refrashState();
					}

				} else if (mItem.getDownloadState() == DownloadState.STATE_PAUSE) {
					// 暂停状态，点击继续下载
					AppMarketUtil.startDownload(mContext, mItem);
					refrashState();
				}

				if (mActionCallBack != null)
					mActionCallBack.invoke();
			}
		});

		im_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mItem == null)
					return;
				final String msg = mContext.getString(R.string.download_delete_msg, mItem.getTitle());
				ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.download_delete_title), msg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 确认取消下载
						boolean b = mDownloadServcie.cancel(mItem.getKey());
						if (b) {
							mItem.setDownloadState(DownloadState.STATE_CANCLE);
							refrashState();
						}
						// 回调操作
						if (mActionCallBack != null)
							mActionCallBack.invoke();
						dialog.dismiss();
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				}).show();
			}
		});

		setOnClickListener(this);
	}

	/**
	 * 初始化数据与状态
	 * 
	 * @param item
	 */
	public void init(AppMarketItem item) {
		mItem = item;
		refrashState();
	}

	/**
	 * 刷新状态
	 */
	public void refrashState() {
		if (mItem != null) {
			if (tipView == null) {
				tipView = new TextView(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				tipView.setLayoutParams(params);
				tipView.setTextColor(Color.WHITE);
				tipView.setTextSize(16);
			}

			// 移除文本View
			removeView(tipView);
			// 先显示进度界面
			showDownloadFace(true);

			switch (mItem.getDownloadState()) {
			case DownloadState.STATE_CANCLE:
			case DownloadState.STATE_NONE:
				mItem.setDownloadProccess(0);
				pb_proccess.setProgress(mItem.getDownloadProccess());
				setVisibility(View.GONE);
				break;
			// 正在下载、暂停、等待中
			case DownloadState.STATE_DOWNLOADING:
				tv_download_tip.setText(mContext.getString(R.string.theme_shop_v2_theme_detail_downprocessing_txt));
				tv_process.setText(mItem.getDownloadProccess() + "%");
				pb_proccess.setProgress(mItem.getDownloadProccess());
				break;
			case DownloadState.STATE_PAUSE:
				tv_download_tip.setText(mContext.getString(R.string.app_market_app_download_pause));
				tv_process.setText(mItem.getDownloadProccess() + "%");
				pb_proccess.setProgress(mItem.getDownloadProccess());
				break;
			case DownloadState.STATE_WAITING:
				tv_download_tip.setText(mContext.getString(R.string.app_market_app_download_wait));
				tv_process.setText(mItem.getDownloadProccess() + "%");
				pb_proccess.setProgress(mItem.getDownloadProccess());
				break;
			// 已安装
			case DownloadState.STATE_INSTALLED:
				// 关闭进度条界面
				showDownloadFace(false);
				// 显示已安装
				tipView.setText(mContext.getString(R.string.app_market_app_installed));
				addView(tipView);
				break;
			// 已下载
			case DownloadState.STATE_FINISHED:
				showDownloadFace(false);
				// 显示已下载
				tipView.setText(mContext.getString(R.string.app_market_app_downloaded));
				addView(tipView);
				break;
			// 正在安装
			case ApkInstaller.INSTALL_STATE_INSTALLING:
				showDownloadFace(false);
				// 显示正在安装
				tipView.setText(mContext.getString(R.string.app_market_installing));
				addView(tipView);

				break;
			}

			if (mItem.getDownloadState() == DownloadState.STATE_DOWNLOADING || mItem.getDownloadState() == DownloadState.STATE_WAITING) {
				// 正在下载、等待中，显示暂停图标
				im_pause.setImageResource(R.drawable.app_market_download_icon_pause_selector);
			} else if (mItem.getDownloadState() == DownloadState.STATE_PAUSE) {
				// 暂停状态，显示开始下载图标
				im_pause.setImageResource(R.drawable.app_market_download_icon_start_selector);
			}
		}
	}

	/**
	 * 显示下载进度界面
	 * 
	 * @param isShow
	 *            true显示，false隐藏
	 */
	private void showDownloadFace(boolean isShow) {
		if (isShow) {
			rl_download_progress_area.setVisibility(View.VISIBLE);
			ll_download_btn_area.setVisibility(View.VISIBLE);
		} else {
			rl_download_progress_area.setVisibility(View.GONE);
			ll_download_btn_area.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置操作回调
	 * 
	 * @param callBack
	 */
	public void setActionCallBack(CommonCallBack<Void> callBack) {
		mActionCallBack = callBack;
	}

	/**
	 * 注册广播监听
	 */
	private void registReceiver() {
		try {

			// 注册下载状态监听
			if (mDownloadReceiver == null) {
				IntentFilter iFilter2 = new IntentFilter(DownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
				mDownloadReceiver = new DownloadReceiver();
				mContext.registerReceiver(mDownloadReceiver, iFilter2);
			}

			// 注册静默安装监听
			if (mSilentInstallReceiver == null) {
				IntentFilter iFilter3 = new IntentFilter(ApkInstaller.RECEIVER_APP_SILENT_INSTALL);
				mSilentInstallReceiver = new SilentInstallReceiver();
				mContext.registerReceiver(mSilentInstallReceiver, iFilter3);
			}

		} catch (Exception e) {
		}

	}

	/**
	 * 注销广播监听
	 */
	private void unRegistReceiver() {

		try {
			if (mDownloadReceiver != null)
				mContext.unregisterReceiver(mDownloadReceiver);
			mDownloadReceiver = null;
			if (mSilentInstallReceiver != null)
				mContext.unregisterReceiver(mSilentInstallReceiver);
			mSilentInstallReceiver = null;

		} catch (Exception e) {
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		registReceiver();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		unRegistReceiver();
	}

	@Override
	public void onClick(View v) {

		if (mItem == null)
			setVisibility(View.GONE);
		else {
			switch (mItem.getDownloadState()) {
			// 已安装,打开应用
			case DownloadState.STATE_INSTALLED:
				AppMarketUtil.showOpenAppTipDialog(mItem, mContext);
				break;
			// 已下载,安装应用
			case DownloadState.STATE_FINISHED:
				File file = new File(mItem.getApkFilePath());
				if (!file.exists()) { // 文件不存，改状态为未下载的
					mItem.setDownloadState(DownloadState.STATE_NONE);
					refrashState();
					MessageUtils.makeShortToast(mContext, mContext.getString(R.string.file_manager_file_not_exist_tips));
				} else
					ApkTools.installApplication(mContext, file);

				break;
			}
		}

	}

	/**
	 * 下载状态监听
	 */
	private class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				String key = intent.getStringExtra(DownloadBroadcastExtra.EXTRA_IDENTIFICATION);
				int downloadState = intent.getIntExtra(DownloadBroadcastExtra.EXTRA_STATE, DownloadState.STATE_NONE);
				if (key != null) {
					if (mItem != null && key.equals(mItem.getKey())) {
						if (downloadState == DownloadState.STATE_DOWNLOADING) { // 下正下载，更新进度
							int process = intent.getIntExtra(DownloadBroadcastExtra.EXTRA_PROGRESS, 0);
							mItem.setDownloadProccess(process);
							mItem.setDownloadState(downloadState);
						} else {
							AppMarketUtil.setDownloadState(mContext, mItem);
						}

						// 刷新状态
						refrashState();
					}
				}

			} catch (Exception e) {
				Log.w(Global.TAG, "DownloadReceiver.onReceive exception:" + e.toString());
			}

		}

	}// end DownloadReceiver

	/**
	 * 静默安装的监听器
	 */
	private class SilentInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String packageName = intent.getStringExtra(ApkInstaller.EXTRA_APP_INSTALL_PACAKGE_NAME);
			if (TextUtils.isEmpty(packageName))
				return;

			try {

				if (mItem != null && packageName.equals(mItem.getPackageName())) {
					AppMarketUtil.setDownloadState(mContext, mItem);

					// 刷新状态
					refrashState();

				}// end if

			} catch (Exception e) {

				Log.w(TAG, "SilentInstallReceiver expose error!", e);
			}

		}// end onReceive

	}// end SilentInstallReceiver
}
