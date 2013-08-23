package com.nd.hilauncherdev.appmarket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nd.android.pandahome2.R;
/**
 * 单元格项中的图标View
 * @author zhuchenghua
 *
 */
public class AppMarketItemIconView extends ImageView {

	private AppMarketItem mItem;
	private boolean mIsLoadingIcon;
	private Bitmap mIcon;
	
	public AppMarketItemIconView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public AppMarketItemIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketItemIconView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mIcon==null)
			mIcon=AppMarketUtil.getIconFromCache(mItem.getPackageName());
		//图标被回收，重新获取
		if(mIcon==null || mIcon.isRecycled()){
			setImageResource(R.drawable.app_market_default_icon);//默认图标
			loadIcon();
		}else{
			setImageBitmap(mIcon);
		}
		
		super.onDraw(canvas);
	}
	
	public void setAppMarketItem(AppMarketItem item)
	{
		mItem=item;
		postInvalidate();
	}
	
	/**
	 * 加载图标
	 */
	private void loadIcon()
	{
		if(mIsLoadingIcon) 
			return;
		mIsLoadingIcon=true;
		AppMarketUtil.executeThread(new Runnable() {
			
			@Override
			public void run() {
				
				boolean needRedraw=AppMarketUtil.loadIcon(mItem.getPackageName(), mItem.getIconFilePath(), mItem.getIconUrl());
				if(needRedraw)
					postInvalidate();
				mIsLoadingIcon=false;
			}
		});
	}

	/**
	 * 设置bitmap
	 * @param mIcon 
	 */
	public void setIconBitmap(Bitmap mIcon) {
		this.mIcon = mIcon;
		postInvalidate();
	}
	
}
