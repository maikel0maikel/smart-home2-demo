/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nd.hilauncherdev.launcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.StringUtil;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class IconCache {
	static final String TAG = "Launcher.IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

	/**
	 * 缓存的应用程序图标与名称 <br>
	 * Author:ryan <br>
	 * Date:2012-7-25上午11:43:19
	 */
	public static class CacheEntry {
		public Bitmap icon;
		public String title;
		/**
		 * 是否为主题图标，主要用于判断是否使用蒙版
		 */
		public boolean isThemeIcon;
		/**
		 * 当一个Package包含两个Main的时候，Icon和Title均不可用
		 */
		public boolean isDirty;
	}

	private Bitmap mDefaultIcon;
	private final Context mContext;
	private final PackageManager mPackageManager;
	// private final Utilities.BubbleText mBubble;
	private final Map<ComponentName, CacheEntry> mCache = new ConcurrentHashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
	/**
	 * 用于只有PackageName的应用使用，如"我的存储"中的内容
	 */
	private final Map<String, CacheEntry> mPackageCache = new ConcurrentHashMap<String, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);

	public IconCache(Context context) {
		mContext = context;
		mPackageManager = context.getPackageManager();
		// mBubble = new Utilities.BubbleText(context);
	}

	private Bitmap makeDefaultIcon() {
		Drawable d = mPackageManager.getDefaultActivityIcon();
		Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1), Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(c);
		return b;
	}

	/**
	 * Remove any records for the supplied ComponentName.
	 */
	public void remove(ComponentName componentName) {
		mCache.remove(componentName);
		mPackageCache.remove(componentName.getPackageName());
	}

	/**
	 * Empty out the cache.
	 */
	public void flush() {
		mCache.clear();
		mPackageCache.clear();
	}

	/**
	 * <br>
	 * Description: 刷新主题图标 <br>
	 * Author:caizp <br>
	 * Date:2012-9-6下午03:58:03
	 */
	public void refreshThemeIcon() {
		/*
		boolean isLargeIconTheme = false;
		int largeIconSize = (int) mContext.getResources().getDimension(R.dimen.app_background_size);
		for (int i = 0; i < ThemeData.iconKeys.length; i++) {
			ComponentName comp = ThemeIconIntentAdaptation.getInstance().getActualComponent(ThemeData.iconKeys[i]);
			if (comp == null)
				continue;
			CacheEntry entry = mCache.get(comp);
			if (null != entry) {
				Drawable d = ThemeManager.getThemeAppIcon(StringUtil.getAppKey(comp));
				if (null == d) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setComponent(comp);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					ResolveInfo info = mPackageManager.resolveActivity(intent, 0);
					if (null == info)
						continue;
					d = info.loadIcon(mPackageManager);
					entry.isThemeIcon = false;
					entry.icon = BitmapUtils.createIconBitmapThumbnail(d, mContext);
				} else {
					entry.isThemeIcon = true;
					entry.icon = BitmapUtils.drawable2Bitmap(d);
					if (!isLargeIconTheme) {// 判断是否包含大图标的主题并做标记 caizp 2012-9-6
						if (null != entry.icon && entry.icon.getWidth() >= largeIconSize && entry.icon.getHeight() >= largeIconSize) {
							isLargeIconTheme = true;
						}
					}
				}
				// 更新动态图标
				DynamicIconHelper.getInstance().updateDynamicIcon(mContext, entry, comp, false);
			}
		}
		if (!isLargeIconTheme) {// 无主题图标时，判断图标蒙板背景
			Drawable maskBg = ThemeManager.getThemeAppIcon(ThemeData.PANDA_ICON_BACKGROUND_MASK);
			if (null != maskBg) {
				if (null != maskBg && maskBg.getIntrinsicWidth() >= largeIconSize && maskBg.getIntrinsicHeight() >= largeIconSize) {
					isLargeIconTheme = true;
				}
			}
		}
		SettingsPreference.getInstance().setLargeIconTheme(isLargeIconTheme);
		*/
	}

	/**
	 * 加载时候优先初始化 <br>
	 * Description:TODO 方法功能描述 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:29:16
	 */
	public void getTitleAndIcon(ApplicationInfo application) {
		if (application == null || application.intent == null || application.componentName == null)
			return;

		final PackageManager pm = mContext.getPackageManager();
		ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(application.intent, pm);
		if (resolve == null)
			return;

		CacheEntry entry = cacheLocked(application.title, application.componentName, resolve);
		if (entry != null) {
			if (StringUtil.isEmpty(application.title)) {
				application.title = entry.title;
			}

			if (!application.customIcon) {
				application.iconBitmap = entry.icon;
				application.useIconMask = !entry.isThemeIcon;
			}
		}
	}

	/**
	 * Fill in "application" with the icon and label for "info."
	 */
	public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info) {
		if (null == application) {
			return;
		}

		CacheEntry entry = cacheLocked(application.title, application.componentName, info);
		if (entry != null) {
			if (StringUtil.isEmpty(application.title)) {
				application.title = entry.title;
			}

			if (!application.customIcon) {
				application.iconBitmap = entry.icon;
				application.useIconMask = !entry.isThemeIcon;
			}
		}
	}

	/**
	 * 兼容接口 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:17:23
	 * 
	 * @param application
	 * @return
	 */
	public Bitmap getIcon(ApplicationInfo application) {
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(application.intent, 0);
		ComponentName component = application.intent.getComponent();

		if (resolveInfo == null || component == null) {
			return getmDefaultIcon();
		}

		CacheEntry entry = cacheLocked(component, resolveInfo);
		application.useIconMask = !entry.isThemeIcon;
		return entry.icon;
	}

	/**
	 * 搜索结果使用 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:20:55
	 */
	public Bitmap getIcon(Intent intent) {
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
		ComponentName component = intent.getComponent();

		if (resolveInfo == null || component == null) {
			return getmDefaultIcon();
		}

		CacheEntry entry = cacheLocked(component, resolveInfo);
		return entry.icon;
	}

	/**
	 * 重命名的不取名称 <br>
	 * Author:ryan <br>
	 * Date:2012-5-9下午10:34:34
	 */
	private CacheEntry cacheLocked(CharSequence title, ComponentName componentName, ResolveInfo info) {
		if (StringUtil.isEmpty(title)) {
			return cacheLocked(componentName, info);
		}

		if (componentName == null) {
			return null;
		}
		CacheEntry entry = mCache.get(componentName);
		if (entry == null) {
			entry = new CacheEntry();
			mCache.put(componentName, entry);
			entry.title = title.toString();
			/*
			Drawable d = ThemeManager.getThemeAppIcon(StringUtil.getAppKey(componentName));
			if (null == d) {
				d = info.loadIcon(mPackageManager);
				entry.isThemeIcon = false;
				entry.icon = BitmapUtils.createIconBitmapThumbnail(d, mContext);
			} else {
				entry.isThemeIcon = true;
				entry.icon = BitmapUtils.drawable2Bitmap(d);
			}
			*/
			// 更新动态图标
			//DynamicIconHelper.getInstance().updateDynamicIcon(mContext, entry, componentName, false);
		}
		return entry;
	}

	/**
	 * 获取应用程序图标与名称 <br>
	 * Author:ryan <br>
	 * Date:2012-11-16下午06:19:20
	 */
	private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info) {
		CacheEntry entry = mCache.get(componentName);
		if (entry == null) {
			entry = new CacheEntry();

			mCache.put(componentName, entry);
			// componentName为空但PackageName不为空说明一个Package带多个Main，
			if (mPackageCache.get(componentName.getPackageName()) != null) {
				CacheEntry dirtyEntry = mPackageCache.get(componentName.getPackageName());
				dirtyEntry.isDirty = true;
			} else {
				mPackageCache.put(componentName.getPackageName(), entry);
			}
			entry.title = info.loadLabel(mPackageManager).toString();
			if (entry.title == null) {
				entry.title = info.activityInfo.name;
			}
			/*
			Drawable d = ThemeManager.getThemeAppIcon(StringUtil.getAppKey(componentName));
			if (null == d) {
				d = info.loadIcon(mPackageManager);
				entry.isThemeIcon = false;
				entry.icon = BitmapUtils.createIconBitmapThumbnail(d, mContext);
			} else {
				entry.isThemeIcon = true;
				entry.icon = BitmapUtils.drawable2Bitmap(d);
			}
			*/
			// 更新动态图标
			//DynamicIconHelper.getInstance().updateDynamicIcon(mContext, entry, componentName, false);
		}
		return entry;
	}

	public Bitmap getCachedIcon(ComponentName cn) {
		if (cn == null)
			return null;

		CacheEntry entry = mCache.get(cn);

		return entry == null ? null : entry.icon;
	}

	public String getCachedTitle(ComponentName cn) {
		if (cn == null)
			return null;

		CacheEntry entry = mCache.get(cn);

		return entry == null ? null : entry.title;
	}

	/**
	 * 创建缓存 <br>
	 * Author:ryan <br>
	 * Date:2012-7-27上午10:03:08
	 */
	public void makeCache(ApplicationInfo app) {
		if (app == null)
			return;

		if (app.intent == null || app.intent.getComponent() == null)
			return;

		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(app.intent, 0);
		if (resolveInfo == null)
			return;

		this.getTitleAndIcon(app, resolveInfo);
	}

	/**
	 * 通过包名获取图标与标题信息<br>
	 * 1. 图标未加载时返回null<br>
	 * 2. 脏数据，即一个package包含两个main，返回null<br>
	 * 3. 无此数据，返回null<br>
	 * <br>
	 * Author:ryan <br>
	 * Date:2012-7-25下午09:31:26
	 * 
	 * @param packageName
	 *            应用程序包名
	 */
	public CacheEntry getPackageIconAndTitle(String packageName) {
		CacheEntry cache = mPackageCache.get(packageName);
		if (cache == null)
			return null;

		if (cache.isDirty)
			return null;

		return cache;
	}
	
	/**
	 * 刷新单个应用图标
	 */
	public Bitmap refreshTheCache(ApplicationInfo app) {
		if (app == null)
			return null;

		if (app.intent == null || app.intent.getComponent() == null)
			return null;

		CacheEntry ce = mCache.get(app.intent.getComponent());
		if (ce == null)
			return null;
		
		final ResolveInfo resolveInfo = mPackageManager.resolveActivity(app.intent, 0);
		if (resolveInfo == null)
			return null;
		/*
		Drawable d = ThemeManager.getThemeAppIcon(StringUtil.getAppKey(app.intent.getComponent()));
		if (null == d) {
			d = resolveInfo.loadIcon(mPackageManager);
			ce.isThemeIcon = false;
			ce.icon = BitmapUtils.createIconBitmapThumbnail(d, mContext);
		} else {
			ce.isThemeIcon = true;
			ce.icon = BitmapUtils.drawable2Bitmap(d);
		}
		*/
		return ce.icon;
	}

	/**
	 * 延迟加载 <br>
	 * Author:ryan <br>
	 * Date:2012-10-30下午08:06:20
	 * 
	 * @return
	 */
	private Bitmap getmDefaultIcon() {
		if (mDefaultIcon == null || mDefaultIcon.isRecycled()) {
			mDefaultIcon = makeDefaultIcon();
		}

		return mDefaultIcon;
	}

	public CacheEntry getCacheEntry(ComponentName cn) {
		return mCache.get(cn);
	}
}
