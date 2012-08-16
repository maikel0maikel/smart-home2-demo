package com.hs.smarthome.ui;



import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.smarthome.R;
import com.hs.smarthome.db.ControlPanel;
import com.hs.smarthome.db.HomeItem;
import com.hs.smarthome.db.HomeSettingAccessor;
import com.hs.smarthome.db.WirelessItem;






public class HomeSettingActivity extends Activity implements View.OnClickListener{
	
	
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
	
	private ListView lastListView;
	
	private ImageView back;
	private Button add;
	
	private int roomID;

	private LayoutInflater mInflater = null;
	
	/**新增设备*/
	private final static int DIALOG_ADD = 1;
	/**修改设备*/
	private final static int DIALOG_EDIT = 2;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.func_home_setting);
		
		mInflater = LayoutInflater.from( this );
		
		this.tabButton1 = (FrameLayout) findViewById(R.id.drawingRoom);
		this.tabButton2 = (FrameLayout) findViewById(R.id.bedRoom);
		this.tabButton3 = (FrameLayout) findViewById(R.id.studyRoom);
		this.tabButton4 = (FrameLayout) findViewById(R.id.kitchenRoom);
		this.tabButton5 = (FrameLayout) findViewById(R.id.Other);
		this.tabButton6 = (FrameLayout) findViewById(R.id.Other2);
		
		this.back = (ImageView) findViewById(R.id.back);
		this.add = (Button) findViewById(R.id.add);
		
		//返回 
		back.setOnClickListener(new BackButtonListener());
		//添加
		add.setOnClickListener(new AddButtonListener());
		

		this.tabButton1.setOnClickListener(this);
		this.tabButton2.setOnClickListener(this);
		this.tabButton3.setOnClickListener(this);
		this.tabButton4.setOnClickListener(this);
		this.tabButton5.setOnClickListener(this);
		this.tabButton6.setOnClickListener(this);

		tabContainer = (FrameLayout) findViewById(R.id.tabs);

		showView(tabButton1);
	}
	
    class BackButtonListener implements OnClickListener{
    	
		@Override
		public void onClick(View arg0) {
			finish();	
			
		}
    	
    }
    class AddButtonListener implements OnClickListener{
    	
		@Override
		public void onClick(View arg0) {
			Intent intent0 = new Intent();
			intent0.setClass(HomeSettingActivity.this, HomeAddDialog.class);
			intent0.putExtra("roomID", roomID);
			
			HomeSettingActivity.this.startActivityForResult(intent0, DIALOG_ADD);
			
		}
    	
    }
	
	public void onClick(View v) {

		showView(v);
	}
	
	public void showView(View paramView) {

		ListView tmpTabListView = null;

		if (lastActionButton == paramView)
			return;

		Intent reqIntent = null;

		if (lastActionButton != null)
			lastActionButton.setSelected(false);

		switch (paramView.getId()) {
		case R.id.drawingRoom:
			roomID = 1;
			lastActionButton = paramView;
			if (tab1ListView == null) {
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab1ListView = new ListView(this);
					tab1ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab1ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(1) );
					tab1ListView.setAdapter(ext);
					tab1ListView.setOnItemClickListener(new ListItemClickListener());
					tab1ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab1ListView;
			lastListView = tab1ListView;
			break;
		case R.id.bedRoom:
			roomID = 2;
			lastActionButton = paramView;
			if (tab2ListView == null) {
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab2ListView = new ListView(this);
					tab2ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab2ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(2) );
					tab2ListView.setAdapter(ext);
					tab2ListView.setOnItemClickListener(new ListItemClickListener()); 
					tab2ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab2ListView;
			lastListView = tab2ListView;
			break;
		case R.id.studyRoom:
			roomID = 3;
			lastActionButton = paramView;
			if (tab3ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab3ListView = new ListView(this);	
					tab3ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab3ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(3) );
					tab3ListView.setAdapter(ext);
					tab3ListView.setOnItemClickListener(new ListItemClickListener());
					tab3ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab3ListView;
			lastListView = tab3ListView;
			break;
		case R.id.kitchenRoom:
			roomID = 4;
			lastActionButton = paramView;
			if (tab4ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab4ListView = new ListView(this);
					tab4ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab4ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(4) );
					tab4ListView.setAdapter(ext);
					tab4ListView.setOnItemClickListener(new ListItemClickListener());
					tab4ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab4ListView;
			lastListView = tab4ListView;
			break;
		case R.id.Other:
			roomID = 5;
			lastActionButton = paramView;
			if (tab5ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab5ListView = new ListView(this);
					tab5ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab5ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(5) );
					tab5ListView.setAdapter(ext);
					tab5ListView.setOnItemClickListener(new ListItemClickListener());
					tab5ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab5ListView;
			lastListView = tab5ListView;
			break;
		case R.id.Other2:
			roomID = 6;
			lastActionButton = paramView;
			if (tab6ListView == null) {
				// tabListView = null; 初始化
				try {
					HomeSettingAccessor.getInstance(this).initHomeTable();
					tab6ListView = new ListView(this);
					tab6ListView.setCacheColorHint(Color.parseColor("#00000000"));
					tab6ListView.setDivider( this.getResources().getDrawable(R.drawable.list_driver) );
					HomeAdapter ext = new HomeAdapter( HomeSettingAccessor.getInstance(this).getHomeItemList(6) );
					tab6ListView.setAdapter(ext);
					tab6ListView.setOnItemClickListener(new ListItemClickListener());
					tab6ListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
						  
				        @Override  
				        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
				                int arg2, long arg3) {  
				        	
				        	Dialog alertDialog = createOperateDialog(arg1, arg2);
							if (alertDialog != null) {				
								alertDialog.show();
							}
							
				            return true;  
				        }  
				      });
					ext.notifyDataSetChanged(); // 刷新数据集
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tmpTabListView = tab6ListView;
			lastListView = tab6ListView;
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
	


	/**
     * 列表项点击
     */
    private class ListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			ItemCache cache = (ItemCache) view.getTag();

			switch ( cache.homeItem.itemControlPanelID ){
			
			case ControlPanel.PANEL1:
				Intent intent1 = new Intent();
				intent1.setClass(HomeSettingActivity.this, EquipementControlOpenOff.class);
				HomeSettingActivity.this.startActivity(intent1);
			break;
			
			case ControlPanel.PANEL2:
				Intent intent2 = new Intent();
				intent2.setClass(HomeSettingActivity.this, EquipementControlKongTiao.class);
				HomeSettingActivity.this.startActivity(intent2);
			break;
			
			case ControlPanel.PANEL3:
					Intent intent3 = new Intent();
					intent3.setClass(HomeSettingActivity.this, EquipementControlTV.class);
					HomeSettingActivity.this.startActivity(intent3);
				break;
		
			case ControlPanel.PANEL4:
				Intent intent4 = new Intent();
				intent4.setClass(HomeSettingActivity.this, EquipementControlPlay.class);
				HomeSettingActivity.this.startActivity(intent4);
			break;
			
			case ControlPanel.PANEL5:
				Intent intent5 = new Intent();
				intent5.setClass(HomeSettingActivity.this, EquipementControlPlay.class);
				HomeSettingActivity.this.startActivity(intent5);
			break;
			
			case ControlPanel.PANEL6:
				Intent intent6 = new Intent();
				intent6.setClass(HomeSettingActivity.this, EquipementControlKongTiao.class);
				HomeSettingActivity.this.startActivity(intent6);
			break;
			
			
			case ControlPanel.PANEL7:
				Intent intent7 = new Intent();
				intent7.setClass(HomeSettingActivity.this, EquipementControlOpenOff_1.class);
				HomeSettingActivity.this.startActivity(intent7);
			break;
			
			case ControlPanel.PANEL8:
				Intent intent8 = new Intent();
				intent8.setClass(HomeSettingActivity.this, EquipementControlOpenOff_2.class);
				HomeSettingActivity.this.startActivity(intent8);
			break;
				
			}
		}
    }
	
	private class HomeAdapter extends BaseAdapter {
		
		public HomeAdapter(){super();}
		
		ArrayList<HomeItem> homeItemList = new ArrayList<HomeItem>();
		
		public HomeAdapter(ArrayList<HomeItem> homeItemList){
			this.homeItemList = homeItemList;
		}
		
		
		public void editHomeItem(HomeItem homeItem){
			this.homeItemList.remove(homeItem);
			this.homeItemList.add(homeItem);
		}
		
		public void addHomeItem(HomeItem homeItem){
			this.homeItemList.add(homeItem);
		}
		
		public void deleteHomeItem(HomeItem homeItem){
			this.homeItemList.remove(homeItem);
		}
		
		@Override
		public int getCount() {
			return homeItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return homeItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			HomeItem homeItem = homeItemList.get(position);
			
			View view = convertView;
			
			ItemCache cache = null;
			if (null == view) {
				view = mInflater.inflate(R.layout.home_list_item, null);
				cache = new ItemCache(view);
				view.setTag(cache);
			} else {
				cache = (ItemCache) view.getTag();
			}
			
			
					
			cache.homeItem = homeItem;
			
			cache.icon.setImageResource( ControlPanel.getImgResByPanelID(homeItem.itemControlPanelID) );
			cache.label.setText(homeItem.itemTitleName);
			
			return view;
		}
	}
	
	private class ItemCache {
		public ImageView icon;
		public TextView label;
		public HomeItem homeItem;

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
			case (DIALOG_ADD):
				HomeItem homeItem = (HomeItem)data.getSerializableExtra("homeItem");
				//修改列表
				lastListView = getSelectListView(roomID);
				
				if (lastListView!=null){
					HomeAdapter selectHomeAdapter = (HomeAdapter)lastListView.getAdapter();
					selectHomeAdapter.addHomeItem(homeItem);
					selectHomeAdapter.notifyDataSetChanged();	//刷新数据集
				}
				//保存数据库
				try {
					HomeSettingAccessor.getInstance(this).updateHomeItem(homeItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			break;
			
			case (DIALOG_EDIT):
				HomeItem homeItemEdit = (HomeItem)data.getSerializableExtra("homeItem");
				int position = data.getIntExtra("position", 0);
			//修改列表
			lastListView = getSelectListView(roomID);
			
			if (lastListView!=null){
				HomeAdapter selectHomeAdapter = (HomeAdapter)lastListView.getAdapter();
				HomeItem selectHomeItem = (HomeItem)selectHomeAdapter.getItem( position );
				
				selectHomeItem.itemId = homeItemEdit.itemId;
				selectHomeItem.itemControlPanelID = homeItemEdit.itemControlPanelID;
				selectHomeItem.itemImgResID = homeItemEdit.itemImgResID;
				selectHomeItem.itemRoomID = homeItemEdit.itemRoomID;
				selectHomeItem.itemTitleName = homeItemEdit.itemTitleName;
						
				selectHomeAdapter.notifyDataSetChanged();	//刷新数据集
			}
			//保存数据库
			try {
				HomeSettingAccessor.getInstance(this).updateHomeItem(homeItemEdit);
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			break;
		}
	};
	
	/**
	 * 点击主题列表后的选项
	 * 
	 * @param clickView
	 * @param simpleTheme
	 * @return
	 */
	public Dialog createOperateDialog(View opObj,final int position) { 
		ItemCache itemCache = (ItemCache)opObj.getTag();
		final HomeItem deleteHomeItem = itemCache.homeItem;
		final HomeItem homeItem = itemCache.homeItem;
		final int itemID = itemCache.homeItem.itemId;
		
		return new AlertDialog.Builder(HomeSettingActivity.this).setTitle("操作").
				setItems(R.array.homesetting_menu, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichcountry) {
						switch (whichcountry) {
						case 0://新增设备
							Intent intent0 = new Intent();
							intent0.setClass(HomeSettingActivity.this, HomeAddDialog.class);
							intent0.putExtra("roomID", roomID);
							
							HomeSettingActivity.this.startActivityForResult(intent0, DIALOG_ADD);
							break;
						case 1://修改设备
							Intent intent1 = new Intent();
							intent1.setClass(HomeSettingActivity.this, HomeSettingDialog.class);
							intent1.putExtra("position", position);
							intent1.putExtra("roomID", roomID);
							intent1.putExtra("itemTitleName", homeItem.itemTitleName);
							intent1.putExtra("homeItem", homeItem);
							
							HomeSettingActivity.this.startActivityForResult(intent1, DIALOG_EDIT);
							break;
						case 2://删除设备
							
							//保存数据库
							try {
								HomeSettingAccessor.getInstance(HomeSettingActivity.this).deleteHomeSetting(itemID);
							} catch (Exception e) {
								e.printStackTrace();
							}							
							
							lastListView = getSelectListView(roomID);
							
							if (lastListView!=null){
								HomeAdapter selectHomeAdapter = (HomeAdapter)lastListView.getAdapter();
								selectHomeAdapter.deleteHomeItem(deleteHomeItem);
								selectHomeAdapter.notifyDataSetChanged();	//刷新数据集
							}
							
							Toast.makeText(HomeSettingActivity.this, "删除设备", Toast.LENGTH_LONG).show();
							break;
						}
						
					}
				}).create();
	}
	
	
	public ListView getSelectListView(int roomNumID){
		ListView resultList = null;
		
		switch (roomNumID) {
		case 1:
			resultList = tab1ListView;
			break;
		case 2:
			resultList = tab2ListView;
			break;
		case 3:
			resultList = tab3ListView;
			break;
		case 4:
			resultList = tab4ListView;
			break;
		case 5:
			resultList = tab5ListView;
			break;
		case 6:
			resultList = tab6ListView;
			break;
		}
		
		return resultList;
	}
	
}
