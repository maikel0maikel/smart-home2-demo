package com.nd.hilauncherdev.lib.theme.view;



import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nd.android.lib.theme.R;

/**
 * 网络错误View
 *
 */
public class NetErrorAndSettingView extends LinearLayout {

	Button netSettingBtn;
	
	public NetErrorAndSettingView(Context context) {
		super(context);
	}

	public NetErrorAndSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.nd_hilauncher_theme_neterror_setting, this,true); 
		initView();
	}
	
	private void initView() {
		
		netSettingBtn = (Button) findViewById(R.id.ndtheme_net_err_btn); 
		netSettingBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getContext().startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(getContext(), R.string.ndtheme_show_netsetting_err, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
