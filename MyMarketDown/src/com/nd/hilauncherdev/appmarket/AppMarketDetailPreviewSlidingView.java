package com.nd.hilauncherdev.appmarket;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;

public class AppMarketDetailPreviewSlidingView extends CommonSlidingView {

	public AppMarketDetailPreviewSlidingView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AppMarketDetailPreviewSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketDetailPreviewSlidingView(Context context) {
		super(context);
	}

	@Override
	protected void initSelf(Context ctx) {

	}
	
	@Override
	public View onGetItemView(ICommonData data, int position) {
		AppMarketDetailPreviewImageItem item=(AppMarketDetailPreviewImageItem) data.getDataList().get(position);
		AppMarketDetailPreviewImage imageView=new AppMarketDetailPreviewImage(getContext());
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		params.weight=1;
		imageView.setLayoutParams(params);
		int paddingLeft=ScreenUtil.dip2px(getContext(), 5);
		int paddingRight=ScreenUtil.dip2px(getContext(), 5);
		int paddingTop=ScreenUtil.dip2px(getContext(), 10);
		int paddingBottom=ScreenUtil.dip2px(getContext(), 10);
		
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		imageView.setImageResource(R.drawable.theme_shop_v2_theme_no_find_small);
		imageView.setVisibility(View.INVISIBLE);
		
		imageView.init(item);
		return imageView;
	}

}
