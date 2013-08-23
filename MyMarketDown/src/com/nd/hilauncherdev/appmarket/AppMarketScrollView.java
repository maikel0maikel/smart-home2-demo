package com.nd.hilauncherdev.appmarket;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class AppMarketScrollView extends ScrollView {

	private float mTouchDownPointX;
	private float mTouchDownPointY;
	private boolean mIsYMoved=false;
	private int mTouchSlop;
	public AppMarketScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	public AppMarketScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	public AppMarketScrollView(Context context) {
		super(context);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsYMoved=true;
			mTouchDownPointX=ev.getX();
			mTouchDownPointY=ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float x = ev.getX();
			final float y = ev.getY();
			final int xDiff = (int) Math.abs(x - mTouchDownPointX);
			final int yDiff = (int) Math.abs(y - mTouchDownPointY);

			mIsYMoved=(yDiff>xDiff && yDiff>mTouchSlop);
			break;
		}
		
		if(mIsYMoved)
			return super.onInterceptTouchEvent(ev);
		else
			return false;
	}
	
	

}
