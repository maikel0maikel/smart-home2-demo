package com.nd.hilauncherdev.appmarket;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonLayout;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.ApkDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadDBManager;

/**
 * 一键装机/热门游戏 界面的划动体View
 * @author zhuchenghua
 *
 */
public class AppMarketSlidingView extends CommonSlidingView {

	private HashMap<String, View> mDownloadViewMap=new HashMap<String, View>();
	private Map<String,ApkDownloadInfo> mDownloadTasks ;
	
	public AppMarketSlidingView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public AppMarketSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMarketSlidingView(Context context) {
		super(context);
	}

	@Override
	protected void initSelf(Context ctx) {
		mDownloadTasks = DownloadDBManager.getDownloadLoadTask(ctx);
	}

	@Override
	public View onGetItemView(ICommonData data, int position) {
		AppMarketItem item=(AppMarketItem) data.getDataList().get(position);
		int lastPos=data.getDataList().size()-1;
		
		View v=LayoutInflater.from(getContext()).inflate(R.layout.app_market_app_boxed, null);
		if(position==lastPos){ //最后一个,点击进入"91助手"
			AppMarketItemIconView iconView = (AppMarketItemIconView) v.findViewById(R.id.im_icon);
			iconView.setIconBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.app_market_more));
			iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			TextView tv = (TextView) v.findViewById(R.id.tv_title);
			tv.setText(R.string.appinfo_load_more);
		}else{
			AppMarketItemView itemView=(AppMarketItemView)v;
			itemView.init(item, this);
			v.setTag(item);
		}
		
		
		return v;
	}

	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		ICommonData data=getData(0);
		
		if(data!=null)
		{
			//一页的列数
			int pcolNum=data.getColumnNum();
			//一页的行数
			int prowNum=data.getRowNum();
			
			//单元格宽
			int cellWidth=getPageWidth()/pcolNum;
			//单元格高
			int cellHeight=getPageHeight()/prowNum;
			
			//所有页的总列数
			int allColNum=pcolNum*getPageCount();
			
			
			float startX=0f;
			float startY=0f;
			float stopX=0f;
			float stopY=getPageHeight();
			Paint paint=new Paint();
			paint.setColor(Color.parseColor("#9a9a9a"));
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(1);
			
			//绘制纵向分隔线
			for(int i=1;i<allColNum;i++)
			{
				stopX=startX=i*cellWidth;
				canvas.drawLine(startX, startY, stopX, stopY, paint);
			}
			
			//绘制横向分隔线
			startX=0f;
			stopX=getPageWidth()*getPageCount();
			for(int j=1;j<prowNum;j++)
			{
				stopY=startY=j*cellHeight;
				canvas.drawLine(startX, startY, stopX, stopY, paint);
			}
			
		}
		
	}//end dispatchDraw
	
	public HashMap<String, View> getDownloadViewMap()
	{
		if(mDownloadViewMap==null)
			mDownloadViewMap=new HashMap<String, View>();
		return mDownloadViewMap;
	}
	/**
	 * 获取下载的View
	 * @param downloadUrl
	 * @return
	 */
	public View getViewInDownloadByUrl(String downloadUrl)
	{
		return getDownloadViewMap().get(downloadUrl);
	}
	
	/**
	 * 打开/关闭可选状态
	 * @param isForSelect
	 */
	public void setForSelect(boolean isForSelect)
	{
		int pageCount=getPageCount();
		for(int i=0;i<pageCount;i++)
		{
			CommonLayout page=getCommonLayout(i);
			int childCount=page.getChildCount();
			for(int j=0;j<childCount;j++)
			{
				AppMarketItemView itemView=(AppMarketItemView)page.getChildAt(j);
				itemView.setForSelect(isForSelect);
			}
			
		}
	}
	
	/**
	 * 选中/清除所有选中项
	 */
	public void setAllSelect(boolean isAllSelect)
	{
		int pageCount=getPageCount();
		for(int i=0;i<pageCount;i++)
		{
			CommonLayout page=getCommonLayout(i);
			int childCount=page.getChildCount();
			for(int j=0;j<childCount;j++)
			{
				AppMarketItemView itemView=(AppMarketItemView)page.getChildAt(j);
				itemView.setItemSelected(isAllSelect);
			}
			
		}
	}
	
	/**
	 * 添加下载的View
	 * @param downloadUrl
	 * @param view
	 */
	public void addDownloadView(String downloadUrl,View view)
	{
		getDownloadViewMap().put(downloadUrl, view);
		
	}
	
	public void removeDownloadView(String downloadUrl)
	{
		getDownloadViewMap().remove(downloadUrl);
	}

	/**
	 * 重新刷新下载状态
	 */
	void refreshDownloadState() {
		int childCount=getChildCount();
		if (childCount > 0) {
			mDownloadTasks = DownloadDBManager.getDownloadLoadTask(getContext());
			for(int i=0;i<childCount;i++)
			{
				CommonLayout page=(CommonLayout) getChildAt(i);
				int pageChildCount=page.getChildCount();
				for(int j=0;j<pageChildCount;j++)
				{
					AppMarketItemView itemView=(AppMarketItemView) page.getChildAt(j);
					itemView.reInitState();
				}
			}
		}
	}

	/**
	 * 获取下载列表
	 * @return
	 */
	public Map<String,ApkDownloadInfo> getDownloadTasks() {
		return mDownloadTasks;
	}

}
