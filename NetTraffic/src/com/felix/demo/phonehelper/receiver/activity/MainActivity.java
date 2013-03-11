package com.felix.demo.phonehelper.receiver.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.ServiceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.internal.telephony.ITelephony;
import com.felix.demo.R;
import com.felix.demo.phonehelper.receiver.TestThread;
import com.nd.hilauncherdev.myphone.phonehelper.CallHelper;
import com.nd.hilauncherdev.myphone.phonehelper.CallHelperService;
import com.nd.hilauncherdev.myphone.phonehelper.CallHelperTool;

public class MainActivity extends Activity {

	private Button button;
	private EditText phoneNumEdt;
	private Button phoneCallBtn;
	
	private boolean bCurrentSwitch = false;
	private int currVolume;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_main);

		button = (Button) findViewById(R.id.button);
		phoneNumEdt = (EditText) findViewById(R.id.phone_num_edt);
		phoneCallBtn = (Button) findViewById(R.id.phone_call_btn);
		
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//System.exit(0);
				
				//closePhone();
				
				if ( bCurrentSwitch ){
					CallHelperTool.closeSpeaker(MainActivity.this);
				}else{
					CallHelperTool.openSpeaker(MainActivity.this);
				}
				
				bCurrentSwitch = !bCurrentSwitch;
			}
		};
		button.setOnClickListener(listener);
		
		//抢票
		phoneCallBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CallHelperService.class);
				intent.putExtra("phoneNumber", "10086");//10086 18065040762
				intent.putExtra("times", 3);
				stopService(intent);
				startService(intent);
				
				CallHelper callHelper = new CallHelper(MainActivity.this, "10086", 3);
				callHelper.startCall();
			}
		});
	}
	
	/* 检查字符串是否为电话号码的方法,并返回true or false的判断值 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * 可接受的电话格式有: ^\\(? : 可以使用 "(" 作为开头 (\\d{3}): 紧接着三个数字 \\)? : 可以使用")"继续
		 * [- ]? : 在上述格式后可以使用具选择性的 "-". (\\d{4}) : 再紧接着三个数字 [- ]? : 可以使用具选择性的
		 * "-" 继续. (\\d{4})$: 以四个数字结束. 可以比较下列数字格式: (123)456-78900,
		 * 123-4560-7890, 12345678900, (123)-4560-7890
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		/* 创建Pattern */
		Pattern pattern = Pattern.compile(expression);
		/* 将Pattern 以参数传入Matcher作Regular expression */
		Matcher matcher = pattern.matcher(inputStr);
		/* 创建Pattern2 */
		Pattern pattern2 = Pattern.compile(expression2);
		/* 将Pattern2 以参数传入Matcher2作Regular expression */
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		//测试拨打订票热线会检测号码为非法
		//return isValid;
		
		return true;
	}
	
	/**
     * 结束通话
	 * @throws  
     */
	private synchronized void closePhone() {
/*
		TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		ITelephony iTelephony;

		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephony, (Object[]) null);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		try {
			ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
			phone.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//打开扬声器
    public void OpenSpeaker() {

		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.ROUTE_SPEAKER);
			currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);

				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


   //关闭扬声器
   public void CloseSpeaker() {
   
       try {
           AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
           if(audioManager != null) {
               if(audioManager.isSpeakerphoneOn()) {
                 audioManager.setSpeakerphoneOn(false);
                 audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume, AudioManager.STREAM_VOICE_CALL);
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
         //Toast.makeText(context,"揚聲器已經關閉",Toast.LENGTH_SHORT).show();
   } 
}
