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
import com.hs.smarthome.db.AlarmItem;
import com.hs.smarthome.db.AlarmSettingAccessor;



public class AlarmSettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_alarm_lv;
	private ArrayList<AlarmItem> alarmItemList = new ArrayList<AlarmItem>(); 
	private AlarmAdapter alarmAdapter;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wireless_setting);
		
		mInflater = LayoutInflater.from( this );
		
		func_alarm_lv = (ListView)findViewById(R.id.result_list);
		
		try {
			AlarmSettingAccessor.getInstance(this).initAlarmTable();
			alarmItemList = AlarmSettingAccessor.getInstance(this).getAlarmItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建无线对象
		//initWirelessList();
		
		alarmAdapter = new AlarmAdapter();
		func_alarm_lv.setAdapter(alarmAdapter);
		func_alarm_lv.setOnItemClickListener(new ListItemClickListener());
		
		alarmAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initAlarmList() {
		
		AlarmItem alarmItem = new AlarmItem();
		alarmItem.itemImgResID = R.drawable.setting_yes;
		alarmItem.itemTitleName = "温度报警";
		alarmItemList.add(alarmItem);	
		
		for(int i = 1; i <= 8; i++)
		{
		AlarmItem alarmItem1 = new AlarmItem();
		alarmItem1.itemImgResID = R.drawable.setting_yes;
		alarmItem1.itemTitleName = "报警组" + i;
		
		alarmItemList.add(alarmItem1);	
		
		}

		
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			AlarmItem alarmItem = alarmItemList.get(position);
			alarmAdapter.notifyDataSetChanged();	//刷新数据集
			

    }
}
	private class AlarmAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return alarmItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return alarmItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			AlarmItem alarmItem = alarmItemList.get(position);
			
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
			
			cache.label.setText(alarmItem.itemTitleName);
			
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
				AlarmItem alarmItem = null;
				if (position<alarmItemList.size() && position>=0) {
					alarmItem = alarmItemList.get(position);
					alarmItem.itemTitleName = itemTitleName;
					alarmAdapter.notifyDataSetChanged();	//刷新数据集
					
					//保存数据库
					try {
						AlarmSettingAccessor.getInstance(this).updateAlarmItem(alarmItem);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			break;
		}
	};
    
}
