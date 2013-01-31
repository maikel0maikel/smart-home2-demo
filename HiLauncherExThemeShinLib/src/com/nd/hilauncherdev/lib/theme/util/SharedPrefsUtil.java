package com.nd.hilauncherdev.lib.theme.util;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {

	public static final String prefsFile = "hi_themelib";

	/** 91桌面安装完要自动应用的主题ID */
	public static final String KEY_AUTO_APPLY_THEMEID = "auto_apply_themeid";

	private static SharedPrefsUtil prefs;
	private static SharedPreferences sp;
	private Context ctx;
	
	public static synchronized SharedPrefsUtil getInstance(Context ctx){
		if(prefs == null){
			prefs = new SharedPrefsUtil(ctx);
		}
		return prefs;
	}
	
	private SharedPrefsUtil(Context ctx){
		this.ctx = ctx;
		sp = getRightSharedPreferences();
	}
	
	private SharedPreferences getRightSharedPreferences() {
		return ctx.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
	}

	public void setString(String sKeyName, String sKeyValue) {
		sp.edit().putString(sKeyName, sKeyValue).commit();
	}

	public String getString(String sKeyName, String defValue) {
		return sp.getString(sKeyName, defValue);
	}

	public void setBoolean(String sKeyName, boolean bKeyValue) {
		sp.edit().putBoolean(sKeyName, bKeyValue).commit();
	}

	public boolean getBoolean(String sKeyName, boolean defValue) {
		return sp.getBoolean(sKeyName, defValue);
	}

	public void setInt(String sKeyName, int iKeyValue) {
		sp.edit().putInt(sKeyName, iKeyValue).commit();
	}

	public int getInt(String sKeyName, int defValue) {
		return sp.getInt(sKeyName, defValue);
	}

	public void setLong(String sKeyName, long iKeyValue) {
		sp.edit().putLong(sKeyName, iKeyValue).commit();
	}

	public long getLong(String sKeyName, long defValue) {
		return sp.getLong(sKeyName, defValue);
	}

	public void setFloat(String sKeyName, float defValue) {
		sp.edit().putFloat(sKeyName, defValue).commit();
	}

	public float getFloat(String sKeyName, float defValue) {
		return sp.getFloat(sKeyName, defValue);
	}
}
