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
import com.hs.smarthome.db.SmartHomeAccessor;
import com.hs.smarthome.db.SwitchItem;



public class RelaySettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_relay_lv;
	private ArrayList<SwitchItem> relayItemList = new ArrayList<SwitchItem>(); 
	private RelayAdapter relayAdapter;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wireless_setting);
		
		mInflater = LayoutInflater.from( this );
		
		func_relay_lv = (ListView)findViewById(R.id.result_list);
		
		try {
			SmartHomeAccessor.getInstance(this).initSwitchTable();
			relayItemList = SmartHomeAccessor.getInstance(this).getSwitchItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建无线对象
		//initWirelessList();
		
		relayAdapter = new RelayAdapter();
		func_relay_lv.setAdapter(relayAdapter);
		func_relay_lv.setOnItemClickListener(new ListItemClickListener());
		
		relayAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initRelayList() {
		
		for(int i = 1; i <= 8; i++)
		{
		SwitchItem relayItem = new SwitchItem();
		relayItem.itemImgResID = R.drawable.setting_yes;
		relayItem.itemTitleName = "继电器" + i;
		
		relayItemList.add(relayItem);	
		
		}

		
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SwitchItem relayItem = relayItemList.get(position);
			relayAdapter.notifyDataSetChanged();	//刷新数据集
			
			Intent intent = new Intent();
			intent.setClass(RelaySettingActivity.this, RelaySettingDialog.class);
			intent.putExtra("position", position);
			intent.putExtra("itemTitleName", relayItem.itemTitleName);
			RelaySettingActivity.this.startActivityForResult(intent, DIALOG_RENAME);
		}
    }
	
	private class RelayAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return relayItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return relayItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			SwitchItem relayItem = relayItemList.get(position);
			
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
			
			cache.label.setText(relayItem.itemTitleName);
			
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
				SwitchItem relayItem = null;
				if (position<relayItemList.size() && position>=0) {
					relayItem = relayItemList.get(position);
					relayItem.itemTitleName = itemTitleName;
					relayAdapter.notifyDataSetChanged();	//刷新数据集
					
					//保存数据库
					try {
						SmartHomeAccessor.getInstance(this).updateSwitchItem(relayItem);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			break;
		}
	};
}