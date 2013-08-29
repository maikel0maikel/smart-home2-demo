package com.nd.hilauncherdev.myphone.util.commonsliding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * 屏幕指示灯
 * 
 * @author Anson
 */
public class CommonLightbar extends LinearLayout {
	
	private static final String TAG = "CommonLightbar";

	private Drawable normal_lighter, selected_lighter;
	
	private int items, lastPos;
	
	private Context context;
	
	public CommonLightbar(Context context) {
		super(context);
		this.context = context;		
	}
	
	public CommonLightbar(Context context, AttributeSet attrs) {		
		super(context, attrs);
		this.context = context;
	}
	
	public void refresh(int size, int current) {
		if (items == size) {
			return;
		}
		if (items < size) {
			for (int i = items; i < size; i++) {
				ImageView iv = new ImageView(context);
				iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				iv.setImageDrawable(normal_lighter);
				this.addView(iv);
			}
		} else {
			this.removeViews(size, items - size); 
		}
		items = size;
		lastPos = -1;
		update(current);
		requestLayout();
	}
	
	/**
	 * 基于0计算
	 */
	public void update(int pos) {
		if (pos >= items) {
			Log.e(TAG, "pos > items!!!");
			return;
		}
		if (pos == lastPos)
			return;
		
		((ImageView)this.getChildAt(pos)).setImageDrawable(selected_lighter);
		if (lastPos != -1) {
			((ImageView)this.getChildAt(lastPos)).setImageDrawable(normal_lighter);
		}
		lastPos = pos;
	}

	public void setNormalLighter(Drawable normal_lighter) {
		this.normal_lighter = normal_lighter;
	}

	public void setSelectedLighter(Drawable selected_lighter) {
		this.selected_lighter = selected_lighter;
	}
}
