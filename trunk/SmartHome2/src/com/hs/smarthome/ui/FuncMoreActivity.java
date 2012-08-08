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
import android.widget.Toast;

import com.hs.smarthome.R;
import com.hs.smarthome.db.MoreItem;


public class FuncMoreActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_more_lv;
	private ArrayList<MoreItem> moreItemList = new ArrayList<MoreItem>(); 
	private MoreAdapter moreAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_more);
		
		mInflater = LayoutInflater.from( this );
		
		func_more_lv = (ListView)findViewById(R.id.func_more_lv);
		
		//构建设置对象
		initMoreList();
		
		moreAdapter = new MoreAdapter();
		func_more_lv.setAdapter(moreAdapter);
		func_more_lv.setOnItemClickListener(new ListItemClickListener());
		
		moreAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initMoreList() {
		
		MoreItem moreItem1 = new MoreItem();
		moreItem1.itemImgResID = R.drawable.menu_list_more_wxsz;
		moreItem1.itemTitleName = "无线名称设置";
		moreItem1.explain = "更改无线输出名称";
		
		MoreItem moreItem2 = new MoreItem();
		moreItem2.itemImgResID = R.drawable.menu_list_more_hwsz;
		moreItem2.itemTitleName = "红外遥控名称设置";
		moreItem2.explain = "更改红外遥控名称及发射端口";
		
		MoreItem moreItem3 = new MoreItem();
		moreItem3.itemImgResID = R.drawable.menu_list_more_xtsz;
		moreItem3.itemTitleName = "家居设备绑定";
		moreItem3.explain = "维护家居设备、绑定功能信号";
		
		MoreItem moreItem4 = new MoreItem();
		moreItem4.itemImgResID = R.drawable.menu_list_more_kgsz;
		moreItem4.itemTitleName = "继电器名称设置";
		moreItem4.explain = "更改继电器名称";
		
		MoreItem moreItem5 = new MoreItem();
		moreItem5.itemImgResID = R.drawable.menu_list_more_room;
		moreItem5.itemTitleName = "房间名称设置";
		moreItem5.explain = "更改房间名称";
		
		MoreItem moreItem6 = new MoreItem();
		moreItem6.itemImgResID = R.drawable.menu_list_more_alarm_ico;
		moreItem6.itemTitleName = "报警铃音设置";
		moreItem6.explain = "报警铃声、震动设置、名称";
		
		MoreItem moreItem7 = new MoreItem();
		moreItem7.itemImgResID = R.drawable.menu_list_more_xtsz;
		moreItem7.itemTitleName = "中央控制器设置";
		moreItem7.explain = "设备中央控制器IP地址、端口号";

		
			
		
	
		
		moreItemList.add(moreItem1);
		moreItemList.add(moreItem2);	
		moreItemList.add(moreItem3);	
		moreItemList.add(moreItem4);	
		moreItemList.add(moreItem5);	
		moreItemList.add(moreItem6);	
		moreItemList.add(moreItem7);	

	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MoreItem moreItem = moreItemList.get(position);
			moreAdapter.notifyDataSetChanged();	//刷新数据集
			
			
			//做相关的处理
			if(moreItem.itemImgResID==R.drawable.menu_list_more_wxsz)
			{
				Intent intent = new Intent();
				intent.setClass(FuncMoreActivity.this, wireless_setting.class);
				FuncMoreActivity.this.startActivity(intent);
				
				
			}
			//Toast.makeText(FuncMoreActivity.this, moreItem.itemTitleName, Toast.LENGTH_SHORT).show();
		}
    }
	
	private class MoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return moreItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return moreItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			MoreItem moreItem = moreItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.func_more_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
			cache.icon.setImageResource(moreItem.itemImgResID);
			
			cache.label.setText(moreItem.itemTitleName);
			cache.exp.setText(moreItem.explain);
			
			
			return view;
		}
	}
	
	private class ItemCache {
		public ImageView icon;
		public TextView label;
		public TextView exp;
		
		
		public ItemCache(View view) {
			icon = (ImageView) view.findViewById(R.id.icon);
			label = (TextView) view.findViewById(R.id.label);
			exp = (TextView) view.findViewById(R.id.exp);
		}
	}
}
