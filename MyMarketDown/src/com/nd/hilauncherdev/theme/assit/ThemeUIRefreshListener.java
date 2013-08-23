package com.nd.hilauncherdev.theme.assit;


/**
 * <br>Title: 主题应用UI刷新接口
 * <br>Author:caizp
 * <br>Date:2012-7-14下午02:26:37
 */
public interface ThemeUIRefreshListener {
	
	/**
	 * <br>
	 * Description: 桌面各部件主题UI刷新。<br>
	 * 1.调用{@link ThemeManager}.getThemeAppIcon()方法可获取主题图标,<br>
	 * 	   调用{@link ThemeManager}.getThemeDrawable()方法可获取主题图片<br>
	 * 2.也可通过调用{@link ThemeManager}.loadThemeResource()方法自动换肤<br>
	 * 3.实现该接口后请调用{@link ThemeUIRefreshAssit}.registerRefreshListener()方法注册监听。<br>
	 * Author:caizp <br>
	 * Date:2012-7-14下午02:19:37
	 */
	public void applyTheme();
	
}
