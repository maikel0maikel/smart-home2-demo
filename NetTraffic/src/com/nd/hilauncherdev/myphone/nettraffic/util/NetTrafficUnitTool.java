package com.nd.hilauncherdev.myphone.nettraffic.util;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.db.NetTrafficBytesItem;


public class NetTrafficUnitTool {

	/**Gprs图标*/
	private static Drawable gprsDrawable;
	/**Wifi图标*/
	private static Drawable wifiDrawable;
	
	/**
	 * 初始化91豆图标
	 * @param ctx
	 * @param resID 图标资源ID
	 */
	public synchronized static Drawable getNetTypeDrawable(Context ctx, int netType){
		
		if ( NetTrafficBytesItem.DEV_GPRS==netType ) {
			if ( gprsDrawable==null ) {
				try{
					gprsDrawable= ctx.getResources().getDrawable(R.drawable.net_traffic_float_gprs);
					gprsDrawable.setBounds(0, 0, gprsDrawable.getMinimumWidth(), gprsDrawable.getMinimumHeight());
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return gprsDrawable;
		}else{
			if ( wifiDrawable==null ) {
				try{
					wifiDrawable= ctx.getResources().getDrawable(R.drawable.net_traffic_float_wifi);
					wifiDrawable.setBounds(0, 0, wifiDrawable.getMinimumWidth(), wifiDrawable.getMinimumHeight());
				}catch (Exception e) {
					e.printStackTrace();
				}
			}		
			return wifiDrawable;
		}
	}
	
	/**
	 * 设置流量TextView的图标
	 * @param priceTextView
	 * @param isShow
	 */
	public static void setNetTypeTextViewDrawable(TextView priceTextView, boolean isShow, int netType){
		
		if (priceTextView!=null){
			if (isShow){
				priceTextView.setCompoundDrawables(NetTrafficUnitTool.getNetTypeDrawable(priceTextView.getContext(), netType),null,null,null);
			}else{
				priceTextView.setCompoundDrawables(null,null,null,null);
			}
		}
	}
	
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
