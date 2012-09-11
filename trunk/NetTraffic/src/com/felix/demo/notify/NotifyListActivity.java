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
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.felix.demo.R;

public class NotifyListActivity extends Activity {
	
	private static final String TAG="Test dumpsys";
	
	private ListView listview;
	
	private LayoutInflater inflater;
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			showApplications();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setContentView(R.layout.notify_list);
    	
    	inflater = getLayoutInflater();
		this.listview = (ListView) this.findViewById(R.id.app_list);
		
		new Thread() {
			public void run() {
				queryNotifyList();
				handler.sendEmptyMessage(0);
			}
		}.start();
    }
    
    public void queryNotifyList(){
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
                List<String> lineList = new ArrayList<String>(); 
                final StringBuilder log = new StringBuilder();   
                String separator = System.getProperty("line.separator"); 
                Pattern pattern = Pattern.compile("pkg=[^\\s]+"); 
                while ((line = reader.readLine()) != null) { 
                    if(line != null && line.trim().startsWith("NotificationRecord")){ 
                        Matcher matcher = pattern.matcher(line); 
                        if(matcher.find()){ 
                            lineList.add(matcher.group()); 
                        }else{ 
                            Log.e(TAG, "what's this?!"); 
                        } 
                    } 
                     
                    log.append(line); 
                    log.append(separator); 
                } 
                Log.v(TAG, "log:" + log.toString()); 
                 
                int size = lineList.size(); 
                for (int i = 0; i < size; i++) { 
                    Log.i(TAG, "app:" + lineList.get(i)); 
                    Log.e(TAG, "Uid:" + getUid(NotifyListActivity.this,lineList.get(i).replace("pkg=", "")));
                } 
            } catch (Exception e) { 
                Log.e(TAG, "copy fail", e); 
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
    
	public static int getUid(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		int size = runningAppProcesses.size();
		ActivityManager.RunningAppProcessInfo runningAppProcessInfo = null;
		for (int i = 0; i < size; i++) {
			runningAppProcessInfo = runningAppProcesses.get(i);
			if (packageName.equals(runningAppProcessInfo.processName)) {
				Log.d(TAG, "pid:" +runningAppProcessInfo.pid);
				return runningAppProcessInfo.uid;
			}
		}
		return -1;
	}
	
	public void showApplications(){
		
	}
}
