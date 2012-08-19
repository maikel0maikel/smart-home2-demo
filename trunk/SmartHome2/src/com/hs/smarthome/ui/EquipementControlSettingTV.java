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
	private Button panel_tv_btleft;
	private Button panel_tv_ok;
	private Button panel_tv_btright;
	private Button panel_tv_btbottom;
	private Button panel_tv_yinlin_up;
	private Button panel_tv_video_up;
	private Button panel_tv_yinlin_down;
	private Button panel_tv_video_down;
	
	private HomeItem homeItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.equipement_control_panel_tv_2);
		
		homeItem = (HomeItem)getIntent().getSerializableExtra("homeItem");
				
		btClose = (Button)findViewById(R.id.btClose);
		panel_tv_bttop = (Button)findViewById(R.id.panel_tv_bttop);
		panel_tv_btleft = (Button)findViewById(R.id.panel_tv_btleft);
		panel_tv_ok = (Button)findViewById(R.id.panel_tv_ok);
		panel_tv_btright = (Button)findViewById(R.id.panel_tv_btright);
		panel_tv_btbottom = (Button)findViewById(R.id.panel_tv_btbottom);
		panel_tv_yinlin_up = (Button)findViewById(R.id.panel_tv_yinlin_up);
		panel_tv_video_up = (Button)findViewById(R.id.panel_tv_video_up);
		panel_tv_yinlin_down = (Button)findViewById(R.id.panel_tv_yinlin_down);
		panel_tv_video_down = (Button)findViewById(R.id.panel_tv_video_down);
		
		
		
		this.btClose.setOnClickListener(this);
		this.panel_tv_bttop.setOnClickListener(this);
		this.panel_tv_btleft.setOnClickListener(this);
		this.panel_tv_ok.setOnClickListener(this);
		this.panel_tv_btright.setOnClickListener(this);
		this.panel_tv_btbottom.setOnClickListener(this);
		this.panel_tv_yinlin_up.setOnClickListener(this);
		this.panel_tv_video_up.setOnClickListener(this);
		this.panel_tv_yinlin_down.setOnClickListener(this);
		this.panel_tv_video_down.setOnClickListener(this);
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
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_bttop);
			break;	
		case R.id.panel_tv_btleft:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_btleft);
			break;
		case R.id.panel_tv_ok:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_ok);
			break;
		case R.id.panel_tv_btright:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_btright);
			break;
		case R.id.panel_tv_btbottom:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_btbottom);
			break;
		case R.id.panel_tv_yinlin_up:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_yinlin_up);
			break;
		case R.id.panel_tv_video_up:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_video_up);
			break;
		case R.id.panel_tv_yinlin_down:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_yinlin_down);
			break;
		case R.id.panel_tv_video_down:
			reqIntent =new Intent(this, EquipementControlPanelSettingDialog.class);
			reqIntent.putExtra("homeItem",homeItem);
			reqIntent.putExtra("clickButtonID", R.id.panel_tv_video_down);
			break;
		}
		
		startActivity(reqIntent);
	}
	
}
