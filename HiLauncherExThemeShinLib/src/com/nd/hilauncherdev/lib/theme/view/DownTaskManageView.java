package com.nd.hilauncherdev.lib.theme.view;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.kitset.util.ApkTools;
import com.nd.hilauncherdev.lib.theme.NdLauncherExDialogDefaultImp;
import com.nd.hilauncherdev.lib.theme.NdLauncherExThemeApi;
import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.db.ThemeLibLocalAccessor;
import com.nd.hilauncherdev.lib.theme.down.AsyncImageLoader;
import com.nd.hilauncherdev.lib.theme.down.AsyncImageLoader.ImageCallback;
import com.nd.hilauncherdev.lib.theme.down.DownloadNotification;
import com.nd.hilauncherdev.lib.theme.down.DownloadService;
import com.nd.hilauncherdev.lib.theme.down.DownloadTask;
import com.nd.hilauncherdev.lib.theme.down.ThemeDownloadStateManager;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;
import com.nd.hilauncherdev.lib.theme.util.HiLauncherThemeGlobal;


/**
 * 下载任务View
 * @author cfb
 *
 */
public class DownTaskManageView extends FrameLayout {

	private Context ctx;
	private LayoutInflater mInflater = null;
	private ExpandableListView mExpandableListView;
	public DownExpandableAdapter mDownExpandableAdapter;
	private RelativeLayout notaskLayout;
	
	/**下载进度接收器*/
	private ThemeDownloadProgressReceiver mThemeDownloadProgressReceiver = new ThemeDownloadProgressReceiver();	
	
	public DownTaskManageView(Context context) {
		super(context);
		ctx = context;
		mInflater = LayoutInflater.from( ctx );
	}	
	
	/**
	 * 接口动态修改item
	 * @param downingTaskItem
	 * @param newState
	 */
	public synchronized void updateDownTask(String themeID, int progress, int opType, String tempFilePath, String newThemeID){

		mDownExpandableAdapter.updateDownTaskProcess(themeID,progress,opType,tempFilePath, newThemeID);		
	}
	
	/**
	 * 更新下载队列状态到数据库
	 * @param downingTaskItem
	 * @param newState
	 */
	public void setDownTaskItemState(DowningTaskItem downingTaskItem, int newState){
		
		downingTaskItem.state = newState;
		
		try {
			ThemeLibLocalAccessor.getInstance(ctx).updateDowningTaskItem(downingTaskItem);
		} catch (Exception e) {
	   	 	e.printStackTrace();
	    }					
	}
	
	public void setButtonTitle(DowningTaskItem downingTaskItem, Button actionButton, Button deleteButton){
		
		deleteButton.setVisibility(View.VISIBLE);
		
		switch (downingTaskItem.state) {
		case DowningTaskItem.DownState_Downing:
			actionButton.setText( R.string.ndtheme_manage_downstate_downing );			
			actionButton.setBackgroundResource(R.drawable.nd_hilauncher_theme_manage_downtask_pause_btn);
			deleteButton.setEnabled(false);
			break;
		case DowningTaskItem.DownState_Finish:
			actionButton.setText( R.string.ndtheme_manage_downstate_finish );
			actionButton.setBackgroundResource(R.drawable.nd_hilauncher_theme_manage_downtask_action_btn);
			deleteButton.setEnabled(true);
			break;
		case DowningTaskItem.DownState_Fail:
			actionButton.setText( R.string.ndtheme_manage_downstate_fail );
			actionButton.setBackgroundResource(R.drawable.nd_hilauncher_theme_manage_downtask_down_btn);
			deleteButton.setEnabled(true);
			break;			
		case DowningTaskItem.DownState_Pause:
			actionButton.setText( R.string.ndtheme_manage_downstate_pause );
			actionButton.setBackgroundResource(R.drawable.nd_hilauncher_theme_manage_downtask_redown_btn);
			deleteButton.setEnabled(true);
			break;				
		default:
			break;
		}
	}
	
	/**
	 * 刷新列表,重新加载下载任务
	 */
	public void refreshDownList(){
		try {
			//加载下载任务
			ArrayList<DowningTaskItem> failList = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskByState(DowningTaskItem.DownState_Fail);
			ArrayList<DowningTaskItem> downIngList = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskByState(DowningTaskItem.DownState_Downing);
			ArrayList<DowningTaskItem> pauseList = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskByState(DowningTaskItem.DownState_Pause);
			LinkedList<LinkedList<DowningTaskItem>> childArray = new LinkedList<LinkedList<DowningTaskItem>>();
			LinkedList<DowningTaskItem> downingTaskList = new LinkedList<DowningTaskItem>();
			downingTaskList.addAll(failList);		
			downingTaskList.addAll(downIngList);
			downingTaskList.addAll(pauseList);
			
			ArrayList<DowningTaskItem> finishList = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskByState(DowningTaskItem.DownState_Finish);
			LinkedList<DowningTaskItem> finishTaskList = new LinkedList<DowningTaskItem>();
			finishTaskList.addAll(finishList);		
			
			childArray.addLast(downingTaskList);
			childArray.addLast(finishTaskList);
			
			LinkedList<ExpandGroupBean> groupArray = new LinkedList<ExpandGroupBean>();
			ExpandGroupBean downingExpGrpBean = new ExpandGroupBean(); 
			downingExpGrpBean.groupName = getResources().getString(R.string.ndtheme_manage_downtasks_downing_txt);
			downingExpGrpBean.titleName = getResources().getString(R.string.ndtheme_manage_downtasks_downtask_nums_txt);
			ExpandGroupBean finishExpGrpBean = new ExpandGroupBean();
			finishExpGrpBean.groupName = getResources().getString(R.string.ndtheme_manage_downtasks_finish);
			finishExpGrpBean.titleName = getResources().getString(R.string.ndtheme_manage_downtasks_downtask_finish_nums_txt);
			groupArray.addLast(downingExpGrpBean);
			groupArray.addLast(finishExpGrpBean);
			
			mDownExpandableAdapter.clearView();
			mDownExpandableAdapter.addGroupList(groupArray, childArray);
			
			mDownExpandableAdapter.notifyDataSetChanged();
			
			//遍历所有group,将所有项设置成默认展开
			int groupCount = mExpandableListView.getCount();
			for (int i=0; i<groupCount; i++) {
				mExpandableListView.expandGroup(i);
			};
		} catch (Exception e) {
	   	 	e.printStackTrace();
	    }
	}
	
	public void addView(int paramInt) {
		LayoutInflater.from( ctx ).inflate(paramInt, this);
	}
	
	public void initView() {
		
		addView(R.layout.nd_hilauncher_theme_manage_downtask_list_group);

		setupViews();
		
		initReceiver();
	}	

	private void setupViews() {
		
		mExpandableListView 	= (ExpandableListView) this.findViewById(R.id.downtaskExpandableList);
		
		mDownExpandableAdapter = new DownExpandableAdapter(mExpandableListView);
		mExpandableListView.setAdapter(mDownExpandableAdapter);
		mExpandableListView.setGroupIndicator(null);
		
		refreshDownList();
		checkNoTaskList();
		return ;
	}
	
	//判断是否有无正在在下的任务,没有则提示下载更多
	private void checkNoTaskList(){
		
		if ( mDownExpandableAdapter.getChildrenCount(0)==0 &&
				mDownExpandableAdapter.getChildrenCount(1)==0 ){
			//没有下载任务的时候
		}else{
			if ( notaskLayout!=null ){
				notaskLayout.setVisibility(View.GONE);
			}
		}
	}
	
	public class DownExpandableAdapter extends BaseExpandableListAdapter {

		private ExpandableListView mExpandableListView;	    
	    private AsyncImageLoader mAsyncImageLoader;	
	    
		private LinkedList<ExpandGroupBean> groupArray = new LinkedList<ExpandGroupBean>();
		private LinkedList<LinkedList<DowningTaskItem>> childArray = new LinkedList<LinkedList<DowningTaskItem>>();
		
		private LinkedList<DowningTaskItem> mListDowningTask;
		private LinkedList<DowningTaskItem> mListFinishTask;
		
		private final int UPDATE_GAP = 1000;// 更新进度间隔时间为1秒
		private long lastUpdatedTime = 0;
		
		public DownExpandableAdapter(ExpandableListView expandableListView) {
		
			mExpandableListView = expandableListView;
			mAsyncImageLoader = new AsyncImageLoader();
		}
		
		public void deleteThemeFile(String diskFile){
			
			//删除硬盘临时文件
        	if ( diskFile!=null ) {
        		if ( diskFile.endsWith(".temp") ) {
        			diskFile = diskFile.substring(0, diskFile.indexOf(".temp"));
        		}
             	File tempFile = new File(diskFile);
                if( tempFile.exists() ){
                	tempFile.delete();
                }
        	}
		}
		
		public void deleteTaskItem(String newThemeID){
			
			if (newThemeID==null) return;
			
			for (int i = 0; i < mListDowningTask.size(); i++) {
				
				DowningTaskItem downingTaskItem = mListDowningTask.get(i);
				if ( newThemeID.equalsIgnoreCase(downingTaskItem.newThemeID) ){	

					deleteThemeFile(downingTaskItem.tmpFilePath);
					mListDowningTask.remove(i);
                	notifyDataSetChanged();
					break;
				}
			}
			
			for (int i = 0; i < mListFinishTask.size(); i++) {
				
				DowningTaskItem downingTaskItem = mListFinishTask.get(i);
				if ( newThemeID.equalsIgnoreCase(downingTaskItem.newThemeID) ){	

					deleteThemeFile(downingTaskItem.tmpFilePath);
					mListFinishTask.remove(i);
                	notifyDataSetChanged();
					break;
				}
			}
		}

		public void clearView(){
			this.groupArray.clear();
			this.childArray.clear();
		}
		
		public void addGroupList(LinkedList<ExpandGroupBean> groupList,
				LinkedList<LinkedList<DowningTaskItem>> childArray){
			
			this.groupArray.addAll(groupList);
			this.childArray.addAll(childArray);
			
			//注意一定要2个队列
			mListDowningTask = childArray.get(0);
			mListFinishTask = childArray.get(1);
		}
		
		public int getGroupCount() {
			return groupArray.size();
		}
		
		public int getChildrenCount(int groupPosition) {
			return childArray.get(groupPosition).size();
		}
		
		public Object getGroup(int groupPosition) {
			return groupArray.get(groupPosition);
		}
		
		public Object getChild(int groupPosition, int childPosition) {
			return childArray.get(groupPosition).get(childPosition);
		}
		
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		
		public boolean hasStableIds() {
			return false;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
		
			View view = convertView;
			GroupItemCache cache = null;
			
			if (null == view) {
				view = mInflater.inflate(R.layout.nd_hilauncher_theme_manage_downtask_list_group_item, null);
				cache = new GroupItemCache(view);
				view.setTag(cache);
			} else {
				cache = (GroupItemCache) view.getTag();
			}
			
			cache.groupNameTV.setText(groupArray.get(groupPosition).groupName);
			cache.titleNameTV.setText(groupArray.get(groupPosition).titleName);
			cache.chileCountTV.setText(""+getChildrenCount(groupPosition));
					
			cache.downTaskImageView.setSelected(isExpanded);
			
			return view;
		}
		
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
		
			View view = convertView;
			
			DowningTaskItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.nd_hilauncher_theme_manage_downtask_list_grid_item, null);
				cache = new DowningTaskItemCache(view);
				view.setTag(cache);
			} else {
				cache = (DowningTaskItemCache) view.getTag();
			}
			
			final int iPosition = childPosition;
			final int iGroupPostion = groupPosition;
			
			final LinkedList<DowningTaskItem> tmpListDowningTask = childArray.get(groupPosition);
			DowningTaskItem downingTaskItem = tmpListDowningTask.get(iPosition);
			
			final ThemeItem mThemeDetail = new ThemeItem();
			mThemeDetail.setName(downingTaskItem.themeName);
			mThemeDetail.setDownloadUrl(downingTaskItem.downUrl);
			mThemeDetail.setId(downingTaskItem.themeID);
			mThemeDetail.setLargePostersUrl(downingTaskItem.picUrl);
			
			cache.downingTaskItem = downingTaskItem;
			cache.themeTitle.setText(downingTaskItem.themeName);
			String strThemeVersion = String.format( HiLauncherThemeGlobal.R(R.string.ndtheme_manage_downtasks_theme_version_txt), "1");
			cache.themeVersion.setText(strThemeVersion);
			cache.themeDownloadBtn.setTag(cache);
			
			
			//判断如果是进度100并且是主题则算完成,皮肤包则显示正在安装...
			if ( downingTaskItem.progress==100 && !ThemeLauncherExAPI.checkItemType(downingTaskItem.themeID, ThemeItem.ITEM_TYPE_SKIN) ) {
				downingTaskItem.state = DowningTaskItem.DownState_Finish;
			}
			if ( (downingTaskItem.state!=DowningTaskItem.DownState_Finish) && downingTaskItem.progress>0 ) {
				
				cache.downprocess_horizontal.setVisibility(View.VISIBLE);	
				cache.downprocess_percent.setVisibility(View.VISIBLE);	
				
				//皮肤插件时才显示正在安装,其他则不显示
				if ( downingTaskItem.progress==100 ) {
					cache.downprocess_horizontal.setIndeterminate(true);
					cache.downprocess_horizontal.setProgress(downingTaskItem.progress);
					cache.downprocess_percent.setText(R.string.ndtheme_theme_detail_installing_txt);
				}else{
					cache.downprocess_horizontal.setIndeterminate(false);
					cache.downprocess_horizontal.setProgress(downingTaskItem.progress);
					cache.downprocess_percent.setText(downingTaskItem.progress+"%");
				}
			}else{
				cache.downprocess_percent.setVisibility(View.INVISIBLE);
				cache.downprocess_horizontal.setVisibility(View.INVISIBLE);
			}
			
			setButtonTitle(downingTaskItem, cache.themeDownloadBtn, cache.themeDownTaskDeleteBtn);
			
			cache.themeDownloadBtn.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	
		            	DowningTaskItemCache itemCache = (DowningTaskItemCache)v.getTag();
		            	DowningTaskItem downingTaskItem = itemCache.downingTaskItem;
		            			
		            	int downState = downingTaskItem.state;
		            	
		            	
		            	switch (downState) {
		            	//当任务正在下载时
						case DowningTaskItem.DownState_Downing:
							
							//一种情况当下载已经完成但是正在解压的时候
		            		if ( downingTaskItem.progress==100 ) {
		            			 Toast.makeText(ctx, R.string.ndtheme_manage_downtasks_down100, Toast.LENGTH_SHORT).show();
		            			return;
		            		}
		            				
		            		DownloadService.pauseDownTask(downingTaskItem.downUrl);
							//取消通知栏消息
							String sNotifyPostion = mThemeDetail.getId();
							int iNotifyPostion = Integer.valueOf(sNotifyPostion);
							DownloadNotification.downloadCancelledNotification(ctx, iNotifyPostion);
							
		            		setDownTaskItemState(downingTaskItem, DowningTaskItem.DownState_Pause);	  
							break;
							
						//当任务下载完成时
						case DowningTaskItem.DownState_Finish:
							//TODO 分3种情况 91桌面时提示安装
							try{
								DowningTaskItem newDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(downingTaskItem.themeID);
								//点击91桌面时
								if ( ThemeLauncherExAPI.checkItemType(downingTaskItem.themeID, ThemeItem.ITEM_TYPE_LAUNCHER) ){
									if ( !ApkTools.isInstallAPK(ctx, HiLauncherThemeGlobal.THEME_MANAGE_PACKAGE_NAME) ){
										ApkTools.installApplication(ctx, newDowningTaskItem.tmpFilePath);
									}else{
										ThemeLauncherExAPI.startHiLauncher(ctx);
									}
									return ;
								}
								
								//点击主题时
								if ( ThemeLauncherExAPI.checkItemType(downingTaskItem.themeID, ThemeItem.ITEM_TYPE_THEME) ) {
									if ( !ApkTools.isInstallAPK(ctx, HiLauncherThemeGlobal.THEME_MANAGE_PACKAGE_NAME) ){
										ThemeLauncherExAPI.showHiLauncherDownDialog(ctx);
										return;
									}else{
										//直接发送皮肤应用广播
					            		ThemeLauncherExAPI.showThemeApplyActivity(ctx, newDowningTaskItem, false);
									}
									return ;
								}
								
								//点击皮肤时	
								if (ThemeLauncherExAPI.checkItemType(downingTaskItem.themeID, ThemeItem.ITEM_TYPE_SKIN)){
									//直接发送皮肤应用广播
				            		ThemeLauncherExAPI.showThemeApplyActivity(ctx, newDowningTaskItem, false);
				            		return ;
								}
							}catch (Exception e) {
								e.printStackTrace();
							}
							break;
							
						//当任务下载失败时
						case DowningTaskItem.DownState_Fail:
						//当任务暂停时
						case DowningTaskItem.DownState_Pause:
							
							DownloadTask manager = new DownloadTask();
							manager.downloadTheme( ctx, mThemeDetail );
							
							setDownTaskItemState(downingTaskItem, DowningTaskItem.DownState_Downing);	
							break;
							
						default:
							break;
						}
						
						setButtonTitle(downingTaskItem, (Button)v, itemCache.themeDownTaskDeleteBtn);
		            }
			});	                
			
			cache.themeDownTaskDeleteBtn.setTag(cache);
			cache.themeDownTaskDeleteBtn.setOnClickListener(new OnClickListener(){
		
				public void onClick(View v) {
					
					DowningTaskItemCache itemCache = (DowningTaskItemCache)v.getTag();
		        	DowningTaskItem downingTaskItem = itemCache.downingTaskItem;
		        	
		        	long downState = downingTaskItem.state;
		        	//判断当前任务的状态 正在下载或者下载完成则返回
		        	if ( downState==DowningTaskItem.DownState_Downing ){
		        		return;
		        	}
		        	       
		        	//确认是否删除
		        	if (iGroupPostion==0){
		        		createConfimDialog(downingTaskItem, mListDowningTask, iPosition, mDownExpandableAdapter).show();
		        	}else{
		        		createConfimDialog(downingTaskItem, mListFinishTask, iPosition, mDownExpandableAdapter).show();
		        	}
				}
			});							
			
			//设置主题图片
			cache.themeLargeImg.setTag(downingTaskItem.picUrl);
			Drawable cachedImage = mAsyncImageLoader.loadDrawable(downingTaskItem.picUrl, new ImageCallback() {
		        public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		        	if ( mExpandableListView ==null )return; 
		            ImageView imageViewByTag = (ImageView) mExpandableListView.findViewWithTag(imageUrl);
		            if (imageViewByTag != null && imageDrawable!=null) {
		                imageViewByTag.setImageDrawable(imageDrawable);
		                //渐变	                    
		                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		                aa.setDuration(500);// 设置动画执行的时间
		                imageViewByTag.startAnimation(aa);	                   
		            }
		        }
		    });
			if (cachedImage == null) {
				cache.themeLargeImg.setImageResource(R.drawable.nd_hilauncher_theme_no_find_small);
			}else{
				cache.themeLargeImg.setImageDrawable(cachedImage);
			}
			
			return view;
		}
		
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
		/**
		 * 根据主题ID获取下载对象
		 */
		public DowningTaskItem getTaskItemByThemeId(String themeID){
			DowningTaskItem downingTaskItem = null;
			DowningTaskItem tmpDowningTaskItem = null;
			
			if (themeID==null) return null; 
			
			for (int i = 0; i < mListDowningTask.size(); i++) {
				tmpDowningTaskItem = mListDowningTask.get(i);
				if ( themeID.equalsIgnoreCase(tmpDowningTaskItem.themeID) ){	
					downingTaskItem = tmpDowningTaskItem;
					break;
				}
			}
			
			return downingTaskItem;
		}
		
		/**
		 * 下载队列更新
		 */
		public void updateDownTaskProcess(String themeID, int progress, int opType, String tempFilePath, String newThemeID){
			
			if (themeID==null) return;
			
			DowningTaskItem downingTaskItem = getTaskItemByThemeId(themeID);
			
			switch( opType ) {
				case DowningTaskItem.DownState_Pause:
					//暂停操作
					if ( downingTaskItem!=null ){	
						
						DownloadService.pauseDownTask(downingTaskItem.downUrl);
	            		setDownTaskItemState(downingTaskItem, DowningTaskItem.DownState_Pause);	   
						notifyDataSetChanged();
					}
					break;
				
				case DowningTaskItem.DownState_Fail:
					//下载失败操作
					if ( downingTaskItem!=null ){							
						downingTaskItem.state = DowningTaskItem.DownState_Fail;	
						notifyDataSetChanged();
					}			
					break;
				
				case DowningTaskItem.DownState_Finish:
					//下载、安装完成操作,
					if ( downingTaskItem!=null ){								
						downingTaskItem.state = DowningTaskItem.DownState_Finish;	
						downingTaskItem.newThemeID = newThemeID;

						taskMoveToFinish(downingTaskItem);
					}
					break;	
				
				case DowningTaskItem.DownState_Downing:
					//开始下载(暂停后又下载也属这类)
					boolean isFind = false;
					if ( downingTaskItem!=null ){
						//有种情况是加入两次正在下载的队列(主题列表打开正在等待下载的主题,并点击下载)
						if ( DowningTaskItem.DownState_Downing!=downingTaskItem.state ){ 
						}
						
						downingTaskItem.state = DowningTaskItem.DownState_Downing;	
						notifyDataSetChanged();
													
						isFind = true;
					}
					
					if ( !isFind ){
						try {
							DowningTaskItem newDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(themeID);	
							newDowningTaskItem.state = DowningTaskItem.DownState_Downing;
							addDowningTaskItem(newDowningTaskItem);														
							notifyDataSetChanged();
							
							checkNoTaskList();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				
				case ThemeDownloadStateManager.CODE_DOWNLOA_PROGRESS:
					//更新下载进度
					boolean isFindProgress = false;
					if ( downingTaskItem!=null ){	
						isFindProgress = true;
						
						downingTaskItem.progress = progress;	
						downingTaskItem.tmpFilePath = tempFilePath;
						if ( System.currentTimeMillis() - lastUpdatedTime > UPDATE_GAP || progress==100 ) {
							notifyDataSetChanged();
							lastUpdatedTime = System.currentTimeMillis();
						}
					}
					
					if ( !isFindProgress ){
						try {
							DowningTaskItem newDowningTaskItem = ThemeLibLocalAccessor.getInstance(ctx).getDowningTaskItem(themeID);
							newDowningTaskItem.state = DowningTaskItem.DownState_Downing;
							addDowningTaskItem(newDowningTaskItem);														
							notifyDataSetChanged();
							
							checkNoTaskList();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					break;								
			}
			
		}
		
		
		/**安装完成后转移到已下载队列*/
		public void taskMoveToFinish(DowningTaskItem downingTaskItem){
			//已下载队列
			mListFinishTask.addFirst(downingTaskItem);
			//正在下载队列
			mListDowningTask.remove(downingTaskItem);

			notifyDataSetChanged();
		}
		
		public void addDowningTaskItem(DowningTaskItem _downingTaskItem) {
			
			mListDowningTask.addFirst(_downingTaskItem);			
		}
		
		class GroupItemCache{
			public TextView groupNameTV;
			public TextView titleNameTV;
			public TextView chileCountTV;
			public TextView unitNameTV;
			public ImageView downTaskImageView;
			
			public GroupItemCache(View view){
				groupNameTV = (TextView) view.findViewById(R.id.theme_shop_v2_manage_downtasks_downing_tv);
				titleNameTV = (TextView) view.findViewById(R.id.theme_shop_v2_manage_downtasks_downtask_nums_tv);
				chileCountTV = (TextView) view.findViewById(R.id.downingtaskNums);
				downTaskImageView = (ImageView) view.findViewById(R.id.theme_shop_v2_manage_downtasks_tubiao);
			}
		}
		
		class DowningTaskItemCache {
			public ImageView themeLargeImg;
			public TextView themeTitle;
			public TextView themeVersion;
			public Button themeDownloadBtn;
			public Button themeDownTaskDeleteBtn;	
			public DowningTaskItem downingTaskItem;
			public ProgressBar downprocess_horizontal;
			public TextView downprocess_percent;
		
			public DowningTaskItemCache(View view) {
				themeLargeImg = (ImageView) view.findViewById(R.id.themeLargeImg);
				themeTitle = (TextView) view.findViewById(R.id.themeTitle);
				themeVersion = (TextView) view.findViewById(R.id.themeVersion);
				themeDownloadBtn = (Button) view.findViewById(R.id.themeDownloadBtn);		
				themeDownTaskDeleteBtn = (Button) view.findViewById(R.id.themeDownTaskDeleteBtn);
				downprocess_horizontal = (ProgressBar) view.findViewById(R.id.downprocess_horizontal);
				downprocess_percent = (TextView) view.findViewById(R.id.downprocess_percent);
			}
		}
		
	}
	
	class ExpandGroupBean {
		String groupName = "";//下载中,已下载
		String titleName = "";//共有下载任务,共有主题
		int chileCount = 0;		
	}

	public Dialog createConfimDialog(final DowningTaskItem downingTaskItem, final LinkedList<DowningTaskItem> mListDowningTask, final int iPosition,
			final DownExpandableAdapter mDownAdapter) {
		
		final DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				//删除硬盘临时文件
            	if ( downingTaskItem.tmpFilePath!=null ) {
                 	File tempFile = new File(downingTaskItem.tmpFilePath);
                    if( tempFile.exists() ){
                    	tempFile.delete();
                    }else{
                    	//已下载完成的情况
                    	String diskFile = downingTaskItem.tmpFilePath;
                		if ( diskFile.endsWith(".temp") ) {
                			diskFile = downingTaskItem.tmpFilePath.substring(0, diskFile.indexOf(".temp"));
                		}
	                 	File downFile = new File(diskFile);
	                    if( downFile.exists() ){
	                    	downFile.delete();
	                    }
                    }
            	}
            	
            	//删除数据库信息
            	try {
        			ThemeLibLocalAccessor.getInstance(ctx).deleteDowningTask(downingTaskItem);
        		} catch (Exception e) {
        	   	 	e.printStackTrace();
        	    }
                                	
            	//动态移除列表信息
            	mListDowningTask.remove(iPosition);
            	mDownAdapter.notifyDataSetChanged();
				
				//发送广播到桌面删除主题.
				if ( downingTaskItem.state==DowningTaskItem.DownState_Finish ){
					//通知桌面删除主题
				}
			}
		};
		
		final DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		};
		
		final String titleStr = getResources().getString(R.string.ndtheme_apply_theme_title);
		final String messageStr = getResources().getString(R.string.ndtheme_alert_dialog_confim_del);
		final String okStr = getResources().getString(R.string.ndtheme_alert_dialog_ok);
		final String cancleStr = getResources().getString(R.string.ndtheme_common_button_cancel);
		
		if (NdLauncherExThemeApi.themeExDialog==null){
			return (new NdLauncherExDialogDefaultImp()).createThemeDialog(getContext(), android.R.drawable.ic_dialog_alert, titleStr,
					messageStr, okStr, cancleStr, positive, negative);
		}else{
			return NdLauncherExThemeApi.themeExDialog.createThemeDialog(getContext(), android.R.drawable.ic_dialog_alert, titleStr,
					messageStr, okStr, cancleStr, positive, negative);
		}
	}
	
	
	private void initReceiver(){
		IntentFilter filter = new IntentFilter( ThemeDownloadStateManager.INTENT_THEME_DOWNLOAD_STATE );	
		ctx.registerReceiver( mThemeDownloadProgressReceiver, filter );	
	}
	
	public void destroyView(){
		ctx.unregisterReceiver( mThemeDownloadProgressReceiver );
	}

	private class ThemeDownloadProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String themeId = intent.getStringExtra( "themeId" );
			String newId = intent.getStringExtra( "id" );

			int state = intent.getIntExtra( "state", -1 );
			int progress = intent.getIntExtra("progress",0);
			String tempFilePath = intent.getStringExtra("tempFilePath");
			
			if( null == themeId ) return; //安装失败的情况

			//更新下载管理模块
			updateDownTask(themeId, progress, state, tempFilePath, newId);
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		
		super.onAttachedToWindow();
		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
		this.requestFocusFromTouch();
	};
}
