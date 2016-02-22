package com.lingshi321.bee.common;

import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.lingshi321.bee.util.SharedPreferencesUtil;

public class HttpClientSingleton {

	private static class HttpClientInstance{
		private static final DefaultHttpClient HTTP_CLIENT = new DefaultHttpClient();
	}
	
	public static DefaultHttpClient getHttpClient(){
		return HttpClientInstance.HTTP_CLIENT;
	}
	
	private HttpClientSingleton(){
		
	}
	
//	public static void initialHttpClient(){
//		BasicCookieStore cookieStore = new BasicCookieStore();
//		String jsession = SharedPreferencesUtil.getSessionId();
//		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", jsession);
//		cookieStore.addCookie(cookie);
//		HttpClientInstance.HTTP_CLIENT.setCookieStore(cookieStore);
//	}
}
