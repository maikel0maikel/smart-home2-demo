package com.nd.hilauncherdev.appmarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadBroadcastExtra;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

/**
 * 进入下载的图标View
 * @author zhuchenghua
 *
 */
public class AppMarketNumIcon extends ImageView {

	private Context mContext;
	private Drawable numIcon;
	private DownloadReceiver mDownloadReceiver;
	private int number;
	/**
	 * 下载服务
	 */
	private DownloadServerServiceConnection mDownloadService=null;
	
	public AppMarketNumIcon(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
	}

	public AppMarketNumIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}

	public AppMarketNumIcon(Context context) {
		super(context);
		mContext=context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//绑定下载服务
		mDownloadService=new DownloadServerServiceConnection(mContext);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mDownloadService!=null && mDownloadService.isBind())
			number=mDownloadService.getTaskCount();
		
		if(numIcon==null)
			numIcon=mContext.getResources().getDrawable(R.drawable.app_notice_bg);
		if(number>0) //绘制正在下载个数的图标
			drawNumIcon(canvas,number);
	}

	private void drawNumIcon(Canvas canvas,int number)
	{
		int top=getTop();
		int width=getWidth();
		
		int numIconLeft=width-numIcon.getIntrinsicWidth();
		int numIconRight=(numIconLeft+numIcon.getIntrinsicWidth());
		int numIconTop=0;
		int numIconButtom=(numIconTop+numIcon.getIntrinsicHeight());
		
		numIcon.setBounds(numIconLeft, numIconTop, numIconRight, numIconButtom);
		numIcon.draw(canvas);
		
		Paint paint=new Paint();
		paint.setAntiAlias( true );
		paint.setTextAlign( Align.CENTER);
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(1);
		paint.setTextSize(ScreenUtil.dip2px(mContext, 12));
		
		float textX=numIconLeft+numIcon.getIntrinsicWidth()/2;
		float textY=top+numIcon.getIntrinsicHeight()/2+ScreenUtil.dip2px(mContext, 2);
		canvas.drawText(number+"", textX, textY, paint);
	}
	
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(mDownloadReceiver!=null)
			return;
		IntentFilter iFilter=new IntentFilter(DownloadBroadcastExtra.ACTION_DOWNLOAD_STATE);
		mDownloadReceiver=new DownloadReceiver();
		mContext.registerReceiver(mDownloadReceiver, iFilter);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mDownloadReceiver!=null){
			mContext.unregisterReceiver(mDownloadReceiver);
			mDownloadReceiver=null;
		}
		
	}


	/**
	 * 下载状态监听
	 */
	private class DownloadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int downloadSate=intent.getIntExtra(DownloadBroadcastExtra.EXTRA_STATE, DownloadState.STATE_NONE);
			if(downloadSate!=DownloadState.STATE_DOWNLOADING) //下载完成，重绘图标
				postInvalidate();
			else if(number==0){
				postInvalidate();
			}
				
		}
		
	}//end DownloadReceiver
}
