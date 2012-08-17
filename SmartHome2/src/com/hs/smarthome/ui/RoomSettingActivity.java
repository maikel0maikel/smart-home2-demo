package com.hs.smarthome.ui;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hs.smarthome.R;
import com.hs.smarthome.db.RoomItem;
import com.hs.smarthome.db.RoomSettingAccessor;
import com.hs.smarthome.ui.WirelessSettingActivity.BackButtonListener;



public class RoomSettingActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_room_lv;
	private ArrayList<RoomItem> roomItemList = new ArrayList<RoomItem>(); 
	private RoomAdapter roomAdapter;
	private ImageView back;
	private TextView setting_title;
	
	/**重命名*/
	private final static int DIALOG_RENAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wireless_setting);
		
		setting_title = (TextView) findViewById(R.id.setting_title);
		setting_title.setText("房间名称设置");
		
		back = (ImageView) findViewById(R.id.back);
		//返回 
		back.setOnClickListener(new BackButtonListener());
		
		mInflater = LayoutInflater.from( this );
		
		func_room_lv = (ListView)findViewById(R.id.result_list);
		
		try {
			RoomSettingAccessor.getInstance(this).initRoomTable();
			roomItemList = RoomSettingAccessor.getInstance(this).getRoomItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//构建无线对象
		//initWirelessList();
		
		roomAdapter = new RoomAdapter();
		func_room_lv.setAdapter(roomAdapter);
		func_room_lv.setOnItemClickListener(new ListItemClickListener());
		
		roomAdapter.notifyDataSetChanged();	//刷新数据集
	}
	

    class BackButtonListener implements OnClickListener{
    	
		@Override
		public void onClick(View arg0) {
			finish();	
			
		}
    	
    }
	
	private void initRoomList() {
		
		RoomItem roomItem1 = new RoomItem();
		roomItem1.itemImgResID = R.drawable.setting_yes;
		roomItem1.itemTitleName = "客厅";
		
		roomItemList.add(roomItem1);	
		
		RoomItem roomItem2 = new RoomItem();
		roomItem2.itemImgResID = R.drawable.setting_yes;
		roomItem2.itemTitleName = "卧室";
		
		roomItemList.add(roomItem2);	
		
		RoomItem roomItem3 = new RoomItem();
		roomItem3.itemImgResID = R.drawable.setting_yes;
		roomItem3.itemTitleName = "书房";
		
		roomItemList.add(roomItem3);	
		
		RoomItem roomItem4 = new RoomItem();
		roomItem4.itemImgResID = R.drawable.setting_yes;
		roomItem4.itemTitleName = "厨房";
		
		roomItemList.add(roomItem4);	
		
		RoomItem roomItem5 = new RoomItem();
		roomItem5.itemImgResID = R.drawable.setting_yes;
		roomItem5.itemTitleName = "其他";
		
		roomItemList.add(roomItem5);	
		
		RoomItem roomItem6 = new RoomItem();
		roomItem6.itemImgResID = R.drawable.setting_yes;
		roomItem6.itemTitleName = "其他2";
		
		roomItemList.add(roomItem6);
		
	
		
		
		

		
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			RoomItem roomItem = roomItemList.get(position);
			roomAdapter.notifyDataSetChanged();	//刷新数据集
			
			Intent intent = new Intent();
			intent.setClass(RoomSettingActivity.this, RoomSettingDialog.class);
			intent.putExtra("position", position);
			intent.putExtra("itemTitleName", roomItem.itemTitleName);
			RoomSettingActivity.this.startActivityForResult(intent, DIALOG_RENAME);
		}
    }
	
	private class RoomAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return roomItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return roomItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			RoomItem roomItem = roomItemList.get(position);
			
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
			
			cache.label.setText(roomItem.itemTitleName);
			
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
				RoomItem roomItem = null;
				if (position<roomItemList.size() && position>=0) {
					roomItem = roomItemList.get(position);
					roomItem.itemTitleName = itemTitleName;
					roomAdapter.notifyDataSetChanged();	//刷新数据集
					
					//保存数据库
					try {
						RoomSettingAccessor.getInstance(this).updateRoomItem(roomItem);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//发送广播
				Intent mIntent1 = new Intent(FuncHomeActivity.ACTION_ROOM_NAME); 
				Intent mIntent2 = new Intent(HomeSettingActivity.ACTION_ROOM_NAME);
				Intent mIntent3 = new Intent(HomeSettingDialog.ACTION_ROOM_NAME);
                sendBroadcast(mIntent1); 
                sendBroadcast(mIntent2);
                sendBroadcast(mIntent3);
			break;
		}
	};
}