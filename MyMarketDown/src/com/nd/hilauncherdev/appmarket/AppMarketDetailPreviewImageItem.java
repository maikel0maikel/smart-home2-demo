package com.nd.hilauncherdev.appmarket;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;

/**
 * 软件详情预览图对象
 * @author zhuchenghua
 *
 */
public class AppMarketDetailPreviewImageItem implements ICommonDataItem{

	private int pos;
	private long resId;
	private String imageUrl;
	
	@Override
	public int getPosition() {
		return pos;
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public void setPosition(int position) {
		pos=position;
	}

	public long getResId() {
		return resId;
	}

	public void setResId(long resId) {
		this.resId = resId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
