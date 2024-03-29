package com.hs.smarthome.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.smarthome.R;
import com.hs.smarthome.db.ControlPanel;
import com.hs.smarthome.db.HomeItem;
import com.hs.smarthome.db.HomeSettingAccessor;
import com.hs.smarthome.db.RoomItem;
import com.hs.smarthome.db.RoomSettingAccessor;

public class FuncHomeActivity extends Activity implements View.OnClickListener {

	private FrameLayout tabContainer;

	private FrameLayout tabButton1;
	private FrameLayout tabButton2;
	private FrameLayout tabButton3;
	private FrameLayout tabButton4;
	private FrameLayout tabButton5;
	private FrameLayout tabButton6;

	private ListView tab1ListView;
	private ListView tab2ListView;
	private ListView tab3ListView;
	private ListView tab4ListView;
	private ListView tab5ListView;
	private ListView tab6ListView;

	private View lastActionButton;

	private LayoutInflater mInflater = null;
	private HomeAdapter homeAdapter;
	private ListView func_home_lv;

	/** 重命名 */
	private final static int DIALOG_RENAME = 1;
	
	public static final String ACTION_NAME = "com.hs.smarthome.UPDATE_HOMESETTING"; 
	
	public static final String ACTION_ROOM_NAME = "com.hs.smarthome.UPDATE_ROOMSETTING"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_home);

		mInflater = LayoutInflater.from( this );
		
		HomeAdapter temp = new HomeAdapter();
		temp.notifyDataSetChanged();	//刷新数据集
		
		this.tabButton1 = (FrameLayout) findViewById(R.id.drawingRoom);
		this.tabButton2 = (FrameLayout) findViewById(R.id.bedRoom);
		this.tabButton3 = (FrameLayout) findViewById(R.id.studyRoom);
		this.tabButton4 = (FrameLayout) findViewById(R.id.kitchenRoom);
		this.tabButton5 = (FrameLayout) findViewById(R.id.Other);
		this.tabButton6 = (FrameLayout) findViewById(R.id.Other2);

		this.tabButton1.setOnClickListener(this);
		this.tabButton2.setOnClickListener(this);
		this.tabButton3.setOnClickListener(this);
		this.tabButton4.setOnClickListener(this);
		this.tabButton5.setOnClickListener(this);
		this.tabButton6.setOnClickListener(this);

		setTabBarTitle();
		
		tabContainer = (FrameLayout) findViewById(R.id.tabs);

		showView(tabButton1);
		
		IntentFilter myIntentFilter = new IntentFilter(); 
        myIntentFilter.addAction(ACTION_NAME); 
        myIntentFilter.addAction(ACTION_ROOM_NAME); 
        //注册广播       
        registerReceiver(mBroadcastReceiver, myIntentFilter); 
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		unregisterReceiver(mBroadcastReceiver);
	}
	
	public void showView(View paramView) {

		ListView tmpTabListView = null;

		if (lastActionButton == paramView)
			return;

		Intent reqIntent = null;

		if (lastActionButton != null)
			lastActionButton.setSelected(false);

		switch (paramView.getId()) {
		case R.id.drawingRoom:
			lastActionButton = paramView;
			if (tab1ListView == null) {
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab1ListView = new ListView(this);
					tab1ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab1ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(1) );
					tab1ListView.setAdapter(ext);
					tab1ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab1ListView;
			break;
		case R.id.bedRoom:
			lastActionButton = paramView;
			if (tab2ListView == null) {
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab2ListView = new ListView(this);
					tab2ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab2ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(2) );
					tab2ListView.setAdapter(ext);
					tab2ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab2ListView;
			break;
		case R.id.studyRoom:
			lastActionButton = paramView;
			if (tab3ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab3ListView = new ListView(this);	
					tab3ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab3ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(3) );
					tab3ListView.setAdapter(ext);
					tab3ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab3ListView;
			break;
		case R.id.kitchenRoom:
			lastActionButton = paramView;
			if (tab4ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab4ListView = new ListView(this);
					tab4ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab4ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(4) );
					tab4ListView.setAdapter(ext);
					tab4ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab4ListView;
			break;
		case R.id.Other:
			lastActionButton = paramView;
			if (tab5ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab5ListView = new ListView(this);
					tab5ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab5ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(5) );
					tab5ListView.setAdapter(ext);
					tab5ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab5ListView;
			break;
		case R.id.Other2:
			lastActionButton = paramView;
			if (tab6ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab6ListView = new ListView(this);
					tab6ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab6ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(6) );
					tab6ListView.setAdapter(ext);
					tab6ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab6ListView;
			break;
		}

		if (lastActionButton != null)
			lastActionButton.setSelected(true);

		if (tmpTabListView == null) {
			return;
		}

		View mainView = tmpTabListView;
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(param);
		tabContainer.removeAllViews();
		tabContainer.addView(mainView);
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			ItemCache cache = (ItemCache) view.getTag();

			switch ( cache.homeItem.itemControlPanelID ){
			
			case ControlPanel.PANEL1:
				Intent intent1 = new Intent();
				intent1.setClass(FuncHomeActivity.this, EquipementControlOpenOff.class);
				FuncHomeActivity.this.startActivity(intent1);
			break;
			
			case ControlPanel.PANEL2:
				Intent intent2 = new Intent();
				intent2.setClass(FuncHomeActivity.this, EquipementControlKongTiao.class);
				FuncHomeActivity.this.startActivity(intent2);
			break;
			
			case ControlPanel.PANEL3:
					Intent intent3 = new Intent();
					intent3.setClass(FuncHomeActivity.this, EquipementControlTV.class);
					FuncHomeActivity.this.startActivity(intent3);
				break;
		
			case ControlPanel.PANEL4:
				Intent intent4 = new Intent();
				intent4.setClass(FuncHomeActivity.this, EquipementControlPlay.class);
				FuncHomeActivity.this.startActivity(intent4);
			break;
			
			case ControlPanel.PANEL5:
				Intent intent5 = new Intent();
				intent5.setClass(FuncHomeActivity.this, EquipementControlPlay.class);
				FuncHomeActivity.this.startActivity(intent5);
			break;
			
			case ControlPanel.PANEL6:
				Intent intent6 = new Intent();
				intent6.setClass(FuncHomeActivity.this, EquipementControlKongTiao.class);
				FuncHomeActivity.this.startActivity(intent6);
			break;
			
			
			case ControlPanel.PANEL7:
				Intent intent7 = new Intent();
				intent7.setClass(FuncHomeActivity.this, EquipementControlOpenOff_1.class);
				FuncHomeActivity.this.startActivity(intent7);
			break;
			
			case ControlPanel.PANEL8:
				Intent intent8 = new Intent();
				intent8.setClass(FuncHomeActivity.this, EquipementControlOpenOff_2.class);
				FuncHomeActivity.this.startActivity(intent8);
			break;
				
			}
		}
    }
    
	@Override
	public void onClick(View v) {

		showView(v);
	}

	private class HomeAdapter extends BaseAdapter {

		public HomeAdapter(){super();}
		ArrayList<HomeItem> homeItemList = new ArrayList<HomeItem>();
		
		public HomeAdapter(ArrayList<HomeItem> homeItemList){
			this.homeItemList = homeItemList;
		}
		
		@Override
		public int getCount() {
			return homeItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return homeItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			HomeItem homeItem = homeItemList.get(position);

			View view = convertView;

			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.home_list_item,
						null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}

			cache.homeItem = homeItem;
			
			cache.icon.setImageResource( ControlPanel.getImgResByPanelID(homeItem.itemControlPanelID) );
			cache.label.setText(homeItem.itemTitleName);

			return view;
		}
	}

	private class ItemCache {
		public ImageView icon;
		public TextView label;
		public HomeItem homeItem;

		public ItemCache(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
			label = (TextView) view.findViewById(R.id.label);
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){ 
        @Override 
        public void onReceive(Context context, Intent intent) { 
            String action = intent.getAction(); 
            		
            if(action.equals(ACTION_NAME)){ 
            	try{
            		ListView tmpListView;
            		int roomID = intent.getIntExtra("roomID", 1);
            		tmpListView = getSelectListView(roomID);
            		if (tmpListView==null) return;
	                HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(FuncHomeActivity.this).getHomeItemList(1) );
	                tmpListView.setAdapter(ext);
					ext.notifyDataSetChanged(); // 刷新数据集
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            } 
            if(action.equals(ACTION_ROOM_NAME)){ 
            	setTabBarTitle();
            }
        } 
         
    }; 
    
    public ListView getSelectListView(int roomNumID){
		ListView resultList = null;
		
		switch (roomNumID) {
		case 1:
			resultList = tab1ListView;
			break;
		case 2:
			resultList = tab2ListView;
			break;
		case 3:
			resultList = tab3ListView;
			break;
		case 4:
			resultList = tab4ListView;
			break;
		case 5:
			resultList = tab5ListView;
			break;
		case 6:
			resultList = tab6ListView;
			break;
		}
		
		return resultList;
	}
    
    public void setTabBarTitle(){
    	
    	RoomItem roomItem;
    	TextView textView = null;
    	try {
			ArrayList<RoomItem> roomItemList = RoomSettingAccessor.getInstance(this).getRoomItemList();
			for (int i=0; i<roomItemList.size(); i++) {
				roomItem = roomItemList.get(i);
				switch (roomItem.itemId) {
				case 1:
					textView = (TextView)findViewById(R.id.roomtitle1);
					break;
				case 2:
					textView = (TextView)findViewById(R.id.roomtitle2);
					break;
				case 3:
					textView = (TextView)findViewById(R.id.roomtitle3);
					break;
				case 4:
					textView = (TextView)findViewById(R.id.roomtitle4);
					break;
				case 5:
					textView = (TextView)findViewById(R.id.roomtitle5);
					break;
				case 6:
					textView = (TextView)findViewById(R.id.roomtitle6);
					break;
				}
				textView.setText(roomItem.itemTitleName);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
