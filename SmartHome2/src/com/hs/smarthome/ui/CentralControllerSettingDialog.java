package com.hs.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hs.smarthome.R;


public class CentralControllerSettingDialog extends Activity implements View.OnClickListener{
	
	public TextView Title;
	public TextView IP;
	public TextView Port;
	public EditText EditIP;
	public EditText EditPort;
	public Button Ok;
	public Button Cancel;
	
	private int position;
	
	private final String PREFS_NAME = "SmartHomeV2";
	private final String KEY_IP = "ip";
	private final String KEY_PORT = "port";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central_controller_setting_dialog);
        
        String itemTitleName = getIntent().getStringExtra("itemTitleName");
        position = getIntent().getIntExtra("position", 0);
		
        Title = (TextView)findViewById(R.id.Title);
        IP = (TextView)findViewById(R.id.IP);
        Port = (TextView)findViewById(R.id.Port);
        EditIP = (EditText)findViewById(R.id.EditIP);
        EditPort = (EditText)findViewById(R.id.EditPort);
        Ok = (Button)findViewById(R.id.Ok);
        Cancel = (Button)findViewById(R.id.Cancel);
        
        
        Title.setText("中央控制器设置");
        IP.setText("请输入服务器IP：");
        Port.setText("请输入网络端口");
        Ok.setText("确定");
        Cancel.setText("取消");
        
        EditIP.setText( getPrefsKey(KEY_IP) );
        EditPort.setText( getPrefsKey(KEY_PORT) );
        
        Ok.setOnClickListener(this);
        Cancel.setOnClickListener(this);
        		
    }

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
			case R.id.Ok:
				//TODO 判断Edit是否为空
				/*
				Intent mIntent = new Intent(CentralControllerSettingDialog.this, FuncMoreActivity.class);
				mIntent.putExtra("IP", IP.getText().toString());
				mIntent.putExtra("Port", Port.getText().toString());
				mIntent.putExtra("position", position);
				
				setResult(Activity.RESULT_OK, mIntent);
				*/
				setPrefsKey(KEY_IP, EditIP.getText().toString());
				setPrefsKey(KEY_PORT, EditPort.getText().toString());
				
				finish();
				break;
			case R.id.Cancel:
				finish();
				break;				
		}
	}
    
    public String getPrefsKey(String keyName){
    	
    	final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getString(keyName,"");
    }
    
    public void setPrefsKey(String keyName, String keyValue){
    	
    	final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
    	Editor editor = prefs.edit();
    	editor.putString(keyName, keyValue);
    	editor.commit();
    }
}