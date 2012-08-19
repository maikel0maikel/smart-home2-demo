package com.hs.smarthome.ui;

import com.hs.smarthome.R;
import com.hs.smarthome.db.HomeItem;

import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EquipementControlSettingPlay extends Activity implements  View.OnClickListener{
	
	private Button btClose;
	private Button panel_play_btbf;
	private Button panel_play_btstop;
	private Button panel_play_btkt;
	private Button panel_play_btmt;
	private Button panel_play_btmj;
	private Button panel_play_btkj;
	
	private HomeItem homeItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_play);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
		
		btClose = (Button)findViewById(R.id.btClose);
		panel_play_btbf = (Button)findViewById(R.id.panel_play_btbf);
		panel_play_btstop = (Button)findViewById(R.id.panel_play_btstop);
		panel_play_btkt = (Button)findViewById(R.id.panel_play_btkt);
		panel_play_btmt = (Button)findViewById(R.id.panel_play_btmt);
		panel_play_btmj = (Button)findViewById(R.id.panel_play_btmj);
		panel_play_btkj = (Button)findViewById(R.id.panel_play_btkj);
		
		this.btClose.setOnClickListener(this);
		this.panel_play_btbf.setOnClickListener(this);
		this.panel_play_btstop.setOnClickListener(this);
		this.panel_play_btkt.setOnClickListener(this);
		this.panel_play_btmt.setOnClickListener(this);
		this.panel_play_btmj.setOnClickListener(this);
		this.panel_play_btkj.setOnClickListener(this);
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
		case R.id.panel_play_btbf:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btbf);
			break;	
		case R.id.panel_play_btstop:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btstop);
			break;
		case R.id.panel_play_btkt:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btkt);
			break;
		case R.id.panel_play_btmt:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btmt);
			break;
		case R.id.panel_play_btmj:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btmj);
			break;
		case R.id.panel_play_btkj:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_play_btkj);
			break;
		
		}
		
		startActivity(reqIntent);
	}
}
