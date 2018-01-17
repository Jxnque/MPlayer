package com.android.mplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * ���繤���࣬���жϸ�����������״̬
 * @author JIANGPENG
 * */
public class NetUtil {
	/**
	 * �ж��Ƿ����������� 
	 * @author JIANGPENG
	 * */
	public static  boolean isNetworkConnected(Context context) { 
		if (context != null) { 
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
		.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null) { 
		return mNetworkInfo.isAvailable(); 
		} 
		} 
		return false; 
		}

	/**
	 * �ж�WIFI�����Ƿ���� 
	 * @author JIANGPENG
	 * */
	public static boolean isWifiConnected(Context context) { 
		if (context != null) { 
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
		.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
		.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		if (mWiFiNetworkInfo != null) { 
		   return mWiFiNetworkInfo.isAvailable(); 
		  } 
		 }  
		  return false; 
		}

	/**
	 * �ж�MOBILE�����Ƿ����
	 * @author JIANGPENG
	 * */
	public static boolean isMobileConnected(Context context) { 
	 if (context != null) { 
	   ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	   .getSystemService(Context.CONNECTIVITY_SERVICE); 
	   NetworkInfo mMobileNetworkInfo = mConnectivityManager 
	   .getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
	   if (mMobileNetworkInfo != null) { 
	     return mMobileNetworkInfo.isAvailable(); 
	  } 
	 } 
	  return false; 
	}

	/**
	 * ��ȡ��ǰ�������ӵ�������Ϣ
	 * @author JIANGPENG
	 * */
	public static int getConnectedType(Context context) { 
	  if (context != null) { 
	    ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    .getSystemService(Context.CONNECTIVITY_SERVICE); 
     	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
    	if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
    	   return mNetworkInfo.getType(); 
	   } 
	  } 
	  return -1; 
	 }

}
