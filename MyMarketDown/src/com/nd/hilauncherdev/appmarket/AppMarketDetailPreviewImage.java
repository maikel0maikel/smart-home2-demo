package com.nd.hilauncherdev.appmarket;

import java.io.File;
import java.lang.ref.SoftReference;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.httplib.HttpCommon;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.StringUtil;

/**
 * 详情页的预览图对象
 * @author zhuchenghua
 */
public class AppMarketDetailPreviewImage extends ImageView {

	
	private AppMarketDetailPreviewImageItem mItem;
	
	private SoftReference<Bitmap> mPreImageBmpWr;
	
	private boolean mIsLoading=false;
	
	public AppMarketDetailPreviewImage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public AppMarketDetailPreviewImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketDetailPreviewImage(Context context) {
		super(context);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(mPreImageBmpWr==null || mPreImageBmpWr.get()==null || mPreImageBmpWr.get().isRecycled()){
			loadImage();
			setImageResource(R.drawable.theme_shop_v2_theme_no_find_small);
		}else
			setImageBitmap(mPreImageBmpWr.get());
		
		super.onDraw(canvas);
	}

	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		invalidate();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		//Log.d(Global.TAG, "AppMarketDetailPreviewImage invisible...");
		if(mPreImageBmpWr!=null && mPreImageBmpWr.get()!=null)
			mPreImageBmpWr.get().recycle();
	}

	/**
	 * 初始化
	 * @param resId
	 * @param index
	 * @param imageUrl
	 */
	public void init(AppMarketDetailPreviewImageItem item)
	{
		mItem=item;
		if(!StringUtil.isEmpty(mItem.getImageUrl()))
			setVisibility(View.VISIBLE);
	}
	
	/**
	 * 加载图片
	 */
	private void loadImage()
	{
		if(mIsLoading)
			return;
		mIsLoading=true;
		
		AppMarketUtil.executeThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					final String fileName=mItem.getResId()+"_"+mItem.getPosition();
					final String filePath=AppMarketUtil.ICON_CACHE_DIR+fileName;
					File file=new File(filePath);
					Bitmap bmp=null;
					if(file.exists())
					{
						bmp=BitmapFactory.decodeFile(file.getAbsolutePath());
						mPreImageBmpWr=new SoftReference<Bitmap>(bmp);
					}else if(!StringUtil.isEmpty(mItem.getImageUrl())){
						HttpCommon httpCommon=new HttpCommon(mItem.getImageUrl());
						HttpEntity  entity=httpCommon.getResponseAsEntityGet(null);
						bmp=BitmapFactory.decodeStream(entity.getContent());
						if(bmp!=null)
						{
							String contentType=entity.getContentType().getValue();
							CompressFormat format=null;
							if(contentType!=null && contentType.equals("image/png")){
								format=Bitmap.CompressFormat.PNG;
							}else{
								format=Bitmap.CompressFormat.JPEG;
							}
							
							FileUtil.saveImageFile(AppMarketUtil.ICON_CACHE_DIR, fileName, bmp, format);
						}
					}
					
					mPreImageBmpWr=new SoftReference<Bitmap>(bmp);
					postInvalidate();
					
				} catch (Exception e) {
					Log.w(Global.TAG, "AppMarketDetailPreviewImage laod image error:"+e.toString());
				} finally{
					mIsLoading=false;
				}
			}
		});
	}
}


