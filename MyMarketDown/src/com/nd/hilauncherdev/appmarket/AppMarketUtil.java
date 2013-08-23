package com.nd.hilauncherdev.appmarket;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.framework.httplib.HttpCommon;
import com.nd.hilauncherdev.framework.view.dialog.CommonDialog;
import com.nd.hilauncherdev.json.JSONArray;
import com.nd.hilauncherdev.json.JSONException;
import com.nd.hilauncherdev.json.JSONObject;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.ApkInstaller;
import com.nd.hilauncherdev.kitset.util.BitmapUtils;
import com.nd.hilauncherdev.kitset.util.ChannelUtil;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.MessageUtils;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.CommonCallBack;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

public class AppMarketUtil {

	private String TAG="com.nd.hilauncherdev.appmarket.AppMarketUtil";
	
	private Context mContext;
	
	/** 线程池 */
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	public static final String BASE_DOMAIN="http://bbx2.sj.91.com/";
	
	public static final String BASE_DOMAIN_BBX_DATA="http://bbxdata.sj.91.com/";
		
	//public static final String BASE_DOMAIN="http://192.168.254.46:8888/";
	/** 一键装机软件列表地址 */
	public static final String BASE_APP_LIST_URL=BASE_DOMAIN+"softs.ashx?act=%1$s" + //操作标识
																		"&mt=%2$s" +   //平台标识android ,iphone
																		"&osv=%3$s" + //Android版本号
																		"&resType=%4$s"+ //资源类别
																		"&pid=%5$s" + //产品ID，桌面是6
																		"&imei=%6$s" + //IME号
																		"&sv=%7$s" + //客户端版本号
																		"&nt=%8$s"+ //网络类型
																		"&iv=%9$s"+ //接口版本
																		"&chl=%10$s"; //渠道ID
	

	/** 下载成功反馈的接口地址 */
	public static final String DOWNLOAD_SUCCESS_FEEDBACK_URL=BASE_DOMAIN_BBX_DATA+"stat.ashx?act=%1$s" +
																								"&resId=%2$s" +
																								"&resType=%3$s" +
																								"&mt=%4$s" +
																								"&statType=%5$s" +
																								"&chl=%6$s" +
																								"&pid=%7$s" +
																								"&imei=%8$s" +
																								"&sv=%9$s" +
																								"&nt=%10$s" ;
	
	
	/**专题主界面的地址*/
	//public static final String SUBJECT_TEMP_DNS="http://192.168.254.47:9292/";
	//public static final String SUBJECT_TEMP_DNS="http://bbx2.sj.91.com/";
	public static final String SUBJECT_MAIN_URL=BASE_DOMAIN+"soft/DeskTop/tag.aspx?" +
																						"act=%1$s" + //操作标识
																						"&iv=%2$s" + //3:91桌面专题列表
																						"&mt=%3$s" + //平台标识android ,iphone
																						"&pid=%4$s" + //产品ID，桌面是6
																						"&imei=%5$s" + //IME号
																						"&sv=%6$s" + //客户端版本号
																						"&osv=%7$s" + //Android版本号
																						"&nt=%8$s"+ //网络类型
																						"&chl=%9$s"+ //渠道ID
																						"&take=1";//单专题标识
	
	/**专题详情页的act标识*/
	public static final String SUBJECT_DETAIL_FLAG="act=228";
		
//-------------------------------------可选参数值------------------------------------------//
	
	/** 软件/游戏软件 标签 */
	public static final String ACT_APPLIST="251";
	
	/** 客户端完成下载/安装统计通知(Act=101) 标签 */
	public static final String ACT_DOWNLOAD_SUCCESS="101";
	
	/** 应用详情数据(Act=101) 标签 */
	public static final String ACT_APP_DETAIL="226";
	
	/** 应用专题(Act=212) 标签 */
	public static final String ACT_SUBJECT_SOFT="212";
	
	/** 游戏专题(Act=217) 标签 */
	public static final String ACT_SUBJECT_GAME="217";
	
	/** 接口版本号1 */
	public static final String IV_1="1";
	
	/** 接口版本号2*/
	public static final String IV_2="2";
	
	/** 接口版本号3*/
	public static final String IV_3="3";
	
	/** 产品ID，熊猫桌面是6，与IPHONE 一样 */
	public static final String PID="6";
	
	/** 统计类型枚举值 4:完成下载 */
	public static final String FEED_BACK_TYPE_DOWNLOAD_SUCCESS="4";
	
	/** 下载成功反馈资源类型：1:软件 */
	public static final String FEED_BACK_RES_TYPE_SOFT="1";
	
	/** 下载成功反馈资源类型：9:游戏 */
	public static final String FEED_BACK_RES_TYPE_GAME="9";
	
	/** 软件列表资源类型：软件 */
	public static final String APP_LIST_RES_TYPE_SOFT="1";
	
	/** 软件列表资源类型：游戏 */
	public static final String APP_LIST_RES_TYPE_GAME="9";
	
//-----------------------------------end 可选参数值------------------------------------------//
	
//-------------------------------------固定参数值------------------------------------------//
	
	/** 平台标识，android是4 */
	public static final String MT="4"; 
	
	/** IMEI号 */
	private String imei;
	
	/** Android版本号 */
	private String osv;
	
	/** 91桌面的版本名称 */
	private String versionName;
	
	/** 网络类型 */
	private String netType;
	
	/** 渠道ID */
	private String channelId;
	
	/** 下载成功反馈的资源类型 */
	private String mFeedBackResType=FEED_BACK_RES_TYPE_SOFT;
//-----------------------------------end 固定参数值------------------------------------------//
	
	/** 一键装机、玩机的缓存目录 */
	public static final String MARKET_DIR=Global.BASE_DIR+"/market/";
	
	/** 图标缓存目录 */
	public static final String ICON_CACHE_DIR=MARKET_DIR+"icon/";
	
	/** 下载软件的目录 */
	public static final String PACKAGE_DOWNLOAD_DIR=MARKET_DIR+"downloads/";
	
	/** 界面显示数据项的最大个数 */
	public static int maxCount=0;
	
	/**图标缓存*/
	public static HashMap<String, SoftReference<Bitmap>> mIconCache;
	
	/**
	 * 客户端类别,一键装机、热门游戏
	 */
	private int mClientType=OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED;
	
	/**
	 * HTTP请求交互类
	 */
	private HttpCommon httpCommon ;
	
	public AppMarketUtil(Context context,int clientType){
		mContext=context;
		osv=TelephoneUtil.getFirmWareVersion();
		imei=TelephoneUtil.getIMEI(mContext);
		versionName=TelephoneUtil.getVersionName(mContext);
		netType=TelephoneUtil.getNT(mContext);
		channelId=ChannelUtil.getChannel(mContext); //渠道ID
		
		mClientType=clientType;
		if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED){ //应用软件
			mFeedBackResType=FEED_BACK_RES_TYPE_SOFT;
		}else if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY){//游戏
			mFeedBackResType=FEED_BACK_RES_TYPE_GAME;
		}
		httpCommon = new HttpCommon();
	}
	
	/**
	 * 图标缓存
	 * @return
	 */
	public static HashMap<String, SoftReference<Bitmap>> getIconCache()
	{
		if(mIconCache==null){
			mIconCache=new HashMap<String, SoftReference<Bitmap>>();
		}
		return mIconCache;
	}
	
	
	/**
	 * 获取应用列表URL
	 * @param page
	 * @return
	 */
	public String getTodayHotUrl()
	{
		return String.format(BASE_APP_LIST_URL, ACT_APPLIST,
												MT,
												osv,
												APP_LIST_RES_TYPE_SOFT,
												PID,
												imei,
												versionName,
												netType,
												IV_1,
												channelId);
	}
	
	/**
	 * 获取游戏列表URL
	 * @param page
	 * @return
	 */
	public String getGameUrl()
	{
		return String.format(BASE_APP_LIST_URL, ACT_APPLIST,
												MT,
												osv,
												APP_LIST_RES_TYPE_GAME,
												PID,
												imei,
												versionName,
												netType,
												IV_1,
												channelId);
	}
	
	/**
	 * 获取主专题的URL
	 * @return
	 */
	public String getMainSubjectUrl()
	{
		String act=null;
		//不的同客户端类型，专题不一样，如应用与游戏
		if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			act=ACT_SUBJECT_SOFT;
		else if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			act=ACT_SUBJECT_GAME;
		
		String url=String.format(SUBJECT_MAIN_URL, act,
									IV_3,
									MT,
									PID,
									imei,
									versionName,
									osv,
									netType,
									channelId);
		
		return url;
	}
	
	/**
	 * 获取精品推荐的列表地址
	 * @return
	 */
	public String getPrimeRecAppUrl()
	{
		String resType=null;
		//不的同客户端类型，专题不一样，如应用与游戏
		if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED)
			resType=APP_LIST_RES_TYPE_SOFT;
		else if(mClientType==OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_PLAY)
			resType=APP_LIST_RES_TYPE_GAME;
		
		return String.format(BASE_APP_LIST_URL, ACT_APPLIST,
												MT,
												osv,
												resType,
												PID,
												imei,
												versionName,
												netType,
												IV_2,
												channelId);
										
	}
	
	/**
	 * 反馈地址
	 * @param resId
	 * @param statType
	 * @return
	 */
	public String getFeedBackURL(String resId,String statType)
	{
		return String.format(DOWNLOAD_SUCCESS_FEEDBACK_URL, ACT_DOWNLOAD_SUCCESS,
															resId,
															mFeedBackResType,
															MT,
															statType,
															channelId,
															PID,
															imei,
															versionName,
															netType);
	}
	
	
	
	/**
	 * 从服务器获取应用集合
	 * @param itemList
	 * @param urlString_nopage 地址（不带页码参数）
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<AppMarketItem> getAppsFromServer(List<AppMarketItem> itemList,String urlString_nopage,int page) throws Exception 
	{
		String urlString=urlString_nopage+"&pi="+page; //加上页码
//		Log.d(TAG, "request app list url:"+urlString);
//		String content=WebUtil.getURLContent(urlString,"utf-8");
		long begin=System.currentTimeMillis();
		httpCommon.setUrl(urlString);
		String content = httpCommon.getResponseAsStringGET(null);
		Log.d(AppMarketSubjectActivity.TAG, "list data connection:"+(System.currentTimeMillis()-begin));
		if(!TextUtils.isEmpty(content))
		{
			JSONObject jobj=new JSONObject(content);
			if(!isDataValidate(jobj)) return null;
			JSONObject resultJson=jobj.getJSONObject("Result");
			if(resultJson==null) return null;
			JSONArray itemsJsonArray=resultJson.getJSONArray("items");
			if(itemsJsonArray==null) return null;
			if(itemList==null)
				itemList=new ArrayList<AppMarketItem>();
			for(int i=0;i<itemsJsonArray.length();i++)
			{
				JSONObject itemJson=itemsJsonArray.getJSONObject(i);
				AppMarketItem item=parseJson(itemJson);
				
				//过滤已安装的
				if(item==null || AndroidPackageUtils.isPkgInstalled(mContext, item.getPackageName())) 
					continue;
				
				itemList.add(item);
				
				/*if(itemList.size()<maxCount)
					itemList.add(item);
				else
					break;*/
			}
			/*if(itemList.size()<maxCount)
				getAppsFromServer(itemList,urlString_nopage,++page);*/
			
			return itemList;
		}
		return null;
	}
	
	/**
	 * 获取专题详情
	 * @param subjectUrl
	 * @return
	 */
	public AppMarketSubjectDetailItem getSubjectDetailFromServer(String subDetailUrl) throws Exception
	{
		httpCommon.setUrl(subDetailUrl);
		String content = httpCommon.getResponseAsStringGET(null);
		if(!TextUtils.isEmpty(content))
		{
			JSONObject jobj=new JSONObject(content);
			if(!isDataValidate(jobj)) return null;
			JSONObject resultJson=jobj.getJSONObject("Result");
			if(resultJson==null) return null;
			JSONArray itemsJsonArray=resultJson.getJSONArray("items");
			if(itemsJsonArray==null || itemsJsonArray.length()==0) return null;
			
			AppMarketSubjectDetailItem subDetailItem=new AppMarketSubjectDetailItem();
			String tagUrl=resultJson.getString("tagUrl"); //主题详情头部的网页地址
			boolean isSingleApp=resultJson.getBoolean("isSingleTag");//是否是单应用专题
			String subjectTitle=resultJson.getString("tagName");
			subDetailItem.setSubjectTitle(subjectTitle);
			subDetailItem.setSubjectWebUrl(tagUrl);
			subDetailItem.setSingleApp(isSingleApp);
			
			List<AppMarketItem> appList=new ArrayList<AppMarketItem>();
			//专题下的所有应用
			for(int i=0;i<itemsJsonArray.length();i++)
			{
				JSONObject itemJson=itemsJsonArray.getJSONObject(i);
				AppMarketItem item=parseJson(itemJson);
				if(item==null) continue;
				appList.add(item);
			}
			subDetailItem.setAppList(appList);
			
			return subDetailItem;
		}
		
		return null;
	}
	
	/**
	 * 校验返回的数据是否正确
	 * @param jobj
	 * @return
	 */
	private boolean isDataValidate(JSONObject jobj)
	{
		try {
			int code=jobj.getInt("Code");
			if(code==0)
				return true;
		} catch (JSONException e) {
		}
		
		return false;
	}
	
	/**
	 * 将JSON封到AppMarketItem
	 * @param jobj
	 * @return
	 */
	public AppMarketItem parseJson(JSONObject jobj)
	{
		AppMarketItem item=new AppMarketItem();
		try {
			
			item.setPackageName(jobj.getString("identifier"));
			
			item.setId(jobj.getLong("resId"));
			item.setDetailUrl(jobj.getString("detailUrl"));
			item.setIconUrl(jobj.getString("icon"));
			item.setTitle(jobj.getString("name"));
			item.setVersionName(jobj.getString("versionName"));
			item.setApkUrl(jobj.getString("downloadUrl"));
			item.setSize(jobj.getString("size"));
			item.setVersionCode(jobj.getString("versionCode"));
			item.setIconFilePath(ICON_CACHE_DIR+item.getPackageName());
			item.setDownloadNumber(jobj.getString("downnum"));
			item.setApkFileName(item.getPackageName()+item.getVersionCode()+".apk");
			item.setApkFilePath(new File(PACKAGE_DOWNLOAD_DIR,item.getApkFileName()).getAbsolutePath());
			item.setStar(jobj.getInt("star"));
			item.setClientType(mClientType);
			
			//下载成功后的反馈地址
			String feedbackUrl=getFeedBackURL(item.getId()+"", FEED_BACK_TYPE_DOWNLOAD_SUCCESS);
			item.setFeedbackUrl(feedbackUrl);
			return item;
		} catch (JSONException e) {
			Log.w(TAG, "parse JSON failed:"+e.toString());
		}
		return null;
	}
	
	/**
	 * 获取应用详情
	 * @param packageName
	 */
	public AppMarketDetailItem getAppDetail(String detailUrl) throws Exception
	{
		//Log.d(Global.TAG, "app detail url:"+detailUrl);
		httpCommon.setUrl(detailUrl);
		String content = httpCommon.getResponseAsStringGET(null);
		//Log.d(Global.TAG, "app detail:"+content);
		
		if(!TextUtils.isEmpty(content))
		{
			JSONObject jobj=new JSONObject(content);
			if(!isDataValidate(jobj)) return null;
			JSONObject resultJson=jobj.getJSONObject("Result");
			if(resultJson==null) return null;
			AppMarketDetailItem item=new AppMarketDetailItem();
			item.setResId(resultJson.getLong("resId"));
			item.setTitle(resultJson.getString("resName"));
			item.setDescription(Html.fromHtml(resultJson.getString("desc")).toString());
			item.setPackageName(resultJson.getString("com.liquable.nemo"));
			item.setAuther(resultJson.getString("author"));
			item.setVersionName(resultJson.getString("version"));
			item.setLanguage(resultJson.getString("lan"));
			item.setSize(resultJson.getString("size"));
			item.setStar(resultJson.getInt("star"));
			item.setIconUrl(resultJson.getString("icon"));
			item.setSharer(resultJson.getString("sharer"));
			JSONArray previewJsonArray=resultJson.getJSONArray("snapshots");
			if(previewJsonArray!=null && previewJsonArray.length()>0)
			{
				List<String> previewImageUrlList=new ArrayList<String>(previewJsonArray.length());
				for(int i=0;i<previewJsonArray.length();i++)
				{
					previewImageUrlList.add(previewJsonArray.getString(i));
				}
				item.setPreviewImageUrlList(previewImageUrlList);
			}
			item.setDownloadUrl(resultJson.getString("downloadUrl"));
			item.setUpdateTime(resultJson.getString("updateTime"));
			item.setDownloadNumber(resultJson.getString("downloadNumber"));
			item.setVersionCode(resultJson.getString("versionCode"));
			
			//安全信息
			item.setSafeInfo(parseJsonToSafeInfo(resultJson.getJSONObject("safeInfo")));
			
			return item;
		}
		
		return null;
	}
	
	/**
	 * 转化安全信息
	 * @param safeInfoJSON
	 * @return
	 */
	private AppMarketAppSafeInfo parseJsonToSafeInfo(JSONObject safeInfoJSON)
	{
		if(safeInfoJSON==null)
			return null;
		try {
			JSONObject scanProviderJSON=safeInfoJSON.getJSONObject("scanProvider");
			JSONObject adJSON=safeInfoJSON.getJSONObject("ad");
			JSONObject privacyJSON=safeInfoJSON.getJSONObject("privacy");
			
			if(scanProviderJSON==null && adJSON==null && privacyJSON==null)
				return null;
			
			AppMarketAppSafeInfo safeInfo=new AppMarketAppSafeInfo();
			
			//病毒信息
			safeInfo.setmScanProvider(parseJSONToSafeItem(scanProviderJSON));
			//广告信息
			safeInfo.setmAdvertisement(parseJSONToSafeItem(adJSON));
			//隐私信息
			safeInfo.setmPrivacy(parseJSONToSafeItem(privacyJSON));
			
			return safeInfo;
			
		} catch (Exception e) {
			Log.w(TAG, "AppMarketUtil parseJsonToSafeInfo failed:"+e.toString());
		}
		
		return null;
	}
	
	/**
	 * 转化安全信息的具体项
	 * @param safeItemJSON
	 * @return
	 */
	private AppMarketAppSafeInfo.SafeItem parseJSONToSafeItem(JSONObject safeItemJSON) throws Exception
	{
		if(safeItemJSON==null)
			return null;
		
		AppMarketAppSafeInfo.SafeItem safeItem=new AppMarketAppSafeInfo.SafeItem();
		
		String title=safeItemJSON.getString("title");
		int state=safeItemJSON.getInt("state");
		
		safeItem.title=title;
		safeItem.state=state;
		
		//详情描述列表
		JSONArray itemsJsonArray=safeItemJSON.getJSONArray("items");
		if(itemsJsonArray!=null && itemsJsonArray.length()>0)
		{
			List<AppMarketAppSafeInfo.DescItem> descItemList=new ArrayList<AppMarketAppSafeInfo.DescItem>();
			for(int i=0;i<itemsJsonArray.length();i++)
			{
				JSONObject itemJSON=itemsJsonArray.getJSONObject(i);
				String iconUrl=itemJSON.getString("icon");
				String subTitle=itemJSON.getString("title");
				int subSate=itemJSON.getInt("state");
				String content=itemJSON.getString("context");
				
				AppMarketAppSafeInfo.DescItem descItem=new AppMarketAppSafeInfo.DescItem();
				descItem.iconUrl=iconUrl;
				descItem.title=subTitle;
				descItem.state=subSate;
				descItem.content=content;
				descItemList.add(descItem);
			}//end for
			
			safeItem.descItemList=descItemList;
		}
		
		return safeItem;
	}
	
	
	/**
	 * 加载图标
	 * @param packageName
	 * @param iconFilePath
	 * @param iconUrl
	 * @return
	 */
	public static boolean loadIcon(final String packageName,final String iconFilePath,final String iconUrl)
	{
		boolean loadSuccess=false;
		try {
			if(!TelephoneUtil.isSdcardExist())
				return false;
			
			
			Bitmap bmp=BitmapFactory.decodeFile(iconFilePath);
			if(bmp!=null){
				//将图标添加到缓存
				addIconToCache(packageName, bmp);
				loadSuccess=true;
				
			}else{
				String newIconFilePath=BitmapUtils.saveInternateImage(iconUrl, iconFilePath);
				if(!TextUtils.isEmpty(newIconFilePath)){
					bmp=BitmapFactory.decodeFile(iconFilePath);
					addIconToCache(packageName, bmp);
					loadSuccess=true;
				}
			}
			
		} catch (Exception e) {
		}
		
		return loadSuccess;
	}
	
	/**
     * 设置下载状态
     * @param context
     * @param item
     */
    public static void setDownloadState(Context context,AppMarketItem item)
    {
    	DownloadServerServiceConnection mDownloadService=new DownloadServerServiceConnection(context);
    	
    	String key=item.getKey();
		ApkDownloadInfo dlInfo=mDownloadService.getDownloadState(key);
		int state=dlInfo==null?DownloadState.STATE_NONE:dlInfo.getState();
		item.setDownloadState(state);
		
		if(dlInfo!=null)
			item.setDownloadProccess(dlInfo.progress);
		
		switch (state) {
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_FINISHED:	
		case DownloadState.STATE_INSTALLED:
		case ApkInstaller.INSTALL_STATE_INSTALLING:
		case DownloadState.STATE_NONE:
			String packageName=item.getPackageName();
			int versionCode=-1;
			try {
				versionCode=Integer.parseInt(item.getVersionCode());
			} catch (Exception e) {
			}
			
			if(mDownloadService.isApkInstalling(packageName)){
				//正在安装
				item.setDownloadState(ApkInstaller.INSTALL_STATE_INSTALLING);
			}else if(AndroidPackageUtils.isPkgInstalled(context, packageName,versionCode)){
	    		//已安装
	    		item.setDownloadState(DownloadState.STATE_INSTALLED);
			}
			break;
		}
		
		mDownloadService=null;
    }
    
    /**
     * 设置下载状态
     * @param downloadInfo
     */
    public static void setDownloadState(Context context,ApkDownloadInfo downloadInfo)
	{
    	DownloadServerServiceConnection mDownloadService=new DownloadServerServiceConnection(context);
		
    	ApkDownloadInfo dlInfo=mDownloadService.getDownloadState(downloadInfo.identification);
    	if(dlInfo!=null)
    	{
    		int dlState=dlInfo.getState();
    		downloadInfo.progress=dlInfo.progress;
    		switch (dlState) {
			case DownloadState.STATE_DOWNLOADING:
				downloadInfo.setState(downloadInfo.getDownloadingState());
				break;
			case DownloadState.STATE_FINISHED:
				downloadInfo.setState(downloadInfo.getFinishedUninstalled());
				break;
			case DownloadState.STATE_INSTALLED:
				downloadInfo.setState(downloadInfo.getFinishedInstalled());
				break;
			case DownloadState.STATE_PAUSE:
				downloadInfo.setState(downloadInfo.getPauseState());
				break;
			case DownloadState.STATE_WAITING:
				downloadInfo.setState(downloadInfo.getWaitingState());
				break;
			}
    	}
    	
		int state=downloadInfo.getState();
		
		
		//下载完成、已安装、未下载状态下判断是否正在安装，是否已安装，如果是其他状态，保持不变
		switch (state) {
		case DownloadState.STATE_CANCLE:
		case DownloadState.STATE_FINISHED:	
		case DownloadState.STATE_INSTALLED:
		case ApkInstaller.INSTALL_STATE_INSTALLING:
		case DownloadState.STATE_NONE:
			//获取包信息
			String packageName=downloadInfo.getPacakgeName(context);
			int versionCode=downloadInfo.getVersionCode(context);
			
			if(mDownloadService.isApkInstalling(packageName)){
				//正在安装
				downloadInfo.setState(downloadInfo.getInstallingState());
			}else if(AndroidPackageUtils.isPkgInstalled(context, packageName,versionCode)){
	    		//已安装
				downloadInfo.setState(downloadInfo.getFinishedInstalled());
			}else{
				
			}
			break;
		}
		
		mDownloadService=null;
	}
    
	/**
	 * 开始下载
	 */
	public static void startDownload(Context context,AppMarketItem item)
	{
		DownloadServerServiceConnection mDownloadService=new DownloadServerServiceConnection(context);
		boolean downloadSuc=false;
		ApkDownloadInfo downloadInfo=makeDownloadInfo(item);
		downloadSuc=mDownloadService.addDownloadTask(downloadInfo);
		
		if(!downloadSuc)
		{
			//下载失败
			boolean isNewLooper=false;
			if(Looper.myLooper()==null)
			{
				Looper.prepare();
				isNewLooper=true;
			}
			
			MessageUtils.makeShortToast(context, R.string.common_download_failed);
			
			Looper.loop();
			if(isNewLooper)
				Looper.myLooper().quit();
		}else{
			item.setDownloadState(DownloadState.STATE_WAITING);
		}
		
	}
	
	/**
	 * 加载图片，线程模式
	 * @param packageName
	 * @param iconFilePath
	 * @param iconUrl
	 */
	public static void loadIconInThread(final String packageName,
			final String iconFilePath,
			final String iconUrl,
			final CommonCallBack<Void> callBack)
	{
		executeThread(new Runnable() {
			
			@Override
			public void run() {
				
				boolean success=AppMarketUtil.loadIcon(packageName, iconFilePath, iconUrl);
				if(success && callBack!=null)
					callBack.invoke();
			}
		});
	}
	
	/**
	 * 添加图标缓存
	 * @param packageName
	 * @param icon
	 */
	public static void addIconToCache(String packageName,Bitmap icon)
	{
		
		if(icon!=null && !icon.isRecycled())
		{
			HashMap<String, SoftReference<Bitmap>> iconCache=getIconCache();
			iconCache.put(packageName, new SoftReference<Bitmap>(icon));
		}
		
	}
	
	/**
	 * 获取图标
	 * @param packageName
	 * @return
	 */
	public static Bitmap getIconFromCache(String packageName)
	{
		HashMap<String, SoftReference<Bitmap>> iconCache=getIconCache();
		SoftReference<Bitmap> sfBmp=iconCache.get(packageName);
		if(sfBmp!=null)
			return sfBmp.get();
		return null;
	}
	
	/**
	 * 清队图标缓存
	 */
	public static void clearIconCache()
	{
		if(mIconCache!=null)
		{
			Set<Entry<String, SoftReference<Bitmap>>> set=mIconCache.entrySet();
			for(Entry<String, SoftReference<Bitmap>> entry:set)
			{
				if(entry.getValue()!=null)
					BitmapUtils.destoryBitmap(entry.getValue().get());
			}
			mIconCache.clear();
			mIconCache=null;
		}
	}
	
	/**
	 * 创建所需目录
	 */
	public static void createBaseDir()
	{
		File iconCacheDir=new File(ICON_CACHE_DIR);
		if(!iconCacheDir.exists() || !iconCacheDir.isDirectory())
			iconCacheDir.mkdirs();
		File pkgDownloadDir=new File(PACKAGE_DOWNLOAD_DIR);
		if(!pkgDownloadDir.exists() || !pkgDownloadDir.isDirectory())
			pkgDownloadDir.mkdirs();
	}
	
	/**
	 * 已安装情况下，显示是否立即运行的对话框提示
	 */
	public static void showOpenAppTipDialog(final AppMarketItem item,final Context context)
	{
		CommonDialog alertd = ViewFactory.getAlertDialog(context, context.getString(R.string.common_tip), context.getString(R.string.app_market_app_installed_run_tip),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						AndroidPackageUtils.runApplication(context, item.getPackageName());
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
	 * 执行线程
	 * @param thread
	 */
	public static void executeThread(Runnable thread)
	{
		if(executorService==null)
			executorService=Executors.newFixedThreadPool(10);
		executorService.execute(thread);
	}
	
	/**
	 * 清理线程池
	 */
	public static void clearThreads()
	{
		if(executorService!=null)
			executorService.shutdownNow();
		
		executorService=null;
	}
	
	//TODO linqiang 单独已抽取出来
	// public static interface CommonCallBack<E>{
	// public void invoke(final E ...arg);
	// }

	/**
	 * 组装下载项
	 * @param ami
	 * @return
	 */
	public static ApkDownloadInfo makeDownloadInfo(AppMarketItem item) {
		ApkDownloadInfo downloadInfo=new ApkDownloadInfo(item.getKey(),item.getApkUrl());
		downloadInfo.appName=item.getTitle();
		downloadInfo.apkFile=item.getApkFileName();
		downloadInfo.iconPath=item.getIconFilePath();
		downloadInfo.totalSize=item.getSize();
		downloadInfo.downloadDir=AppMarketUtil.PACKAGE_DOWNLOAD_DIR;
		downloadInfo.feedbackUrl=item.getFeedbackUrl();
		return downloadInfo;
	}
}