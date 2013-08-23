package com.nd.hilauncherdev.appmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.datamodel.Global;
import com.nd.hilauncherdev.framework.ViewFactory;
import com.nd.hilauncherdev.webconnect.downloadmanage.OneKeyPhoneHelper;

/**
 * 往期专题
 * @author zhuchenghua
 *
 */
public class AppMarketSubjectLaterActivity extends Activity {

	private Context mContext;
	private LinearLayout ll_data_main;
	private WebView mWebView;
	private ImageView app_running_back_btn;
	
	/***
	 * 正在加载。。的视图
	 */
	private View mLoadingView;
	/**
	 * 无数据的视图
	 */
	private View mNoDataView;
	/**
	 * 网络故障的视图
	 */
	private View mNetworkErrorView;
	
	/**
	 * 页加载是否出错
	 */
	private boolean mIsWebLoadingError=false;
	
	/**
	 * 网页地址
	 */
	private String webUrl=null;
	
	private int mClientType;
	
	/**记录WebView的滚动坐标Y*/
	private int mWebViewScrollY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_market_subject_later_activity);
		mContext=this;
		webUrl=getIntent().getStringExtra(Intent.EXTRA_TEXT);
		mClientType=getIntent().getIntExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, OneKeyPhoneHelper.EXTRA_ONE_KEY_PHONE_NEED);
		initView();
		initData();
	}
	
	private void initView()
	{
		ll_data_main=(LinearLayout)findViewById(R.id.ll_data_main);
		mWebView=(WebView)findViewById(R.id.web_view);
		app_running_back_btn=(ImageView)findViewById(R.id.app_running_back_btn);
		
		mLoadingView=ViewFactory.getNomalErrInfoView(mContext, ll_data_main, ViewFactory.LOADING_DATA_INFO_VIEW);
		mNoDataView=ViewFactory.getNomalErrInfoView(mContext, ll_data_main, ViewFactory.SEARCH_NO_DATA_VIEW);
		mNetworkErrorView=ViewFactory.getNomalErrInfoView(mContext, ll_data_main, ViewFactory.NET_BREAK_VIEW);
		LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)mLoadingView.getLayoutParams();
		params.height=LinearLayout.LayoutParams.MATCH_PARENT;
		mLoadingView.setLayoutParams(params);
		mNoDataView.setLayoutParams(params);
		mNetworkErrorView.setLayoutParams(params);
		
		mLoadingView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		mNetworkErrorView.setVisibility(View.GONE);
		
		app_running_back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLightTouchEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(true);
		//设置浏览器加载状态处理
    	mWebView.setWebViewClient(new WebViewClient(){

    		
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.d(Global.TAG, "AppMarketSubjectLaterActivity web view onPageStarted:"+url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				showDataFace();
				mWebView.getSettings().setBlockNetworkImage(false);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				mIsWebLoadingError=true;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent intent=new Intent(mContext,AppMarketSubjectDetailActivity.class);
				intent.putExtra(Intent.EXTRA_TEXT, url);
				intent.putExtra(OneKeyPhoneHelper.EXTRA_ONE_KEY_TYPE, mClientType);
				startActivity(intent);
				return true;
			}
    		
    	});
    	
	}
	
	/**
	 * 加载数据
	 */
	private void initData()
	{
		mLoadingView.setVisibility(View.VISIBLE);
		if(webUrl!=null){
			try {
	    		mWebView.loadUrl(webUrl);
			} catch (Exception e) {
				mIsWebLoadingError=true;
				showDataFace();
				Log.w(Global.TAG, "AppMarketSubjectLaterActivity.initData load web failed:"+e.toString());
			}
		}else{
			mIsWebLoadingError=true;
			showDataFace();
		}
	}
	
	private void showDataFace()
	{
		if(mIsWebLoadingError)
			mNoDataView.setVisibility(View.VISIBLE);
		else
			mWebView.setVisibility(View.VISIBLE);
		
		mLoadingView.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		if(mWebView!=null)
			mWebViewScrollY=mWebView.getScrollY();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//WebView滚动到之前记录的位置
		if(mWebView!=null)
			mWebView.scrollTo(0, mWebViewScrollY);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(mWebView!=null){
				mWebView.stopLoading();
				mWebView.freeMemory();
				mWebView.destroy();
			}
		} catch (Exception e) {
		}
		
	}
	
	
}
