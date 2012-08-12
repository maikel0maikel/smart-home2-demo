package com.hs.smarthome.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;
import com.hs.smarthome.db.HomeSettingAccessor;
import com.hs.smarthome.db.WirelessItem;
import com.hs.smarthome.db.WirelessSettingAccessor;
import com.hs.smarthome.ui.WirelessSettingActivity.ListItemClickListener;
import com.hs.smarthome.ui.WirelessSettingActivity.WirelessAdapter;


public class FuncHomeActivity extends Activity implements View.OnClickListener{

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
	
	private ArrayList<HomeItem> homeItemList = new ArrayList<HomeItem>(); 
	private LayoutInflater mInflater = null;
	private HomeAdapter homeAdapter;
	private ListView func_home_lv;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_home);
		
		this.tabButton1 = (FrameLayout)findViewById(R.id.drawingRoom);
		this.tabButton2 = (FrameLayout)findViewById(R.id.bedRoom);
		this.tabButton3 = (FrameLayout)findViewById(R.id.studyRoom);
		this.tabButton4 = (FrameLayout)findViewById(R.id.kitchenRoom);
		this.tabButton5 = (FrameLayout)findViewById(R.id.Other);
		this.tabButton6 = (FrameLayout)findViewById(R.id.Other2);
		
		this.tabButton1.setOnClickListener(this);
		this.tabButton2.setOnClickListener(this);
		this.tabButton3.setOnClickListener(this);
		this.tabButton4.setOnClickListener(this);
		this.tabButton5.setOnClickListener(this);
		this.tabButton6.setOnClickListener(this);
		
		tabContainer = (FrameLayout) findViewById(R.id.tabs);
		
		try {
			HomeSettingAccessor.getInstance(this).initHomeTable();
			homeItemList = HomeSettingAccessor.getInstance(this).getHomeItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建设备对象
		//initWirelessList();
		func_home_lv = (ListView)findViewById(R.id.result_list);
		
		homeAdapter = new HomeAdapter();
		func_home_lv.setAdapter(homeAdapter);
	//	func_home_lv.setOnItemClickListener(new ListItemClickListener());
		
		homeAdapter.notifyDataSetChanged();	//刷新数据集
		
		showView(tabButton1);
	}
	
	public void showView(View paramView){
		
		ListView tmpTabListView = null;
		
		if (lastActionButton==paramView) 
			return ; 
		
		Intent reqIntent = null;
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(false);
		
		switch (paramView.getId()) {
			case R.id.drawingRoom:
				lastActionButton = paramView;
				if (tab1ListView==null) {
					
					
					tab1ListView = new ListView(this);
					initHomeList1();			       
			        HomeAdapter ext = new HomeAdapter();
			        tab1ListView.setAdapter(ext);
				}
				tmpTabListView = tab1ListView;
				break;
			case R.id.bedRoom:
				lastActionButton = paramView;
				if (tab2ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab2ListView;
				break;
			case R.id.studyRoom:
				lastActionButton = paramView;
				if (tab3ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab3ListView;
				break;
			case R.id.kitchenRoom:
				lastActionButton = paramView;
				if (tab4ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab4ListView;
				break;
			case R.id.Other:
				lastActionButton = paramView;
				if (tab5ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab5ListView;
				break;		
			case R.id.Other2:
				lastActionButton = paramView;
				if (tab6ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab6ListView;
				break;					
		}
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(true);
		
		if (tmpTabListView==null ) {
			return;
		}
		
		View mainView = tmpTabListView;
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(param);
		tabContainer.removeAllViews();
		tabContainer.addView(mainView);
	}
	
	@Override
	public void onClick(View v) {
		
		showView(v);
	}
	
/*	public List<HashMap<String, String>> initlist() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		for (int i = 0; i < 10; i++) {
			map = new HashMap<String, String>();
			map.put("id", i + "");
			map.put("name", i + "ww");
			map.put("age", i + "aa");
			list.add(map);
		}
		map = new HashMap<String, String>();
		map.put("id", "用户");
		map.put("name", "姓名");
		map.put("age", "年龄");
		list.add(0, map);
		return list;
	}
}*/

	private void initHomeList1() {
		
	    HomeItem homeItem1 = new HomeItem();
		homeItem1.itemTitleName = "电视机";
		chooseImgRes(homeItem1);
		homeItemList.add(homeItem1);	
		
		HomeItem homeItem2 = new HomeItem();
			homeItem2.itemTitleName = "饮水机";
			chooseImgRes(homeItem2);
			homeItemList.add(homeItem2);	
			 
		HomeItem homeItem3 = new HomeItem();
    		homeItem3.itemTitleName = "顶灯";
			chooseImgRes(homeItem3);
			homeItemList.add(homeItem3);
			
		HomeItem homeItem4 = new HomeItem();
    		homeItem4.itemTitleName = "空调";
			chooseImgRes(homeItem4);
			homeItemList.add(homeItem4);
			
			
		HomeItem homeItem5 = new HomeItem();
	   		homeItem5.itemTitleName = "窗帘";
			chooseImgRes(homeItem5);
			homeItemList.add(homeItem5);
		
			
				
					
			
		


		
	}
	
	
	public void chooseImgRes(HomeItem item){
		
		String str=item.itemTitleName;
		switch(str){
		case "电视机":item.itemImgResID=R.drawable.menu_list_equipement_tv;break;
		case "饮水机":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "顶灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "窗帘":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "台灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间顶灯":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间热水器":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "卫生间洗衣机":item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		case "空调":item.itemImgResID=R.drawable.menu_list_equipement_kt;break;
		case "DVD":item.itemImgResID=R.drawable.menu_list_equipement_dvd;break;
		case "多媒体":item.itemImgResID=R.drawable.menu_list_equipement_media;break;
		default:item.itemImgResID=R.drawable.menu_list_equipement_kg;break;
		
		}
	}
private class HomeAdapter extends BaseAdapter {

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
			view = mInflater.inflate(R.layout.wireless_setting_list_item, null);
			cache = new ItemCache(view);
			view.setTag(cache);
		} else {
			cache = (ItemCache) view.getTag();
		}
		
		
		cache.icon.setImageResource(homeItem.itemImgResID);
		
		cache.label.setText(homeItem.itemTitleName);
		
		return view;
	}
}

private class ItemCache {
	public ImageView icon;
	public TextView label;
	
	public ItemCache(View view) {
		icon = (ImageView) view.findViewById(R.id.icon);
		label = (TextView) view.findViewById(R.id.label);
	}
}

protected void onActivityResult(int reqCode, int resultCode, Intent data) {
	super.onActivityResult(reqCode, resultCode, data);
	if (resultCode != Activity.RESULT_OK) {	
		return;
	}
	
	switch (reqCode) {
		case (DIALOG_RENAME):
			int position = data.getIntExtra("position",0);
			String itemTitleName = data.getStringExtra("itemTitleName");
			//修改列表
			HomeItem homeItem = null;
			if (position<homeItemList.size() && position>=0) {
				homeItem = homeItemList.get(position);
				homeItem.itemTitleName = itemTitleName;
				homeAdapter.notifyDataSetChanged();	//刷新数据集
				
				//保存数据库
				try {
					HomeSettingAccessor.getInstance(this).updateHomeItem(homeItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		break;
	}
};
}
