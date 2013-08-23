package com.nd.hilauncherdev.appmarket;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonLightbar;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.CommonSlidingViewData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.myphone.util.TabContainerUtil;

public class AppMarketLargeImagePreviewActivity extends Activity {

	/**
	 * 主显示View
	 */
	private RelativeLayout mainLayout;
	/**
	 * 滑动体View
	 */
	private PreviewSlidingView mSlidingView;
	private Context mContext;
	
	/**
	 * 指示灯
	 */
	private CommonLightbar lightBar;
	
	/**
	 * 指示灯选中状态图片
	 */
	private Drawable mLightChecked;
	/**
	 * 指示灯普通状态图片
	 */
	private Drawable mLightNormal;
	
	/**
	 * 滑动体中数据的封装
	 */
	private CommonSlidingViewData mSlidingViewData;
	/**
	 * 详情对象
	 */
	private AppMarketDetailItem mDetailItem;
	
	/**
	 * 图片对象集合
	 */
	private List<ICommonDataItem> mPreviewImageItems;
	
	private int mPosition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TabContainerUtil.fullscreen(this);//隐藏标题条
		super.onCreate(savedInstanceState);
		mContext=this;
		mDetailItem=(AppMarketDetailItem) getIntent().getSerializableExtra(AppMarketConstants.EXTRA_APP_MARKET_DETAIL_ITEM);
		if(mDetailItem==null)
		{
			finish();
			return;
		}
		
		mPosition=getIntent().getIntExtra(AppMarketConstants.EXTRA_APP_MARKET_POSITION, 0);
		
		mLightChecked = getResources().getDrawable(R.drawable.drawer_lightbar_checked);
		mLightNormal = getResources().getDrawable(R.drawable.drawer_lightbar_normal);
		
		initView();
		initParameter();
		initData();
	}
	
	/**
	 * 初始化界面
	 */
	private void initView()
	{
		mainLayout=new RelativeLayout(mContext);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
																			RelativeLayout.LayoutParams.MATCH_PARENT);
		mainLayout.setLayoutParams(params);
		
		//初始化滑动体
		mSlidingView=new PreviewSlidingView(mContext);
		RelativeLayout.LayoutParams slidingParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
																			RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
		mSlidingView.setLayoutParams(slidingParams);
		mSlidingView.setBackgroundColor(Color.parseColor("#888888"));
		mSlidingView.setPadding(0, 0, 0, 0);
		mainLayout.addView(mSlidingView);
		
		
		
		//初始化指示灯
		lightBar=new CommonLightbar(mContext);
		RelativeLayout.LayoutParams lightBarParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				ScreenUtil.dip2px(mContext, 8));
		lightBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
		lightBar.setLayoutParams(lightBarParams);
		lightBar.setGravity(Gravity.CENTER);
		lightBar.setNormalLighter(mLightNormal);
		lightBar.setSelectedLighter(mLightChecked);
		mainLayout.addView(lightBar);
		lightBar.update(0);
		
		mSlidingView.setCommonLightbar(lightBar);
		//关闭循环滑动
		mSlidingView.setEndlessScrolling(false);
		setContentView(mainLayout);
	}
	
	/**
	 * 初始化参数
	 */
	private void initParameter()
	{
		int screenWidth=ScreenUtil.getCurrentScreenWidth(mContext);
		int screenHeight=ScreenUtil.getCurrentScreenHeight(mContext);
		
		//计算单元格高宽
		int cellWidth=screenWidth;
		int cellHeight=screenHeight;
		
		mSlidingViewData=new CommonSlidingViewData(cellWidth, cellHeight, 1, 1, new ArrayList<ICommonDataItem>());
		mPreviewImageItems=mSlidingViewData.getDataList();
		mPreviewImageItems.clear();
		ArrayList<ICommonData> datas = new ArrayList<ICommonData>();
		datas.add(mSlidingViewData);
		mSlidingView.setList(datas);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData()
	{
		for(int i=0;i<mDetailItem.getPreviewImageUrlList().size();i++)
		{
			AppMarketDetailPreviewImageItem preItem=new AppMarketDetailPreviewImageItem();
			preItem.setResId(mDetailItem.getResId());
			preItem.setImageUrl(mDetailItem.getPreviewImageUrlList().get(i));
			mPreviewImageItems.add(preItem);
		}
		mSlidingView.setCurrentScreen(mPosition);
		mSlidingView.reLayout();
		//mSlidingView.snapToScreen(mPosition);
	}

	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && mSlidingView!=null){
			mSlidingView.removeAllViews();
			mLightNormal.setCallback(null);
			mLightChecked.setCallback(null);
		}
		return super.onKeyUp(keyCode, event);
	}


	/**
	 * 滑动体
	 */
	private class PreviewSlidingView extends CommonSlidingView{

		
		public PreviewSlidingView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		public PreviewSlidingView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public PreviewSlidingView(Context context) {
			super(context);
		}

		@Override
		protected void initSelf(Context ctx) {
			
		}
		
		

		@Override
		public void setCurrentScreen(int mCurrentScreen) {
			super.setCurrentScreen(mCurrentScreen);
		}

		@Override
		public View onGetItemView(ICommonData data, int position) {
			AppMarketDetailPreviewImageItem item=(AppMarketDetailPreviewImageItem) data.getDataList().get(position);
			AppMarketDetailPreviewImage preView=new AppMarketDetailPreviewImage(mContext);
			ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
			preView.setLayoutParams(params);
			preView.setPadding(5, 0, 5, 0);
			preView.setScaleType(ImageView.ScaleType.FIT_XY);
			preView.init(item);
			return preView;
		}
	}
}
