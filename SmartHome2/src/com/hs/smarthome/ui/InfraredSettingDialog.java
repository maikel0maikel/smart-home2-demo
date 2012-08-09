package com.hs.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hs.smarthome.R;


public class InfraredSettingDialog extends Activity implements View.OnClickListener{
	
	public TextView Title;
	public TextView Name;
	public EditText Edit;
	public Button Ok;
	public Button Cancel;
	
	private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wireless_setting_dialog);
        
        String itemTitleName = getIntent().getStringExtra("itemTitleName");
        position = getIntent().getIntExtra("position", 0);
		
        Title = (TextView)findViewById(R.id.Title);
        Name = (TextView)findViewById(R.id.Name);
        Edit = (EditText)findViewById(R.id.Edit);
        Ok = (Button)findViewById(R.id.Ok);
        Cancel = (Button)findViewById(R.id.Cancel);
        
        Edit.setText(itemTitleName);
        Title.setText("编辑");
        Name.setText("请输入红外名称：");
        Ok.setText("确定");
        Cancel.setText("取消");
        
        Ok.setOnClickListener(this);
        Cancel.setOnClickListener(this);
        		
    }

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
			case R.id.Ok:
				//TODO 判断Edit是否为空
				
				Intent mIntent = new Intent(InfraredSettingDialog.this, InfraredSettingActivity.class);
				mIntent.putExtra("itemTitleName", Edit.getText().toString());
				mIntent.putExtra("position", position);
				
				setResult(Activity.RESULT_OK, mIntent);
				finish();
				break;
			case R.id.Cancel:
				finish();
				break;				
		}
	}
    
}