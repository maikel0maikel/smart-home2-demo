package com.nd.hilauncherdev.appmarket;

import java.util.List;

/**
 * 安全信息
 * @author zhuchenghua
 */
public class AppMarketAppSafeInfo {

	/**
	 * 病毒信息
	 */
	private SafeItem mScanProvider;
	
	/**
	 * 广告信息
	 */
	private SafeItem mAdvertisement;
	
	/**
	 * 隐私信息
	 */
	private SafeItem mPrivacy;
	
	public SafeItem getmScanProvider() {
		return mScanProvider;
	}

	public void setmScanProvider(SafeItem mScanProvider) {
		this.mScanProvider = mScanProvider;
	}

	public SafeItem getmAdvertisement() {
		return mAdvertisement;
	}

	public void setmAdvertisement(SafeItem mAdvertisement) {
		this.mAdvertisement = mAdvertisement;
	}

	public SafeItem getmPrivacy() {
		return mPrivacy;
	}

	public void setmPrivacy(SafeItem mPrivacy) {
		this.mPrivacy = mPrivacy;
	}

//-----------------------内部类---------------------------------------------------------------//
	
	private static class CommonItem{
		/**标题*/
		public String title;
		/**状态量*/
		public int state;
	}
	
	/**
	 * 安全信息子项
	 */
	public static class SafeItem extends CommonItem{
		
		/**描述项列表*/
		public List<DescItem> descItemList;
		
	}//end class ScanProvider
	
	/**
	 * 安全信息子项的描述项
	 */
	public static class DescItem extends CommonItem{
		
		public String iconUrl;
		public String content;
	}//end class DescItem
}
