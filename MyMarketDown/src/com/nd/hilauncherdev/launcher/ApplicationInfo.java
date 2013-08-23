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

import android.content.ComponentName;
import android.content.ContentProviderOperation.Builder;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import com.nd.hilauncherdev.app.SerializableAppInfo;

/**
 * Represents an app in AllAppsView.
 */
public class ApplicationInfo extends CommonApplicationInfo {

	public ApplicationInfo() {
		itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
	}

	public ApplicationInfo(int type) {
		itemType = type;
	}

	public ApplicationInfo(ApplicationInfo info) {
		super(info);
	}

	/**
	 * Must not hold the Context.
	 */
	public ApplicationInfo(ResolveInfo info) {
		super(info);
	}

	/**
	 * Must not hold the Context.
	 */
	public ApplicationInfo(ResolveInfo info, IconCache iconCache) {
		this.componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);

		this.container = ItemInfo.NO_ID;
		this.setActivity(componentName);

		iconCache.getTitleAndIcon(this, info);
	}

	public ApplicationInfo(SerializableAppInfo info) {
		super(info);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof ApplicationInfo))
			return false;

		ApplicationInfo other = (ApplicationInfo) o;
		if (componentName != null) {
			return componentName.equals(other.componentName);
		}

		return id == other.id && itemType == other.itemType;
	}

	public Bitmap getIcon(IconCache iconCache) {
		if (iconBitmap == null) {
			iconBitmap = iconCache.getIcon(this);
		}
		return iconBitmap;
	}

	public ApplicationInfo copy() {
		return new ApplicationInfo(this);
	}
	
	public void onAddToDatabaseEx(Builder builder) {
		if (builder != null) {
			builder.withValue(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
    		if (!isGesture) {
    			builder.withValue(LauncherSettings.Favorites.CONTAINER, container);
    			builder.withValue(LauncherSettings.Favorites.SCREEN, screen);
    			/*
    			int[] xy = CellLayoutConfig.getXY(cellX, cellY);
    			int[] size = CellLayoutConfig.spanXYToWh(spanX, spanY, this);
    			builder.withValue(LauncherSettings.Favorites.X, xy[0]);
    			builder.withValue(LauncherSettings.Favorites.Y, xy[1]);
    			builder.withValue(LauncherSettings.Favorites.WIDTH, size[0]);
    			builder.withValue(LauncherSettings.Favorites.HEIGHT, size[1]);
    			*/
            }
			String titleStr = title != null ? title.toString() : null;
			builder.withValue(LauncherSettings.BaseLauncherColumns.TITLE,
					titleStr);
			String intentUri = intent != null ? intent.toUri(0) : null;
			builder.withValue(LauncherSettings.BaseLauncherColumns.INTENT,
					intentUri);
			if (customIcon) {
				builder.withValue(
						LauncherSettings.BaseLauncherColumns.ICON_TYPE,
						LauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
				byte[] data = flattenBitmap(iconBitmap);
				builder.withValue(LauncherSettings.Favorites.ICON, data);
			} else {
				if (onExternalStorage && !usingFallbackIcon) {
					byte[] data = flattenBitmap(iconBitmap);
					builder.withValue(LauncherSettings.Favorites.ICON, data);
				}
				builder.withValue(
						LauncherSettings.BaseLauncherColumns.ICON_TYPE,
						LauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
				if (iconResource != null) {
					builder.withValue(
							LauncherSettings.BaseLauncherColumns.ICON_PACKAGE,
							iconResource.packageName);
					builder.withValue(
							LauncherSettings.BaseLauncherColumns.ICON_RESOURCE,
							iconResource.resourceName);
				}
			}
		}

	}
}
