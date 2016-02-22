package com.lingshi321.bee.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	public final static String USER_INFO = "user_info";
	private final static String SESSION_INFO = "session_info";
	public static Context context;
	
	public static void setSessionId(String sessionId){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(SESSION_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.SESSION, sessionId);
		mEditor.commit();
	}
	
	public static void setUserName(String userName){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.USERNAME, userName);
		mEditor.commit();
	}
	public static void setUserPhone(String userPhone){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.USERPHONE, userPhone);
		mEditor.commit();
	}
	public static void setPhone(String phone){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.PHONE, phone);
		mEditor.commit();
	}
	public static void setStaffId(String staffId){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.STAFF_ID, staffId);
		mEditor.commit();
	}
	public static void setPassword(String password){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.PASSWORD, password);
		mEditor.commit();
	}
	public static void setUserEmail(String userEmail){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.USEREMAIL, userEmail);
		mEditor.commit();
	}
	public static void setUserJoinTime(String userJoinTime){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.USERJOINTIME, userJoinTime);
		mEditor.commit();
	}
	public static void setUserManagerPart(String userManagerPart){
		SharedPreferences.Editor mEditor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
		mEditor.putString(DataInterfaceConstants.USERMANAGERPART, userManagerPart);
		mEditor.commit();
	}
	
	public static String getSessionId(){
		SharedPreferences mPreferences= context.getSharedPreferences(SESSION_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.SESSION, "");
	}
	
	public static String getUserName(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.USERNAME, "");
	}
	public static String getUserPhone(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.USERPHONE, "");
	}
	public static String getUserEmail(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.USEREMAIL, "");
	}
	public static String getUserJoinTime(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.USERJOINTIME, "");
	}
	public static String getUserManagerPart(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.USERMANAGERPART, "");
	}
	public static String getPhone(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.PHONE, "");
	}
	public static String getPassword(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.PASSWORD, "");
	}
	public static String getStaffId(){
		SharedPreferences mPreferences= context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		return mPreferences.getString(DataInterfaceConstants.STAFF_ID, "");
	}
}
