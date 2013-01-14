package com.nd.hilauncherdev.lib.theme.down;



import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nd.hilauncherdev.lib.theme.util.FileUtil;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;


/**
 * 模式:使用线程池为每个异步加载的图片提供服务
 * @author cfb
 *
 */
public class AsyncImageLoader {

	 private ExecutorService executorService = Executors.newCachedThreadPool(); 

	 private HashMap<String, WeakReference<Drawable>> imageCache;
	  
	     public AsyncImageLoader() {
	    	 imageCache = new HashMap<String, WeakReference<Drawable>>();
	     }
	  
	     public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
	         if (imageCache.containsKey(imageUrl)) {
	             WeakReference<Drawable> softReference = imageCache.get(imageUrl);
	             Drawable drawable = softReference.get();
	             if (drawable != null) {
	                 return drawable;
	             }
	         }
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	                 imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
	             }
	         };
	         
	         Thread t = new Thread() {
	             @Override
	             public void run() {
	                 Drawable drawable = loadImageFromUrl(imageUrl);
	                 imageCache.put(imageUrl, new WeakReference<Drawable>(drawable));
	                 Message message = handler.obtainMessage(0, drawable);
	                 handler.sendMessage(message);
	             }
	         };
	         //t.start();
	         executorService.execute(t);	         
	         return null;
	     }
	  
	 	/**
	 	 * 本地化缓存
	 	 * 
	 	 * @param url
	 	 *            链接地址
	 	 */
	 	public static Drawable loadImageFromUrl(String url) {
	 		String path = HiLauncherThemeGlobal.url2path(url, HiLauncherThemeGlobal.CACHES_HOME_MARKET);
	 		File pic = new File(path);
	 		if (!pic.exists()) {
	 			if ( !HiLauncherThemeGlobal.downloadImageByURL(url, path) ) {
	 				return null;
	 			}
	 		}
	 		
	 		Drawable dw = null;
	 		
	 		try {
	 			dw = Drawable.createFromPath(path);
	 			//文件存在但是读取出来为空的情况
	 			if (dw==null) {
	 				Log.e("AsyncImageLoader", "图片文件被损坏 null");
	 				//删除图片,等待下次重新下载
	 				FileUtil.delFile(path);
	 			}
	 		} catch (OutOfMemoryError e) {
				Log.e("AsyncImageLoader", "Out of memory",e);
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
	 		return dw;
	 	}

	  
	    public interface ImageCallback {
	       public void imageLoaded(Drawable imageDrawable, String imageUrl);
	    }	
	    
	    public void releaseImageCache(){
	    	if (imageCache==null) return;
	    	for (WeakReference<Drawable> weakReference : imageCache.values()) {
	             Drawable drawable = weakReference.get();
	             if (drawable != null) {
	            	 drawable.setCallback( null );
	             }
	    	}
	    	
	    	imageCache.clear();	    	 
	    }

}
