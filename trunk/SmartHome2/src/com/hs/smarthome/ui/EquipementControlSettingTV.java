package com.hs.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;

public class EquipementControlSettingTV extends Activity implements View.OnClickListener{

	private Button btClose;
	private Button panel_tv_bttop;
	private HomeItem homeItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_tv_2);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
				
		btClose = (Button)findViewById(R.id.btClose);
		panel_tv_bttop = (Button)findViewById(R.id.panel_tv_bttop);
		
		this.btClose.setOnClickListener(this);
		this.panel_tv_bttop.setOnClickListener(this);
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
		case R.id.panel_tv_bttop:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			break;	
		}
		
		startActivity(reqIntent);
	}
	
}
