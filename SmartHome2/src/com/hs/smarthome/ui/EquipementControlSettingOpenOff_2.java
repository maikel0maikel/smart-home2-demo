package com.hs.smarthome.ui;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EquipementControlSettingOpenOff_2 extends Activity implements View.OnClickListener{
	
	private Button panel_kg_bt1;
	private Button panel_kg_bt2;
	
	private HomeItem homeItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_openoff_2);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
		
		panel_kg_bt1 = (Button)findViewById(R.id.panel_kg_bt1);
		panel_kg_bt2 = (Button)findViewById(R.id.panel_kg_bt2);
		
		this.panel_kg_bt1.setOnClickListener(this);
		this.panel_kg_bt2.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View paramView) {
		
		Intent reqIntent = null;
		switch (paramView.getId()) {
		case R.id.panel_kg_bt1:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_kg_bt1);
			break;
		case R.id.panel_kg_bt2:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_kg_bt2);
			break;	
			
		}
		
		startActivity(reqIntent);
	}
}
