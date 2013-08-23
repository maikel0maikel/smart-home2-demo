package com.nd.hilauncherdev.appmarket;

import com.nd.android.pandahome2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 防止图片被回收引起崩溃的ImageView
 * @author zhuchenghua
 *
 */
public class AppMarketImageView extends ImageView {

	public AppMarketImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AppMarketImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketImageView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable image=getDrawable();
		if(image!=null)
		{
			Bitmap bmp=((BitmapDrawable)image).getBitmap();
			if(bmp==null || bmp.isRecycled())
				setImageResource(R.drawable.app_market_default_icon);
		}
		
		super.onDraw(canvas);
	}
	
	

}
