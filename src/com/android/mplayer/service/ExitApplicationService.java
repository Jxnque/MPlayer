package com.android.mplayer.service;

 import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * Ӧ���˳�����
 * @author JIANGPENG
 * */
 public class ExitApplicationService extends Application {
 

 private List<Activity> activityList=new LinkedList<Activity>();
 
 private static ExitApplicationService instance;

   private ExitApplicationService(){
  
   }
   /**
    * ����ģʽ�л�ȡΨһ��ExitApplication ʵ��
    * @author JIANGPENG
    * */
  public static ExitApplicationService getInstance(){
    if(null == instance){
      instance = new ExitApplicationService(); 
     }
     return instance;
   }
  
  /**
   * ���Activity ��������
   * @author JIANGPENG
   * */
    public void addActivity(Activity activity){
     activityList.add(activity);
   }
    
    /**
     * ��Activity ���������Ƴ�
     * @author JIANGPENG
     * */
    public void removeActivity(Activity activity){
        activityList.remove(activity);
      }
    
    /**
     * �˳�Ӧ��
     * @author JIANGPENG
     * */
   public void exit(){
	     for(int i = 0;i < activityList.size() ;i++){
		  Activity activity = activityList.get(i);
		  activityList.set(i, null);
		  activity.finish();
	     } 
	     android.os.Process.killProcess(android.os.Process.myPid());
    }
 }
