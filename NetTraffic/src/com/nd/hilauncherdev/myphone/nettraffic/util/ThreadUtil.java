package com.nd.hilauncherdev.myphone.nettraffic.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
	/**
	 * 固定数量线程池
	 */
	private static ExecutorService netTrafficExecutorService = Executors.newFixedThreadPool(1);
	
	/**
	 * 固定一个线程的线程池
	 * @param command
	 */
	public static void executeNetTraffic(Runnable command) {
		netTrafficExecutorService.execute(command);
	}
}
