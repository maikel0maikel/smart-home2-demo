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
import com.hs.smarthome.db.SceneItem;

public class FuncSceneActivity extends Activity{

	private LayoutInflater mInflater = null;
	
	private ListView func_scene_lv;
	private ArrayList<SceneItem> sceneItemList = new ArrayList<SceneItem>(); 
	private SceneAdapter sceneAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_scene);
		
		mInflater = LayoutInflater.from( this );
		
		func_scene_lv = (ListView)findViewById(R.id.func_scene_lv);
		
		//构建情景模式对象
		initSceneList();
		
		sceneAdapter = new SceneAdapter();
		func_scene_lv.setAdapter(sceneAdapter);
		func_scene_lv.setOnItemClickListener(new ListItemClickListener());
		
		sceneAdapter.notifyDataSetChanged();	//刷新数据集
	}
	
	private void initSceneList() {
		
		SceneItem sceneItem1 = new SceneItem();
		sceneItem1.itemImgResID = R.drawable.menu_list_scene_hj;
		sceneItem1.itemTitleName = "回家模式";
		
		SceneItem sceneItem2 = new SceneItem();
		sceneItem2.itemImgResID = R.drawable.menu_list_scene_hk;
		sceneItem2.itemTitleName = "会客模式";
		
		SceneItem sceneItem3 = new SceneItem();
		sceneItem3.itemImgResID = R.drawable.menu_list_scene_sj;
		sceneItem3.itemTitleName = "睡觉模式";

		SceneItem sceneItem4 = new SceneItem();
		sceneItem4.itemImgResID = R.drawable.menu_list_scene_dy;
		sceneItem4.itemTitleName = "影视模式";
		
		SceneItem sceneItem5 = new SceneItem();
		sceneItem5.itemImgResID = R.drawable.menu_list_scene_qc;
		sceneItem5.itemTitleName = "起床模式";
		
		SceneItem sceneItem6 = new SceneItem();
		sceneItem6.itemImgResID = R.drawable.menu_list_scene_wc;
		sceneItem6.itemTitleName = "晚餐模式";
		
		SceneItem sceneItem7 = new SceneItem();
		sceneItem7.itemImgResID = R.drawable.menu_list_scene_bf;
		sceneItem7.itemTitleName = "布防模式";
		
		SceneItem sceneItem8 = new SceneItem();
		sceneItem8.itemImgResID = R.drawable.menu_list_scene_cf;
		sceneItem8.itemTitleName = "撤防模式";
		
		sceneItemList.add(sceneItem1);		
		sceneItemList.add(sceneItem2);
		sceneItemList.add(sceneItem3);
		sceneItemList.add(sceneItem4);
		sceneItemList.add(sceneItem5);
		sceneItemList.add(sceneItem6);
		sceneItemList.add(sceneItem7);
		sceneItemList.add(sceneItem8);
	}

	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SceneItem sceneItem = sceneItemList.get(position);
			sceneAdapter.notifyDataSetChanged();	//刷新数据集
			
			//做相关的处理
			Toast.makeText(FuncSceneActivity.this, sceneItem.itemTitleName, Toast.LENGTH_SHORT).show();
		}
    }
	
	private class SceneAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return sceneItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return sceneItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			SceneItem sceneItem = sceneItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.func_scene_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
			cache.icon.setImageResource(sceneItem.itemImgResID);
			
			cache.label.setText(sceneItem.itemTitleName);
			
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
}
