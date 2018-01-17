package com.android.mplayer.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * ���ڹ����࣬������ȡ������Ļ����
 * */
public class WindowUtils {

	private static int width;
	private static int height;
   /**
    * ��ȡ�豸��Ļ���
    * @author JIANGPENG
    * @param context ������
    * @return int
    * */
	public static int getWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		return width;
	}
	
	
	 /**
	    * ��ȡ�豸��Ļ�߶�
	    * @author JIANGPENG
	    * @param context ������
	    * @return int
	    * */
	public static int getHeight(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		height = metric.heightPixels;
		return height;
	}

}
