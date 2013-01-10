package com.nd.hilauncherdev.kitset.analytics;


import java.net.URLEncoder;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.lib.theme.util.TelephoneUtil;

/**
 * 新增的数据统计常量类，不依赖于数据统计的jar包
 * @author zhuchenghua
 * @date 2012-10-12
 */
public class OtherAnalyticsConstants {

	/**
	 * 域名
	 * http://192.168.254.69:803
	 */
	private final static String BASE_DOMAIN="http://pandahome.sj.91.com/";
	
	/**
	 * URL的基本参数
	 */
	private final static String BASE_URL_ARGUMENTS="mt=%d" + //平台枚举值 android平台是4
													"&SupPhone=%s" + //机型，手机型号
													"&SupFirm=%s" + //固件版本
													"&fid=%d" + //功能ID
													"&imei=%s" + 
													"&format=%s" + //json或xml
													"&JailBroken=%d" + //0表示未越狱或未ROOT,1表示已越狱或已ROOT
													"&NetWork=%s" + //网络类型值
													"&DivideVersion=%s"+ //客户端版本号
													"&PID=%d"; //产品ID默认为熊猫桌面 6，如果是其他桌面如：安卓桌面 39
	
	/**
	 * 普通统计地址
	 */
	private final static String ANALYTICS_NORMAL_URL=BASE_DOMAIN+"theme.ashx/SuppleFunc?"
																	+BASE_URL_ARGUMENTS;
																		
	/**
	 * 带返回资源数据的统计地址
	 */
	private final static String ANALYTICS_RES_CONTENT_URL=BASE_DOMAIN+"theme.ashx/ResContent?"
																		+BASE_URL_ARGUMENTS
																		+"&act=%d";//动作值 
	
	/**
	 * 平台枚举值 android平台是4
	 */
	public final static int ANDROID_MT=4;
	
	/**
	 * 功能ID：桌面请求安装智能锁
	 */
	public final static int FUNC_ID_INTELLIGENT_LOCK_INSTALL_REQUEST=1;
	
	/**
	 * 功能ID：桌面请求安装黄历天气
	 */
	public final static int FUNC_ID_HAUNG_LI_WEATHER_INSTALL_REQUEST=2;
	
	/**
	 * 功能ID：黄历天气安装桌面
	 */
	public final static int FUNC_ID_HAUNG_LI_WEATHER_INSTALL=3;
	
	/**
	 * 功能ID：智能锁安装桌面
	 */
	public final static int FUNC_ID_INTELLIGENT_LOCK_INSTALL=4;
	
	/**
	 * 功能ID：一键装机打开
	 */
	public final static int FUNC_ID_APP_MARKET_APP_NECESSARY_OPEN=5;
	
	/**
	 * 功能ID：热门游戏打开
	 */
	public final static int FUNC_ID_APP_MARKET_APP_GAME_OPEN=6;
	
	/**
	 * 功能ID：获取桌面分享数据--带返回资源
	 */
	public final static int FUNC_ID_RES_CONTENT_LAUNCHER_SHARE=7;
	
	/**
	 * 功能ID：获取91助手下载地址--带返回资源（下载地址）
	 */
	public final static int FUNC_ID_RES_CONTENT_91_ASSIT_APP_URL=8;
	
	
	/**
	 * 动作值：：获取桌面分享数据(这里与其功能ID一致)
	 */
	public final static int ACT_ID_LAUNCHER_SHARE=FUNC_ID_RES_CONTENT_LAUNCHER_SHARE;
	
	/**
	 * 动作值：：获取91助手下载地址(这里与其功能ID一致)
	 */
	public final static int ACT_ID_GET_91_ASSIT_APP_URL=FUNC_ID_RES_CONTENT_91_ASSIT_APP_URL;
	
	/**
	 * 返回数据格式:json
	 */
	public final static String FORMAT_JSON="json";
	
	/**
	 * 返回数据格式:xml
	 */
	public final static String FORMAT_XML="xml";
	
	/**
	 * 产品ID-91桌面
	 */
	public final static int PID_91_HOME=6;
	
	/**
	 * ExtName参数的apk标识值 
	 */
	public final static int EXT_NAME_APK=3;
	
	/**
	 * 获取统计接口地址
	 * @param fid 功能ID
	 * @param format 返回数据格式：json或xml，默认是json
	 * @param context
	 * @param pid 产品ID
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @return
	 */
	public static String getNormalAnalyticsUrl(int fid,
											String format,
											Context context,
											int pid,
											String lbl)
	{
		int mt=ANDROID_MT;
		String phoneName=TelephoneUtil.getMachineName();//手机型号
		String wareVersionName=TelephoneUtil.getFirmWareVersion();//固件版本名称
		String imei=TelephoneUtil.getIMEI(context);//IMEI号
		format=TextUtils.isEmpty(format)?FORMAT_JSON:format.toLowerCase(); //返回数据格式
		int isRoot=TelephoneUtil.hasRootPermission()?1:0; //是否root
		String netWorkType=TelephoneUtil.getNT(context);//网络类型
		String clientVersionName=TelephoneUtil.getVersionName(context);
		
		String url=String.format(ANALYTICS_NORMAL_URL,	
									mt,
									phoneName,
									wareVersionName,
									fid,
									imei,
									format,
									isRoot,
									netWorkType,
									clientVersionName,
									pid);
		
		try {
			if(!TextUtils.isEmpty(lbl))
				url+="&lbl="+URLEncoder.encode(lbl, "utf-8");
		} catch (Exception e) {
		}
		
		url=url.replaceAll("\\s", "%20");
		
		return url;
	}
	
	/**
	 * 获取带返回资源数据的地址
	 * @param fid 功能ID
	 * @param act 动作值
	 * @param format 返回数据格式：json或xml，默认是json
	 * @param context
	 * @param pid 产品ID
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @return
	 */
	public static String getResContentAnalyticsUrl(int fid,
													int act,
													String format,
													Context context,
													int pid,
													String lbl)
	{
		int mt=ANDROID_MT;
		String phoneName=TelephoneUtil.getMachineName();//手机型号
		String wareVersionName=TelephoneUtil.getFirmWareVersion();//固件版本名称
		String imei=TelephoneUtil.getIMEI(context);//IMEI号
		format=TextUtils.isEmpty(format)?FORMAT_JSON:format.toLowerCase(); //返回数据格式
		int isRoot=TelephoneUtil.hasRootPermission()?1:0; //是否root
		String netWorkType=TelephoneUtil.getNT(context);//网络类型
		String clientVersionName=TelephoneUtil.getVersionName(context);//客户端版本名称
		
		String url=String.format(ANALYTICS_RES_CONTENT_URL,	
									mt,
									phoneName,
									wareVersionName,
									fid,
									imei,
									format,
									isRoot,
									netWorkType,
									clientVersionName,
									pid,
									act);
		
		try {
			if(!TextUtils.isEmpty(lbl))
				url+="&lbl="+URLEncoder.encode(lbl, "utf-8");
		} catch (Exception e) {
		}
		
		
		url=url.replaceAll("\\s", "%20");
		
		return url;
	}// getResContentAnalyticsUrl
	
	/**
	 * 
	 * 获取带返回资源数据(下载地址)的地址
	 * @param fid 功能ID
	 * @param act 动作值
	 * @param format 返回数据格式：json或xml，默认是json
	 * @param context
	 * @param pid 产品ID
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @param extName 下载资源的扩展名标识 Pxl 1 ,ipa 2,apk 3
	 * @return 
	 */
	public static String getDownloadUrlResContentAnalyticsUrl(int fid,
																int act,
																String format,
																Context context,
																int pid,
																String lbl,
																int extName)
	{
		String resUrl=getResContentAnalyticsUrl(fid,act,format,context,pid,lbl);
		
		resUrl+="&ExtName="+extName;
		return resUrl;
	}
	
}












