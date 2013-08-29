package com.nd.hilauncherdev.myphone.util.commonsliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Anson
 */
public class CommonLayout extends ViewGroup {

	public CommonLayout(Context context) {
		super(context);
	}

	public CommonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean addViewInLayout(View child, int index,
			LayoutParams params, boolean preventRequestLayout) {
		return super
				.addViewInLayout(child, index, params, preventRequestLayout);
	}

	@Override
	protected void setChildrenDrawingCacheEnabled(boolean enabled) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View view = getChildAt(i);
			view.setDrawingCacheEnabled(enabled);
			view.buildDrawingCache(enabled);
			if (enabled) {
				view.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
			}
		}
		super.setChildrenDrawingCacheEnabled(enabled);
	}

	@Override
	public void setChildrenDrawnWithCacheEnabled(boolean enabled) {
		super.setChildrenDrawnWithCacheEnabled(enabled);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

	}

}
