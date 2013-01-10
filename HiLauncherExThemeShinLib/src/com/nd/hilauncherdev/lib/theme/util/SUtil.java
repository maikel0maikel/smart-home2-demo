package com.nd.hilauncherdev.lib.theme.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;

/**
 * String工具类
 * 
 */
public final class SUtil {

	/**
	 * TAG
	 */
	private final static String TAG = "SUtil";

	/**
	 * CONNECT_TIMEOUT
	 */
	private final static int CONNECT_TIMEOUT = 1000 * 4;

	/**
	 * IFLAG
	 */
	private static final String IFLAG = "i";

	/**
	 * ICONST
	 */
	private static final int ICONST = 971496;

	
	private static final String[] MD5_KEY_ARRAY = new String[] {
		"0F224B212E3A404098EBDB61CAA79804",
		"1B49D2C733BA49D195F21BF69B0F354F",
		"1C2D85268E8A4EAD8D45C7014EF62F75",
		"2FACACD15F224A36B140C263E62C1A31",
		"3C19D53124714CD3BEA580580909CC0B",
		"8B2EF1ACA7244D8E8FD93606808E1898",
		"9AEB4E5D3B36400EB0CD1A52EA5D1812",
		"23F9ED791394425BA316D6B35072ECA7",
		"34ED6DF271934286BB689DE8F94DEDB6" };
	/**
	 * 判断字符串是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(CharSequence s) {
		if ((s == null) || (s.length() <= 0)) {
			return true;
		}
		return false;
	}

	/**
	 * 从通过column和cursor获取值
	 * 
	 * @param cursor
	 * @param columnName
	 * @return
	 */
	public static String getString(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndex(columnName);
		if (index != -1) {
			return cursor.getString(index);
		}
		return null;
	}

	/**
	 * 判断两字符串是否相等
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean equal(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		if ((s1 != null) && (s2 != null)) {
			return s1.equals(s2);
		}
		return false;
	}

	/**
	 * 颜色设置
	 * 
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static int setColorAlpha(int color, int alpha) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(alpha, r, g, b);
	}
	
	/**
	 * 取相反颜色
	 */
	public static int antiColorAlpha(int alpha, int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(alpha, 255 - r, 255 - g, 255 - b);
	}

	/**
	 * @param color
	 *            颜色转换
	 * @return
	 */
	public static String toHexColor(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return toHexColor(r, g, b);
	}

	/**
	 * 颜色格式转换
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static String toHexColor(int r, int g, int b) {
		StringBuffer result = new StringBuffer("#");
		if ((r >= 0) && (r <= 255)) {
			String tmp = Integer.toHexString(r);
			if (tmp.length() == 1) {
				result.append('0');
			}
			result.append(tmp);
		} else {
			result.append("00");
		}
		if ((g >= 0) && (g <= 255)) {
			String tmp = Integer.toHexString(g);
			if (tmp.length() == 1) {
				result.append('0');
			}
			result.append(tmp);
		} else {
			result.append("00");
		}
		if ((b >= 0) && (b <= 255)) {
			String tmp = Integer.toHexString(b);
			if (tmp.length() == 1) {
				result.append('0');
			}
			result.append(tmp);
		} else {
			result.append("00");
		}
		return result.toString();
	}

	/**
	 * 颜色解析
	 * 
	 * @param sColor
	 * @return
	 */
	public static int parseColor(String sColor) {
		int color = -1;
		try {
			color = Color.parseColor(sColor);
		} catch (Exception e) {
			Log.w(TAG, "color parse error:" + sColor);
		}
		return color;
	}

	/**
	 * 颜色转换
	 * 
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static int alphaColor(int color, int alpha) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		return Color.argb(alpha, r, g, b);
	}

	/**
	 * 通过url获取内容
	 * 
	 * @param surl
	 * @return
	 */
	public static String getURLContent(String surl) {
		InputStream is = null;
		try {
			URL url = new URL(Utf8URLencode(surl));
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.setConnectTimeout(CONNECT_TIMEOUT);
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
			Log.w(TAG, "getURLContent:" + e.getMessage());
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

	/**
	 * 获取语言
	 * 
	 * @param language
	 * @return
	 */
	public static String getLanguageCode(String language) {
		if ("zh".equalsIgnoreCase(language)) {
			return "CHS";
		} else {
			return "ENU";
		}
	}
	
	/**
	 * 获取当前语言
	 * @return
	 */
    public static String getCurrentLanguage() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        return lang;
    }
  
    public static boolean isZhLanguage(){
    	String lang=getCurrentLanguage();
		if( lang.equalsIgnoreCase( "zh")) {
			return true;
		}
    	return false;
    }

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFielSuffix(String fileName) {
		int end = fileName.lastIndexOf('.');
		if (end >= 0) {
			return fileName.substring(end + 1);
		}
		return null;
	}

	/**
	 * 判断是否有sd卡
	 * 
	 * @return
	 */
	public static boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * utf8编码
	 * 
	 * @param text
	 * @return
	 */
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
	 * 删除文件，可以是单个文件或文件夹
	 * 
	 * @param fileName
	 *            待删除的文件名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {

				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (files == null) {
			return true;
		}
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param path
	 * @return
	 */
	public static String renameRes(String path) {
		if (path == null) {
			return null;
		}
		return path.replace(".png", ".a").replace(".jpg", ".b");
	}
	
	public static boolean isPicturePath(String path) {
		if (path == null) {
			return false;
		}
		return path.contains(".png")||path.contains(".jpg");
	}

	/**
	 * @param src
	 * @param flag
	 * @return
	 */
	public static boolean checkThemeType(String src, String flag) {
		String[] arr = flag.split("|");
		for (int i = 0; i < arr.length; i++) {
			if (src.indexOf(arr[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * code1>code2 返回1，相等0，小于-1
	 * 
	 * @param code1
	 * @param code2
	 * @return
	 */
	public static int compareVerCode(String code1, String code2) {
		float f1 = 0f;
		float f2 = 0f;
		try {
			f1 = Float.parseFloat(code1);
		} catch (Exception e) {

		}
		try {
			f2 = Float.parseFloat(code2);
		} catch (Exception e) {

		}
		if (f1 > f2) {
			return 1;
		} else if (f1 < f2) {
			return -1;
		} else {
			return 0;
		}

	}

	/**
	 * 返回大小写敏感的程序key
	 * @param info
	 * @return
	 */
	public static String getCaseSensitiveAppKey( ResolveInfo info ) {
		return info.activityInfo.packageName + "|" + info.activityInfo.name;
	}
	/**
	 * @param info
	 * @return
	 */
	public static String getAppKey(ResolveInfo info) {
		String appKey = (info.activityInfo.packageName + "|" + info.activityInfo.name).toLowerCase();
		//为兼容特殊机型的电话和联系人包名的特殊处理(如HTC SENSE)
//		appKey = (U.getSpecialPhoneAndContacts(appKey)==null) ? appKey :U.getSpecialPhoneAndContacts(appKey);
		return appKey;
	}

	
	/**
	 * @param info
	 * @return
	 */
	public static String getAppKey(ComponentName info) {
		String appKey = (info.getPackageName() + "|" + info.getClassName()).toLowerCase();
		//为兼容特殊机型的电话和联系人包名的特殊处理(如HTC SENSE)
//		appKey = (U.getSpecialPhoneAndContacts(appKey)==null) ? appKey :U.getSpecialPhoneAndContacts(appKey);
		return appKey;
	}

	public static String getCaseSensitiveAppKey( ComponentName info ) {
		String appKey = info.getPackageName() + "|" + info.getClassName();
		return appKey;
	}
	/**
	 * @param info
	 * @return
	 */
	public static String getAppKey(ActivityInfo info) {
		String appKey = (info.packageName + "|" + info.name).toLowerCase();
		//为兼容特殊机型的电话和联系人包名的特殊处理(如HTC SENSE)
//		appKey = (U.getSpecialPhoneAndContacts(appKey)==null) ? appKey :U.getSpecialPhoneAndContacts(appKey);
		return appKey;
	}
	
	/**
	 * 文件
	 * 
	 * @param ctx
	 * @param packname
	 * @return
	 */
	public static boolean isPackageExist(Context ctx, String packname) {
		try {
			ctx.createPackageContext(packname, 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 获取memory 信息
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getMemoryInfo(Context ctx) {
		StringBuffer buffer = new StringBuffer();
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		buffer.append("\nFree:---->").append(outInfo.availMem >> 10).append("k");
		buffer.append("\nFree:---->").append(outInfo.availMem >> 20).append("M");
		buffer.append("\nlowMemory:---->").append(outInfo.lowMemory);
		return buffer.toString();
	}

	/**
	 * 转null为空
	 * 
	 * @param s
	 * @return
	 */
	public static String u(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	/**
	 * OpenHome key
	 * 
	 * @param packname
	 * @param skey
	 * @return
	 */
	public static String parseKey(String packname, String skey) {
		return IFLAG + (Math.abs(packname.hashCode() + skey.hashCode() + ICONST));
	}

	/**
	 * OpenHome 2.6.2版本的key
	 * 
	 * @param actName
	 * @param packName
	 * @param id
	 * @return
	 */
	public static String parseKeyNew(String actName, String packName, String id) {
		actName = actName.toLowerCase();
		packName = packName.toLowerCase();
		id = id.toLowerCase();
		int ret = id.hashCode() + (-25);
		try {
			char[] buf = new char[actName.length()]; // v0
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (char) (actName.toCharArray()[i] << 2);
			}

			for (int i = 0; i < buf.length; i++) {
				if (i % buf[i] == 0) {
					ret += buf[i];
				} else {
					ret -= buf[i];
				}
			}
			ret = ret << 2;
			ret = ret >> 3;
			char[] buf2 = new char[packName.length()];
			for (int i = 0; i < buf2.length - 2; i++) {
				buf2[i] = (char) ((packName.toCharArray()[i] + buf[i % buf.length]) << 3);
				if ((buf2[i] * 2 - buf[i] * 2) % 2 == 0) {
					ret += buf2[i];
				} else {
					ret -= buf2[i];
				}
			}
			ret = Math.abs(ret);
		} catch (Exception e) {
			return "";
		}
		return "i" + ret;
	}

	/**
	 * getKeysByValue
	 * 
	 * @param map
	 * @param value
	 * @return
	 */
	public static List<String> getKeysByValue(HashMap<String, String> map, String value) {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();
			String v = map.get(k);
			if (equal(v, value)) {
				list.add(k);
			}
		}
		return list;
	}

	/**
	 * parseInt
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * parseLong
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static long parseLong(String str, Long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 获得节点text
	 * 
	 * @param node
	 * @return
	 */
	public static String getNodeText(Node node) {
		String text = null;
		Node tNode = node.getFirstChild();
		if ((tNode != null) && "#text".equals(tNode.getNodeName())) {
			text = tNode.getNodeValue();
			if (text != null) {
				text = text.trim();
			}
		}
		return text;
	}

	public static String getResourcesString(Context ctx, String name) {
		final Resources res = ctx.getResources();
		int resId = res.getIdentifier(name.toLowerCase(), "string", null);
		if (resId == 0) {
			return null;
		}
		return res.getString(resId);
	}
	
	public static void printBundle(Bundle bundle){
        if(bundle != null){
            Log.w("TAG", "bundle size:" + bundle.size());
            Iterator<String> it =  bundle.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                Log.w("TAG", key + "=" + bundle.get(key));
            }
        }
        else{
            Log.w("TAG", "bundle=null.");
        }
    }
	
	/**
     * 返回-1为非alpha值
     * @param src
     * @return
     */
    public static int parseAlpha(String src) {
        int ret = -1;
        try {
            ret = Integer.parseInt(src);
        } catch (Exception e) {
        }
        if ((ret >= 0) && (ret <= 255)) {
            return ret;
        }
        return -1;
    }
      
//    public static boolean checkSharedPreferences(Context ctx, String name){
//    	File f = ctx.getSharedPrefsFile(name);
//    	if(f == null || !f.exists()){
//    		return false;
//    	}
//    	return true;
//    }
    
    /**
     * 字符串对象是否为Null或空
     * @param str
     * @return 为null或空则返回true，否则返回false
     */
    public static boolean isEmptyOrNull( String str ) {
    	if( null == str ) return true;
    	if( str.trim().equals( "" ) ) return true;
    	return false;
    }
    
    /**
	 * 生成指定范围内的随机整数(1至number随机数)
	 */
	public static int generateRandomInNum(int number) {
		double a = Math.random()*number;  
		a = Math.ceil(a);  
		int randomNum = new Double(a).intValue();  
		return randomNum;
	}
	
	/**
	 * 生成指定范围内的随机整数(0至max随机数,不包含max)
	 */
	public static int generateRandom(int max) {
		Random random = new Random();
		return random.nextInt(max);
	}
	
    /**
     * 得到文件的大小
     * @param ctx
     * @param str
     * @return
     */
    public static String formatFileSize(Context ctx,String str){
    	long size = 0L;
    	try{
    		size = Long.parseLong(str);
    	} catch( NumberFormatException e ) {
    		size = 0L;
    	}
    	return Formatter.formatFileSize(ctx, size );
    }
    
    /**
     * 判断传入的字符串是否是数字类型
     * @param sNum
     * @return
     */
    public static boolean isNumberic( String sNum ) {
    	try{
    		Float.parseFloat( sNum );
    		return true;
    	} catch( NumberFormatException e ) {
    		return false;
    	}
    }
    
    /**
     * 获取非null的字符串
     * @param str
     * @return 为null则返回空字符串，否则返回字符串本身
     */
    public static String checkString( String str ) {
    	if( null == str ) return "";
    	return str;
    }
    
    /**
     * 验证注册名称是否正确
     * @param str
     * @return
     */
    public static boolean checkOnlyContainCharaterAndNumbers( String str ) {
    	Pattern p = Pattern.compile( "^[A-Za-z0-9]+$" );
    	Matcher m = p.matcher( str );
    	return m.matches();
    }
    

    /**
     * 验证邮件地址格式是否正确
     * @param str
     * @return
     */
    public static boolean checkValidMailAddress( String str ) {
        Pattern p1 = Pattern.compile("\\w+@(\\w+\\.)+[a-z]{2,3}");
        Matcher m = p1.matcher( str );
        return m.matches();
    }

    /**
     * <br>Description:文件大小转换
     * <br>Author:caizp
     * <br>Date:2011-5-16下午06:22:02
     * @param num
     * @param scale
     * @return
     */
    public static String parseLongToKbOrMb(long num, int scale) {
		float scaleNum;
		switch (scale) {
		case 0:
			scaleNum = 1;
			break;
		case 1:
			scaleNum = 10f;
			break;
		case 2:
			scaleNum = 100f;
			break;
		case 3:
			scaleNum = 1000f;
			break;
		case 4:
			scaleNum = 10000f;
			break;
		default:
			scaleNum = 1;
		}
		float n = num;
		if (n < 1024) {
			return Math.round(n * scaleNum) / scaleNum + "B";
		}
		n = n / 1024;
		if (n < 1024) {
			return Math.round(n * scaleNum) / scaleNum + "KB";
		}
		n = n / 1024;
		if (n < 1024) {
			return Math.round(n * scaleNum) / scaleNum + "MB";
		}
		n = n / 1024;
		return Math.round(n * scaleNum) / scaleNum + "GB";
	}
    
    /**
	 * 将字节数转换为GB,MB,KB单位
	 * <br>Author:zhuchenghua
	 * @param bytes
	 * @param faction 保留小数位的位数
	 * @return
	 */
	public static String getComputerSize(long bytes,int faction)
	{
		
		if(bytes/Math.pow(1000, 2)>1000) //以GB为单位
			return formatNum2String(bytes/Math.pow(1000, 3),faction)+"GB";
		else if(bytes/1000>1000) //以MB为单位
			return formatNum2String(bytes/Math.pow(1000, 2),faction)+"MB";
		else if(bytes/1000>0) 					//以KB为单位
			return bytes/1000+"KB"; 
		else		//以B为单位
			return bytes+"B";
	}
	
	/**
	 * 格式化数字，并转成字符串，
	 * <br>Author:zhuchenghua
	 * @param num
	 * @param pattern 格式：如".##"表示保留两位小数,"#.##"表示整数位一位，小数位两位
	 * @param minFaction 多少位小数
	 * @return
	 */
	public static String formatNum2String(double num,int faction)
	{
		NumberFormat numFormat=NumberFormat.getInstance();
		numFormat.setMinimumFractionDigits(faction);
		numFormat.setMaximumFractionDigits(faction);
		return numFormat.format(num);
	}

	private static final char[] hexDigit = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

	private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }
	
	 /**
     * 将字符串编码成 Unicode 。
     * @param theString 待转换成Unicode编码的字符串。
     * @param escapeSpace 是否忽略空格。
     * @return 返回转换后Unicode编码的字符串。
     */
    public static String toUnicode(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) 
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                case '&':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    /**
     * 
     * <br>Description:把16进制字符串转换成字节数组
     * <br>Author:zhenghonglin
     * <br>Date:2011-11-28下午04:52:48
     * @param hex
     * @return
     */
	public static byte[] hexStringToByte(String hex) { 
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	} 
	
	private static byte toByte(char c) { 
	    byte b = (byte) "0123456789ABCDEF".indexOf(c); 
	    return b; 
	}
	
	/**
	 * 将字符串转成BCD码，并以字符串形式返回
	 * @param str 传入字符串
	 * @return
	 */
	
	public static  String str2bcd(String str){
		StringBuffer buff = null;
		try {
			byte[] bytes = str.getBytes("US-ASCII");
			buff = new StringBuffer(bytes.length * 2);
			String temp;
			for (int i = 0; i < bytes.length; i++) {
				temp = Integer.toHexString(bytes[i]);
				// byte是两个字节的,而上面的Integer.toHexString会把字节扩展为4个字节
				buff.append(temp.length() > 2 ? temp.substring(6, 8) : temp);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buff.toString();		
	}
	/**
	 * 返回非空字符串
	 * @param str
	 * @return
	 */
	public static String getNotNullString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

	/**
	 * 获取随机数，0 和 max之间 ，不包含max
	 * @param max
	 * @return
	 */
	public static int getRandom(int max){
		if(max <= 0) throw new IllegalArgumentException("Error argument , must large than zero");
		return Integer.parseInt(String.valueOf(System.currentTimeMillis() % max)) ;
	}
	
	/**
	 * 获取随即的加密串
	 */
	public static String getMD5Key(int index){
		return MD5_KEY_ARRAY[index] ;
	}
	
	 
    /**
     * 自动分割文本
     * @param content 需要分割的文本
     * @param p  画笔，用来根据字体测量文本的宽度
     * @param width 最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    public static String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if(textWidth <= width) {
            return new String[]{content};
        }
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        
        int start = 0, end = 1, i = 0;
        

        while(start < length) {
            if(p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if(end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }
    
    public static String[] splitByWord( String content, Paint p, float width ) {
    	 float textWidth = p.measureText(content);
         if(textWidth <= width) {
             return new String[]{content};
         }
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        if( lines > 1 ) { 
        	ArrayList<String> listWords = parseEnglishWord( content );
        	String[] lineTexts = new String[lines];
        	int size = listWords.size();
        	int pos = -1;
        	if( size == 1 ) {
        		pos = content.indexOf( listWords.get(0) );
        	} else if( size > 1 ) {
        		pos = content.indexOf( listWords.get(1)  );
        	}
        	if( pos != -1 ) {                
        		lineTexts[0] = content.substring( 0, pos );
        		lineTexts[1] = content.substring( pos );
        		return lineTexts;
        	} 
        }
        return null;
    }
    
    public static ArrayList<String> parseEnglishWord( String content ) { 
    	ArrayList<String> list = new ArrayList<String>();
        String s = "\\d+.\\d+|\\w+";
        Pattern  pattern=Pattern.compile(s);  
        Matcher  ma=pattern.matcher(content.trim());  
        while(ma.find()){  
            list.add( ma.group());  
        }  
        return list;
    }
    
    /**
	  * 获取Component中的字串
	  */
   public static String getIntentComponentString(String s) {
	   int index = s.indexOf("component=");
	   if (index < 0)
		   return null;
	   s = s.substring(index);
	   int eIndex = s.indexOf(";");
	   if (eIndex < 0) {
		   return s;
	   } else {
		   s = s.substring(0, eIndex);
		   return s;
	   }		   
	}
}
