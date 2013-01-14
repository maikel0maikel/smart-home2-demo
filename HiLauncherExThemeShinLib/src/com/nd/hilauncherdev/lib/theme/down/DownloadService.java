package com.nd.hilauncherdev.lib.theme.down;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.lib.theme.HiLauncherExDownTaskManagerActivity;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.util.FileUtil;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;
import com.nd.hilauncherdev.lib.theme.util.SUtil;
import com.nd.hilauncherdev.lib.theme.util.ZipUtil;


public class DownloadService extends Service {

    private final static String TAG = "com.nd.hilauncherdev.lib.theme.down.DownloadService";
    
    /**downList 未开始下载皮肤列表*/
    private static ConcurrentLinkedQueue<String> downList = new ConcurrentLinkedQueue<String>();

    /**DOWN_DIR*/
    private final static String DOWN_DIR = HiLauncherThemeGlobal.PACKAPGES_HOME;

    /**itemMap  未下载完成的主题列表(包含未开始下载和下载中的主题)*/
    private static ConcurrentHashMap<String,DowningTaskItem> itemMap = new ConcurrentHashMap<String, DowningTaskItem>(); 

    /**task_running*/
    public static boolean task_running = false;

    /**downThread*/
    private DownThread downThread = null;

    private Context mContext;
    
    /**下载中*/
    public static final int STATE_DOWNLOADING = 0;
    
    /**下载完成*/
    public static final int STATE_DOWNLOAD_COMPLETE = 1;
    
    private static boolean running = false;
    
    /**
     * 暂停pasuseUrl下载
     * @param pauseUrl
     */
    public static void pauseDownTask(String pauseUrl){
    	
    	if ( downList.contains(pauseUrl) )
    		downList.remove(pauseUrl);
    	
    	itemMap.remove(pauseUrl);
    }

    @Override
    public void onCreate() {
        this.setForeground(true);
        mContext = this;        
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart...");

        if (intent==null)
        	return;
        
        ThemeItem mTheme = (ThemeItem)intent.getSerializableExtra("mTheme");
        if (mTheme==null)
        	return;
        		
        String url = mTheme.getDownloadUrl();
        if ( url==null || inDownList(url) ) {
        	return;
        }
        
		DowningTaskItem item = new DowningTaskItem();
		item.themeName = mTheme.getName();
		// 因为存在皮肤及全套主题使用同一个ThemeRes的情况所以需要重新定义ThemeID
		item.themeID = mTheme.getId();
		try {
			item.startID = Integer.valueOf(item.themeID);
		} catch (Exception e) {
			item.startID = 9100;
			e.printStackTrace();
		}
		item.state = DowningTaskItem.DownState_Downing;
		item.downUrl = url;
		item.picUrl = mTheme.getLargePostersUrl() == null ? "" : mTheme.getLargePostersUrl();

		itemMap.put(url, item);
		downList.add(url);

		try {
			ThemeLibLocalAccessor.getInstance(mContext).updateDowningTaskItem(item);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PendingIntent pIntent = buildDownloadingPendingIntent(item.themeID);
		DownloadNotification.downloadRunningNotification(mContext, item.startID, item.themeName + " " + HiLauncherThemeGlobal.R(R.string.ndtheme_theme_wait_for_downloading),
				HiLauncherThemeGlobal.R(R.string.ndtheme_theme_downloading_tip), pIntent, 0);

		ThemeDownloadStateManager.sendDownloadingMessage(mContext, item.themeID);

        if((downThread == null)  || !running){
        	downThread = new DownThread();
            downThread.start();	
        }
    }
    
    
	public static boolean inDownList(String url) {
		return itemMap.containsKey( url );
	}
	

    private class DownThread extends Thread {

        public DownThread() {
        	
        }
        
        @Override
        public void run() {
            if (!running) {
                running = true;
	            String mUrl = downList.poll();
                while(mUrl != null){
                    String filename = getFileName( mUrl );
                    if (filename != null) {
                    	
                    	DowningTaskItem dTaskItem = itemMap.get( mUrl );
                    	ThemeDownloadStateManager.sendDownloadingMessage( mContext, dTaskItem.themeID );
                    	
                        String filePath = DOWN_DIR + filename;
                        
                        PendingIntent pIntent = buildDownloadingPendingIntent( dTaskItem.themeID );
                        
                        Log.d(TAG, "DownThread run() themeId="+dTaskItem.themeID);
                        // 1. 下载
                        String ret = downloadFile( mUrl, filePath,  mContext, dTaskItem.startID, 
                        		dTaskItem.themeName, mContext.getResources().getString(R.string.ndtheme_theme_downloading_tip), 
                        		pIntent);
                        
                        Log.v(TAG, "downloadFile return = "+ret);
                        
                        if (ret!=null) {                           	                        
                        	
                        	//修改队列状态 Begin
                        	int newState = 0;
                        	if ( ret.equalsIgnoreCase("pause") ) {
                        		newState = DowningTaskItem.DownState_Pause;
                        		ThemeLibLocalAccessor.getInstance(mContext).updateDownTaskItemForDownState( dTaskItem.themeID, newState);
                        	}else{
                        		newState = DowningTaskItem.DownState_Finish;
                        		dTaskItem.tmpFilePath = ret;
                        		ThemeLibLocalAccessor.getInstance(mContext).updateDownTaskItemForDownState( dTaskItem.themeID, newState, ret);    
                        	}
                            //修改队列状态 End
                            
                            if ( ret.equalsIgnoreCase("pause") ) {
                            	//下载暂停执行以下操作
                            	DownloadNotification.downloadCancelledNotification(mContext, dTaskItem.startID);
                            }else{
                            	//下载成功执行以下操作
                            	
	                            //保存缩略图
	                            if(DownloadTask.hashMap.containsKey( mUrl )){
	                            	DownloadTask.hashMap.put(ret,DownloadTask.hashMap.get( mUrl ));
	                            	DownloadTask.hashMap.remove( mUrl );
	                            }                        
	                            // 2. 安装
	                            InstallThread thread = new InstallThread(ret, dTaskItem.themeID, mUrl );
	                            thread.start();
                            }
                        } else { //失败
                        	ThemeLibLocalAccessor.getInstance(mContext).updateDownTaskItemForDownState( dTaskItem.themeID, DowningTaskItem.DownState_Fail );                        	                            
                        	
                        	DownloadNotification.downloadFailedNotification( mContext, dTaskItem.startID, dTaskItem.themeName, pIntent );
                        	ThemeDownloadStateManager.sendDownloadFailMessage(mContext, dTaskItem.themeID);
                        	
                    	    if(itemMap.containsKey(mUrl)) {
                    	    	itemMap.remove( mUrl );
                    	    }
                        }
                        mUrl = downList.poll();
                    }
                }
            }
            running = false;
        }
        
    }

    private class InstallThread extends Thread {
        private String filePath;
        private String serverThemeID;
        private String url;
        
        /**
         * 安装
         * @param filePath	文件本地存放路径
         * @param serverThemeID 资源id
         * @param url 文件下载地址
         */
        public InstallThread(String filePath, String serverThemeID, String url) {
            this.filePath = filePath;
            this.serverThemeID = serverThemeID;
            this.url = url;
        }

        @Override
        public void run() {
            if (filePath != null) {
                try {
                	final DowningTaskItem dTaskItem = itemMap.get( url );
                    itemMap.remove(url);
                    
                    //如果91桌面未安装则主题都不弹出应用窗口
                    if ( !ApkTools.isInstallAPK(mContext, HiLauncherThemeGlobal.THEME_MANAGE_PACKAGE_NAME) ){ 
                    	if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_THEME) ){
                    		return ;
                    	}
                    }
                    
                    if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_LAUNCHER) ){
                    	//91Launcher Apk filePath
                    	File launcherApk=new File(filePath);
                    	if(launcherApk.exists()){
                    		DownloadNotification.sendHiLauncerExFinishMessage(mContext, dTaskItem.startID, dTaskItem.themeName+"下载完成", "点击安装", filePath);
                    		ApkTools.installApplication(mContext, launcherApk);
                    	}
                    	return ;
                    }
                    
                    if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
                    	
                    	String tmpSkinPath = NdLauncherExThemeApi.ND_HILAUNCHER_THEME_APP_SKIN_PATH_VALUE;

                    	if ( tmpSkinPath==null || "".equals(tmpSkinPath) ){
                    		tmpSkinPath = HiLauncherThemeGlobal.PACKAPGES_HOME;
                    	}
                    	String lastChar = tmpSkinPath.substring(tmpSkinPath.length()-1);
                    	if ( !"/".equals(lastChar) ){
                    		tmpSkinPath = tmpSkinPath+"/";
                    	}
	            		String unZipPath = tmpSkinPath+serverThemeID+"/";
                        if ( ZipUtil.ectract(filePath, unZipPath, false)==null ){
                        	//文件解析失败提示
                        	return ;
                        }else{
                        	//保存解析后的地址到数据库看中
                        	ThemeLibLocalAccessor.getInstance(mContext).updateDownTaskItemForNewThemeID( dTaskItem.themeID, unZipPath);
                        	//发送安装完成的广播更新下载列表界面
                        	ThemeDownloadStateManager.sendDownloadFinishMessage(mContext, serverThemeID, serverThemeID, dTaskItem.tmpFilePath);
                        }
                    }

                    ThemeLauncherExAPI.showThemeApplyActivity(mContext, dTaskItem);
                } catch (Exception e) {
                    Log.e(TAG, "install theme error path: " + filePath);
                	e.printStackTrace();
                }
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind...");
        return null;
    }

    /**
     * @param url
     * @return
     */
    private String getFileName(String url) {
        int lastIndex = url.lastIndexOf('/');
        try {
            return url.substring(lastIndex + 1);
        } catch (Exception e) {
            Log.d(TAG, "getFileName error:" + url);
        }
        return null;
    }

    /**
     * @param destUrl
     * @param filePath
     * @return
     */
    private String downloadFile(String destUrl, String filePath, Context context, 
			int position, String title, String content, PendingIntent pIntent) {

    	String sourceUrl = destUrl;
        HttpURLConnection httpUrl = null;
        if (destUrl.contains(".aspx") || destUrl.contains(".ashx")) {
        	//通过重定向获取最终apt、apk的下载地址并保存在destUrl
			try {
				//destUrl += "&sessionid=" + SessionManage.getSessionId(null);
				HttpParams params = new BasicHttpParams();
				// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
				HttpConnectionParams.setConnectionTimeout(params, HiLauncherThemeGlobal.CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(params, HiLauncherThemeGlobal.CONNECTION_TIMEOUT);
				HttpConnectionParams.setSocketBufferSize(params, 8192);
				// 设置重定向，缺省为 true
				HttpClientParams.setRedirecting(params, false);
				HttpClient client = new DefaultHttpClient(params);
				destUrl = destUrl.replaceAll(" ", "%20");
				HttpGet request = new HttpGet(destUrl);
				HttpResponse response = client.execute(request);
				Header head = response.getFirstHeader("Location");
				if (head != null) {				
					destUrl = head.getValue();
					String filename = getFileName(destUrl);
                    if (filename != null) 
                        filePath = DOWN_DIR + filename;
				}
				Log.v(TAG, "down load filePath="+filePath);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}

        
        String tempFilePath = null;
        
        //是否成载完成
        boolean isStop = false;
        RandomAccessFile outFile =null;
		long currentSize =0;
		boolean  isReConn = false;
		int currentTimes =0;
		final int download_bytes =8192;
		final int update_bytes =2048;
		//本次已下载的字节数
		long downloaded_bytes=0;
		//要下载的文件总字节数
		long fileTotalBytes=0;
		InputStream in=null;
		try {
			 // 建立文件
            File file = new File(filePath);
            tempFilePath = filePath +".temp";
            File temp = new File(tempFilePath);
            if(temp.exists()){
            	//temp.delete();
            }                       
            
			currentSize = getCurrentSize(tempFilePath);
			// 建立链接
            httpUrl=getConnection(SUtil.Utf8URLencode(destUrl));
			/**** 设置断点 **/
			String sProperty = "bytes=" + currentSize + "-";
			httpUrl.setRequestProperty("RANGE", sProperty);
			httpUrl.connect();
			
			Log.d(TAG, "totalsize="+(httpUrl.getContentLength()+currentSize));
			//要下载的文件的总字节数
			fileTotalBytes=httpUrl.getContentLength()+currentSize;
			outFile = new RandomAccessFile(tempFilePath, "rw");
			outFile.seek(currentSize);
			//获取网络输入流
			in=httpUrl.getInputStream();
		
			//记录下载临时文件的保存位置到DB Begin
            DowningTaskItem downingTaskItem = ThemeLibLocalAccessor.getInstance(context).getDowningTaskItemByDownUrl(sourceUrl);
            downingTaskItem.totalSize = fileTotalBytes;
            downingTaskItem.tmpFilePath = tempFilePath;
            ThemeLibLocalAccessor.getInstance(context).updateDowningTaskItem(downingTaskItem);
            String themeID = downingTaskItem.themeID;
            //记录下载临时文件的保存位置到DB End
            
			byte[] tmpBytes=new byte[download_bytes];//8K
			int len=-1;
			long totalBytes=0;//记录当前已读取的字节总数
            int readTryTimes = 0;
            
    		int UPDATE_GAP = 1000;// 更新进度间隔时间为1秒
    		long lastUpdatedTime = 0;
    		int progress = 0;
    		
			while(!isStop){
				/**加入重连机制**/
				if ( isReConn && currentTimes<(5+readTryTimes) ) { 
					try {
						//要把计算器添加在此，不然重连机制会进入死循环
						currentTimes++;
						
						httpUrl=getConnection(SUtil.Utf8URLencode(destUrl));
						/**** 设置断点 **/
						currentSize = getCurrentSize(tempFilePath);
						sProperty = "bytes=" + currentSize + "-";
						httpUrl.setRequestProperty("RANGE", sProperty);
						httpUrl.connect();						
						
						in=httpUrl.getInputStream();
						outFile.seek(currentSize);
						isReConn = false;
					} catch (Exception e) {
						Log.e(TAG, "get responseCode fail");
						isReConn = true;		
						
						try {
							Thread.sleep(1000);
							in.close();
							httpUrl.disconnect();													
						} catch (Exception ex) {
						} finally{
							//不再尝试,保存文件退出
							if ( readTryTimes==5 && currentTimes>=(5+readTryTimes) ){								
								outFile.close();								
								return null;
							}
						}
						continue;
					}
				}
				/****/
				
				// 下载
				try {
					
					//判断是否已经暂停了
					if ( !inDownList(sourceUrl) ) {
						//保存相关信息
						outFile.close();
						
						isStop = true;
						return "pause";
					}
					
					len = in.read(tmpBytes, 0, download_bytes);
					if (len > 0) {
						outFile.write(tmpBytes, 0, len);
						totalBytes+=len; //累加已读取的字节总数
						currentSize+=len; //当前文件已下载的大小
						
						//下载到指定字节数时才通知流动条
						if((totalBytes-downloaded_bytes)>=update_bytes||currentSize==fileTotalBytes)
						{
							//记录已下载字节数
							downloaded_bytes=totalBytes;
							try{
								
								progress = (int)(currentSize*100/fileTotalBytes);
								
								if ( System.currentTimeMillis() - lastUpdatedTime > UPDATE_GAP || progress==100 ) {
									//更新进度条
			    					DownloadNotification.downloadRunningNotification(context, position, title, content, pIntent, (int)(currentSize*100/fileTotalBytes));
			    					
									lastUpdatedTime = System.currentTimeMillis();
								}		
		    					
		    					//发送下载进度广播
		    					ThemeDownloadStateManager.sendDownloadProgressMessage(context, themeID, (int)(currentSize*100/fileTotalBytes), tempFilePath);
		    					
		    					//TODO DEBUG 耗时的操作  
		                        //Thread.sleep(2000);  
		    					
		    				}catch (Exception e) {
		    					e.printStackTrace();
		    				}   
						}
						//TODO 断点续传这边没有处理好
						//本地已下载的字节数量与服务端文件的字节数量相同时，表示下载完成
						if(currentSize==fileTotalBytes){
							outFile.close();
							
							File tempFile = new File(tempFilePath);
							tempFile.renameTo(file);
							isStop = true;
							return filePath;
						}
						
					} 
					
				} catch (Exception e) {
					Log.e(TAG, "get responseCode fail down");
					isReConn = true;
					readTryTimes = 5;
					try {
						Thread.sleep(1000);
						if(in!=null)
							in.close();
						if(httpUrl!=null)
							httpUrl.disconnect();
					} catch (Exception ex) {
					}
					continue;
					
				} //end try
				
				//线程被干扰掉，一般是人为取消下载时触发的
				if(!Thread.currentThread().isAlive() || Thread.currentThread().isInterrupted())
				{
					Log.d(TAG, "run.......InterruptedException.");
					throw new InterruptedException();
				}
				
			}//end while
            
        } catch (Exception e) {
        	if(tempFilePath!=null&&new File(tempFilePath).exists()){
        		new File(tempFilePath).delete();
        	}
            Log.e(TAG, "downfile:" + destUrl + ",Exception:" + e);
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        } //end try
	        
        

        return null;
    }
    
    /**
	 * 获取网络连接
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection getConnection(String urlString) throws Exception
	{
		URL url=null;
		HttpURLConnection conn=null;
		try {
			url=new URL(urlString);
			conn=(HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(30000);//超时30秒钟
			conn.setReadTimeout(30000);//连接之后30秒钟的时间内没有读到数据，即超时
		} catch (Exception e) {
			if(conn!=null)
				conn.disconnect();
		}
		return conn;
	}
	
	/**
	 * 
	 * @Title: getCurrentSize 
	 * @Description: 获取文件大小
	 * @param path
	 * @return     
	 * @throws
	 */
	private long getCurrentSize(String path){
		
			File file = FileUtil.createFile(path);
			long size = 0;
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				size = file.length();
			}
			return size;
	}
	
    
    /**
     * 用户点击通知时进入下载管理页面
     * @param themeId
     * @return
     */
    private PendingIntent buildDownloadingPendingIntent( String themeId ) {
    	
    	int iThemeId = 0;
    	try {
    		iThemeId = Integer.valueOf( themeId ); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent( mContext, HiLauncherExDownTaskManagerActivity.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
		PendingIntent pIntent = PendingIntent.getActivity( mContext, iThemeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pIntent;
    }
}
