package com.nd.hilauncherdev.myphone.nettraffic.firewall;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.basecontent.HiActivity;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingItem;

public class FireWallMainActivity extends HiActivity {

	private ListView listview;
	private ImageView safeBackImg;
	private View slidingLoading;
	private View commonLoadingView;
	private View commonNoDataView;

	private List<FireWallAppItem> appCacheList = new ArrayList<FireWallAppItem>();
	private AppViewAdapter appAdapter;

	private LayoutInflater inflater;

	private boolean isModify = false;

	private int selectUid = -1;

	HashSet<String> allUidSet = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.net_traffic_firewall_list);

		selectUid = getIntent().getIntExtra("appUid", -1);

		inflater = getLayoutInflater();
		listview = (ListView) findViewById(R.id.firewall_app_listview);
		safeBackImg = (ImageView) findViewById(R.id.safe_back_btn);
		slidingLoading = findViewById(R.id.sliding_loading);

		// 数据加载View
		commonLoadingView = ViewFactory.getNomalErrInfoView(this, slidingLoading, ViewFactory.LOADING_DATA_INFO_VIEW);
		commonNoDataView = ViewFactory.getNoDataInfoView(this, slidingLoading, 0, R.string.safe_notify_firewall_forbid_nodata);
		commonNoDataView.setVisibility(View.GONE);

		safeBackImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitActivity();
			}
		});

		showOrLoadApplications();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isModify) {

			isModify = false;

			// 保存并应用规则
			new Thread() {
				public void run() {
					if (!ShellApi.applyIptablesRules(getBaseContext(), appCacheList, true)) {
						Log.e("FireWallMainActivity", "应用规则失败！！！");
					}
				}
			}.start();
		}
	}

	/**
	 * 排序appList列表 void
	 */
	private void sortAppList() {
		List<FireWallAppItem> forbitAppList = new ArrayList<FireWallAppItem>();
		List<FireWallAppItem> normalAppList = new ArrayList<FireWallAppItem>();

		FireWallAppItem fireWallAppItem = null;
		for (int i = 0; i < appCacheList.size(); i++) {
			fireWallAppItem = appCacheList.get(i);
			if (fireWallAppItem.b3GSelect || fireWallAppItem.bWifiSelect) {
				forbitAppList.add(fireWallAppItem);
			} else {
				normalAppList.add(fireWallAppItem);
			}
		}

		sortAppByNetTraffic(forbitAppList);
		sortAppByNetTraffic(normalAppList);

		appCacheList.clear();
		appCacheList.addAll(forbitAppList);		
		appCacheList.addAll(normalAppList);

	}

	/**
	 * 根据流量排序
	 * 
	 * @return List<FireWallAppItem>
	 */
	private void sortAppByNetTraffic(List<FireWallAppItem> mList) {

		Collections.sort(mList, new SortByTotalTraffic());
	}

	private void showOrLoadApplications() {

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {

				if (commonLoadingView != null) {
					commonLoadingView.setVisibility(View.GONE);
				}

				if (allUidSet.isEmpty()) {
					/*
					commonNoDataView.setVisibility(View.VISIBLE);
					return;
					*/
				}
				slidingLoading.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);

				// appCacheList排序
				sortAppList();

				// 更新ListView
				appAdapter = new AppViewAdapter(appCacheList);
				listview.setAdapter(appAdapter);

				// 查询uid并定位
				/*
				 * if (selectUid!=0){ FireWallAppItem fireWallAppItem = null;
				 * for (int i = 0; i < appCacheList.size(); i++) {
				 * fireWallAppItem = appCacheList.get(i); if
				 * (fireWallAppItem.uid == selectUid) { atListPos = i; try {
				 * listview.setSelection(atListPos); } catch (Exception e) {
				 * e.printStackTrace(); } break; } } }
				 */
			}
		};
		new Thread() {
			public void run() {
				//getForbitAppList();
				getAllToList();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 获取禁用的列表
	 */
	public void getForbitAppList() {

		try {

			NetTrafficRankingItem netTrafficRankingItem = null;

			HashSet<String> g3UidSet = ShellApi.getSavedUids3G(this);
			HashSet<String> wifiUidSet = ShellApi.getSavedUidsWifi(this);

			allUidSet.clear();
			allUidSet.addAll(g3UidSet);
			allUidSet.addAll(wifiUidSet);

			// 判断应用是否已存在,不存在则增加本地应用
			if (!allUidSet.contains(selectUid + "") && selectUid != -1) {
				allUidSet.add(selectUid + "");
			}

			if (allUidSet.isEmpty()) {
				return;
			}

			// 流量汇总
			Map<String, NetTrafficRankingItem> netMap = NetTrafficRankingGprsWifiAccessor.getInstance(this).getAllNetTrafficRankingMap();

			final PackageManager pkgmanager = getPackageManager();

			appCacheList.clear();

			Object[] strSetArr = allUidSet.toArray();
			String appUidStr;
			int appUidInt = -1;
			for (int i = 0; i < strSetArr.length; i++) {
				appUidStr = (String) strSetArr[i];
				appUidInt = Integer.parseInt(appUidStr);

				String[] pkgNames = pkgmanager.getPackagesForUid(appUidInt);

				if (pkgNames != null) {
					for (int j = 0; j < pkgNames.length; j++) {
						String pkgName = pkgNames[j];
						/*
						if (SafeUtil.bootAppFilter(this, pkgName))
							continue;
						 */
						FireWallAppItem fireWallAppItem = new FireWallAppItem();
						fireWallAppItem.appinfo = pkgmanager.getApplicationInfo(pkgName, 0);
						fireWallAppItem.title = (String) fireWallAppItem.appinfo.loadLabel(pkgmanager);
						fireWallAppItem.uid = fireWallAppItem.appinfo.uid;

						// 判断应用是否已禁用
						fireWallAppItem.b3GSelect = g3UidSet.contains(fireWallAppItem.uid + "");
						fireWallAppItem.bWifiSelect = wifiUidSet.contains(fireWallAppItem.uid + "");

						netTrafficRankingItem = netMap.get(pkgName);
						if (netTrafficRankingItem != null)
							fireWallAppItem.totalTraffic = netTrafficRankingItem.tal;

						appCacheList.add(fireWallAppItem); // 添加至列表中
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取禁用应用的数量
	 */
	public static int getForbitAppListCount(Context ctx) {

		int iResult = 0;

		if (ctx == null) {
			return iResult;
		}

		try {
			HashSet<String> g3UidSet = ShellApi.getSavedUids3G(ctx);
			HashSet<String> wifiUidSet = ShellApi.getSavedUidsWifi(ctx);

			HashSet<String> uidsSet = new HashSet<String>();
			uidsSet.addAll(g3UidSet);
			uidsSet.addAll(wifiUidSet);

			if (uidsSet.isEmpty()) {
				return iResult;
			}

			final PackageManager pkgmanager = ctx.getPackageManager();

			Object[] strSetArr = uidsSet.toArray();
			String appUidStr;
			int appUidInt = -1;
			for (int i = 0; i < strSetArr.length; i++) {
				appUidStr = (String) strSetArr[i];
				appUidInt = Integer.parseInt(appUidStr);

				String[] pkgNames = pkgmanager.getPackagesForUid(appUidInt);

				if (pkgNames != null) {
					for (int j = 0; j < pkgNames.length; j++) {
						String pkgName = pkgNames[j];
						/*
						if (SafeUtil.bootAppFilter(ctx, pkgName))
							continue;
						 */
						iResult++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iResult;
	}

	/**
	 * 获取全部应用
	 */
	public void getAllToList() {

		try {
			// 流量汇总
			Map<String, NetTrafficRankingItem> netMap = NetTrafficRankingGprsWifiAccessor.getInstance(this).getAllNetTrafficRankingMap();

			NetTrafficRankingItem netTrafficRankingItem = null;

			HashSet<String> g3UidSet = ShellApi.getSavedUids3G(this);
			HashSet<String> wifiUidSet = ShellApi.getSavedUidsWifi(this);

			final PackageManager pkgmanager = getPackageManager();

			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> resolveInfos = pkgmanager.queryIntentActivities(mainIntent, 0);
			Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pkgmanager));
			appCacheList.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				String activityName = reInfo.activityInfo.name;
				String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
/*
				if (SafeUtil.bootAppFilter(this, pkgName))
					continue;
*/
				String appLabel = (String) reInfo.loadLabel(pkgmanager); // 获得应用程序的Label
				// Drawable icon = reInfo.loadIcon(pkgmanager); // 获得应用程序图标

				ComponentName componentName = new ComponentName(pkgName, activityName);
				FireWallAppItem fireWallAppItem = new FireWallAppItem();
				fireWallAppItem.title = appLabel;
				fireWallAppItem.componentName = componentName;

				try {
					fireWallAppItem.appinfo = getPackageManager().getApplicationInfo(componentName.getPackageName(), 0);
					fireWallAppItem.uid = fireWallAppItem.appinfo.uid;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				// 判断应用是否已禁用
				fireWallAppItem.b3GSelect = g3UidSet.contains(fireWallAppItem.uid + "");
				fireWallAppItem.bWifiSelect = wifiUidSet.contains(fireWallAppItem.uid + "");

				netTrafficRankingItem = netMap.get(pkgName);
				if (netTrafficRankingItem != null)
					fireWallAppItem.totalTraffic = netTrafficRankingItem.tal;

				appCacheList.add(fireWallAppItem); // 添加至列表中
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class ItemCache {
		private ImageView app_item_img;
		private TextView app_item_name;
		private TextView app_item_nettraffic;
		private ImageView app_item_wifi_img;
		private ImageView app_item_3g_img;
		private FireWallAppItem app;

		private View safe_up_view;
	}

	private class AppViewAdapter extends BaseAdapter {

		List<FireWallAppItem> appList;

		public AppViewAdapter(List<FireWallAppItem> apps) {
			this.appList = apps;
		}

		@Override
		public int getCount() {
			if (appList != null)
				return appList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {

			if (appList != null) {

				if (position > 0 && position < appList.size() - 1)
					return appList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ItemCache cache;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.net_traffic_firewall_list_item, null);
				cache = new ItemCache();
				cache.app_item_img = (ImageView) convertView.findViewById(R.id.app_item_img);
				cache.app_item_name = (TextView) convertView.findViewById(R.id.app_item_name);
				cache.app_item_nettraffic = (TextView) convertView.findViewById(R.id.app_item_nettraffic);
				cache.app_item_wifi_img = (ImageView) convertView.findViewById(R.id.app_item_wifi_img);
				cache.app_item_3g_img = (ImageView) convertView.findViewById(R.id.app_item_3g_img);
				cache.safe_up_view = convertView.findViewById(R.id.safe_up_view);

				convertView.setTag(cache);
			} else {
				cache = (ItemCache) convertView.getTag();
			}

			cache.app = appList.get(position);
			cache.app_item_name.setText(cache.app.title);
			cache.app_item_nettraffic.setText(getString(R.string.safe_notify_firewall_item_nettraffic_desc, cache.app.getTal()));
			cache.app_item_img.setImageDrawable(cache.app.cached_icon);
			if (!cache.app.icon_loaded && cache.app.appinfo != null) {
				new LoadIconTask().execute(cache.app, getPackageManager(), convertView);
			}

			cache.app_item_wifi_img.setSelected(cache.app.bWifiSelect);
			cache.app_item_wifi_img.setTag(cache);
			cache.app_item_wifi_img.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					isModify = true;

					ItemCache clickCache = (ItemCache) v.getTag();
					clickCache.app.bWifiSelect = !clickCache.app.bWifiSelect;

					setStateByUid(clickCache.app.uid, 2, clickCache.app.bWifiSelect);

					selectUid = -1;

					notifyDataSetChanged();
				}
			});

			cache.app_item_3g_img.setSelected(cache.app.b3GSelect);
			cache.app_item_3g_img.setTag(cache);
			cache.app_item_3g_img.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					isModify = true;

					ItemCache clickCache = (ItemCache) v.getTag();
					clickCache.app.b3GSelect = !clickCache.app.b3GSelect;

					setStateByUid(clickCache.app.uid, 1, clickCache.app.b3GSelect);

					selectUid = -1;

					notifyDataSetChanged();
				}
			});

			/*
			 * 不再设置选中态 if ( cache.app.uid == selectUid ){
			 * cache.safe_up_view.setBackgroundResource
			 * (R.drawable.myphone_tv_press_blue); }else{
			 * cache.safe_up_view.setBackgroundResource(0); }
			 */

			return convertView;
		}

	}

	// 异步加载程序图标
	private static class LoadIconTask extends AsyncTask<Object, Void, View> {
		@Override
		protected View doInBackground(Object... params) {
			try {
				final FireWallAppItem app = (FireWallAppItem) params[0];
				final PackageManager pkgMgr = (PackageManager) params[1];
				final View viewToUpdate = (View) params[2];
				if (!app.icon_loaded) {
					app.cached_icon = pkgMgr.getApplicationIcon(app.appinfo);
					app.icon_loaded = true;
				}
				return viewToUpdate;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(View viewToUpdate) {
			try {
				final ItemCache entryToUpdate = (ItemCache) viewToUpdate.getTag();
				entryToUpdate.app_item_img.setImageDrawable(entryToUpdate.app.cached_icon);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	private static class SortByTotalTraffic implements Comparator<FireWallAppItem> {

		@Override
		public int compare(FireWallAppItem appItem1, FireWallAppItem appItem2) {
			if (appItem1.totalTraffic < appItem2.totalTraffic) {
				return 1;
			}
			if (appItem1.totalTraffic > appItem2.totalTraffic) {
				return -1;
			}
			return 0;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置退出状态
	 */
	private void exitActivity() {

		int iForbidCount = 0;
		FireWallAppItem fireWallAppItem = null;
		for (int i = 0; i < appCacheList.size(); i++) {
			fireWallAppItem = appCacheList.get(i);
			if (fireWallAppItem.b3GSelect || fireWallAppItem.bWifiSelect) {
				iForbidCount++;
			}
		}

		/*
		Intent intent = new Intent(FireWallMainActivity.this, SafeCenterActivity.class);
		intent.putExtra("ForbidCount", iForbidCount);
		setResult(RESULT_OK, intent);
		*/
		finish();
	}

	/**
	 * 根据UID设置,UID相同的应用
	 * 
	 * @param uid
	 * @param keyType
	 *            类型 1:3G 2:WIFI
	 * @param bValue
	 *            true或则false
	 */
	private void setStateByUid(int uid, int keyType, boolean bValue) {

		int iForbidCount = 0;
		FireWallAppItem fireWallAppItem = null;
		for (int i = 0; i < appCacheList.size(); i++) {
			fireWallAppItem = appCacheList.get(i);
			if (uid == fireWallAppItem.uid) {
				if (keyType == 1) {
					fireWallAppItem.b3GSelect = bValue;
				}
				if (keyType == 2) {
					fireWallAppItem.bWifiSelect = bValue;
				}
				iForbidCount++;
			}
		}
		// 如果iForbidCount超过1则提示
		if (iForbidCount > 1 && bValue) {
			// Toast 提示有iForbidCount个应用同时被禁止联网
		}
	}
}
