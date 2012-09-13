package com.felix.demo.notify;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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


public class NotifyListActivity extends Activity {
	
	private static final String TAG="Test dumpsys";
	
	private ListView listview;
	
	private ArrayList<NotificationItem> notifyList =  new ArrayList<NotificationItem>(); 
	
	private LayoutInflater inflater;
	
	private TextView appListEmptyTV;
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			showApplications();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.notify_list);
    	
    	inflater = getLayoutInflater();
		this.listview = (ListView) this.findViewById(R.id.app_list);
		this.appListEmptyTV = (TextView) this.findViewById(R.id.app_list_empty);
		
		new Thread() {
			public void run() {
				queryNotifyList();
				handler.sendEmptyMessage(0);
			}
		}.start();
		/*
		if ( NotifyApi.hasRootAccess(this) ){
		
			new Thread() {
				public void run() {
					queryNotifyList();
					handler.sendEmptyMessage(0);
				}
			}.start();
		}else{
			//手机未Root
			Log.d("NotifyListActivity", "您的手机未Root");
			
			appListEmptyTV.setVisibility(View.VISIBLE);
			appListEmptyTV.setText("您的手机未Root,无法使用通知栏广告检测功能");
			listview.setVisibility(View.GONE);
		}
		*/
    }
    
    public void queryNotifyList(){
    	
    		notifyList.clear();
    	
            String[] commands = {"dumpsys notification"}; 
            Process process = null; 
            DataOutputStream dataOutputStream = null; 
     
            try { 
                process = Runtime.getRuntime().exec("su"); 
                dataOutputStream = new DataOutputStream(process.getOutputStream()); 
                int length = commands.length; 
                for (int i = 0; i < length; i++) { 
                    Log.e(TAG, "commands[" + i + "]:" + commands[i]); 
                    dataOutputStream.writeBytes(commands[i] + "\n"); 
                } 
                dataOutputStream.writeBytes("exit\n"); 
                dataOutputStream.flush(); 
                 
                process.waitFor(); 
                 
                BufferedReader reader = null; 
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));   
                String line = ""; 
                Pattern pattern = Pattern.compile("pkg=[^\\s]+"); 
                Pattern idPattern = Pattern.compile("id=[^\\s]+"); 
                while ((line = reader.readLine()) != null) { 
                    if(line != null && line.trim().startsWith("NotificationRecord")){ 
                    	
                    	NotificationItem notifyRec = new NotificationItem();
                    	
                        Matcher matcher = pattern.matcher(line);
                        if(matcher.find()){ 
                            notifyRec.pkgName = matcher.group().replace("pkg=", "");
                            
                            //跳过白名单
                            if ("com.android.systemui".equals(notifyRec.pkgName)){
                            	continue;
                            }
                            
                            Matcher idMatcher = idPattern.matcher(line);
                            if (idMatcher.find()){
                            	notifyRec.noId = Integer.parseInt(idMatcher.group().replace("id=", ""),16);
                            }
                            
                            //往下读取 contentIntent并判断, 输出的具体结构查看命令dumpsys notification效果
                            line = reader.readLine();//滤过一行
                            if ( (line = reader.readLine()) != null ){
                            	String tmpContentIntent  = line.trim();
                            	if( tmpContentIntent.startsWith("contentIntent=") && !"contentIntent=null".equalsIgnoreCase(tmpContentIntent) ){ 
                            		//往下读取 tickerText
                            		line = reader.readLine();//滤过一行
                            		if ( (line = reader.readLine()) != null ){
                            			String tmpTickerText  = line.trim();
                            			if( tmpTickerText.startsWith("tickerText=") ){ 
                            				if ( !"tickerText=null".equalsIgnoreCase(tmpTickerText) ) {
                            					notifyRec.tickerText = tmpTickerText.replace("tickerText=", "");
                            				}else{
                            					notifyRec.tickerText = "";
                            				}
                            				
                            				notifyRec.pid = getPid(this, notifyRec.pkgName);
                            				
                            				notifyList.add(notifyRec);
                            			}
                            		}
                            	}
                            }
                        }
                    } 
                } 
                
            } catch (Exception e) { 
                Log.e(TAG, "fail", e); 
            } finally { 
                try { 
                    if (dataOutputStream != null) { 
                        dataOutputStream.close(); 
                    } 
                    process.destroy(); 
                } catch (Exception e) { 
                } 
            } 
            Log.v(TAG, "finish"); 
    }
    
	public static int getPid(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		
		int size = runningAppProcesses.size();
		ActivityManager.RunningAppProcessInfo runningAppProcessInfo = null;
		for (int i = 0; i < size; i++) { 
			runningAppProcessInfo = runningAppProcesses.get(i);
			
			for (int j = 0; j < runningAppProcessInfo.pkgList.length; j++) {
				System.out.println( runningAppProcessInfo.processName+" pkg=" +runningAppProcessInfo.pkgList[j]);
			}
			
			if (packageName.equals(runningAppProcessInfo.processName)) {
				Log.d(TAG, "pid:" +runningAppProcessInfo.pid);
				return runningAppProcessInfo.pid;
			}
		}
		return -1;
	}
	
	public void showApplications(){
		int size = notifyList.size(); 
        for (int i = 0; i < size; i++) { 
            Log.i(TAG, "app:" + notifyList.get(i).pkgName+" tickerText="+ notifyList.get(i).tickerText); 
            Log.e(TAG, "pid:" + notifyList.get(i).pid );
        } 

        NotificationViewAdapter adapter = new NotificationViewAdapter(notifyList);
		this.listview.setAdapter(adapter);
	}
	
	private static class ItemCache {
		private ImageView net_item_icon;
		private TextView net_item_pkg;
		private TextView net_item_total;
		private TextView net_item_tx;
		private TextView net_item_rx;
		private NotificationItem app;
	}
	
	private class NotificationViewAdapter extends BaseAdapter {
		
		PackageManager pkgmanager = getPackageManager();
		
		ArrayList<NotificationItem> appList;
		
		public NotificationViewAdapter(ArrayList<NotificationItem> apps) {
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
				convertView = inflater.inflate(R.layout.notify_item, null);
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
			final NotificationItem app = appList.get(position);
			
			cache.app = app;
			//设置NetTrafficRankingItem appinfo属性值
			try {
				cache.app.appinfo = pkgmanager.getApplicationInfo(app.pkgName,0);
				cache.app.appName = pkgmanager.getApplicationLabel(app.appinfo).toString();
				cache.net_item_pkg.setText(cache.app.appName);	
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cache.net_item_tx.setText(cache.app.tickerText);
			cache.net_item_icon.setImageDrawable(app.cached_icon);
    		if (!app.icon_loaded && app.appinfo!=null) {
        		new LoadIconTask().execute(app, getPackageManager(), convertView);
    		}
    		
    		convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					ItemCache cache = (ItemCache)v.getTag();
					
					NotificationManager nManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
					nManager.cancel( cache.app.noId );		
					
					android.os.Process.killProcess(cache.app.pid);
				}
			});
    		
			return convertView;
		}
			
	}

	//异步加载程序图标
	private static class LoadIconTask extends AsyncTask<Object, Void, View> {
		@Override
		protected View doInBackground(Object... params) {
			try {
				final NotificationItem app = (NotificationItem) params[0];
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
