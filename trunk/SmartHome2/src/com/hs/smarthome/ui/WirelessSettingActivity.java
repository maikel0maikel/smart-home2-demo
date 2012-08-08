package com.hs.smarthome.ui;



import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.smarthome.R;
import com.hs.smarthome.db.WirelessItem;


public class WirelessSettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_wireless_lv;
	private ArrayList<WirelessItem> wirelessItemList = new ArrayList<WirelessItem>(); 
	private WirelessAdapter wirelessAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wireless_setting);
		
		mInflater = LayoutInflater.from( this );
		
		func_wireless_lv = (ListView)findViewById(R.id.result_list);
		
		//构建无线对象
		initWirelessList();
		
		wirelessAdapter = new WirelessAdapter();
		func_wireless_lv.setAdapter(wirelessAdapter);
		func_wireless_lv.setOnItemClickListener(new ListItemClickListener());
		
		wirelessAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initWirelessList() {
		
		WirelessItem wirelessItem1 = new WirelessItem();
		wirelessItem1.itemImgResID = R.drawable.setting_yes;
		wirelessItem1.itemTitleName = "无线1";
		
		WirelessItem wirelessItem2 = new WirelessItem();
		wirelessItem2.itemImgResID = R.drawable.setting_yes;
		wirelessItem2.itemTitleName = "无线2";
		
		WirelessItem wirelessItem3 = new WirelessItem();
		wirelessItem3.itemImgResID = R.drawable.setting_yes;
		wirelessItem3.itemTitleName = "无线3";
		
		WirelessItem wirelessItem4 = new WirelessItem();
		wirelessItem4.itemImgResID = R.drawable.setting_yes;
		wirelessItem4.itemTitleName = "无线4";

		
		wirelessItemList.add(wirelessItem1);		
		wirelessItemList.add(wirelessItem2);	
		wirelessItemList.add(wirelessItem3);	
		wirelessItemList.add(wirelessItem4);	
		
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			WirelessItem wirelessItem = wirelessItemList.get(position);
			wirelessAdapter.notifyDataSetChanged();	//刷新数据集
			

		}
    }
	
	private class WirelessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return wirelessItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return wirelessItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			WirelessItem wirelessItem = wirelessItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.wireless_setting_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
			cache.icon.setImageResource(wirelessItem.itemImgResID);
			
			cache.label.setText(wirelessItem.itemTitleName);
			
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
}
