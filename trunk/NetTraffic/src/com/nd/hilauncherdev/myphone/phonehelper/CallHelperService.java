package com.nd.hilauncherdev.myphone.phonehelper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

import com.felix.demo.R;
import com.felix.demo.phonehelper.receiver.TestThread;
import com.felix.demo.phonehelper.receiver.activity.MainActivity;


/**
 * 自动拨号服务 
 * 1、启动拨号助手
 * 2、浮动展示中断拨号助手View
 * 
 * @author cfb
 */
public class CallHelperService extends Service implements CallHelperStopCallBack {

	//private static final String TAG = "CallHelperService";
	private static final String PREFS_NAME = "CallHelperPrefs";
	private static final String TOUCH_LAST_X = "touchLastX";
	private static final String TOUCH_LAST_Y = "touchLastY";

	private TelephonyManager phoneMgr;
	private CallHelperListener mMyPhoneListener;

	private String phoneNumber = null;
	private int iTimes;

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
	private View floatView;
	private Button imageWhitespot;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;

	private float mWinTouchStartX;
	private float mWinTouchStartY;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		floatView = LayoutInflater.from(this).inflate(R.layout.callhelper_floating, null);

		imageWhitespot = (Button) floatView.findViewById(R.id.image_whitespot);
		imageWhitespot.setVisibility(View.VISIBLE);
		createView();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (intent==null)
			return;
		
		// TODO 暂时不考虑号码和次数的合法性
		phoneNumber = intent.getStringExtra("phoneNumber");
		iTimes = intent.getIntExtra("times", 1);
		init();
		
		new Thread(new TestThread(getApplicationContext())).start();
	}

	private void init() {
		try {
			CallHelper callHelper = new CallHelper(this, phoneNumber, iTimes);
			mMyPhoneListener = new CallHelperListener();
			mMyPhoneListener.setCallHelper(callHelper);
			mMyPhoneListener.setCallHelperStop(this);
			CallHelperListener.initAll();

			phoneMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			phoneMgr.listen(mMyPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createView() {

		wm = (WindowManager) getApplicationContext().getSystemService("window");

		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角

		wmParams.x = (int) getPrefsKey(TOUCH_LAST_X);
		wmParams.y = (int) getPrefsKey(TOUCH_LAST_Y);
		// 设置悬浮窗口长宽数据
		wmParams.width = 300;// WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = 200;// WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		// 显示floatView图像
		wm.addView(floatView, wmParams);

		floatView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - 25;// 25是系统状态栏的高度
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					mWinTouchStartX = x;
					mWinTouchStartY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					if (Math.abs(mWinTouchStartX - x) > 5 || Math.abs(mWinTouchStartY - y) > 5) {
						updateViewPosition();
					} else {
						// floatStopCall();
					}
					mTouchStartX = mTouchStartY = 0;
					mWinTouchStartX = mWinTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		imageWhitespot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				floatStopCall();
			}
		});
	}

	private void updateViewPosition() {
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(floatView, wmParams);
	}

	@Override
	public void onDestroy() {

		CallHelperListener.clearAll();
		if (mMyPhoneListener != null) {
			mMyPhoneListener.setCallHelperStop(null);
			if (phoneMgr != null) {
				phoneMgr.listen(mMyPhoneListener, PhoneStateListener.LISTEN_NONE);
				phoneMgr = null;
			}
		}

		// 保存位置到配置文件
		setPrefsKey(TOUCH_LAST_X, x);
		setPrefsKey(TOUCH_LAST_Y, y);
		if (wm!=null)
			wm.removeView(floatView);

		super.onDestroy();
	}

	public float getPrefsKey(String keyName) {

		final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		return prefs.getFloat(keyName, 0);
	}

	public void setPrefsKey(String keyName, float xPostion) {

		final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		Editor editor = prefs.edit();
		editor.putFloat(keyName, xPostion);
		editor.commit();
	}

	private void floatStopCall() {

		CallHelperTool.closePhone();
		callHelperStopCall();
	}

	
	public void callHelperStopCall() {
		stopSelf();
	}

	@Override
	public boolean checkCallPhoneStateAlive() {
		
		String callNums = Calls.getLastOutgoingCall(this);
		Cursor cursor = null;
		try{
			cursor = getContentResolver().query(Calls.CONTENT_URI, new String[] { Calls.DURATION, Calls.TYPE, Calls.DATE }, 
					" NUMBER=? and _id IN (select max(_id) FROM calls)", new String[]{callNums}, Calls.DEFAULT_SORT_ORDER);
			if (cursor!=null){
				boolean hasRecord = cursor.moveToFirst();
				long outgoing = 0L;
				while (hasRecord) {
					int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));
					long duration = cursor.getLong(cursor.getColumnIndex(Calls.DURATION));
					switch (type) {
					case Calls.OUTGOING_TYPE:
						outgoing += duration;
					default:
						break;
					}
					hasRecord = cursor.moveToNext();
				}
				return outgoing>0;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
            if (cursor != null) cursor.close();
        }
		return false;
	}
}
