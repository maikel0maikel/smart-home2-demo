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
import com.hs.smarthome.db.InfraredItem;
import com.hs.smarthome.db.InfraredSettingAccessor;



public class InfraredSettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_infrared_lv;
	private ArrayList<InfraredItem> infraredItemList = new ArrayList<InfraredItem>(); 
	private InfraredAdapter infraredAdapter;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.infrared_setting);
		
		mInflater = LayoutInflater.from( this );
		
		func_infrared_lv = (ListView)findViewById(R.id.result_list);
		
		try {
			InfraredSettingAccessor.getInstance(this).initInfraredTable();
			infraredItemList = InfraredSettingAccessor.getInstance(this).getInfraredItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建无线对象
		initInfraredList();
		
		infraredAdapter = new InfraredAdapter();
		func_infrared_lv.setAdapter(infraredAdapter);
		func_infrared_lv.setOnItemClickListener(new ListItemClickListener());
		
		infraredAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initInfraredList() {
		
		for(int i = 1; i <= 50; i++)
		{
		InfraredItem infraredItem = new InfraredItem();
		infraredItem.itemImgResID = R.drawable.setting_yes;
		infraredItem.itemTitleName = "红外" + i;
		
		infraredItemList.add(infraredItem);	
		
		}

		
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			InfraredItem infraredItem = infraredItemList.get(position);
			infraredAdapter.notifyDataSetChanged();	//刷新数据集
			
			Intent intent = new Intent();
			intent.setClass(InfraredSettingActivity.this, InfraredSettingDialog.class);
			intent.putExtra("position", position);
			intent.putExtra("itemTitleName", infraredItem.itemTitleName);
			InfraredSettingActivity.this.startActivityForResult(intent, DIALOG_RENAME);
		}
    }
	
	private class InfraredAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infraredItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return infraredItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			InfraredItem infraredItem = infraredItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.infrared_setting_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
			cache.icon.setImageResource(infraredItem.itemImgResID);
			
			cache.label.setText(infraredItem.itemTitleName);
			
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
				InfraredItem infraredItem = null;
				if (position<infraredItemList.size() && position>=0) {
					infraredItem = infraredItemList.get(position);
					infraredItem.itemTitleName = itemTitleName;
					infraredAdapter.notifyDataSetChanged();	//刷新数据集
					
					//保存数据库
					try {
						InfraredSettingAccessor.getInstance(this).updateInfraredItem(infraredItem);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			break;
		}
	};
}