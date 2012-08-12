package com.hs.smarthome.ui;

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
import android.widget.Toast;

import com.hs.smarthome.R;
import com.hs.smarthome.db.AlarmItem;
import com.hs.smarthome.db.AlarmSettingAccessor;
import com.hs.smarthome.db.InfraredItem;
import com.hs.smarthome.db.InfraredSettingAccessor;
import com.hs.smarthome.db.SwitchItem;

public class AlarmSettingDetailActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_alarm_detail_lv;	
	private AlarmSettingDetailAdapter alarmSettingDetailAdapter;
	
	private AlarmItem mAlarmItem;
	
	/**路径设置*/
	private final static int DIALOG_PATH_SETTING = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_alarm_detail);
		
		//mAlarmItem = (AlarmItem)getIntent().getSerializableExtra("alarmItem");
		
		int alarmItemID = getIntent().getIntExtra("alarmItemID", 0);
		
		try {
			mAlarmItem = AlarmSettingAccessor.getInstance(this).getAlarmItem(alarmItemID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mInflater = LayoutInflater.from( this );
		
		func_alarm_detail_lv = (ListView)findViewById(R.id.func_alarm_detail_lv);
		
		alarmSettingDetailAdapter = new AlarmSettingDetailAdapter();
		func_alarm_detail_lv.setAdapter(alarmSettingDetailAdapter);
		func_alarm_detail_lv.setOnItemClickListener(new ListItemClickListener());
		
		alarmSettingDetailAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		try {
			AlarmSettingAccessor.getInstance(this).updateAlarmItem(mAlarmItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if (position==0){
				if ( mAlarmItem.itemShock==AlarmItem.ITEM_FLAG_OFF )
					mAlarmItem.itemShock=AlarmItem.ITEM_FLAG_ON;
				else
					mAlarmItem.itemShock=AlarmItem.ITEM_FLAG_OFF;
			}
			
			if (position==1){
				if ( mAlarmItem.itemSound==AlarmItem.ITEM_FLAG_OFF )
					mAlarmItem.itemSound=AlarmItem.ITEM_FLAG_ON;
				else
					mAlarmItem.itemSound=AlarmItem.ITEM_FLAG_OFF;			
			}
			
			if (position==2){
				if ( mAlarmItem.itemDefaultSound==AlarmItem.ITEM_FLAG_OFF )
					mAlarmItem.itemDefaultSound=AlarmItem.ITEM_FLAG_ON;
				else
					mAlarmItem.itemDefaultSound=AlarmItem.ITEM_FLAG_OFF;
			}
			
			if (position==3){
				//弹出路径设置对话框
				
				Intent intent = new Intent();
				intent.setClass(AlarmSettingDetailActivity.this, AlarmSettingDialog.class);
				intent.putExtra("position", position);
				intent.putExtra("itemOtherSoundPath", mAlarmItem.itemOtherSoundPath);
				intent.putExtra("itemTitleName", mAlarmItem.itemTitleName);
				AlarmSettingDetailActivity.this.startActivityForResult(intent, DIALOG_PATH_SETTING);
			}
			
			alarmSettingDetailAdapter.notifyDataSetChanged();	//刷新数据集
		}
    }
    
	private class AlarmSettingDetailAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.func_switch_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			if (position==0){
				cache.label.setText("报警震动提示开关");
				boolean selected = mAlarmItem.itemShock==AlarmItem.ITEM_FLAG_ON?true:false;
				cache.check_ico.setImageResource(R.drawable.checkbox);
				cache.check_ico.setSelected(selected);
			}
			if (position==1){
				cache.label.setText("报警声音提示开关");
				boolean selected = mAlarmItem.itemSound==AlarmItem.ITEM_FLAG_ON?true:false;
				cache.check_ico.setImageResource(R.drawable.checkbox);
				cache.check_ico.setSelected(selected);
			}
			if (position==2){
				cache.label.setText("为内部报警默认音");
				boolean selected = mAlarmItem.itemDefaultSound==AlarmItem.ITEM_FLAG_ON?true:false;
				cache.check_ico.setImageResource(R.drawable.checkbox);
				cache.check_ico.setSelected(selected);
			}
			if (position==3){
				cache.label.setText("外部报警音路径");
				cache.check_ico.setImageResource(R.drawable.left);
			}
			
			return view;
		}
	}
	
	private class ItemCache {
		public ImageView icon;
		public TextView label;
		public ImageView check_ico;
		
		public ItemCache(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
			label = (TextView) view.findViewById(R.id.label);
			check_ico = (ImageView) view.findViewById(R.id.check_ico);
		}
	}
	
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {	
			return;
		}
		
		switch (reqCode) {
			case (DIALOG_PATH_SETTING):
				String itemOtherSoundPath = data.getStringExtra("itemOtherSoundPath");
				mAlarmItem.itemOtherSoundPath = itemOtherSoundPath;
				
			break;
		}
	};
}
