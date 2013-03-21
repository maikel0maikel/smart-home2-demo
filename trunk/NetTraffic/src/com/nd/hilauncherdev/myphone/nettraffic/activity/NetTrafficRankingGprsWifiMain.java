package com.nd.hilauncherdev.myphone.nettraffic.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingItem;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficUnitTool;

public class NetTrafficRankingGprsWifiMain extends Activity {

	private final static String TAG = "NetTrafficRankingGprsWifiMain"; 
	
	private ListView listview;
	
	private boolean reLoad = true; 
	
	private final int UPDATE_GAP = 15000;// 更新进度间隔时间为1秒
	private long lastUpdatedTime = 0;
	
	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.net_traffic_list);
		
		inflater = getLayoutInflater();
		this.listview = (ListView) this.findViewById(R.id.net_traffic_list_top);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//判断两次操作间隔15s以上
		if ( System.currentTimeMillis() - lastUpdatedTime < UPDATE_GAP ) {		
			reLoad = true;
			lastUpdatedTime = System.currentTimeMillis();
		}
		showOrLoadApplications();
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
	}
	
	private void showOrLoadApplications() {
		final Resources res = getResources();
		if (reLoad == true) {
			
			final ProgressDialog progress = ProgressDialog.show(this,
					res.getString(R.string.net_traffic_loading),
					res.getString(R.string.net_traffic_reading_apps), true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					try {
						progress.dismiss();
					} catch (Exception ex) {
					}
					showApplications();
				}
			};
			
			ThreadUtil.executeNetTraffic(new Runnable() {
				@Override
				public void run() {
					try {
		        		NetTrafficRankingGprsWifiAccessor.getInstance(getBaseContext()).
	        				insertALLAppNetTrafficToDB(CrashTool.getNetType(getBaseContext()), CrashTool.getStringDate());
		        		
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(0);
				}
			});
		} else {			
			showApplications();
		}
	}
	
	private void showApplications() {

		reLoad = false;

		ArrayList<NetTrafficRankingItem> netTrafficRankingList = new ArrayList<NetTrafficRankingItem>();
		try {
			netTrafficRankingList = NetTrafficRankingGprsWifiAccessor.getInstance(getBaseContext()).getAllNetTrafficRanking(CrashTool.getNetType(getBaseContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		NetTrafficViewAdapter adapter = new NetTrafficViewAdapter(netTrafficRankingList);
		
		this.listview.setAdapter(adapter);
	}
	
	
	private static class ItemCache {
		private ImageView net_item_icon;
		private TextView net_item_pkg;
		private TextView net_item_total;
		private TextView net_item_tx;
		private TextView net_item_rx;
		private NetTrafficRankingItem app;
	}
	
	private class NetTrafficViewAdapter extends BaseAdapter {
		
		PackageManager pkgmanager = getPackageManager();
		
		ArrayList<NetTrafficRankingItem> appList;
		
		public NetTrafficViewAdapter(ArrayList<NetTrafficRankingItem> apps) {
			this.appList = apps;
		}
		
		@Override
		public int getCount() {
			if (appList!=null)
				return appList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			
			if (appList!=null){
				
				if (position>0 && position<appList.size()-1)
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
				convertView = inflater.inflate(R.layout.net_traffic_item, null);
				cache = new ItemCache();
				cache.net_item_icon = (ImageView) convertView.findViewById(R.id.net_item_icon);
				cache.net_item_pkg = (TextView) convertView.findViewById(R.id.net_item_pkg);
				cache.net_item_total = (TextView) convertView.findViewById(R.id.net_item_total);
				cache.net_item_rx = (TextView) convertView.findViewById(R.id.net_item_rx);
				cache.net_item_tx = (TextView) convertView.findViewById(R.id.net_item_tx);
				convertView.setTag(cache);
			} else {
				cache = (ItemCache) convertView.getTag();
			}
			final NetTrafficRankingItem app = appList.get(position);
			
			cache.app = app;
			//设置NetTrafficRankingItem appinfo属性值
			try {
				cache.app.appinfo = pkgmanager.getApplicationInfo(app.pkg,0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cache.net_item_pkg.setText(app.names);			
			cache.net_item_tx.setText( NetTrafficUnitTool.netTrafficSortUnitHandler(app.tx) );
			cache.net_item_rx.setText( NetTrafficUnitTool.netTrafficSortUnitHandler(app.rx) );
			cache.net_item_total.setText( NetTrafficUnitTool.netTrafficSortUnitHandler(app.tal) );
			
			cache.net_item_icon.setImageDrawable(app.cached_icon);
    		if (!app.icon_loaded && app.appinfo!=null) {
        		new LoadIconTask().execute(app, getPackageManager(), convertView);
    		}
    		
			return convertView;
		}
			
	}

	//异步加载程序图标
	private static class LoadIconTask extends AsyncTask<Object, Void, View> {
		@Override
		protected View doInBackground(Object... params) {
			try {
				final NetTrafficRankingItem app = (NetTrafficRankingItem) params[0];
				final PackageManager pkgMgr = (PackageManager) params[1];
				final View viewToUpdate = (View) params[2];
				if (!app.icon_loaded) {
					app.cached_icon = pkgMgr.getApplicationIcon(app.appinfo);
					app.icon_loaded = true;
				}
				return viewToUpdate;
			} catch (Exception e) {
				Log.e(TAG, "Error loading icon", e);
				return null;
			}
		}
		protected void onPostExecute(View viewToUpdate) {
			try {
				final ItemCache entryToUpdate = (ItemCache) viewToUpdate.getTag();
				entryToUpdate.net_item_icon.setImageDrawable(entryToUpdate.app.cached_icon);
			} catch (Exception e) {
				Log.e(TAG, "Error showing icon", e);
			}
		};
	}
}
