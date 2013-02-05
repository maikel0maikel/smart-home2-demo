package com.nd.hilauncherdev.lib.theme.view;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.kitset.analytics.OtherAnalytics;
import com.nd.hilauncherdev.lib.theme.HiLauncherExDownTaskManagerActivity;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.down.DownloadService;
import com.nd.hilauncherdev.lib.theme.down.DownloadTaskManager;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;
import com.nd.hilauncherdev.lib.theme.util.DigestUtils;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;
import com.nd.hilauncherdev.lib.theme.util.RequestParmUtil;
import com.nd.hilauncherdev.lib.theme.util.SUtil;
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
	
	/**插件预览图*/
	private static final String FIELD_NAME_IMAGE_NAME = "prevurl"; 
	
	/**在线获取91桌面的下载地址*/
	private static String downloadUrl = "";
			
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
		
		Thread t = new Thread() {
            @Override
            public void run() {
            	try{
            		OtherAnalytics.submitCalendarThemeOpen( ctx );
            	}catch (Exception e) {
					e.printStackTrace();
				}
            }
        };
        t.start();	
	}
	
	/**
	 * View资源释放
	 */
	public void destroyView(){
		try {
			if (webContent!=null){
				webContent.stopLoading();
				webContent.freeMemory();
				webContent.destroy();
			}
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
		if (neterrorLayout!=null){
			refreshView = neterrorLayout.findViewById(R.id.ndtheme_net_refresh_btn);
			if (refreshView!=null){
				refreshView.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						loadWebView();
					}
				});
			}
		}
		
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
		if (neterrorLayout!=null){
			neterrorLayout.setVisibility(View.GONE);
		}
		webContent.loadUrl(HiLauncherThemeGlobal.HOST+"/Default.aspx?Mt=4&Tfv=40000&Imei="+TelephoneUtil.getIMEI(ctx)+"&Wtype="+NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_ID_VALUE);
	}
	
	private void initWebView(){
		
		loadWebView();
		
		// webContent.setBackgroundColor(0);
		WebSettings settings = webContent.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLightTouchEnabled(true);
		settings.setPluginsEnabled(true);
		//settings.setPluginState(PluginState.ON);////只支持2.2以上的版本 

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
						if (neterrorLayout!=null){
							neterrorLayout.setVisibility(View.VISIBLE);
						}
					}
				});
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {

				final Map<String, String> mapRequest = RequestParmUtil.URLRequest(url);
				final String tidRequestValue = mapRequest.get(FIELD_NAME_TID);
				final String widRequestValue = mapRequest.get(FIELD_NAME_WID);
				final String dtypeRequestValue = mapRequest.get(FIELD_NAME_DTYPE);
				final String wtypeRequestValue = mapRequest.get(FIELD_NAME_WTYPE);
				final String prevurlRequestValue = mapRequest.get(FIELD_NAME_IMAGE_NAME);
				
				if( !downType_Skin.equals(dtypeRequestValue) && !downType_Theme.equals(dtypeRequestValue) ){
					view.loadUrl(url); 
					return true;
				}
				
				handler.post(new Runnable() {

					public void run() {

						String tmpResNameRequestValue = mapRequest.get(FIELD_NAME_RES_NAME);
						
						try {
							tmpResNameRequestValue = java.net.URLDecoder.decode(tmpResNameRequestValue,"UTF-8");							
						} catch (Exception e) {
							e.printStackTrace();
						}

						final String resNameRequestValue = tmpResNameRequestValue;
						
						if (downType_Skin.equals(dtypeRequestValue)) {
							downThemeSkin(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue, prevurlRequestValue);
						}

						if (downType_Theme.equals(dtypeRequestValue)) {
							downThemeAPT(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue, resNameRequestValue, prevurlRequestValue);
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
				//如果进程没有退出,这个判断会无效
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
	private void downThemeAPT(String tidRequestValue, String widRequestValue, String wtypeRequestValue, String dtypeRequestValue, String resNameRequestValue, String prevurlRequestValue){
		
		String themeID = tidRequestValue + ("" + ThemeItem.ITEM_TYPE_THEME);
		if ( hasDownloaded(themeID) ){
			return;
		}
		
		ThemeItem mThemeItem = new ThemeItem();
		String buildDownloadUrl = buildDownloadParam(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue);
		mThemeItem.setItemType(ThemeItem.ITEM_TYPE_THEME);
		mThemeItem.setDownloadUrl(buildDownloadUrl);
		mThemeItem.setLargePostersUrl(""+prevurlRequestValue);
		mThemeItem.setName(resNameRequestValue+" 主题");
		mThemeItem.setId(themeID);
		DownloadTaskManager manager = new DownloadTaskManager();
		manager.downloadTheme(ctx, mThemeItem);
	}
	
	/**
	 * 下载第三方皮肤
	 * @param tidRequestValue
	 * @param widRequestValue
	 * @param wtypeRequestValue
	 * @param dtypeRequestValue
	 * @param resNameRequestValue
	 */
	private void downThemeSkin(String tidRequestValue, String widRequestValue, String wtypeRequestValue, String dtypeRequestValue, String resNameRequestValue, String prevurlRequestValue){
		
		String themeID = tidRequestValue + ("" + ThemeItem.ITEM_TYPE_SKIN);
		if ( hasDownloaded(themeID) ){
			return;
		}
		
		ThemeItem mThemeItem = new ThemeItem();
		String buildDownloadUrl = buildDownloadParam(tidRequestValue, widRequestValue, wtypeRequestValue, dtypeRequestValue);
		mThemeItem.setItemType(ThemeItem.ITEM_TYPE_SKIN);
		mThemeItem.setDownloadUrl(buildDownloadUrl);
		mThemeItem.setLargePostersUrl(""+prevurlRequestValue);
		mThemeItem.setName(resNameRequestValue+" 皮肤");
		mThemeItem.setId(themeID);
		DownloadTaskManager manager = new DownloadTaskManager();
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
		int index = 5;//SUtil.getRandom(9);
		String imei = TelephoneUtil.getIMEI(ctx);
		String imsi = TelephoneUtil.getIMSI(ctx);
		String md5Source = (tid+"") + (wid+"") + (wtype+"") + (dtype+"") + imei + imsi + index + SUtil.getMD5Key(index);
		sb.append("&ts=").append(index).append("&sign=").append(DigestUtils.md5Hex(md5Source)).append("&imei=").append(imei).append("&imsi=").append(imsi);
		return sb.toString();
	}
	
	/**
	 * 检查本地是否下载过此主题
	 * @return
	 */
	private boolean hasDownloaded(String themeID) {
		
		try{
			DowningTaskItem newDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(themeID);
		
			if (newDowningTaskItem!=null){ 
				if (newDowningTaskItem.state==DowningTaskItem.DownState_Finish){
		    		ThemeLauncherExAPI.showThemeApplyDialog(ctx, newDowningTaskItem);
		    		return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	protected void onAttachedToWindow() {
		
		super.onAttachedToWindow();
		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
		this.requestFocusFromTouch();
	};
}
