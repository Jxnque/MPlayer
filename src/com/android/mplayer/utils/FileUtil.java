package com.android.mplayer.utils;

public class FileUtil {
	/**
	 * �ж��Ƿ����ⲿ�洢�豸
	 * @author JIANGPENG
	 * */
	public static boolean usesExternalStorage() {  
	 if (android.os.Environment.getExternalStorageState().equals(  
		    android.os.Environment.MEDIA_MOUNTED)) {  
	          return true;  
		  } else  
		   return false;  
		 } 
}
