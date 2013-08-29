package com.nd.hilauncherdev.myphone.util.commonsliding;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;


/**
 *
 * @author Anson
 */
public class CommonViewHolder {
	
	/**
	 * 记录View在所在数据集中的位置
	 */
	public int positionInData;
	
	/**
	 * 记录View在所在屏幕中的位置
	 */
	public int positionInScreen;
	
	/**
	 * 记录View在所在屏幕
	 */
	public int screen;
	
	public ICommonDataItem item;
}
