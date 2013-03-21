package com.nd.hilauncherdev.myphone.nettraffic.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.felix.demo.R;
import com.nd.hilauncherdev.myphone.nettraffic.util.SIMCardInfo;

/**
 * class name：AndroidUtilActivity<BR>
 * class description：show get sim card info activity<BR>
 * PS：注意权限 <BR>
 * Date:2012-3-12<BR>
 * @version 1.00
 * @author CODYY)peijiangping
 */
public class AndroidUtilActivity extends Activity {
	private Button button_getSIMInfo;
	private TextView number;
	private TextView privoid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.siminfo_main);
		button_getSIMInfo = (Button) this.findViewById(R.id.getSIMInfo);
		number = (TextView) this.findViewById(R.id.textView1);
		privoid = (TextView) this.findViewById(R.id.textView2);
		button_getSIMInfo.setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == button_getSIMInfo) {
				SIMCardInfo siminfo = new SIMCardInfo(AndroidUtilActivity.this);
				System.out.println(siminfo.getProvidersName());
				System.out.println(siminfo.getNativePhoneNumber());
				number.setText(siminfo.getNativePhoneNumber());
				privoid.setText(siminfo.getProvidersName());
			}
		}

	}
}