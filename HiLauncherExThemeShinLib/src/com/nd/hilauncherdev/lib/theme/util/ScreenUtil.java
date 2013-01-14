package com.nd.hilauncherdev.lib.theme.util;


import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕显示相关 <br>
 * Author:ryan <br>
 * Date:2012-8-13下午08:51:15
 */
public class ScreenUtil {

	private static ScreenUtil setting;

	private static float currentDensity = 0;

	private static Object lock = new Object();

	private DisplayMetrics metrics = new DisplayMetrics();

	private int iconSize = 48;

	private int notification_height = 25;

	private int max_dockbar_height = 455;

	private int[] wallpaperWH = { 640, 480 };

	private int[] screenWH = { 320, 480 };

	/**
	 * 大屏幕的高度
	 */
	private final static int LARDGE_SCREEN_HEIGHT = 960;

	/**
	 * 大屏幕的宽度
	 */
	final static int LARDGE_SCREEN_WIDTH = 720;
	
	/**
	 * 
	 */
	private final static int M9_SCREEN_WIDTH = 640;

	private ScreenUtil() {
		loadSetting();
	}

	public static void recyle() {
		setting = null;
	}

	public static ScreenUtil getInstance() {
		synchronized (lock) {
			if (setting == null) {
				setting = new ScreenUtil();
			}
		}
		return setting;
	}

	private void loadSetting() {
		final Context ctx = HiLauncherThemeGlobal.getContext();
		final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
		display.getMetrics(metrics);

		boolean isPortrait = display.getWidth() < display.getHeight();
		final int width = isPortrait ? display.getWidth() : display.getHeight();
		final int height = isPortrait ? display.getHeight() : display.getWidth();
		metrics.widthPixels = width;
		metrics.heightPixels = height;

		wallpaperWH[0] = width * 2;
		wallpaperWH[1] = height;
		screenWH[0] = width;
		screenWH[1] = height;

		if (wallpaperWH[0] <= 0 || wallpaperWH[1] <= 0) {
			wallpaperWH[0] = metrics.widthPixels * 2;
			wallpaperWH[1] = metrics.heightPixels;
		}

		notification_height = 25;
		// StringUtil.parseInt(ctx
		// .getString(R.string.notification_height), 25);

		max_dockbar_height = metrics.heightPixels - notification_height;
		iconSize = (int) (48 * metrics.density);
	}

	public void setNotificationHeight(int notificationHeight) {
		notification_height = notificationHeight;
		max_dockbar_height = metrics.heightPixels - notification_height;
	}

	public DisplayMetrics getMetrics() {
		return metrics;
	}

	public int getIconSize() {
		return iconSize;
	}

	public int getNotificationHeight() {
		return notification_height;
	}

	public int getMaxDockbarHeight() {
		return max_dockbar_height;
	}

	public int[] getWallpaperWH() {
		return wallpaperWH;
	}

	public int[] getScreenWH() {
		return screenWH;
	}

	public float getDensity() {
		return metrics.density;
	}

	public int getDensityDpi() {
		return metrics.densityDpi;
	}

	public DisplayMetrics getDisplayMetrics() {
		return metrics;
	}

	/**
	 * 
	 * <br>
	 * Description: 是否大屏幕 <br>
	 * SimpleHome <br>
	 * Date:2011-8-16下午06:28:35
	 * 
	 * @param context
	 * @return
	 */
	public boolean isLargeScreen() {
		int w = getScreenWH()[0];
		if (w >= 480)
			return true;
		else
			return false;
	}

	/**
	 * 是否是更大的屏幕<br>
	 * 高度大于 960<br>
	 * 宽度不等于640<br>
	 */
	public boolean isExLardgeScreen() {
		int[] wh = getScreenWH();
		//return wh[0] >= LARDGE_SCREEN_WIDTH && wh[1] >= LARDGE_SCREEN_HEIGHT;
		return wh[1] >= LARDGE_SCREEN_HEIGHT && wh[0] != M9_SCREEN_WIDTH ;
	}

	/**
	 * 
	 * <br>
	 * Description: 是否小屏幕 <br>
	 * SimpleHome <br>
	 * Date:2011-8-16下午06:28:44
	 * 
	 * @param context
	 * @return
	 */
	public boolean isLowScreen() {
		int w = getScreenWH()[0];
		if (w < 320)
			return true;
		else
			return false;
	}

	/**
	 * 返回屏幕尺寸(宽)
	 * 
	 * @param context
	 * @return
	 */
	public static int getCurrentScreenWidth(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		boolean isLand = isOrientationLandscape(context);
		if (isLand) {
			return metrics.heightPixels;
		}
		return metrics.widthPixels;
	}

	/**
	 * 返回屏幕尺寸(高)
	 * 
	 * @param context
	 * @return
	 */
	public static int getCurrentScreenHeight(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		boolean isLand = isOrientationLandscape(context);
		if (isLand) {
			return metrics.widthPixels;
		}
		return metrics.heightPixels;
	}

	/**
	 * 返回屏幕尺寸
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getResources().getDisplayMetrics();
	}

	/**
	 * 判断是否横屏
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOrientationLandscape(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}

	public static int dip2px(Context context, float dipValue) {
		if (currentDensity > 0)
			return (int) (dipValue * currentDensity + 0.5f);

		currentDensity = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * currentDensity + 0.5f);
	}

}
