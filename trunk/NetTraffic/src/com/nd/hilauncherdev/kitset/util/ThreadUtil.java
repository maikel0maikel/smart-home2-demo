package com.nd.hilauncherdev.kitset.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 线程池管理
 * <br>Author:ryan
 * <br>Date:2012-7-27下午03:12:28
 */
public class ThreadUtil {
	/**
	 * 固定数量线程池
	 */
	private static ExecutorService executorService = Executors.newFixedThreadPool(1);
	/**
	 * 匣子专用线程池
	 */
	private static ExecutorService drawerExecutorService = Executors.newFixedThreadPool(1);
	/**
	 * 非固定数量线程池
	 */
	private static ExecutorService moreExecutorService = Executors.newCachedThreadPool();
	
	/**
	 * 固定数量线程池
	 */
	private static ExecutorService netTrafficExecutorService = Executors.newFixedThreadPool(1);
	
	
	/**
	 * 该方法为单线程执行仅适用于应用程序图标刷新
	 * <br>Author:ryan
	 * <br>Date:2012-7-27下午03:11:32
	 */
	public static void execute(Runnable command) {
		executorService.execute(command);
	}
	
	/**
	 * 该方法仅为匣子应用程序图标刷新
	 * <br>Author:ryan
	 * <br>Date:2012-7-27下午03:11:32
	 */
	public static void executeDrawer(Runnable command) {
		drawerExecutorService.execute(command);
	}

	/**
	 * 非固定数量线程池
	 * <br>Author:ryan
	 * <br>Date:2012-7-27下午03:11:55
	 */
	public static void executeMore(Runnable command) {
		moreExecutorService.execute(command);
	}
	
	/**
	 * 固定一个线程的线程池
	 * @param command
	 */
	public static void executeNetTraffic(Runnable command) {
		netTrafficExecutorService.execute(command);
	}
}
