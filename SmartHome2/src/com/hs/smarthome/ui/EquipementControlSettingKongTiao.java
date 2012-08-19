package com.hs.smarthome.ui;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EquipementControlSettingKongTiao extends Activity implements View.OnClickListener{
	
	private Button btClose;
	private Button btOpen;
	private Button bt_kt_cool;
	private Button bt_kt_hot;
	private Button bt_kt_1;
	private Button bt_kt_2;
	private Button bt_kt_3;
	private Button bt_kt_4;
	private Button bt_kt_5;
	private Button bt_kt_6;
	private Button bt_kt_7;
	private Button bt_kt_8;
	private Button bt_kt_9;

	private HomeItem homeItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_kongtiao);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
		
		btClose = (Button)findViewById(R.id.btClose);
		btOpen = (Button)findViewById(R.id.btOpen);
		bt_kt_cool = (Button)findViewById(R.id.bt_kt_cool);
		bt_kt_hot = (Button)findViewById(R.id.bt_kt_hot);
		bt_kt_1 = (Button)findViewById(R.id.bt_kt_1);
		bt_kt_2 = (Button)findViewById(R.id.bt_kt_2);
		bt_kt_3 = (Button)findViewById(R.id.bt_kt_3);
		bt_kt_4 = (Button)findViewById(R.id.bt_kt_4);
		bt_kt_5 = (Button)findViewById(R.id.bt_kt_5);
		bt_kt_6 = (Button)findViewById(R.id.bt_kt_6);
		bt_kt_7 = (Button)findViewById(R.id.bt_kt_7);
		bt_kt_8 = (Button)findViewById(R.id.bt_kt_8);
		bt_kt_9 = (Button)findViewById(R.id.bt_kt_9);
		
		
		
		this.btClose.setOnClickListener(this);
		this.btOpen.setOnClickListener(this);
		this.bt_kt_cool.setOnClickListener(this);
		this.bt_kt_hot.setOnClickListener(this);
		this.bt_kt_1.setOnClickListener(this);
		this.bt_kt_2.setOnClickListener(this);
		this.bt_kt_3.setOnClickListener(this);
		this.bt_kt_4.setOnClickListener(this);
		this.bt_kt_5.setOnClickListener(this);
		this.bt_kt_6.setOnClickListener(this);
		this.bt_kt_7.setOnClickListener(this);
		this.bt_kt_8.setOnClickListener(this);
		this.bt_kt_9.setOnClickListener(this);
	
	}
	@Override
	public void onClick(View paramView) {
		
		Intent reqIntent = null;
		switch (paramView.getId()) {
		case R.id.btClose:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.btClose);
			break;
		case R.id.btOpen:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.btOpen);
			break;	
		case R.id.bt_kt_cool:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_cool);
			break;
		case R.id.bt_kt_hot:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_hot);
			break;
		case R.id.bt_kt_1:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_1);
			break;
		case R.id.bt_kt_2:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_2);
			break;
		case R.id.bt_kt_3:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_3);
			break;
		case R.id.bt_kt_4:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_4);
			break;
		case R.id.bt_kt_5:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_5);
			break;
		case R.id.bt_kt_6:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_6);
			break;
		case R.id.bt_kt_7:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_7);
			break;
		case R.id.bt_kt_8:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_8);
			break;
		case R.id.bt_kt_9:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.bt_kt_9);
			break;
	
		}
		
		startActivity(reqIntent);
	}
}
