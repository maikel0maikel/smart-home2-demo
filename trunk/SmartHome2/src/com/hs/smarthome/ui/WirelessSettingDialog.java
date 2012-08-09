package com.hs.smarthome.ui;

import com.hs.smarthome.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; 
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;


public class WirelessSettingDialog extends Activity {
	
	public TextView Title;
	public TextView Name;
	public EditText Edit;
	public Button Ok;
	public Button Cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wireless_setting_dialog);
        
        Title = (TextView)findViewById(R.id.Title);
        Name = (TextView)findViewById(R.id.Name);
        Edit = (EditText)findViewById(R.id.Edit);
        Ok = (Button)findViewById(R.id.Ok);
        Cancel = (Button)findViewById(R.id.Cancel);
        
        Title.setText("编辑");
        Name.setText("请输入无线名称：");
        Ok.setText("确定");
        Cancel.setText("取消");
        
        
        		
    }
    
}