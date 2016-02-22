package com.lingshi321.bee.service;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.lingshi321.bee.MainActivity;
import com.lingshi321.bee.R;
import com.lingshi321.bee.util.SharedPreferencesUtil;
import com.lingshi321.bee.util.XmppTool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PushService extends Service {

	private final String PASSWORD = "@bee";
	private final String TAG = "PushService";
	private PushServiceConnectionListener listener = null;
	private PushServiceChatListener chatListener = null;

	public NotificationManager nm = null;
	public int NOTIFY_ID = 186;

	public static boolean isLogin = false;
	private boolean isInit = false;

	@Override
	public IBinder onBind(Intent intent) {

		return new PushBinder();
	}

	@Override
	public void onCreate() {

		Log.i(TAG, "create");
		listener = new PushServiceConnectionListener();
		chatListener = new PushServiceChatListener();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		init();

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {

//		XmppTool.closeConnection();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

	/**
	 * 初始化openfire链接,如果本地存有帐号信息则直接进行登录
	 */
	public void init() {

		if (!isInit) {
			Log.i(TAG, "init()");
			isInit = true;
			if (!isLogin)
				new Thread() {
					public void run() {

						XmppTool.closeConnection();
						XmppTool.getConnection();

						if (XmppTool.getConnection() != null
								&& XmppTool.getConnection().isConnected()) {
							XmppTool.getConnection().addConnectionListener(
									listener);
							if (!SharedPreferencesUtil.getUserPhone()
									.equals("")) {
								loginOpenfire(SharedPreferencesUtil
										.getUserPhone());
							}
						}
						isInit = false;
					};
				}.start();
		}
	}

	/**
	 * 登录openfire
	 * 
	 * @param username
	 */
	public void loginOpenfire(final String username) {

		if (XmppTool.getConnection() != null && isLogin == false
				&& XmppTool.getConnection().isConnected()) {

			new Thread() {
				@SuppressWarnings("static-access")
				public void run() {
					try {
						XmppTool.getConnection().login(username,
								username + PASSWORD);
						isLogin = true;
						Log.i(TAG, username + "登录openfire成功!");
					} catch (XMPPException e) {

						Log.e(TAG, username + "第一次尝试登录失败! " + e.getMessage());
						final AccountManager accountManager = XmppTool
								.getConnection().getAccountManager();
						try {
							accountManager.createAccount(username, username
									+ PASSWORD);
							Log.i(TAG, username + "注册openfire成功!");

							try {
								// 这里休眠，和断开链接是为了注册和能成功登入openfire
								XmppTool.closeConnection();
								this.sleep(10000);
							} catch (InterruptedException e1) {

								e1.printStackTrace();
							}
							XmppTool.getConnection().login(username,
									username + PASSWORD);
							isLogin = true;
							Log.i(TAG, username + "登录openfire成功!");
						} catch (XMPPException e1) {

							Log.e(TAG,
									username + "注册或登录openfire失败!"
											+ e1.getMessage());
							// 尝试更改密码
							try {
								accountManager.changePassword(username
										+ PASSWORD);
								Log.i(TAG, username + "更改密码成功!");
							} catch (XMPPException e2) {
								Log.e(TAG, username + "更改密码失败!");
								e2.printStackTrace();
							}
						}
					}

					if (isLogin) {

						ChatManager cm = XmppTool.getConnection()
								.getChatManager();
						cm.addChatListener(chatListener);
					}

					isInit = false;

				};
			}.start();

		}
	}

	/**
	 * 通知栏显示信息
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param cls
	 */
	public void notify(String arg0, String arg1, String arg2, Class<?> cls) {

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		Intent i = new Intent(PushService.this, cls);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentTitle(arg0).setContentIntent(pendingIntent)
				.setTicker(arg1).setSmallIcon(R.drawable.ic_launcher)
				.setContentInfo(arg2);
		mBuilder.setDefaults(Notification.DEFAULT_VIBRATE
				| Notification.DEFAULT_SOUND);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.sound = Uri.parse("android.resource://" +
		// getPackageName() + "/" +R.raw.bee);
		nm.notify(NOTIFY_ID++, notification);
	}

	/**
	 * 清除所有通知栏
	 */
	public void canclaNotify() {

		if (nm != null)
			nm.cancelAll();
	}

	class PushServiceConnectionListener implements ConnectionListener {

		@Override
		public void connectionClosed() {
			isLogin = false;
			isInit = false;
			Log.i(TAG, "connectionClosed");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			isLogin = false;
			isInit = false;
			Log.i(TAG, "connectionClosedOnError:" + arg0.getMessage());
			// 如果是冲突，也就是别的设备进行了登陆，那么，就不要在登陆了
			if (!arg0.getMessage().contains("conflict"))
				init();
		}

		@Override
		public void reconnectingIn(int arg0) {

			Log.i(TAG, "reconnectingIn:" + arg0);
		}

		@Override
		public void reconnectionFailed(Exception arg0) {

			Log.i(TAG, "reconnectionFailed:" + arg0);
		}

		@Override
		public void reconnectionSuccessful() {

			Log.i(TAG, "reconnectionSuccessful");
		}

	}

	class PushServiceChatListener implements ChatManagerListener {

		@Override
		public void chatCreated(Chat chat, boolean arg1) {

			chat.addMessageListener(new MessageListener() {

				@Override
				public void processMessage(Chat chat1, Message msg) {

					Log.i(TAG, "收到来自openfire的消息" + msg.toString());
					Log.i(TAG, "body=" + msg.getBody());
					Log.i(TAG, "from=" + msg.getFrom());
					Log.i(TAG, "subject=" + msg.getSubject());
					Log.i(TAG, "to=" + msg.getTo());
					Log.i(TAG, "packetid=" + msg.getPacketID());
					PushService.this.notify(msg.getSubject(), msg.getBody(),
							msg.getBody(), MainActivity.class);
				}
			});
		}

	}

	public class PushBinder extends Binder {

		public PushService getPushService() {

			return PushService.this;
		}
	}

}
