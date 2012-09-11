package com.felix.demo.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.TrafficStats;
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

public class NetTrafficRankingMain extends Activity {

	private final static String TAG = "NetTrafficMain"; 
	
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
		
		//判断网络是否可用
		if ( CrashTool.isNetworkAvailable(this) ){
			//启动服务
			Intent intentService = new Intent(this, NetTrafficBytesService.class);
			intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startService(intentService);
		}
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
			new Thread() {
				public void run() {
					try {
						//NetTrafficAccessor.getInstance(NetTrafficRankingMain.this).clearNetTrafficRanking();
						NetTrafficRankingAccessor.getInstance(NetTrafficRankingMain.this).insertALLAppNetTrafficToDB();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					handler.sendEmptyMessage(0);
				}
			}.start();
		} else {			
			showApplications();
		}
	}
	
	private void showApplications() {

		reLoad = false;

		ArrayList<NetTrafficRankingItem> netTrafficRankingList = new ArrayList<NetTrafficRankingItem>();
		try {
			netTrafficRankingList = NetTrafficRankingAccessor.getInstance(this).getAllNetTrafficRanking();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		NetTrafficViewAdapter adapter = new NetTrafficViewAdapter(netTrafficRankingList);
		
		this.listview.setAdapter(adapter);
	}
	
	private String unitHandler(float floatnum) {
		String value = null;
		//float floatnum = count/1024f; //SQL语句已经转会单位了
		DecimalFormat format = new DecimalFormat("0.00");
		value = format.format(floatnum) + "MB";
		return value;
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
			cache.net_item_tx.setText( unitHandler(app.tx) );
			cache.net_item_rx.setText( unitHandler(app.rx) );
			cache.net_item_total.setText( unitHandler(app.tal) );
			
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
