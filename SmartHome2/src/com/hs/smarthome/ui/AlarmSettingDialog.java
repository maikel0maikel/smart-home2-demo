package com.hs.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hs.smarthome.R;


public class AlarmSettingDialog extends Activity implements View.OnClickListener{
	
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
        
        String itemOtherSoundPath = getIntent().getStringExtra("itemOtherSoundPath");
        position = getIntent().getIntExtra("position", 0);
		
        Title = (TextView)findViewById(R.id.Title);
        Name = (TextView)findViewById(R.id.Name);
        Edit = (EditText)findViewById(R.id.Edit);
        Ok = (Button)findViewById(R.id.Ok);
        Cancel = (Button)findViewById(R.id.Cancel);
        
        Edit.setText(itemOtherSoundPath);
        Title.setText("报警铃音编辑");
        Name.setText("请输入报警铃音路径：");
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
				
				Intent mIntent = new Intent(AlarmSettingDialog.this, AlarmSettingDetailActivity.class);
				mIntent.putExtra("itemOtherSoundPath", Edit.getText().toString());
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