package com.hs.smarthome.ui;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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

import com.hs.smarthome.R;
import com.hs.smarthome.db.WirelessItem;
import com.hs.smarthome.db.WirelessSettingAccessor;


public class WirelessSettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_wireless_lv;
	private ArrayList<WirelessItem> wirelessItemList = new ArrayList<WirelessItem>(); 
	private WirelessAdapter wirelessAdapter;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wireless_setting);
		
		mInflater = LayoutInflater.from( this );
		
		func_wireless_lv = (ListView)findViewById(R.id.result_list);
		
		try {
			WirelessSettingAccessor.getInstance(this).initWirelessTable();
			wirelessItemList = WirelessSettingAccessor.getInstance(this).getWirelessItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建无线对象
		//initWirelessList();
		
		wirelessAdapter = new WirelessAdapter();
		func_wireless_lv.setAdapter(wirelessAdapter);
		func_wireless_lv.setOnItemClickListener(new ListItemClickListener());
		
		wirelessAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initWirelessList() {
		
		for(int i = 1; i <= 100; i++)
		{
		WirelessItem wirelessItem = new WirelessItem();
		wirelessItem.itemImgResID = R.drawable.setting_yes;
		wirelessItem.itemTitleName = "无线" + i;
		
		wirelessItemList.add(wirelessItem);	
		
		}

		
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
			
			Intent intent = new Intent();
			intent.setClass(WirelessSettingActivity.this, WirelessSettingDialog.class);
			intent.putExtra("position", position);
			intent.putExtra("itemTitleName", wirelessItem.itemTitleName);
			WirelessSettingActivity.this.startActivityForResult(intent, DIALOG_RENAME);
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
			
			
			cache.icon.setImageResource(R.drawable.setting_yes);
			
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
				WirelessItem wirelessItem = null;
				if (position<wirelessItemList.size() && position>=0) {
					wirelessItem = wirelessItemList.get(position);
					wirelessItem.itemTitleName = itemTitleName;
					wirelessAdapter.notifyDataSetChanged();	//刷新数据集
					
					//保存数据库
					try {
						WirelessSettingAccessor.getInstance(this).updateWirelessItem(wirelessItem);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			break;
		}
	};
}
