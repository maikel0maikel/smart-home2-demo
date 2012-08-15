package com.hs.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;


public class HomeSettingDialog extends Activity implements View.OnClickListener{
	
	private static final String[] panel_Countries = { "开关面板", "空调面板", "电视机面板", "播放器", "多媒体控制面板","空调多功能面板","1路开关面板","2路开关面板" };
	private static final String[] room_Countries = { "客厅", "卧室", "书房", "厨房", "其他","其他2" };
	
	public TextView Title;
	public TextView Name;
	public TextView Panel;
	public TextView Room;
	public EditText Edit;
	public Button Ok;
	public Button Cancel;
	private Spinner panel_Spinner;
	private Spinner room_Spinner;
	private ArrayAdapter<String> paneldapter;
	private ArrayAdapter<String> roomdapter;
	
	private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_setting_dialog);
        
        String itemTitleName = getIntent().getStringExtra("itemTitleName");
        position = getIntent().getIntExtra("position", 0);
		
        Title = (TextView)findViewById(R.id.Title);
        Name = (TextView)findViewById(R.id.Name);
        Panel = (TextView)findViewById(R.id.Panel);
        Room = (TextView)findViewById(R.id.Room);
        Edit = (EditText)findViewById(R.id.Edit);
        Ok = (Button)findViewById(R.id.Ok);
        Cancel = (Button)findViewById(R.id.Cancel);
        panel_Spinner = (Spinner) findViewById(R.id.panelSpinner);
        room_Spinner = (Spinner) findViewById(R.id.roomSpinner);
        
        Edit.setText(itemTitleName);
        Title.setText("编辑");
        Room.setText("设备分类:");
        Panel.setText("控制面板：");
        Name.setText("设备名称：");
        Ok.setText("确定");
        Cancel.setText("取消");
        
    	// 将可选内容与ArrayAdapter连接
        paneldapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, panel_Countries);
        roomdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, room_Countries);

		// 设置下拉列表的风格
        paneldapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter添加到m_Spinner中
        panel_Spinner.setAdapter(paneldapter);
        room_Spinner.setAdapter(roomdapter);

		// 添加Spinner事件监听
        panel_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//m_TextView.setText("你的血型是：" + m_Countries[arg2]);
				// 设置显示当前选择的项
				//arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
        room_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//m_TextView.setText("你的血型是：" + m_Countries[arg2]);
				// 设置显示当前选择的项
				//arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
        
        Ok.setOnClickListener(this);
        Cancel.setOnClickListener(this);
        		
    }



	
public void onClick(View paramView) {
		switch (paramView.getId()) {
			case R.id.Ok:
				//TODO 判断Edit是否为空
				
	HomeItem homeItem = new HomeItem();
				
				homeItem.itemTitleName = Edit.getText().toString();
				homeItem.itemRoomID = room_Spinner.getSelectedItemPosition()+1;
				homeItem.itemControlPanelID = panel_Spinner.getSelectedItemPosition()+1;
				
				Intent mIntent = new Intent(HomeSettingDialog.this, HomeSettingActivity.class);
				mIntent.putExtra("homeItem", homeItem);
				
				setResult(Activity.RESULT_OK, mIntent);
				finish();
				break;
			case R.id.Cancel:
				finish();
				break;					
		}
	}
    
}