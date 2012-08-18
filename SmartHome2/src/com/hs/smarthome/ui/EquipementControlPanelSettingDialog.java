package com.hs.smarthome.ui;

import java.util.ArrayList;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;
import com.hs.smarthome.db.HomeSettingAccessor;
import com.hs.smarthome.db.InfraredItem;
import com.hs.smarthome.db.InfraredSettingAccessor;
import com.hs.smarthome.db.WirelessItem;
import com.hs.smarthome.db.WirelessSettingAccessor;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class EquipementControlPanelSettingDialog extends Activity implements View.OnClickListener{

	private int clickButtonID;
	
	private HomeItem homeItem;
	
	public RadioButton rbGroupCommand_WX;
	public RadioButton rbGroupCommand_HW;
	private View lastActionButton;
	private ListView tab1ListView;
	private ListView tab2ListView;
	private FrameLayout tabContainer;
	private LayoutInflater mInflater = null;
	private ArrayList<WirelessItem> wirelessItemList = new ArrayList<WirelessItem>(); 
	private ArrayList<InfraredItem> infraredItemList = new ArrayList<InfraredItem>(); 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_setting_dialog);
		
		rbGroupCommand_WX = (RadioButton)findViewById(R.id.rbGroupCommand_WX);
		rbGroupCommand_HW = (RadioButton)findViewById(R.id.rbGroupCommand_HW);
		this.rbGroupCommand_WX.setOnClickListener(this);
		this.rbGroupCommand_HW.setOnClickListener(this);
		mInflater = LayoutInflater.from( this );
		tabContainer = (FrameLayout) findViewById(R.id.tabs);

		showView(rbGroupCommand_WX);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
		clickButtonID = getIntent().getIntExtra("clickButtonID",1);
		
	}
	
	
	public void showView(View paramView) {

		ListView tmpTabListView = null;

		if (lastActionButton == paramView)
			return;

		Intent reqIntent = null;

		if (lastActionButton != null)
			lastActionButton.setSelected(false);

		switch (paramView.getId()) {
		case R.id.rbGroupCommand_WX:
			lastActionButton = paramView;
			if (tab1ListView == null) {
				try {
					WirelessSettingAccessor.getInstance(this).initWirelessTable();
					tab1ListView = new ListView(this);
					tab1ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab1ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					WirelessAdapter ext = new WirelessAdapter( WirelessSettingAccessor.getInstance(this).getWirelessItemList() );
					tab1ListView.setAdapter(ext);
					//tab1ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab1ListView;
			break;
		case R.id.rbGroupCommand_HW:
			lastActionButton = paramView;
			if (tab2ListView == null) {
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab2ListView = new ListView(this);
					tab2ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab2ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					InfraredAdapter ext = new InfraredAdapter( InfraredSettingAccessor.getInstance(this).getInfraredItemList() );
					tab2ListView.setAdapter(ext);
					//tab2ListView.setOnItemClickListener(new ListItemClickListener());
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab2ListView;
			break;
		}

		if (lastActionButton != null)
			lastActionButton.setSelected(true);

		if (tmpTabListView == null) {
			return;
		}

		View mainView = tmpTabListView;
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(param);
		tabContainer.removeAllViews();
		tabContainer.addView(mainView);
	}
	
	
	
	
	private class WirelessAdapter extends BaseAdapter {

		public WirelessAdapter(){super();}
		ArrayList<WirelessItem> WirelessItemList = new ArrayList<WirelessItem>();
		
		public WirelessAdapter(ArrayList<WirelessItem> WirelessItemList){
			this.WirelessItemList = WirelessItemList;
		}
		
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
	
	private class InfraredAdapter extends BaseAdapter {
		
		public InfraredAdapter(){super();}
		ArrayList<InfraredItem> infraredItemList = new ArrayList<InfraredItem>();
		
		public InfraredAdapter(ArrayList<InfraredItem> infraredItemList){
			this.infraredItemList = infraredItemList;
		}

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
			
			
			cache.icon.setImageResource(R.drawable.setting_yes);
			
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

	@Override
	public void onClick(View v) {

		showView(v);
	}
}
