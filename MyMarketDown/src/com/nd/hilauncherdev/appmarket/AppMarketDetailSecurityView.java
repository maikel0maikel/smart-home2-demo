package com.nd.hilauncherdev.appmarket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.BitmapUtils;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
/**
 * 软件安全信息View
 * @author zhuchenghua
 */
public class AppMarketDetailSecurityView extends LinearLayout {

	/**安全信息条*/
	private LinearLayout ll_security_bar;
	
	/**安全信息条上的安全信息文字条*/
	private LinearLayout ll_security_bar_tip;
	
	/**展开按钮*/
	private ImageView im_security_bar_expand;
	
	/**扫描结果显示容器*/
	private LinearLayout ll_security_desc_container;
	
	/**软件安全信息*/
	private AppMarketAppSafeInfo mSafeInfo;
	
	/**病毒项*/
	AppMarketAppSafeInfo.SafeItem mScanItem;
	/**广告项*/
	AppMarketAppSafeInfo.SafeItem mAdvertiseItem;
	/**隐私项*/
	AppMarketAppSafeInfo.SafeItem mPrivacyItem;
	
	/**绿图标*/
	private Drawable mDrawableGreen;
	/**黄图标*/
	private Drawable mDrawableYellow;
	/**红图标*/
	private Drawable mDrawableRed;
	/**灰图标*/
	private Drawable mDrawableGray;
	
	
	/**绿色*/
	private int mColorGreen=Color.parseColor("#71A701");
	/**黄色*/
	private int mColorYellow=Color.parseColor("#F86900");
	/**红色*/
	private int mColorRed=Color.parseColor("#E60808");
	/**灰色*/
	private int mColorGray=Color.GRAY;
	
	
	/**安全级别-绿*/
	private final int STATE_GREEN=1;
	
	/**安全级别-黄*/
	private final int STATE_YELLOW=2;
	
	/**安全级别-红*/
	private final int STATE_RED=3;
	
	/**安全级别-灰*/
	private final int STATE_GRAY=4;
	
	/**文字大小*/
	private float textSize=16f;
	
	/**TextView的图片与文字间距*/
	private int textDrawablePadding=5;
	
	/**扫描结果的缩进距离*/
	private int scanTextMarginLeft=0;
	
	/**扫描结果默认是不展开*/
	private boolean mIsDescVisible=false;
	
	public AppMarketDetailSecurityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketDetailSecurityView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ll_security_bar=(LinearLayout)findViewById(R.id.ll_security_bar);
		ll_security_bar_tip=(LinearLayout)findViewById(R.id.ll_security_bar_tip);
		im_security_bar_expand=(ImageView)findViewById(R.id.im_security_bar_expand);
		ll_security_desc_container=(LinearLayout)findViewById(R.id.ll_security_desc_container);
	}

	public void init(AppMarketAppSafeInfo safeInfo)
	{
		if(safeInfo==null)
			return;
		
		mSafeInfo=safeInfo;
		
		mDrawableGreen=getContext().getResources().getDrawable(R.drawable.app_market_security_green);
		mDrawableYellow=getContext().getResources().getDrawable(R.drawable.app_market_security_yellow);
		mDrawableRed=getContext().getResources().getDrawable(R.drawable.app_market_security_red);
		mDrawableGray=getContext().getResources().getDrawable(R.drawable.app_market_security_gray);
		
		//扫描结果的缩进距离
		scanTextMarginLeft=mDrawableGreen.getIntrinsicWidth()+textDrawablePadding;
		
		//病毒项
		mScanItem=mSafeInfo.getmScanProvider();
		//广告项
		mAdvertiseItem=mSafeInfo.getmAdvertisement();
		//隐私项
		mPrivacyItem=mSafeInfo.getmPrivacy();
		initSecurityBar();
		initSecurityDesc();
		
	}
	
	/**
	 * 初始化安全信息条
	 */
	private void initSecurityBar()
	{
		
		View scanItemView=getSafeItemView(mScanItem);
		View advertiseItemView=getSafeItemView(mAdvertiseItem);
		View privacyItemView=getSafeItemView(mPrivacyItem);
		
		if(scanItemView!=null)
			ll_security_bar_tip.addView(scanItemView);
		if(advertiseItemView!=null)
			ll_security_bar_tip.addView(advertiseItemView);
		if(privacyItemView!=null)
			ll_security_bar_tip.addView(privacyItemView);
		
		//点击展开安全信息详情
		ll_security_bar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mIsDescVisible)
				{
					ll_security_desc_container.setVisibility(View.GONE);
					im_security_bar_expand.setImageResource(R.drawable.app_market_detail_security_expand);
				}else{
					ll_security_desc_container.setVisibility(View.VISIBLE);
					im_security_bar_expand.setImageResource(R.drawable.app_market_detail_security_hide);
				}
				
				mIsDescVisible=!mIsDescVisible;
			}
		});
	}
	
	/**
	 * 初始化安全信息详情
	 */
	private void initSecurityDesc()
	{
		//病毒项详情View
		View scanDescItemView=getDescItemView(mScanItem,true);
		//广告项详情View
		View advertiseDescItemView=getDescItemView(mAdvertiseItem,false);
		//隐私项详情View
		View privacyDescItemView=getDescItemView(mPrivacyItem,false);
		if(scanDescItemView!=null)
			ll_security_desc_container.addView(scanDescItemView);
		if(advertiseDescItemView!=null)
			ll_security_desc_container.addView(advertiseDescItemView);
		if(privacyDescItemView!=null)
			ll_security_desc_container.addView(privacyDescItemView);
	}
	
	/**
	 * 获取安全项View
	 * @param safeItem
	 * @return
	 */
	private View getSafeItemView(AppMarketAppSafeInfo.SafeItem safeItem)
	{
		if(safeItem==null || safeItem.state==0 || safeItem.state>STATE_GRAY)
			return null;
		
		TextView textView=new TextView(getContext());
		textView.setTextSize(textSize);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		params.rightMargin=textDrawablePadding;
		textView.setLayoutParams(params);
		textView.setText(safeItem.title);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setCompoundDrawablePadding(textDrawablePadding);
		
		switch (safeItem.state) {
		case STATE_GREEN:
			textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableGreen, null, null, null);
			textView.setTextColor(mColorGreen);
			break;
		case STATE_YELLOW:
			textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableYellow, null, null, null);
			textView.setTextColor(mColorYellow);		
			break;
		case STATE_RED:
			textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableRed, null, null, null);
			textView.setTextColor(mColorRed);
			break;
			
		case STATE_GRAY:
			textView.setCompoundDrawablesWithIntrinsicBounds(mDrawableGray, null, null, null);
			textView.setTextColor(mColorGray);
			break;
			
		}
		
		return textView;
		
	}//end getSafeItemView
	
	/**
	 * 获取信息详情项的View
	 * @param safeItem
	 * @param isScanProvider 是否是第三方病毒检测
	 * @return
	 */
	private View getDescItemView(AppMarketAppSafeInfo.SafeItem safeItem,boolean isScanProvider)
	{
		if(safeItem==null || safeItem.state==0 || safeItem.state>STATE_GRAY)
			return null;
		
		LinearLayout descContainer=new LinearLayout(getContext());
		
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		params.bottomMargin=ScreenUtil.dip2px(getContext(),5);
		
		descContainer.setOrientation(LinearLayout.VERTICAL);
		descContainer.setLayoutParams(params);
		
		//安全信息项
		View safeItemView=getSafeItemView(safeItem);
		descContainer.addView(safeItemView);
		
		//安全信息项对应的详情项
		if(safeItem.descItemList!=null)
		{
			LinearLayout.LayoutParams textParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			textParams.leftMargin=scanTextMarginLeft;
			
			for(AppMarketAppSafeInfo.DescItem descItem:safeItem.descItemList)
			{
				if(descItem.state==0)
					continue;
				
				DrawableTextView scanItemView=new DrawableTextView(getContext());
				scanItemView.setTextSize(textSize);
				scanItemView.setTextColor(Color.BLACK);
				
				StringBuffer descText=new StringBuffer();
				descText.append(descItem.title);
				
				int color=Color.BLACK;
				String suffix="";
				switch (descItem.state) {
				case STATE_GREEN:
					suffix=getContext().getString(R.string.app_market_scan_ok);
					color=mColorGreen;
					break;
				case STATE_GRAY:
					suffix=getContext().getString(R.string.app_market_non_check);
					color=mColorGray;
					break;
				default:
					suffix=getContext().getString(R.string.app_market_non_check);
					color=mColorGray;
					break;
				}
				
				if(isScanProvider){
					descText.append(":").append(suffix);
					scanItemView.setText(getScanColorFulText(descText.toString(), color));
					//设置图标网址
					scanItemView.setIconUrl(descItem.iconUrl, null, true, false, false, false);
				}else{
					if(!TextUtils.isEmpty(descItem.content))
						descText.append(":").append("\n").append(descItem.content);
					
					scanItemView.setText(descText.toString());
				}
				
				scanItemView.setLayoutParams(textParams);
				
				descContainer.addView(scanItemView);
				
			}//end for
			
		}//end if
		
		return descContainer;
		
	}//end getDescItemView

	private SpannableString getScanColorFulText(String content,int color)
	{
		int index=content.indexOf(":");
		int begin=index+1;
		int end=content.length();
		
		SpannableString ss = new SpannableString(content); 
		
		ss.setSpan(new ForegroundColorSpan(color), begin, end,  
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
		return ss;
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try {
			mDrawableGray.setCallback(null);
			mDrawableGreen.setCallback(null);
			mDrawableRed.setCallback(null);
			mDrawableYellow.setCallback(null);
		} catch (Exception e) {
		}
	}
	
//-----------------------------------内部类-----------------------------------------------------------------//
	/**
	 * 带图片的TextView,目的在于获取网络的图片
	 */
	private class DrawableTextView extends TextView{

		/**图标地址*/
		private String mIconUrl;
		/**默认图标*/
		private Bitmap mDefaultBmp;
		/**图标*/
		private Bitmap mIconBmp;
		
		/**图标正在加载否*/
		private boolean mIsIconLoading=false;
		
		/**图标位置-左*/
		private boolean isLeft;
		/**图标位置-上*/
		private boolean isTop;
		/**图标位置-右*/
		private boolean isRight;
		/**图标位置-下*/
		private boolean isBottom;
		
		/**图标网址*/
		private String mIconFilePath;
		
		public DrawableTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public DrawableTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public DrawableTextView(Context context) {
			super(context);
		}
		
		/**
		 * 设置图标网址
		 * @param iconUrl
		 * @param defBmp 默认图标
		 * @param left 左边图标
		 * @param top 上边图标
		 * @param right 右边图标
		 * @param bottom 下边图标
		 */
		public void setIconUrl(final String iconUrl,
					Bitmap defBmp,
					boolean left,
					boolean top,
					boolean right,
					boolean bottom)
		{
			if(mIsIconLoading)
				return;
			if(TextUtils.isEmpty(iconUrl) && (defBmp==null || defBmp.isRecycled()))
				return;
			
			mIconUrl=iconUrl;
			mDefaultBmp=defBmp;
			isLeft=left;
			isTop=top;
			isRight=right;
			isBottom=bottom;
			
		}//end setIconUrl
		
		/**
		 * 加载图标
		 */
		private void loadIcon()
		{
			String fileName=FileUtil.getFileName(mIconUrl, true);
			fileName=TextUtils.isEmpty(fileName)?""+System.currentTimeMillis():fileName;
			final String filePath=AppMarketUtil.ICON_CACHE_DIR+fileName;
			
			if(FileUtil.isFileExits(filePath))
			{
				//获取本地图标
				mIconFilePath=filePath;
				refrashIcon();
			}else{
				
				//加载网络图标
				mIsIconLoading=true;
				AppMarketUtil.executeThread(new Runnable() {
					
					@Override
					public void run() {
						
						mIconFilePath=BitmapUtils.saveInternateImage(mIconUrl, filePath);
						mIsIconLoading=false;
						if(!TextUtils.isEmpty(mIconFilePath))
							refrashIcon();
					}
				});
			}
			
		}//end loadIcon
		
		/**
		 * 刷新图标
		 */
		private void refrashIcon()
		{
			post(new Runnable() {
				
				@Override
				public void run() {
					if((mIconBmp==null || mIconBmp.isRecycled()) && FileUtil.isFileExits(mIconFilePath))
					{
						mIconBmp=BitmapFactory.decodeFile(mIconFilePath);
					}else if(mDefaultBmp!=null && !mDefaultBmp.isRecycled()){
						mIconBmp=mDefaultBmp;
					}else
						return;
					
					if(mIconBmp!=null && !mIconBmp.isRecycled())
					{
						Drawable iconDrawable=new BitmapDrawable(getContext().getResources(), mIconBmp);
						if(isLeft)
							setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
						else if(isTop)
							setCompoundDrawablesWithIntrinsicBounds(null, iconDrawable, null, null);
						else if(isRight)
							setCompoundDrawablesWithIntrinsicBounds(null, null, iconDrawable, null);
						else if(isBottom)
							setCompoundDrawablesWithIntrinsicBounds(null, null, null, iconDrawable);
						
					}//end if
				}
			});
			
			
		}//end refrashIcon

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
			loadIcon();
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			if(mDefaultBmp!=null)
				mDefaultBmp.recycle();
			if(mIconBmp!=null)
				mIconBmp.recycle();
		}
		
		
	}//end class DrawableText
}
















