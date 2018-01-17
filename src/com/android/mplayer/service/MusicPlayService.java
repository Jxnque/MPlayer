package com.android.mplayer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.mplayer.R;
import com.android.mplayer.constants.AppStateConstant;
import com.android.mplayer.dao.AlbumArtDao;
import com.android.mplayer.dao.MusicDao;
import com.android.mplayer.domain.LrcInfo;
import com.android.mplayer.domain.LrcProcess;
import com.android.mplayer.domain.Mp3Info;

/**
 * ������������  Ӧ�ú��Ĳ���
 * @author JIANGPENG
 * */
public class MusicPlayService extends Service{

	private MediaPlayer mediaPlayer; // ý�岥��������
	private String path; 			// �����ļ�·��
	private boolean isPause = false; 		// ��ͣ״̬
	public static boolean IS_FIRST_PLAY = true;
	public static boolean IS_PLAYING_MSG = false;  
	private int current  = 0; 		// ��¼��ǰ���ڲ��ŵ�����
	private int old = 0;
	private List<Mp3Info> mp3Infos;	//���Mp3Info����ļ���
	private int collectId ;  //���ϵ�Id
	private String collectType ;
	private int status = 3;			//����״̬��Ĭ��Ϊ˳�򲥷�
	private MyServiceReceiver myReceiver;	//�Զ���㲥������
	private int currentTime;		//��ǰ���Ž���
	private int duration;			//���ų���
	private LrcProcess mLrcProcess;	//��ʴ���
	private List<LrcInfo> lrcList = new ArrayList<LrcInfo>(); //��Ÿ���б����
	private int index = 0;			//��ʼ���ֵ
	private Notification notification; //֪ͨ
	private NotificationManager notificationManager; //֪ͨ������
	/**
	 * handler�������շ����ڲ���Ϣ���ú���Ϣ�Թ㲥��ʽ֪ͨ�������
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int message = msg.what;
			switch (message) {
			//���µ�ǰ���ŵĸ���
			case AppStateConstant.UPDATE_CURRENT_INFO_MSG:
				if(mediaPlayer != null) {
					Intent intent = new Intent();
					intent.setAction("MUSIC_SERVICE_UPDATEUI_RECEIVER");
					if(old!=current){
						intent.putExtra("change",true);
						old=current;
					}else{
						intent.putExtra("change",false);
					}
					intent.putExtra("mp3Info",mp3Infos.get(current));
					sendBroadcast(intent);
				}
				break;
			//��ʼ��С����̨
			case AppStateConstant.INIT_BOTTOM_INFO_MSG:
				if(mediaPlayer != null && !IS_FIRST_PLAY) {
					Intent intent = new Intent();
					intent.setAction("MUSIC_SERVICE_INIT_BOTTOM_RECEIVER");
					intent.putExtra("mp3Info",mp3Infos.get(current));
					sendBroadcast(intent);
				}
				break;
			//���µ�ǰ��������
			case AppStateConstant.INIT_CURRENT_SONG_INFO_MSG:
				Intent intent = new Intent();
				intent.setAction("MUSIC_SERVICE_INIT_CURRENT_SONG_RECEIVER");
				intent.putExtra("collectType",collectType);
				intent.putExtra("collectId",collectId);
				sendBroadcast(intent);
				break;
			default:
				break;
			}
		
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		mediaPlayer = new MediaPlayer();
		//Ĭ�ϼ�������������и���
		mp3Infos = MusicDao.getAllMp3Infos(MusicPlayService.this);
		collectId = -1000; //-1000������������
		collectType = AppStateConstant.IS_ALLSONG_ID;
		
		//�������ֲ������ʱ�ļ�����
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (status == 2) { // ����ѭ��
					mediaPlayer.start();
				} else if (status == 3) { // ȫ��ѭ��
					current++;
					if(current > mp3Infos.size() - 1) {	//��Ϊ��һ�׵�λ�ü�������
						current = 0;
					}
					play(0);
				} else if (status == 1) { // ˳�򲥷�
					current++;	//��һ��λ��
					if (current <= mp3Infos.size() - 1) {
						play(0);
					}else {
						mediaPlayer.seekTo(0);
						current = 0;
					}
				} else if(status == 4) {	//�������
					current = getRandomIndex(mp3Infos.size() - 1);
					play(0);
				}
			}
		});
        
		//ע��㲥
		myReceiver = new MyServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("PLAY_SERVICE_ACTION");
		registerReceiver(myReceiver, filter);
		
		
		//������Ϣ��
		showNotification();
		
	}

	/**
	 * ��ȡ���λ��
	 * @param end
	 * @return
	 */
	protected int getRandomIndex(int end) {
		int index = (int) (Math.random() * end);
		return index;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		  super.onStart(intent, startId);
	}

	/**
	 * ��ʼ���������
	 */
	public void initLrc(Mp3Info mp3){
		  mLrcProcess = new LrcProcess(getApplicationContext());
		  //��ȡ����ļ�
		  mLrcProcess.readLRC(mp3);
		  //���ش����ĸ���ļ�
		  lrcList = mLrcProcess.getLrcList();
		  Intent intent = new Intent();
		  intent.putExtra("mp3", mp3);
		  intent.setAction("MUSIC_SERVICE_INIT_LRC_FOR_CURRENT_SONG");
		  sendBroadcast(intent);
	}
	
	/**
	 * �ý������ڸ��½���������ʾ���
	 * */
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setAction("MUSIC_SERVICE_SEEKBAR_LRC_UPDATE");
			int time = mediaPlayer.getCurrentPosition();
			intent.putExtra("time", time);
			intent.putExtra("lrcIndex", lrcIndex());
			sendBroadcast(intent);
			handler.postDelayed(mRunnable, 100);
		}
	};
	
	/**
	 * ����ʱ���ȡ�����ʾ������ֵ
	 * @return
	 */
	public int lrcIndex() {
		if(mediaPlayer.isPlaying()) {
			currentTime = mediaPlayer.getCurrentPosition();
			duration = mediaPlayer.getDuration();
		}
		if(currentTime < duration) {
			for (int i = 0; i < lrcList.size(); i++) {
				if (i < lrcList.size() - 1) {
					if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
						index = i;
					}
					if (currentTime > lrcList.get(i).getLrcTime()
							&& currentTime < lrcList.get(i + 1).getLrcTime()) {
						index = i;
					}
				}
				if (i == lrcList.size() - 1
						&& currentTime > lrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}
	
	/**
	 * ��������
	 * @param position
	 */
	private void play(int currentTime) {
		try {
			path = mp3Infos.get(current).getUrl();
			initLrc(mp3Infos.get(current));
			handler.post(mRunnable);
			mediaPlayer.reset();// �Ѹ�������ָ�����ʼ״̬
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare(); // ���л���
			mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// ע��һ��������
			isPause = false;
			IS_PLAYING_MSG = true;
			IS_FIRST_PLAY = false;
			Message msg = new Message();
			msg.what = AppStateConstant.UPDATE_CURRENT_INFO_MSG;
			handler.sendMessage(msg);
			updateNotification(current);
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * �϶��������ǵ��õĺ�������play��ʡ��Դ���ɷ�ֹseekBar��˸
     * */
	private void seekToPlay(int currentTime) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * ��ͣ����
	 */
	private void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			IS_PLAYING_MSG = false;
			isPause = true;
			updateNotification();
			Message msg = new Message();
			msg.what = AppStateConstant.UPDATE_CURRENT_INFO_MSG;
			handler.sendMessage(msg);
		}
	}
   
	/**
	 * ���¿�ʼ����
	 * */
	private void resume() {
		if (isPause) {
			mediaPlayer.start();
			IS_PLAYING_MSG = true;
			isPause = false;
			updateNotification();
			Message msg = new Message();
			msg.what = AppStateConstant.UPDATE_CURRENT_INFO_MSG;
			handler.sendMessage(msg);
		}
	}

	/**
	 * ��һ��
	 */
	private void previous() {
		current--;
		if(current == -1)
			current = mp3Infos.size() - 1;
		path = mp3Infos.get(current).getUrl();
		play(0);
	}

	/**
	 * ��һ��
	 */
	private void next() {
        current++;
        if(current == mp3Infos.size())
			current = 0;
        path = mp3Infos.get(current).getUrl();
		play(0);
	}

	/**
	 * ֹͣ����
	 */
	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			IS_PLAYING_MSG = false;
			updateNotification();
			try {
				mediaPlayer.prepare(); // �ڵ���stop�������Ҫ�ٴ�ͨ��start���в���,��Ҫ֮ǰ����prepare����
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
	  if (mediaPlayer != null) {//�ͷ�ý����Դ
			mediaPlayer.stop();
			mediaPlayer.release();
			IS_PLAYING_MSG = false;
			mediaPlayer = null;
			handler.removeCallbacks(mRunnable);
			mp3Infos = null;
			lrcList = null;
		}
	  notificationManager.cancel(0);
	  super.onDestroy();
	}

	/**
	 * ʵ��һ��OnPrepareLister�ӿ�,������׼���õ�ʱ��ʼ����
	 */
	private final class PreparedListener implements OnPreparedListener {
		private int currentTime;

		public PreparedListener(int currentTime) {
			this.currentTime = currentTime;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start(); // ��ʼ����
			if (currentTime > 0) { // ������ֲ��Ǵ�ͷ����
				mediaPlayer.seekTo(currentTime);
			}
		}
	}
	
	/**
	 * �������ڲ��㲥�����ܽ��淢�͹�����ָ��
	 * */

	public class MyServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int msg = intent.getIntExtra("MSG", 0);	
			Message message = new Message();
			switch (msg) {
			  case AppStateConstant.PLAY_MODE:
				int control = intent.getIntExtra("playMode", 3);
				switch (control) {
				case 1:
					status = 1; // ������״̬��Ϊ1��ʾ��˳�򲥷�
					break;
				case 2:
					status = 2;	//������״̬��Ϊ2��ʾ������ѭ��
					break;
				case 3:
					status = 3;	//������״̬��Ϊ3��ʾ��
					break;
				case 4:
					status = 4;	//������״̬��Ϊ4��ʾ���������
					break;
			    default:
			    	status = 3;	//������״̬��Ϊ3��ʾ��ȫ��ѭ�� 
					break;
				}
				break;
				
			  case AppStateConstant.PLAY_MSG://ֱ�Ӳ�������
					  current = intent.getIntExtra("listPosition", 0);	//��ǰ���Ÿ�������mp3Infos��λ��
					  String type = intent.getStringExtra("collectType");
					  int id = intent.getIntExtra("collectId", -1000);//Ĭ��Ϊ��������
					  changeCollect(type,id);
					  play(0);
			    break;
			  case AppStateConstant.PLAY_CURRENT_MSG://���ŵ�ǰ����
					  if(isPause){
						resume();
					  }else{
						//ֱ�Ӳ��ŵ�ǰ��������ĵ�һ������
					    play(0);
					  }
                break;
			  case AppStateConstant.PAUSE_MSG:	//��ͣ
				   pause();	
				   break; 	
			  case AppStateConstant.STOP_MSG:		//ֹͣ
				   stop();
				   break;
			  case AppStateConstant.CONTINUE_MSG:	//��������
				   resume();
				   break;
			  case AppStateConstant.PRIVIOUS_MSG:	//��һ��
				   previous();
				   break;
			  case AppStateConstant.NEXT_MSG:		//��һ��
				   next();
				   break;
			  case AppStateConstant.PROGRESS_CHANGE:	//���ȸ���
				   currentTime = intent.getIntExtra("jumppoint", 0);
				   seekToPlay(currentTime);
				   break; 	  
			  case AppStateConstant.PLAYING_MSG:  
				   handler.sendEmptyMessage(1);
				   break;
			  case AppStateConstant.INIT_BOTTOM_CALL_BACK_INFO://��ʼ��С����̨
				   message.what = AppStateConstant.INIT_BOTTOM_INFO_MSG;
				   handler.sendMessage(message);
				   break;
			  case AppStateConstant.INIT_CURRENT_SONG_CALL_BACK_INFO:
				    message.what = AppStateConstant.INIT_CURRENT_SONG_INFO_MSG;
					handler.sendMessage(message);
				   break;
			  case AppStateConstant.INIT_LRC_AGAIN://�����������ɺ�ڶ��γ�ʼ�����
				   initLrc(mp3Infos.get(current));
				   break;
			  case AppStateConstant.LRC_NOT_FOUND://���û���� ��ӡ��˾
				  Toast.makeText(context, getString(R.string.not_found_lrc), Toast.LENGTH_SHORT).show(); 
				   break;
			  case AppStateConstant.EXIT_APPLICATION://�˳�Ӧ��
				   notificationManager.cancel(0);
				   ExitApplicationService.getInstance().exit();
				   break;
			default:
				break;
			}
		}
		
		/**
		 * �ı��������
		 * @author JIANGPENG
		 * @param type ��������
		 * @param id ����ID
		 * */
		private void changeCollect(String type, int id){
			if(type.equals(collectType) && id == collectId){//���͵��ڼ���һ�� �����κβ���
				return;
			}else{
				if(type.equals(AppStateConstant.IS_ALLSONG_ID)){
					mp3Infos = MusicDao.getAllMp3Infos(getApplicationContext());
					collectType = type;
					collectId = id;
				}else if(type.equals(AppStateConstant.IS_ALBUM_ID)){
					mp3Infos = MusicDao.getMp3InfosByAlbumID(getApplicationContext(), id);
					collectType = type;
					collectId = id;
				}else if(type.equals(AppStateConstant.IS_ARTIST_ID)){
					mp3Infos = MusicDao.getMp3InfosByArtistID(getApplicationContext(), id);
					collectType = type;
					collectId = id;
				}else if(type.equals(AppStateConstant.IS_PLAYLIST_ID)){
					mp3Infos = MusicDao.getMp3InfosByPlayListId(getApplicationContext(), id);
					collectType = type;
					collectId = id;
				}
			}
		}
	}
       
     /**
      * ��ʾ֪ͨ��
      * @author JIANGPENG
      * */
	 private void showNotification() {
		    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    Mp3Info mp3Info = mp3Infos.get(0);
	        Bitmap b = AlbumArtDao.getArtwork(getApplicationContext(), 
	        		mp3Info.getId(), mp3Info.getAlbumId(), true, true);
	        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_small_layout);
	        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.notification_big_layout);	        
	        if (b != null) {
	            views.setViewVisibility(R.id.status_bar_icon, View.GONE);
	            views.setViewVisibility(R.id.status_bar_album_art, View.VISIBLE);
	            views.setImageViewBitmap(R.id.status_bar_album_art, b);
	            bigViews.setImageViewBitmap(R.id.status_bar_album_art, b);
	        } else {
	            views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
	            views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
	        }
	        
	        Intent intentNex = new Intent();
            intentNex.putExtra("MSG", AppStateConstant.NEXT_MSG);
            PendingIntent pendNex = PendingIntent.getBroadcast(getApplicationContext(), 0, intentNex, 0); 
            bigViews.setOnClickPendingIntent(R.id.status_bar_next, pendNex);
            views.setOnClickPendingIntent(R.id.status_bar_next, pendNex);
	        
	        
	        Intent intentPre = new Intent();
	        intentPre.setAction("PLAY_SERVICE_ACTION");
	        intentPre.putExtra("MSG", AppStateConstant.PRIVIOUS_MSG);
            PendingIntent pendPre = PendingIntent.getBroadcast(getApplicationContext(), 1, intentPre, 1); 
            bigViews.setOnClickPendingIntent(R.id.status_bar_prev, pendPre);
	        
           
           
            Intent intentPlay = new Intent();
            if(IS_PLAYING_MSG){
            	intentPlay.putExtra("MSG", AppStateConstant.PAUSE_MSG);
  		    }else{
  		    	intentPlay.putExtra("MSG", AppStateConstant.PLAY_CURRENT_MSG);
  		    }
            PendingIntent pendPlay = PendingIntent.getBroadcast(getApplicationContext(), 2, intentPlay, 2); 
            bigViews.setOnClickPendingIntent(R.id.status_bar_play, pendPlay);
            views.setOnClickPendingIntent(R.id.status_bar_play, pendPlay);
            
            Intent intentExit = new Intent();
            intentExit.putExtra("MSG",AppStateConstant.EXIT_APPLICATION);
            PendingIntent pendExit = PendingIntent.getBroadcast(getApplicationContext(), 3, intentExit, 3); 
            bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pendExit);
            views.setOnClickPendingIntent(R.id.status_bar_collapse, pendExit);
            
            
	        views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
	        bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);

	        views.setTextViewText(R.id.status_bar_track_name, mp3Info.getDisplayName());
	        bigViews.setTextViewText(R.id.status_bar_track_name, mp3Info.getDisplayName());
	        
	        views.setTextViewText(R.id.status_bar_artist_name, mp3Info.getArtist());
	        bigViews.setTextViewText(R.id.status_bar_artist_name, mp3Info.getArtist());
	        
	        bigViews.setTextViewText(R.id.status_bar_album_name, mp3Info.getAlbum());
	        
	        notification = new Notification.Builder(this).build();
	        notification.contentView = views;
	        notification.bigContentView = bigViews;
	       // notification.flags = Notification.;
	        notification.icon = R.drawable.stat_notify_music;
	        notification.contentIntent = PendingIntent
	                .getActivity(this, 0, new Intent("com.android.mplayer.MainPlayerActivity")
	                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
	        notificationManager.notify(0, notification);
	        startForeground(0, notification);
	    }
	    
	
	  /**
      * ����֪ͨ��
      * @author JIANGPENG
      * */
	public void updateNotification(int current){
		notification.contentView.setImageViewBitmap(R.id.status_bar_album_art,
      		  AlbumArtDao.getArtwork(getApplicationContext(), 
      				  mp3Infos.get(0).getId(), mp3Infos.get(current).getAlbumId(), true, true) );  
		notification.contentView.setTextViewText(R.id.status_bar_track_name,mp3Infos.get(current).getDisplayName());
		notification.contentView.setTextViewText(R.id.status_bar_artist_name,mp3Infos.get(current).getArtist());
		notification.contentView.setTextViewText(R.id.status_bar_album_name,mp3Infos.get(current).getAlbum());
		notification.bigContentView.setImageViewBitmap(R.id.status_bar_album_art,
	      		  AlbumArtDao.getArtwork(getApplicationContext(), 
	      				  mp3Infos.get(0).getId(), mp3Infos.get(current).getAlbumId(), true, true) );  
	    notification.bigContentView.setTextViewText(R.id.status_bar_track_name,mp3Infos.get(current).getDisplayName());
	    notification.bigContentView.setTextViewText(R.id.status_bar_artist_name,mp3Infos.get(current).getArtist());
	    notification.bigContentView.setTextViewText(R.id.status_bar_album_name,mp3Infos.get(current).getAlbum());
		
	    if(IS_PLAYING_MSG){
	    	notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
	    	notification.bigContentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
		}else{
			notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
	    	notification.bigContentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
		}
	    notificationManager.notify(0, notification);  
	}
	
	 /**
     * ����֪ͨ��
     * @author JIANGPENG
     * */
	public void updateNotification(){
	    if(IS_PLAYING_MSG){
	    	notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
	    	notification.bigContentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
		}else{
			notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
	    	notification.bigContentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
		}
	    notificationManager.notify(0, notification);  
	}
}
