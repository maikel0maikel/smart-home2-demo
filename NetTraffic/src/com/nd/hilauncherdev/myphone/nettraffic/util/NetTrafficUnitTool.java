package com.nd.hilauncherdev.myphone.nettraffic.util;

import java.text.DecimalFormat;

public class NetTrafficUnitTool {

	/**
	 * 流量显示格式转换 转为相应的单位
	 * 
	 * @param floatnum
	 *            传入大小为 KB
	 * @return
	 */
	public static String netTrafficUnitHandler(float floatNum) {

		String value = null;
		String suffix = "KB";
		float result = floatNum;

		// 如果小于0.1k用B显示
		if (result < 0.1) {
			suffix = "B";
			result = result * 1024;
			DecimalFormat format = new DecimalFormat("0");
			value = format.format(result) + suffix;
			return value;
		} else {
			if (result > 900) {
				suffix = "MB";
				result = result / 1024;
			}
		}

		DecimalFormat format = new DecimalFormat("0.00");
		value = format.format(result) + suffix;

		return value;
	}
	
	
	public static String netTrafficSortUnitHandler(float floatnum) {
		String value = null;
		floatnum = floatnum/1024f;
		if (floatnum<0.01){
			value = "<0.01M";
		}else{
			DecimalFormat format = new DecimalFormat("0.00");
			value = format.format(floatnum) + "MB";
		}
		return value;
	}
}
