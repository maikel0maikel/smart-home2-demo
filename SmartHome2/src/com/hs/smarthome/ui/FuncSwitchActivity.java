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
import com.hs.smarthome.db.SmartHomeAccessor;
import com.hs.smarthome.db.SwitchItem;

public class FuncSwitchActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_switch_lv;
	private ArrayList<SwitchItem> switchItemList = new ArrayList<SwitchItem>(); 
	private SwitchAdapter switchAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_switch);
		
		mInflater = LayoutInflater.from( this );
		
		func_switch_lv = (ListView)findViewById(R.id.func_switch_lv);
		
		//获取继电器列表:数据量小,且不需要联网。暂时不单独开启线程加载		
		try {
			SmartHomeAccessor.getInstance(this).initSwitchTable();
			switchItemList = SmartHomeAccessor.getInstance(this).getSwitchItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		switchAdapter = new SwitchAdapter();
		func_switch_lv.setAdapter(switchAdapter);
		func_switch_lv.setOnItemClickListener(new ListItemClickListener());
		
		switchAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
    /**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SwitchItem switchItem = switchItemList.get(position);
			
			if (switchItem.itemFlag==SwitchItem.ITEM_FLAG_OFF)
				switchItem.itemFlag=SwitchItem.ITEM_FLAG_ON;
			else
				switchItem.itemFlag=SwitchItem.ITEM_FLAG_OFF;
			switchAdapter.notifyDataSetChanged();	//刷新数据集
			
			//做相关的处理
			Toast.makeText(FuncSwitchActivity.this, switchItem.itemTitleName, Toast.LENGTH_SHORT).show();
		}
    }
    
	private class SwitchAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return switchItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return switchItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			SwitchItem switchItem = switchItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.func_switch_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
			cache.icon.setImageResource(R.drawable.relay_ico);
			
			cache.label.setText(switchItem.itemTitleName);
			
			boolean selected = switchItem.itemFlag==SwitchItem.ITEM_FLAG_ON?true:false;
			cache.check_ico.setSelected(selected);
			
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
}
