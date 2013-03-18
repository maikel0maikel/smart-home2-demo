package com.felix.demo.notify;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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
			
			Intent mIntent = new Intent();
			mIntent.setClass(NotifyListActivity.this,NotifyListActivity.class);
			
			NotificationManager nManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
			Notification notif = new Notification( R.drawable.ic_launcher, "主进程", System.currentTimeMillis() );
			
			PendingIntent pIntent = PendingIntent.getActivity(NotifyListActivity.this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notif.flags = Notification.FLAG_AUTO_CANCEL;
	        notif.setLatestEventInfo(NotifyListActivity.this, "主进程--title", "主进程--context", pIntent);
	        
			nManager.notify(0, notif);
			
			nManager.notify(9100, notif);
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
    
   
    
    public void kill9Process(int pid){
    	
    	if (pid==-1) return;
    	
    	String[] commands = {"kill -9 "+pid}; 
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
            while ((line = reader.readLine()) != null) { 
            	Log.d(TAG, "kill response:"+line); 
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
                            	notifyRec.noId = Long.parseLong(idMatcher.group().replace("id=", ""),16);
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
					
					final ItemCache cache = (ItemCache)v.getTag();
					
					//Log.d(TAG, "try kill -9 "+cache.app.pid);
					//kill9Process(cache.app.pid);
					Log.d(TAG, cache.app.pkgName);
					//确认是否删除
                	new AlertDialog.Builder(NotifyListActivity.this)
    				.setIcon(android.R.drawable.ic_dialog_alert)
    				.setTitle("是否确定卸载“"+cache.app.appName+"”")
    				.setPositiveButton("确定",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,
    									int whichButton) {
    								uninstallApp(NotifyListActivity.this,cache.app.pkgName);
    							}
    						})
    				.setNegativeButton("取消",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,
    									int whichButton) {
    							}
    						}).create().show(); 
					/*
					ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					
					if (Build.VERSION.SDK_INT < 8) {
						activityManager.restartPackage(cache.app.pkgName);
					} else {
						activityManager.killBackgroundProcesses(cache.app.pkgName);
					}
					*/
					//killProcessByPkg(cache.app.pkgName);
					
					/*
					ActivityManager ma = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					ma.forceStopPackage(cache.app.pkgName);
            */
					/*
					INotificationManager inot = NotificationManager.getService();
					try {
						
						Log.d(TAG, "cache.app.pkgName");
						inot.cancelAllNotifications(cache.app.pkgName);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
					
					if (mService!=null){
						try {
							mService.cancelAllNotifications(cache.app.pkgName);
							
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						// 绑定AIDL服务
												
					}
					*/
					
				}
			});
    		
			return convertView;
		}
			
	}
	
	@SuppressWarnings({ "rawtypes" })
    private boolean killProcessByPkg(String pkgName){
            Class c;
            try {
                    c = Class.forName("android.app.ActivityManagerNative");
                    Method getDefaultMethod = c.getMethod("getDefault");
                    getDefaultMethod.setAccessible(true);
                    Object nativeManager = getDefaultMethod.invoke(null);
                    c = nativeManager.getClass();
                    Method forceStopPackageMethod = c.getMethod("forceStopPackage", String.class);
                    forceStopPackageMethod.setAccessible(true);
                    forceStopPackageMethod.invoke(nativeManager, pkgName);
            } catch (ClassNotFoundException e) {
                    e.printStackTrace();
            } catch (SecurityException e) {
                    e.printStackTrace();
            } catch (NoSuchMethodException e) {
                    e.printStackTrace();
            } catch (IllegalArgumentException e) {
                    e.printStackTrace();
            } catch (IllegalAccessException e) {
                    e.printStackTrace();
            } catch (InvocationTargetException e) {
                    e.printStackTrace();
            }

            return true;
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

	/**
	 * 卸载应用程序
	 * @param ctx
	 * @param packageName
	 */
	private static void uninstallApp(Context ctx, String packageName){
		Uri uri = Uri.fromParts("package", packageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		ctx.startActivity(it);
	}
}
