package com.hs.smarthome.ui;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;

import android.app.Activity;
import android.os.Bundle;

public class EquipementControlPanelSettingDialog extends Activity{

	private int clickButtonID;
	
	private HomeItem homeItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_setting_dialog);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
		clickButtonID = getIntent().getIntExtra("clickButtonID",1);
		
	}
}
