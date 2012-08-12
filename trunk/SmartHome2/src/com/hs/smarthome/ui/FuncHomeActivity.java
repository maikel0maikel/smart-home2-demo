package com.hs.smarthome.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hs.smarthome.R;

public class FuncHomeActivity extends Activity implements View.OnClickListener{

	private FrameLayout tabContainer;
	
	private FrameLayout tabButton1;
	private FrameLayout tabButton2;
	private FrameLayout tabButton3;
	private FrameLayout tabButton4;
	private FrameLayout tabButton5;
	private FrameLayout tabButton6;
	
	private ListView tab1ListView;
	private ListView tab2ListView;
	private ListView tab3ListView;
	private ListView tab4ListView;
	private ListView tab5ListView;
	private ListView tab6ListView;
	
	private View lastActionButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_home);
		
		this.tabButton1 = (FrameLayout)findViewById(R.id.drawingRoom);
		this.tabButton2 = (FrameLayout)findViewById(R.id.bedRoom);
		this.tabButton3 = (FrameLayout)findViewById(R.id.studyRoom);
		this.tabButton4 = (FrameLayout)findViewById(R.id.kitchenRoom);
		this.tabButton5 = (FrameLayout)findViewById(R.id.Other);
		this.tabButton6 = (FrameLayout)findViewById(R.id.Other2);
		
		this.tabButton1.setOnClickListener(this);
		this.tabButton2.setOnClickListener(this);
		this.tabButton3.setOnClickListener(this);
		this.tabButton4.setOnClickListener(this);
		this.tabButton5.setOnClickListener(this);
		this.tabButton6.setOnClickListener(this);
		
		tabContainer = (FrameLayout) findViewById(R.id.tabs);
		
		showView(tabButton1);
	}
	
	public void showView(View paramView){
		
		ListView tmpTabListView = null;
		
		if (lastActionButton==paramView) 
			return ; 
		
		Intent reqIntent = null;
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(false);
		
		switch (paramView.getId()) {
			case R.id.drawingRoom:
				lastActionButton = paramView;
				if (tab1ListView==null) {
					tab1ListView = new ListView(this);
			        final List<HashMap<String,String>> list = initlist();
			        Log.v("ss",list.size()+"");
			        ExtAdapter ext = new ExtAdapter(this,list);
			        tab1ListView.setAdapter(ext);
				}
				tmpTabListView = tab1ListView;
				break;
			case R.id.bedRoom:
				lastActionButton = paramView;
				if (tab2ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab2ListView;
				break;
			case R.id.studyRoom:
				lastActionButton = paramView;
				if (tab3ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab3ListView;
				break;
			case R.id.kitchenRoom:
				lastActionButton = paramView;
				if (tab4ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab4ListView;
				break;
			case R.id.Other:
				lastActionButton = paramView;
				if (tab5ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab5ListView;
				break;		
			case R.id.Other2:
				lastActionButton = paramView;
				if (tab6ListView==null) {
					//tabListView = null;  初始化
				}
				tmpTabListView = tab6ListView;
				break;					
		}
		
		if (lastActionButton!=null) 
			lastActionButton.setSelected(true);
		
		if (tmpTabListView==null ) {
			return;
		}
		
		View mainView = tmpTabListView;
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(param);
		tabContainer.removeAllViews();
		tabContainer.addView(mainView);
	}
	
	@Override
	public void onClick(View v) {
		
		showView(v);
	}
	
	public List<HashMap<String, String>> initlist() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		for (int i = 0; i < 10; i++) {
			map = new HashMap<String, String>();
			map.put("id", i + "");
			map.put("name", i + "ww");
			map.put("age", i + "aa");
			list.add(map);
		}
		map = new HashMap<String, String>();
		map.put("id", "用户");
		map.put("name", "姓名");
		map.put("age", "年龄");
		list.add(0, map);
		return list;
	}
}

class ExtAdapter extends BaseAdapter {

	// 数据源
	private List<HashMap<String, String>> list;
	private Context context;

	public ExtAdapter(Context context, List<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TableLayout tab = new TableLayout(context);
		tab.setStretchAllColumns(true);
		TableRow row = new TableRow(context);
		TextView tv = null;
		HashMap<String, String> map = list.get(position);
		for (String key : map.keySet()) {
			tv = new TextView(context);
			tv.setText(map.get(key));
			tv.setHeight(30);
			row.addView(tv);
		}
		tab.addView(row);
		return tab;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}