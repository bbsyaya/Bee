package com.lingshi321.bee;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.client.android.CaptureActivity;
import com.lingshi321.bee.common.LoginRunnable;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.service.PushService;
import com.lingshi321.bee.service.PushService.PushBinder;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;
import com.lingshi321.bee.util.SharedPreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	DefaultHttpClient httpClient = new DefaultHttpClient();

	private EditText inputPhone = null;
	private EditText inputPassword = null;
	private Button login = null;
	private RelativeLayout cover = null;

	private Handler handler = null;

	private Dialog dialog = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		handler = new LoginHandler();

		cover = (RelativeLayout) findViewById(R.id.cover);
		cover.setVisibility(View.INVISIBLE);

		inputPhone = (EditText) findViewById(R.id.input_phone_value);
		inputPassword = (EditText) findViewById(R.id.input_password_value);
		login = (Button) findViewById(R.id.submit);
		
		inputPhone.setText(SharedPreferencesUtil.getPhone());
		inputPassword.setText(SharedPreferencesUtil.getPassword());
		
		login.setOnClickListener(this);

//		startService(new Intent().setClass(getApplicationContext(),
//				PushService.class));

		Intent intent = getIntent();
		String tip = (String) intent.getSerializableExtra("tip");
		if (null != tip) {
			Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_LONG)
					.show();
		}

	}

	public void handBackInfo(Message msg) {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
		switch (msg.what) {
		case RequestResult.NORMAL:
			JSONObject userInfo = (JSONObject) msg.obj;
			try {
				Toast.makeText(getApplicationContext(),
						userInfo.getString("staff_name"), Toast.LENGTH_LONG)
						.show();
				SharedPreferencesUtil.setUserEmail(userInfo
						.getString("staff_email"));
				SharedPreferencesUtil.setUserJoinTime(userInfo
						.getString("staff_join_time"));
				SharedPreferencesUtil.setUserManagerPart(userInfo
						.getString("staff_manage_partid"));
				SharedPreferencesUtil.setUserName(userInfo
						.getString("staff_name"));
				SharedPreferencesUtil.setUserPhone(userInfo
						.getString("staff_tel"));
				SharedPreferencesUtil.setStaffId(userInfo.getString("staff_id"));
				SharedPreferencesUtil.setPhone(inputPhone.getText().toString());
				SharedPreferencesUtil.setPassword(inputPassword.getText()
						.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

			// 登录openfire服务器  推送服务取消
//			if (pushService != null) {
//				pushService.loginOpenfire(inputPhone.getText().toString());
//			}

			break;
		case RequestResult.NONETWORK:
			Toast.makeText(getBaseContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
			break;
		case RequestResult.REQUESTERROR:
			Toast.makeText(getBaseContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View arg0) {

		 String phoneNumber = inputPhone.getText().toString();
		 String password = inputPassword.getText().toString();
		
		 Map<String, String> params = new HashMap<String, String>();
		 params.put(DataInterfaceConstants.PHONE, phoneNumber);
		 params.put(DataInterfaceConstants.PASSWORD, password);
		
		 // cover.setVisibility(View.VISIBLE);
		 dialog = new Dialog(LoginActivity.this, R.style.dialogStyle);
		 dialog.setContentView(R.layout.login_dialog);
		 dialog.setCancelable(false);
		 dialog.show();
		
		 new Thread(new LoginRunnable(handler, params, this)).start();

	}

	@SuppressLint("HandlerLeak")
	private class LoginHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			cover.setVisibility(View.INVISIBLE);
			handBackInfo(msg);
		}
	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (dialog != null && dialog.isShowing()) {
				dialog.cancel();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
