package com.nd.hilauncherdev.lib.theme.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.kitset.analytics.OtherAnalytics;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.lib.theme.HiLauncherExDownTaskManagerActivity;
import com.nd.hilauncherdev.lib.theme.NdLauncherExDialogDefaultImp;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi.NdLauncherExDialogCallback;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.down.DownloadService;
import com.nd.hilauncherdev.lib.theme.down.DownloadTask;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;
import com.nd.hilauncherdev.lib.theme.util.DigestUtils;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;
import com.nd.hilauncherdev.lib.theme.util.RequestParmUtil;
import com.nd.hilauncherdev.lib.theme.util.SUtil;
import com.nd.hilauncherdev.lib.theme.util.SharedPrefsUtil;
import com.nd.hilauncherdev.lib.theme.util.TelephoneUtil;


/**
 * 皮肤主题webView
 * @author cfb
 *
 */
public class HiLauncherExThemeShinView  extends FrameLayout {
	
	private static final String TAG = "com.nd.hilauncherdev.lib.theme.HiLauncherExThemeShinView";
	
	private Context ctx;
	private WebView webContent;
	private ProgressBar webProgressBar;
	private View webProgressBarFl;
	private Button downtask;
	private View neterrorLayout, refreshView;
	
	public NdLauncherExDialogCallback ndLauncherExDialogCallback = null;
	
	/**插件皮肤*/
	private String downType_Skin = "1";
	/**整套主题*/
	private String downType_Theme = "2";
	
	/**主题id*/
	private static final String FIELD_NAME_TID = "tid";
	
	/**插件皮肤id*/
	private static final String FIELD_NAME_WID = "wid";
	
	/**下载类型:区分是 1插件皮肤或者 2整套主题*/
	private static final String FIELD_NAME_DTYPE = "dtype";
	
	/**插件皮肤类型(指接入的软件)*/
	private static final String FIELD_NAME_WTYPE = "wtype";
	
	/**插件皮肤名称*/
	private static final String FIELD_NAME_RES_NAME = "name";	
	
	private Handler handler = new Handler();
	
	private void addView(int paramInt) {
		LayoutInflater.from( ctx ).inflate(paramInt, this);
	}
	
	public HiLauncherExThemeShinView(Context context) {
		super(context);
		ctx = context;
	}
	
	public void initView(){
		addView(R.layout.nd_hilauncher_theme_main);
		setupViews();
	}
	
	/**
	 * 设置创建对话框回调
	 * @param ndLauncherExDialogCallback
	 */
	public void setNdLauncherExDialogCallback(NdLauncherExDialogCallback ndLauncherExDialogCallback){
		this.ndLauncherExDialogCallback = ndLauncherExDialogCallback;
	}
	
	/**
	 * View资源释放
	 */
	public void destroyView(){
		try {
			webContent.stopLoading();
			webContent.freeMemory();
			webContent.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//TODO 清空下载通知栏任务及关闭下载service
		Intent intent = new Intent(ctx, DownloadService.class);
		ctx.stopService(intent);
	}
	
	private void setupViews() {
		
        //校验下载队列
		doLoadInitData();
		
		//网络错误View设置
		neterrorLayout = findViewById(R.id.neterror_layout);
		refreshView = findViewById(R.id.ndtheme_net_refresh_btn);
		refreshView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadWebView();
			}
		});
		
		//下载管理入口
		downtask = (Button) findViewById(R.id.downtask);
        downtask.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ctx, HiLauncherExDownTaskManagerActivity.class);
				ctx.startActivity(intent);
			}
		});

        //WebView设置
		webProgressBarFl = findViewById(R.id.web_progress_bar_fl);
		webProgressBar = (ProgressBar) findViewById(R.id.web_progress_bar);
		webContent = (WebView) findViewById(R.id.theme_list_content);
		initWebView();
	}
	
	/**
	 * 重新加载网页
	 */
	private void loadWebView(){
		
		webContent.setVisibility(View.VISIBLE);		
		neterrorLayout.setVisibility(View.GONE);
		webContent.loadUrl(HiLauncherThemeGlobal.HOST+"/Default.aspx?Mt=4&Tfv=40000&Imei="+TelephoneUtil.getIMEI(ctx)+"&Wtype="+NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_VALUE);
	}
	
	private void initWebView(){
		
		loadWebView();
		
		// webContent.setBackgroundColor(0);
		WebSettings settings = webContent.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLightTouchEnabled(true);
		settings.setPluginsEnabled(true);
		settings.setPluginState(PluginState.ON);

		//Web加载进度更新
		webContent.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				webProgressBar.setProgress(progress);
				if (progress >= 100) {
					webProgressBar.setVisibility(View.GONE);
					webProgressBarFl.setVisibility(View.GONE);
				} else {
					webProgressBarFl.setVisibility(View.VISIBLE);
					webProgressBar.setVisibility(View.VISIBLE);
				}
			}
		});

		webContent.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				handler.post(new Runnable() {

					public void run() {
						webContent.setVisibility(View.GONE);		
						neterrorLayout.setVisibility(View.VISIBLE);
					}
				});
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {

				handler.post(new Runnable() {

					public void run() {

						Map<String, String> mapRequest = RequestParmUtil.URLRequest(url);
						final String tidRequestValue = mapRequest.get(FIELD_NAME_TID);
						final String widRequestValue = mapRequest.get(FIELD_NAME_WID);
						final String dtypeRequestValue = mapRequest.get(FIELD_NAME_DTYPE);
						final String wtypeRequestValue = mapRequest.get(FIELD_NAME_WTYPE);
						String tmpResNameRequestValue = mapRequest.get(FIELD_NAME_RES_NAME);
						try {
							tmpResNameRequestValue = java.net.URLDecoder.decode(tmpResNameRequestValue,"UTF-8");							
						} catch (Exception e) {
							e.printStackTrace();
						}

						final String resNameRequestValue = tmpResNameRequestValue;
						
						if (downType_Skin.equals(dtypeRequestValue)) {
							downThemeSkin(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue);
						}

						if (downType_Theme.equals(dtypeRequestValue)) {
							// 判断是否安装过桌面
							if ( !ApkTools.isInstallAPK(ctx, HiLauncherThemeGlobal.THEME_MANAGE_PACKAGE_NAME) ){
								
								//提示未安装桌面是否下载安装
								final DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										downLauncherTheme(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue);
									}
								};
								
								final DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
									}
								};
								if (ndLauncherExDialogCallback==null){
									Dialog dialog = (new NdLauncherExDialogDefaultImp()).createThemeDialog(getContext(), -1, "提示", "全套主题需要下载91桌面,确定开始下载.", "确定", "取消", positive, negative);
									dialog.show();
								}else{
									Dialog dialog = ndLauncherExDialogCallback.createThemeDialog(getContext(), -1, "提示", "全套主题需要下载91桌面,确定开始下载.", "确定", "取消", positive, negative);
									dialog.show();
								}
							}else{
								downThemeAPT(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue);
							}
						}
					}
				});
				return true;
			}

		});   
	}
	
	/**
	 * 初始化下载队列
	 */
	private void doLoadInitData() {
		
		//检测数据库中的下载队列数据,将状态为下载中的任务修改为暂停
		try {
			ArrayList<DowningTaskItem> downIngList = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskByState(DowningTaskItem.DownState_Downing);
			for (int i = 0; i < downIngList.size(); i++) {
				DowningTaskItem downingTaskItem = downIngList.get(i);
				if ( !DownloadService.inDownList(downingTaskItem.downUrl) ){
					downingTaskItem.state = DowningTaskItem.DownState_Pause;
					ThemeLibLocalAccessor.getInstance(ctx).updateDowningTaskItem(downingTaskItem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}						
	}
	
	/**
	 *  下载APT主题
	 * @param tidRequestValue
	 * @param widRequestValue
	 * @param wtypeRequestValue
	 * @param dtypeRequestValue
	 * @param resNameRequestValue
	 */
	private void downThemeAPT(String tidRequestValue, String widRequestValue, String wtypeRequestValue, String dtypeRequestValue, String resNameRequestValue){
		ThemeItem mThemeItem = new ThemeItem();
		String buildDownloadUrl = buildDownloadParam(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue);
		Log.e(TAG, "==" + buildDownloadUrl);
		mThemeItem.setItemType(ThemeItem.ITEM_TYPE_THEME);
		mThemeItem.setDownloadUrl(buildDownloadUrl);
		mThemeItem.setLargePostersUrl("");
		mThemeItem.setName(resNameRequestValue+" 主题");
		mThemeItem.setId(tidRequestValue + ("" + mThemeItem.getItemType()));
		DownloadTask manager = new DownloadTask();
		manager.downloadTheme(ctx, mThemeItem);
	}
	
	/**
	 * 下载91桌面及主题
	 * @param tidRequestValue
	 * @param widRequestValue
	 * @param wtypeRequestValue
	 * @param dtypeRequestValue
	 * @param resNameRequestValue
	 */
	private void downLauncherTheme(String tidRequestValue, String widRequestValue, String wtypeRequestValue, String dtypeRequestValue, String resNameRequestValue){
		
		//判断是否已经下载完成，完成则直接安装
		try{
			DowningTaskItem hiDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem("91" + ThemeItem.ITEM_TYPE_LAUNCHER);
			if (hiDowningTaskItem.state==DowningTaskItem.DownState_Finish){
				//91Launcher Apk filePath
				String filePath = hiDowningTaskItem.tmpFilePath;
				if (filePath!=null) {
                	File launcherApk=new File(filePath);
                	if(launcherApk.exists()){
                		ApkTools.installApplication(ctx, launcherApk);
                	}
				}
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//先下载主题包
		downThemeAPT(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue);
		
		//在线获取91助手的下载地址
		String downloadUrl=OtherAnalytics.get91LauncherAppDownloadUrl(ctx);
		//未获取到采用默认地址
		if(SUtil.isEmpty(downloadUrl))
			downloadUrl=HiLauncherThemeGlobal.assit_app_download_url;
		downloadUrl = HiLauncherThemeGlobal.assit_app_download_url;
		//添加桌面的下载任务
		ThemeItem mThemeItem = new ThemeItem();
		mThemeItem.setItemType(ThemeItem.ITEM_TYPE_LAUNCHER);
		mThemeItem.setDownloadUrl(downloadUrl);
		mThemeItem.setLargePostersUrl("");
		mThemeItem.setName("91桌面");
		mThemeItem.setId("91" + ThemeItem.ITEM_TYPE_LAUNCHER);
		DownloadTask manager = new DownloadTask();
		manager.downloadTheme(ctx, mThemeItem);
		
		//记录要自动应用的主题
		SharedPrefsUtil.getInstance(ctx).setString(SharedPrefsUtil.KEY_AUTO_APPLY_THEMEID, tidRequestValue+(""+ThemeItem.ITEM_TYPE_THEME));
	}
	
	/**
	 * 下载第三方皮肤
	 * @param tidRequestValue
	 * @param widRequestValue
	 * @param wtypeRequestValue
	 * @param dtypeRequestValue
	 * @param resNameRequestValue
	 */
	private void downThemeSkin(String tidRequestValue, String widRequestValue, String wtypeRequestValue, String dtypeRequestValue, String resNameRequestValue){
		ThemeItem mThemeItem = new ThemeItem();
		String buildDownloadUrl = buildDownloadParam(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue);
		Log.e(TAG, "==" + buildDownloadUrl);
		mThemeItem.setItemType(ThemeItem.ITEM_TYPE_SKIN);
		mThemeItem.setDownloadUrl(buildDownloadUrl);
		mThemeItem.setLargePostersUrl("");
		mThemeItem.setName(resNameRequestValue+" 皮肤");
		mThemeItem.setId(tidRequestValue + ("" + mThemeItem.getItemType()));
		DownloadTask manager = new DownloadTask();
		manager.downloadTheme(ctx, mThemeItem);
	}
	
	/**
	 * 构造和服务器约定的参数
	 * @param tid 主题id
	 * @param wid 插件皮肤id
	 * @param wtype 插件皮肤类型
	 * @param dtype 下载类型
	 * @return
	 */
	private String buildDownloadParam(String tid, String wid, String wtype, String dtype) {
		StringBuffer sb = new StringBuffer(HiLauncherThemeGlobal.HOST+"/Download.aspx?Mt=4&Tfv=40000&tid="+tid+"&Wid="+wid+"&Dtype="+dtype+"&Wtype="+wtype);
		int index = SUtil.getRandom(9);
		String imei = TelephoneUtil.getIMEI(ctx);
		String imsi = TelephoneUtil.getIMSI(ctx);
		String md5Source = (tid+"") + (wid+"") + (wtype+"") + (dtype+"") + imei + imsi + index + SUtil.getMD5Key(index);
		sb.append("&ts=").append(index).append("&sign=").append(DigestUtils.md5Hex(md5Source)).append("&imei=").append(imei).append("&imsi=").append(imsi);
		return sb.toString();
	}
}
