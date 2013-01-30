package com.nd.hilauncherdev.lib.theme.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nd.hilauncherdev.lib.theme.down.ThemeItem;



/**
 * 全局类
 */
public class HiLauncherThemeGlobal {
	
	public static final String TAG = "com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal";
	
	/**91桌面包名*/
	public static final String THEME_MANAGE_PACKAGE_NAME = "com.nd.android.pandahome2";
	
	/**91桌面版本*/
	public static final String THEME_MANAGE_PACKAGE_VERSION_NAME = "3.5.1";
	
	
	/**91桌面默认下载地址*/
	private static final String hilauncher_app_download_url="http://pandahome.sj.91.com/soft.ashx/softurlV2?mt=4&redirect=1&fwv=40000&sjxh=123&fbl=123&imei=123&packagename=com.nd.android.pandahome2";
	
	/**服务器地址*/
	//public static final String HOST = "http://192.168.254.69:803/TpbTheme";
	public static final String HOST = "http://pandahome.sj.91.com/TpbTheme";
	
	/**91桌面下载任务ID*/
	public static final String HiLauncherTaskItemID = "91" + ThemeItem.ITEM_TYPE_LAUNCHER;
	
	public static final int CONNECTION_TIMEOUT = 10000;
    
	/**设置*/
    public final static String APPLICATION = "application";
	
    /**BASE_DIR*/
    public final static String BASE_DIR = "/sdcard/PandaHome2ThemeLib";
    
    /**
     * 防止图库扫描该目录图片
     */
    public final static String BASE_DIR_AVOID_MEDIA_SCAN = BASE_DIR + "/.nomedia";
    
    /**THEME_HOME*/
    public final static String PACKAPGES_HOME = BASE_DIR + "/Packages/";
	
    /**THEME_HOME*/
    public final static String CACHES_HOME = BASE_DIR + "/caches/";

    /**服务器的图片缓存*/
    public final static String CACHES_HOME_MARKET = CACHES_HOME + "91space/";
    
	/**程序第一次启动加载时给与赋值*/
    private static Context baseContext;
      
	/**
     * 获取全局Context
     * @return Context
     */
    public static Context getContext() {
        return baseContext;
    }
    
    /**
     * 设置Context
     * @param context Context
     */
    public static void setContext(Context context) {
        baseContext = context;
    }
    
    /**
     * 创建系统常用目录
     */
	public static void createDefaultDir() {
		final String baseDir = BASE_DIR;
		File dir = new File(baseDir);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(BASE_DIR_AVOID_MEDIA_SCAN);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(PACKAPGES_HOME);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(CACHES_HOME);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		
		dir = new File(CACHES_HOME_MARKET);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
	}
	public static String R(int i){		
		String rs = "";		
		if(baseContext!=null)
			rs = baseContext.getResources().getString(i);
		return rs;
	}
    
	
	public static boolean isZh(){
		Locale lo;
		if( null == baseContext ) {
			return true;
		} else {
			lo = baseContext.getResources().getConfiguration().locale;
		}
		if (lo.getLanguage().equals("zh")) 
			return true;
		return false;
	}
	
	
	private static long timeOld = 0;
	public static void dpost(Context ctx, String str){
		if(timeOld==0){
			timeOld = System.currentTimeMillis();
		}else{
			if(System.currentTimeMillis() - timeOld <3000)
				return;
		}
		
    	if(str==null)
    		str = "null point";
    	if(ctx==null)
    		Log.e(TAG, "context is null!!!");
    	Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    	timeOld = System.currentTimeMillis();
    }
	
	public static void ddpost(String str){
		 Toast.makeText(baseContext, str, Toast.LENGTH_SHORT).show();
	}
	
	public static String getURLContent(String surl) {
		InputStream is = null;
		try {
			URL url = new URL(Utf8URLencode(surl));
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.setConnectTimeout(CONNECTION_TIMEOUT);
			is = httpUrl.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "getURLContent:" + surl);
			//Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c >= 0) && (c <= 255)) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 处理xml的时候需要加上"utf-8"
	 * @param surl
	 * @param encode
	 * @return
	 */
	public static String getURLContent(String surl, String encode) {		
		InputStream is = null;
		try {
			Log.d(TAG, "get url="+ Utf8URLencode(surl) );
			URL url = new URL( Utf8URLencode(surl));
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.setConnectTimeout(CONNECTION_TIMEOUT);
			is = httpUrl.getInputStream();			
			BufferedReader br = new BufferedReader(new InputStreamReader(is,encode));
			StringBuffer sb = new StringBuffer();
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "getURLContent:" + surl);
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static boolean downloadImageByURL(String imgurl, String path) {
		try {
			Log.d(TAG, "downloadImageByurl = "+imgurl);
			File f = new File(path);
			if (!f.exists()) {
				URL url = new URL(imgurl);
				URLConnection con = url.openConnection();
				//连接超时时间设置为10秒
				con.setConnectTimeout(CONNECTION_TIMEOUT);
				con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6");
				InputStream is = con.getInputStream();
				if (con.getContentEncoding() != null && con.getContentEncoding().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(con.getInputStream());
				}
				byte[] bs = new byte[256];
				int len = -1;
				OutputStream os = new FileOutputStream(f);
				try {
					while ((len = is.read(bs)) != -1) {
						os.write(bs, 0, len);
					}
					// os.flush();
				} finally {
					try {
						os.close();
					} catch (Exception ex) {
					}
					try {
						is.close();
					} catch (Exception ex) {
					}
					os = null;
					is = null;
					con = null;
					url = null;
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			
		}

	}
	
	public static String url2path (String url, String rootpath){
		String rs = rootpath;
		String picname = getPicNameFromUrlWithSuff(url);
		rs = rs+picname;
		rs = SUtil.renameRes(rs);
		return rs;
	}	
	
	/**
	 * 从图片url中获得图片名
	 * @param url
	 * @return
	 */
	public static String getPicNameFromUrlWithSuff(String url){
		String str = url;
		String [] s = str.split("\\/");
		str = s[s.length-1];
		return str; 
	}
	
	public static int paseInt(String s){
		int rs = -1;
		try{
			rs = Integer.parseInt(s);
		}catch(Exception e){
			Log.e(TAG, "pase int error");
		}		
		return rs;
	}
	
	public static int paseInt(Object o){
		return paseInt(o.toString());
	}
	
	public static boolean isEmpty(Object o){
		if(o==null||o.toString().equals(""))
			return true;
		return false;
	}
	
	/**
	 * 隐藏软键盘(已经 有实例化Global)
	 * add by zjf 2010-09-15
	 */
	public static  void hideKeyboard( View view) {		
		hideKeyboard(getContext(), view);
	}
	
	/**
	 * 	隐藏软键盘
	 * add by zxb 2010-03-15
	 * @param ctx
	 * @param view
	 */
	public static void hideKeyboard(Context ctx, View view){
		if( null == view ) return;
		InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
	}
	
	public static boolean isLowScreen() {
		WindowManager wm = (WindowManager)baseContext.getSystemService(Context.WINDOW_SERVICE);
	    int w = wm.getDefaultDisplay().getWidth();
	    if(w <= 320 )
	    	return true;
	    else
	    	return false;
	}
	
	public static Drawable getDrawableFromPath(String path){
		
		Drawable cachedImage = null;
		
		File pic = new File(path);
 		if (pic.exists()) {
 			try {
 				cachedImage = Drawable.createFromPath(path);
	 			//文件存在但是读取出来为空的情况
	 			if (cachedImage==null) {
	 				Log.e("Global.getDrawableFromPath", "图片文件被损坏 null");
	 				//删除图片,等待下次重新下载
	 				FileUtil.delFile(path);
	 			}
	 		} catch (OutOfMemoryError e) {
				Log.e("Global.getDrawableFromPath", "Out of memory",e);
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
 		
 		return cachedImage;
	}
	
	/**
	 * 91桌面默认下载地址
	 * @param ctx
	 * @return
	 */
	public static String getHiLauncherDefaultDownUrl(Context ctx){
		
		return hilauncher_app_download_url;
	}
}