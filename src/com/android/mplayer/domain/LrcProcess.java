package com.android.mplayer.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.mplayer.R;
import com.android.mplayer.constants.AppStateConstant;
import com.android.mplayer.utils.NetUtil;
import com.android.mplayer.utils.SearchLrcUtil;



/**
 *	�����ʵ���
 */
public class LrcProcess {
	private List<LrcInfo> lrcList;	//List���ϴ�Ÿ�����ݶ���
	private LrcInfo mLrcContent;		//����һ��������ݶ���
	private Context context;
	/**
	 * �޲ι��캯������ʵ��������
	 */
	public LrcProcess(Context context) {
		lrcList = new ArrayList<LrcInfo>();
		this.context = context;
	}
	
	/**
	 * ��ȡ����ָ��.lrc���
	 * @param path
	 * @return
	 */
	public String readLRC(String path) {
		//����һ��StringBuilder����������Ÿ������
		StringBuilder stringBuilder = new StringBuilder();
		File f = new File(path.replace(".mp3", ".lrc"));
		
		try {
			//����һ���ļ�����������
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while((s = br.readLine()) != null) {
				//�滻�ַ�
				s = s.replace("[", "");
				s = s.replace("]", "@");
				
				//���롰@���ַ�
				String splitLrcData[] = s.split("@");
				if(splitLrcData.length > 1) {
					//�´���������ݶ���
					mLrcContent = new LrcInfo();
					
					mLrcContent.setLrcStr(splitLrcData[1]);
					
					//������ȡ�ø�����ʱ��
					int lrcTime = time2Str(splitLrcData[0]);
					
					mLrcContent.setLrcTime(lrcTime);
					
					//��ӽ��б�����
					lrcList.add(mLrcContent);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			stringBuilder.append("ľ�и���ļ����Ͻ�ȥ���أ�...");
		} catch (IOException e) {
			e.printStackTrace();
			stringBuilder.append("ľ�ж�ȡ�����Ŷ��");
		}
		return stringBuilder.toString();
	}
	
	/**
	 * ��ȡ����lrc��ʣ���û���������������
	 * @param path
	 * @return
	 */
	public int readLRC(Mp3Info mp3) {
		//����һ��StringBuilder����������Ÿ������
		File f ;
		String title = mp3.getTitle();
		String artist = mp3.getArtist();
		String displayName = mp3.getDisplayName().split("\\.")[0];
		
		f = new File(AppStateConstant.APP_PATH+"lrc/"+title+"-"+artist+".lrc");
		if(f.exists()){
			parseLrc(f);
			return 1;
		}else{
			f = new File(AppStateConstant.APP_PATH+"lrc/"+displayName+".lrc");
		    if(f.exists()){
		    	parseLrc(f);
		    	return 1;
		    }else{
		    	if(!NetUtil.isNetworkConnected(context)){
	                Toast.makeText(context, context.getString(R.string.net_not_use), Toast.LENGTH_SHORT).show();     				
				}else{
					downLoadLrc(title,artist);
				} 
		    }
		}
		return 0;	
//			  new Thread(new Runnable() {//�½��߳̿����������
//				@Override
//				public void run() {
//					boolean flag = new SearchLrcUtil(title, artist,context).saveLrcInScard(title+"-"+artist+".lrc");
//				    if(flag){
//				    	Intent intent = new Intent();
//				    	intent.setAction("PLAY_SERVICE_ACTION");
//					    intent.putExtra("MSG", AppStateConstant.INIT_LRC_AGAIN);
//					    context.sendBroadcast(intent);
//				    }else{
//				    	Intent intent = new Intent();
//				    	intent.setAction("PLAY_SERVICE_ACTION");
//					    intent.putExtra("MSG", AppStateConstant.LRC_NOT_FOUND);
//					    context.sendBroadcast(intent);
//				    }
//				}
//			  }).start();
			
	}
	
	
	/**
	 * ���������������������ϻ�ȡ���
	 * �Ը�������������Ϊ�ļ���
	 * */
    public void downLoadLrc(final String title,final String artist){
    	new Thread(new Runnable() {//�½��߳̿����������
			@Override
			public void run() {
				boolean flag = new SearchLrcUtil(title, artist,context).saveLrcInScard(title+"-"+artist+".lrc");
			    if(flag){
			    	Intent intent = new Intent();
			    	intent.setAction("PLAY_SERVICE_ACTION");
				    intent.putExtra("MSG", AppStateConstant.INIT_LRC_AGAIN);
				    context.sendBroadcast(intent);
			    }else{
			    	Intent intent = new Intent();
			    	intent.setAction("PLAY_SERVICE_ACTION");
				    intent.putExtra("MSG", AppStateConstant.LRC_NOT_FOUND);
				    context.sendBroadcast(intent);
			    }
			}
		  }).start();
    }
	
	
	/**
	 * ���ļ�����������
	 * */
	
	public void parseLrc(File f){
		 try {
			//����һ���ļ�����������
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while((s = br.readLine()) != null) {
				//�滻�ַ�
				s = s.replace("[", "");
				s = s.replace("]", "@");
				//���롰@���ַ�
				String splitLrcData[] = s.split("@");
				if(splitLrcData.length > 1) {
					for(int i = 0;i < splitLrcData.length-1;i++){
					//�´���������ݶ���
					mLrcContent = new LrcInfo();
					//������ȡ�ø�����ʱ��
					int lrcTime = time2Str(splitLrcData[i]);
					mLrcContent.setLrcTime(lrcTime);
					mLrcContent.setLrcStr(splitLrcData[splitLrcData.length-1].replace(" ","\n"));
					//��ӽ��б�����
					lrcList.add(mLrcContent);
					}
				}
			}
			for(int i = 0;i<lrcList.size()-1;i++)//�Ը�ʰ�ʱ���������
				for(int j = i+1;j<lrcList.size();j++){
					if(lrcList.get(i).getLrcTime()>lrcList.get(j).getLrcTime()){
						mLrcContent = lrcList.get(i);
						lrcList.set(i, lrcList.get(j));
						lrcList.set(j, mLrcContent);
					}
				}
			  } catch (FileNotFoundException e) {
			    e.printStackTrace();
			  } catch (IOException e) {
				e.printStackTrace();
			  }
	}
	
	
	/**
	 * �������ʱ��
	 * ������ݸ�ʽ���£�
	 * [00:02.32]����Ѹ
	 * [00:03.43]�þò���
	 * [00:05.22]�������  ����
	 * @param timeStr
	 * @return
	 */
	public int time2Str(String timeStr) {
		int minute = 0,second = 0,millisecond = 0;
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");
		
		String timeData[] = timeStr.split("@");	//��ʱ��ָ����ַ�������
		
		if(timeData.length == 3){
			//������֡��벢ת��Ϊ����
		   minute = Integer.parseInt(timeData[0]);
		   second = Integer.parseInt(timeData[1]);
		   millisecond = Integer.parseInt(timeData[2]);
		}else if(timeData.length == 2){
		   minute = Integer.parseInt(timeData[0]);
		   second = Integer.parseInt(timeData[1]);
		   millisecond = 0;
		}
		
		//������һ������һ�е�ʱ��ת��Ϊ������
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}
	
	public List<LrcInfo> getLrcList() {
		return lrcList;
	}
}
