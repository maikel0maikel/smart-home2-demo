package com.nd.hilauncherdev.myphone.nettraffic.view;


import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingGprsWifiAccessor;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficRankingItem;
import com.nd.hilauncherdev.myphone.nettraffic.util.CrashTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.NetTrafficUnitTool;
import com.nd.hilauncherdev.myphone.nettraffic.util.ThreadUtil;
import com.nd.hilauncherdev.myphone.nettraffic.view.base.NetTrafficTabBaseView;

/**
 * 流量排行
 */
public class NetTrafficRankingView extends NetTrafficTabBaseView{

	private final static String TAG = "NetTrafficRankingView"; 
	
	private boolean bStartLoad = false; 		//表示是否初始化过

	private Context ctx;
	
	private LayoutInflater mInflater;
	
	private View wait_layout;
	
	private ListView appListView;
	
	private	AppNetTrafficRankingAdapter appNetTrafficRankingAdapter;
	
	private int sleepTime = 3 * 1000;
	
	private boolean isRefreshView = true;
	
	private ArrayList<RealNetTraffic> appUidList = new ArrayList<RealNetTraffic>();

	//定时刷新流量
	private Handler handler = new Handler();
	
	private Runnable task = new Runnable() {

		@Override
		public void run() {
			/*
			ThreadUtil.executeMore(new Runnable() {
				@Override
				public void run() {
					try { 
						if (isRefreshView) {
							//handler.postDelayed(task, sleepTime);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			*/
		}
	};
	
	@Override
	public void onResume() {
		isRefreshView = true;
		handler.postDelayed(task, sleepTime);
	}
	
	@Override
	public void onPause() {
		isRefreshView = false;
		handler.removeCallbacks(task);
	}
	
	@Override
	public void onDestroy() {
		isRefreshView = false;
		handler.removeCallbacks(task);
	}
	
	public NetTrafficRankingView(Context context) {
		
		super(context);
		ctx = context;
		mInflater = LayoutInflater.from( ctx );
		
		LayoutInflater.from( ctx ).inflate(R.layout.net_traffic_ranking_list, this);
		
		setupViews();
	}
	
	private void setupViews() {
		
		wait_layout = this.findViewById( R.id.wait_layout );
		wait_layout.setVisibility(View.VISIBLE);
		appListView = (ListView) this.findViewById(R.id.list_ranking);
	}
	
	/**
	 * 判断是否加载过
	 * @return
	 */
	public boolean isStartLoad() {
		return bStartLoad;
	}
	
	/**
	 * 开始加载数据
	 */
	public void startLoadData(){
		
		bStartLoad = true;
		
		ThreadUtil.executeNetTraffic(new Runnable() {
			@Override
			public void run() {
				try { 
					NetTrafficRankingGprsWifiAccessor.getInstance(ctx).
    				 	insertALLAppNetTrafficToDB(CrashTool.getNetType(ctx), CrashTool.getStringDate());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						showApplications();
						wait_layout.setVisibility(View.GONE);
					}
				});
			}
		});
	}
	
	private void showApplications() {

		ArrayList<NetTrafficRankingItem> netTrafficRankingList = new ArrayList<NetTrafficRankingItem>();
		
		netTrafficRankingList = NetTrafficRankingGprsWifiAccessor.getInstance(ctx)
				.getAllNetTrafficRanking(NetTrafficBytesItem.DEV_WIFI, CrashTool.getStringDate(), CrashTool.getStringDate());
		
		//流量前十的软件UID
		for (int i = 0; i < netTrafficRankingList.size(); i++) {
			
			if (i==10)
				break;
			
			RealNetTraffic realNetTraffic = new RealNetTraffic();
			realNetTraffic.uid = netTrafficRankingList.get(i).uid;
			realNetTraffic.new_total = (TrafficStats.getUidTxBytes(realNetTraffic.uid)+TrafficStats.getUidRxBytes(realNetTraffic.uid)) /1024f;
			realNetTraffic.old_total = realNetTraffic.new_total;
			realNetTraffic.app = netTrafficRankingList.get(i);
			
			appUidList.add(realNetTraffic);
		}
		
		appNetTrafficRankingAdapter = new AppNetTrafficRankingAdapter(appUidList);
		appListView.setAdapter(appNetTrafficRankingAdapter);
	}
	
	private class AppNetTrafficRankingAdapter extends BaseAdapter {

		PackageManager pkgmanager = ctx.getPackageManager();
		
		ArrayList<RealNetTraffic> appList;
		public AppNetTrafficRankingAdapter(ArrayList<RealNetTraffic> apps) {
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
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ItemCache cache;
			if (convertView == null) {				
				convertView = mInflater.inflate(R.layout.net_traffic_ranking_list_item, null);
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
			
			final RealNetTraffic realNetTraffic = appList.get(position);
			final NetTrafficRankingItem app = appList.get(position).app;
			
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
			cache.net_item_total.setText( NetTrafficUnitTool.netTrafficSortUnitHandler(realNetTraffic.new_total-realNetTraffic.old_total) );
			
			cache.net_item_icon.setImageDrawable(app.cached_icon);
    		if (!app.icon_loaded && app.appinfo!=null) {
        		new LoadIconTask().execute(app, ctx.getPackageManager(), convertView);
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
	
	private static class ItemCache {
		private ImageView net_item_icon;
		private TextView net_item_pkg;
		private TextView net_item_total;
		private TextView net_item_tx;
		private TextView net_item_rx;
		private NetTrafficRankingItem app;
	}
	
	private static class RealNetTraffic {
		private int uid;
		private float old_total;
		private float new_total;
		private NetTrafficRankingItem app;
	}
}
