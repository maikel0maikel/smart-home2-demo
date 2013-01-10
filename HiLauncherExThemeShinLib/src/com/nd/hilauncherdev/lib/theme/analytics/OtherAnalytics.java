package com.nd.hilauncherdev.lib.theme.analytics;


import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.framework.httplib.HttpCommon;
import com.nd.hilauncherdev.lib.theme.util.TelephoneUtil;


public class OtherAnalytics {
	
	private static final String TAG = "OtherAnalytics";
	
	/**
	 * 统计一键装机界面打开次数
	 */
	public static boolean submitAppNecessaryOpen(Context context)
	{
		String format=OtherAnalyticsConstants.FORMAT_JSON;
		int fid=OtherAnalyticsConstants.FUNC_ID_APP_MARKET_APP_NECESSARY_OPEN;
		boolean res=submitNormalAnalyticsEvent(format, fid,context,null);
		return res;
	}
	
	/**
	 * 统计热门游戏界面打开次数
	 */
	public static boolean submitAppGameOpen(Context context)
	{
		String format=OtherAnalyticsConstants.FORMAT_JSON;
		int fid=OtherAnalyticsConstants.FUNC_ID_APP_MARKET_APP_GAME_OPEN;
		boolean res=submitNormalAnalyticsEvent(format, fid,context,null);
		return res;
	}
	
	/**
	 * 获取桌面分享带统计功能的资源
	 * @param context
	 * @return
	 */
	public static String getLaucherShareResContent(Context context)
	{
		String format=OtherAnalyticsConstants.FORMAT_JSON;
		int fid=OtherAnalyticsConstants.FUNC_ID_RES_CONTENT_LAUNCHER_SHARE;
		int act=OtherAnalyticsConstants.ACT_ID_LAUNCHER_SHARE;
		return submitResContentAnalyticsEvent(format, fid, act, context,null);
	}
	
	/**
	 * TODO 修改的方法
	 * 获取带统计的'91桌面'下载地址
	 * @param context
	 * @return
	 */
	public static String get91LauncherAppDownloadUrl(Context context){
		
		String format=OtherAnalyticsConstants.FORMAT_JSON;
		int fid=OtherAnalyticsConstants.FUNC_ID_RES_CONTENT_91_ASSIT_APP_URL;
		int act=OtherAnalyticsConstants.ACT_ID_GET_91_ASSIT_APP_URL;
		int extName=OtherAnalyticsConstants.EXT_NAME_APK;
		return submitResDownloadUrlContentAnalyticsEvent(format, fid, act, context, null, extName);
	}
	
	/**
	 * 提交统计数据
	 * @param format 返回数据的格式
	 * @param fid 功能ID
	 * @param context
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @return
	 */
	private static boolean submitNormalAnalyticsEvent(String format,int fid,Context context,String lbl)
	{
		if(!TelephoneUtil.isNetworkAvailable(context))
			return false;
		
		//获取接口地址
		String url=OtherAnalyticsConstants.getNormalAnalyticsUrl(fid, format, context,OtherAnalyticsConstants.PID_91_HOME,lbl);
		logDebug("OtherAnalytics submitNormalAnalyticsEvent url:"+url);
		HttpCommon httpCommon=new HttpCommon(url);
		String responseString=httpCommon.getResponseAsStringGET(null);
		logDebug("OtherAnalytics submitNormalAnalyticsEvent response string:"+responseString);
		
		//校验返回结果，分JSON和XML格式两种
		Object[] result=null;
		if(format.equals(OtherAnalyticsConstants.FORMAT_JSON)){
			result= checkResponseJSONValidate(responseString);
		}else if(format.equals(OtherAnalyticsConstants.FORMAT_XML))
			result= checkResponseXMLValidate(responseString);
		if(result!=null && ((Integer)result[0])==0)
			return true;
		
		return false;
	}
	
	/**
	 * 提交带返回资源的统计接口
	 * @param format 返回数据的格式
	 * @param fid 功能ID
	 * @param act 动作值
	 * @param context
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @return
	 */
	private static String submitResContentAnalyticsEvent(String format,int fid,int act,Context context,String lbl)
	{
		if(!TelephoneUtil.isNetworkAvailable(context))
			return null;
		
		//获取接口地址
		String url=OtherAnalyticsConstants.getResContentAnalyticsUrl(fid, act, format, context,OtherAnalyticsConstants.PID_91_HOME,lbl);
		logDebug("OtherAnalytics submitResContentAnalyticsEvent url:"+url);
		HttpCommon httpCommon=new HttpCommon(url);
		String responseString=httpCommon.getResponseAsStringGET(null);
		logDebug("OtherAnalytics submitResContentAnalyticsEvent response string:"+responseString);
		//校验返回结果，分JSON和XML格式两种
		Object[] result=null;
		if(format.equals(OtherAnalyticsConstants.FORMAT_JSON)){
			result= checkResponseJSONValidate(responseString);
		}else if(format.equals(OtherAnalyticsConstants.FORMAT_XML))
			result= checkResponseXMLValidate(responseString);
		if(result!=null && ((Integer)result[0])==0)
			return (String)result[1];
		
		return null;
	}
	
	/**
	 * 
	 * 提交带返回资源的统计接口
	 * @param format 返回数据的格式
	 * @param fid 功能ID
	 * @param act 动作值
	 * @param context
	 * @param lbl 同个功能id不同拓展的请传入此参数,汉字请urlencode编码
	 * @param extName 下载资源的扩展名标识 Pxl 1 ,ipa 2,apk 3
	 * @return
	 */
	private static String submitResDownloadUrlContentAnalyticsEvent(String format,int fid,int act,Context context,String lbl,int extName)
	{
		if(!TelephoneUtil.isNetworkAvailable(context))
			return null;
		
		//获取接口地址
		String url=OtherAnalyticsConstants.getDownloadUrlResContentAnalyticsUrl(fid, act, format, context,OtherAnalyticsConstants.PID_91_HOME, lbl, extName);
		logDebug("OtherAnalytics submitResDownloadUrlContentAnalyticsEvent url:"+url);
		HttpCommon httpCommon=new HttpCommon(url);
		String responseString=httpCommon.getResponseAsStringGET(null);
		logDebug("OtherAnalytics submitResDownloadUrlContentAnalyticsEvent response string:"+responseString);
		//校验返回结果，分JSON和XML格式两种
		Object[] result=null;
		if(format.equals(OtherAnalyticsConstants.FORMAT_JSON)){
			result= checkResponseJSONValidate(responseString);
		}else if(format.equals(OtherAnalyticsConstants.FORMAT_XML))
			result= checkResponseXMLValidate(responseString);
		if(result!=null && ((Integer)result[0])==0)
			return (String)result[1];
		
		return null;
	}
	
	
	
	/**
	 * 检验提交统计是否成功(JSON结果)
	 * @param responseString 返回的数据
	 * @return Object[] 返回的数据数组，第一个元素：返回的Code：0成功，否则失败;第二个元素：成功且有带返回资源的情况下的资源数据
	 */
	private static Object[] checkResponseJSONValidate(String responseString)
	{
		//返回的数据数组，第一个元素：返回的Code：0成功，否则失败;第二个元素：成功且有带返回资源的情况下的资源数据
		Object[] resArray=new Object[]{-1,null};
		
		try {
			JSONObject jsonObj=new JSONObject(responseString);
			int code=jsonObj.getInt("Code");
			resArray[0]=code;
			if(code==0){ //为0表示提交成功
				
				//有返回资源的情况获取返回的资源
				JSONObject resultJson=jsonObj.getJSONObject("Result");
				if(resultJson!=null){
					//返回的Content结节
					String content=resultJson.getString("Content");
					//返回的是DownloadUrl节点
					String downloadUrl=resultJson.getString("DownloadUrl");
					if(!TextUtils.isEmpty(content))
						resArray[1]=content;
					else if(!TextUtils.isEmpty(downloadUrl))
						resArray[1]=downloadUrl;
				}
				
			}else{
				String msg=jsonObj.getString("Msg");//失败的消息
				Log.w(TAG, "OtherAnalytics checkResponseJSONValidate invalidate:"+responseString+" message:"+msg);
			}
		} catch (Exception e) {
			Log.w(TAG, "OtherAnalytics checkResponseJSONValidate failed:"+e.toString());
		}
		
		return resArray;
		
	}//end checkResponseJSONValidate
	
	/**
	 * 检验提交统计是否成功(XML结果)
	 * @param responseString 返回的数据
	 * @return @return Object[] 返回的数据数组，第一个元素：返回的Code：0成功，否则失败;第二个元素：成功且有带返回资源的情况下的资源数据
	 */
	private static Object[] checkResponseXMLValidate(String responseString)
	{
		//返回的数据数组，第一个元素：返回的Code：0成功，否则失败;第二个元素：成功且有带返回资源的情况下的资源数据
		Object[] resArray=new Object[]{-1,null};
		
		try {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder builder = factory.newDocumentBuilder();
	         Document document = builder.parse(new ByteArrayInputStream(responseString.getBytes()));
	         // 得到根元素
	         Element root = document.getDocumentElement();
	         NodeList nodeList = root.getElementsByTagName("code");
	         Node codeNode=nodeList.item(0);
	         String code=codeNode.getFirstChild().getNodeValue();
	         int intCode=Integer.parseInt(code);
	         resArray[0]=intCode;
	         if(intCode==0){
	        	 
	        	 //返回资源,<content>节点
	        	 NodeList contentNodeList=root.getElementsByTagName("content");
	        	//返回资源,<DownloadUrl>节点
	        	 NodeList DownloadUrlNodeList=root.getElementsByTagName("DownloadUrl");
	        	 
	        	 if(contentNodeList!=null && contentNodeList.getLength()>0)
	        	 {
	        		 Node contentNode=contentNodeList.item(0);
	        		 resArray[1]=contentNode.getFirstChild().getNodeValue();
	        	 }else if(DownloadUrlNodeList!=null && DownloadUrlNodeList.getLength()>0){
	        		 Node DownloadUrlNode=DownloadUrlNodeList.item(0);
	        		 resArray[1]=DownloadUrlNode.getFirstChild().getNodeValue();
	        	 }
	        	 
	         }else
	        	 Log.w(TAG, "OtherAnalytics checkResponseXMLValidate invalidate:"+responseString);
	         
		} catch (Exception e) {
			Log.w(TAG, "OtherAnalytics checkResponseXMLValidate failed:"+e.toString());
		}
		
		return resArray;
	}
	
	private static void logDebug(String msg)
	{
		boolean logOpen=false;
		if(logOpen)
			Log.d(TAG, msg);
	}
}






