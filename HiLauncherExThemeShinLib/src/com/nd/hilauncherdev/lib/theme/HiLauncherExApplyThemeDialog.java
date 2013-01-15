package com.nd.hilauncherdev.lib.theme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nd.android.lib.theme.R;
import com.nd.hilauncherdev.lib.theme.api.ThemeLauncherExAPI;
import com.nd.hilauncherdev.lib.theme.db.DowningTaskItem;
import com.nd.hilauncherdev.lib.theme.down.ThemeItem;

public class HiLauncherExApplyThemeDialog extends Activity implements OnClickListener{
	
	private Button confirmBtn;

	private Button cancelBtn;
	
	private TextView app_choose_title;
	
	private TextView tv_nodata_main;
	
	private DowningTaskItem dTaskItem;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.nd_hilauncher_theme_apply_dialog);
		
		app_choose_title = (TextView) findViewById(R.id.app_choose_title);
		tv_nodata_main = (TextView) findViewById(R.id.tv_nodata_main);
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
		dTaskItem = (DowningTaskItem)getIntent().getSerializableExtra("dTaskItem");
		
		app_choose_title.setText( R.string.ndtheme_apply_theme_title );
		if (dTaskItem!=null) 
			tv_nodata_main.setText( getString(R.string.ndtheme_apply_theme_text)+ dTaskItem.themeName );
	}

	public void onClick(View v) {
		if (v == confirmBtn) {
			if (dTaskItem==null)
				return ;
			String filePath = dTaskItem.tmpFilePath;
			String serverThemeID = dTaskItem.themeID;
			String newThemeID = dTaskItem.newThemeID;
			int notifyPosition = dTaskItem.startID; 
			if (filePath == null) {
				return ;
			}
			
			if ( ThemeLauncherExAPI.checkItemType(serverThemeID, ThemeItem.ITEM_TYPE_SKIN) ){
				ThemeLauncherExAPI.sendApplySkin(this, dTaskItem);
			}else{
				//如果未安装桌面的情况下
				if ( newThemeID==null || "".equals(newThemeID) ) {
					ThemeLauncherExAPI.installAndApplyAPT(this, filePath, serverThemeID, notifyPosition);
				}else{
					ThemeLauncherExAPI.sendApplyAPT(this, newThemeID);
				}
			}
			finish();
		} else if (v == cancelBtn) {
			finish();
		}		
	}
}