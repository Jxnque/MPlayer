package com.android.mplayer;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mplayer.adapter.PlayerContentAdapter;
import com.android.mplayer.constants.AppStateConstant;
import com.android.mplayer.dao.AlbumArtDao;
import com.android.mplayer.dao.MusicDao;
import com.android.mplayer.domain.LrcInfo;
import com.android.mplayer.domain.LrcProcess;
import com.android.mplayer.domain.Mp3Info;
import com.android.mplayer.service.ExitApplicationService;
import com.android.mplayer.service.MusicPlayService;
import com.android.mplayer.utils.SearchLrcUtil;
import com.android.mplayer.view.MusicLrcView;
import com.android.mplayer.view.ProgressBarView;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Sample class illustrating how to add a menu drawer above the content area.
 */
public class MainPlayerActivity extends FragmentActivity implements OnClickListener,OnMenuItemClickListener {

	private MenuDrawer mMenuDrawer;
	private ViewPager viewPager;
	private Button bt_play_to_meun;
	private Button bt_playmode;
	private Button bt_playprevious;
	private Button bt_play;
	private Button bt_playnext;
	private ImageButton bt_play_to_current;
	private MainPlayerReceiver myReceiver;
	private TextView audio_player_music_name;
	private TextView audio_player_album_artist;
	private TextView tv_current_time;
	private TextView tv_total_time;
	private TextView tv_drawer_left;
	private TextView tv_drawer_right;
	private ProgressBarView progress_bar;
	private ImageView iv_music_ablum;
	public static MusicLrcView lrcView;
	private int lrcIndex ;
	private int playMode = 1;
	private Mp3Info mp3Info;
	private LrcProcess mLrcProcess;	//��ʴ���
	private List<LrcInfo> lrcList = new ArrayList<LrcInfo>(); //��Ÿ���б����
	private Boolean isTrackingTouch = false; 
	private Animation anim ;
    private LayoutInflater mInflater;
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.activity_main);
		initShowActivity();
		attachListeners();	
		
		myReceiver = new MainPlayerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("MUSIC_SERVICE_UPDATEUI_RECEIVER");
		filter.addAction("MUSIC_SERVICE_SEEKBAR_LRC_UPDATE");
		filter.addAction("MUSIC_SERVICE_INIT_LRC_FOR_CURRENT_SONG");
		registerReceiver(myReceiver, filter);
		
        ExitApplicationService.getInstance().addActivity(this);
        
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		 //�������濪������
        Intent intent = new Intent();
        intent.setAction("service.MusicPlayService");
        startService(intent);
	}
	
	/***
	 * ��ʼ���ؼ�
	 * */
	 private void initShowActivity(){
		anim = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
		mMenuDrawer = MenuDrawer.attach(this, Position.BOTTOM);
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(R.layout.drawer_bottom);
		
		PlayerContentAdapter adapter = new PlayerContentAdapter(getSupportFragmentManager());
		viewPager = (ViewPager)findViewById(R.id.player_viewpager);
		viewPager.setAdapter(adapter);

		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.player_indicator);
	    indicator.setViewPager(viewPager);
		
		audio_player_music_name = (TextView)findViewById(R.id.audio_player_music_name);
		audio_player_album_artist = (TextView)findViewById(R.id.audio_player_album_artist);
		iv_music_ablum = (ImageView)findViewById(R.id.iv_music_ablum);
		bt_playmode = (Button) findViewById(R.id.bt_playmode);
		bt_playprevious = (Button) findViewById(R.id.bt_playprevious);
		bt_play = (Button) findViewById(R.id.bt_play);
		bt_playnext = (Button) findViewById(R.id.bt_playnext);
		bt_play_to_meun = (Button)findViewById(R.id.bt_play_to_meun);
		bt_play_to_current = (ImageButton)findViewById(R.id.bt_play_to_current);
		tv_current_time = (TextView)findViewById(R.id.tv_current_time);
		tv_total_time = (TextView)findViewById(R.id.tv_total_time);
		lrcView = (MusicLrcView)findViewById(R.id.lrcShowView);
		progress_bar = (ProgressBarView)findViewById(R.id.progress_bar);
		tv_drawer_right = (TextView)findViewById(R.id.exit_app);
		tv_drawer_left = (TextView)findViewById(R.id.bind_lrc);
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	
	 /**
	  * ����¼�
	  * */
	private void attachListeners(){
		bt_playmode.setOnClickListener(this);
		bt_playprevious.setOnClickListener(this);
		bt_play.setOnClickListener(this);
		bt_playnext.setOnClickListener(this);
		bt_play_to_meun.setOnClickListener(this);
        bt_play_to_current.setOnClickListener(this);
        tv_drawer_left.setOnClickListener(this);
        tv_drawer_right.setOnClickListener(this);
      //�������϶��¼�
        progress_bar.setOnTouchListener(new OnTouchListener() {
  			
  			public boolean onTouch(View view, MotionEvent event) {
  				switch(event.getAction()){
  				case MotionEvent.ACTION_DOWN:
  					isTrackingTouch = true;
  					((ProgressBarView)view).setTouching(true);
  					((ProgressBarView)view).setTouchCoords(
  							event.getX(), 
  							event.getY());
  					try {
  						Thread.sleep(16);
  					} catch (InterruptedException e) {
  						e.printStackTrace();
  					}
  					break;
  				case MotionEvent.ACTION_MOVE:
  					int progress = ((ProgressBarView)view).getCoordSeekPosition(
  							event.getX(), 
  							event.getY());
  					tv_current_time.setText(MusicDao.formatTime(progress));
  					((ProgressBarView)view).setTouchCoords(
  							event.getX(), 
  							event.getY());
  					break;
  				case MotionEvent.ACTION_UP:
  					((ProgressBarView)view).setTouching(false);
  					((ProgressBarView)view).setTouchCoords(
  							event.getX(), 
  							event.getY());
  					if(!MusicPlayService.IS_FIRST_PLAY){
  						  Intent intent = new Intent();
  						  intent.setAction("PLAY_SERVICE_ACTION");
  						  int jumppoint = ((ProgressBarView)view).getCoordSeekPosition(
  									event.getX(), 
  									event.getY());
  					      intent.putExtra("MSG", AppStateConstant.PROGRESS_CHANGE);
  					      intent.putExtra("jumppoint", jumppoint);
  					      sendBroadcast(intent);
  					      isTrackingTouch = false;
  					}else{
  						((ProgressBarView)view).setProgress(0,true);
  						Toast.makeText(MainPlayerActivity.this, getString(R.string.seekBar_information), Toast.LENGTH_SHORT).show();
  					    isTrackingTouch = false;
  					}					
  					break;
  				}
  				
  				return true;
  			}
  		});
	}
	
	/**
	 * ��д���˼��¼�
	 * */
	private long firstTime = 0; 
	@Override  
	public boolean onKeyUp(int keyCode, KeyEvent event) {  
	       // TODO Auto-generated method stub  
       switch(keyCode){
	       case KeyEvent.KEYCODE_BACK:  
	            long secondTime = System.currentTimeMillis();   
	             if (secondTime - firstTime > 2000) {                                         //������ΰ���ʱ��������2�룬���˳�  
	                 Toast.makeText(this,  getString(R.string.exit_app_information), Toast.LENGTH_SHORT).show();   
	                 firstTime = secondTime;//����firstTime  
	                 return true;   
	             } else {   
	            	 //���ΰ���С��2��ʱ���˳�Ӧ��  
	            	 ExitApplicationService.getInstance().exit();
	             }   
	           break; 
	       default:
	           break;    
        }
	     return super.onKeyUp(keyCode, event);  
	   } 
  
	
	 public boolean  onCreateOptionsMenu(Menu menu){
	    	super.onCreateOptionsMenu(menu);

	    	String[] menuOptionsTitleArray = 
	    		getResources().
	    			getStringArray(R.array.menu_options_title_array);
	    	int[] menuOptionsIdxArray =
	    		getResources().
	    			getIntArray(R.array.menu_options_index_array);

	    	for(int i=0; i<menuOptionsTitleArray.length; i++){
	    		menu.add(
	    				0, // subgroup 
	    				menuOptionsIdxArray[i], // id 
	    				menuOptionsIdxArray[i], // order
	    				menuOptionsTitleArray[i]); // title
	    		menu.getItem(i).setOnMenuItemClickListener(this);
	    		if(menuOptionsTitleArray[i].equals(getString(R.string.meun_title1)))
	    			menu.getItem(i).setIcon(android.R.drawable.ic_menu_add);
	    		else if(menuOptionsTitleArray[i].equals(getString(R.string.meun_title2)))
	    			menu.getItem(i).setIcon(android.R.drawable.ic_menu_share);
	    		else if(menuOptionsTitleArray[i].equals(getString(R.string.meun_title3)))
	    			menu.getItem(i).setIcon(android.R.drawable.ic_menu_info_details);
	    		else if(menuOptionsTitleArray[i].equals(getString(R.string.meun_title4)))
	    			menu.getItem(i).setIcon(android.R.drawable.ic_lock_lock);
	    	}
	    	return true;
	    }
	
	
	/**
	 * ������ر�ʱֹͣ����
	 * */
	 @Override
	public void finish() {
		  Intent intent = new Intent(this,MusicPlayService.class);
		  stopService(intent);
		  super.finish();
	}
	
	@Override
	//��ť�����¼�
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt_play_to_current:
			intent.setClassName(this, "com.android.mplayer.CurrentPlayListActivity");
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		    break;
		case R.id.bt_play_to_meun:
			intent.setClassName(this, "com.android.mplayer.MusicListActivity");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
	    case R.id.bt_playmode:
	    	playMode++;
	    	Drawable drawable;
	    	if(playMode == 5)
	    		playMode = 1;
	    	if(playMode == 1){
	    		drawable = getResources().getDrawable(
	    				R.drawable.selector_play_mode_normal);
	    		bt_playmode.setBackgroundDrawable(drawable);
	    		Toast.makeText(this,getString(R.string.change_mode_normal), Toast.LENGTH_SHORT).show();
	    	}else if(playMode == 2){
	    		drawable = getResources().getDrawable(
	    				R.drawable.selector_play_mode_repeat_current);
	    		bt_playmode.setBackgroundDrawable(drawable);
	    		Toast.makeText(this,getString(R.string.change_mode_repeat_current), Toast.LENGTH_SHORT).show();
	    	}else if(playMode == 3){
	    		drawable = getResources().getDrawable(
	    				R.drawable.selector_play_mode_repeat_all);
	    		bt_playmode.setBackgroundDrawable(drawable);
	    		Toast.makeText(this,getString(R.string.change_mode_repeat_all), Toast.LENGTH_SHORT).show();
	    	}else if(playMode == 4){
	    		drawable = getResources().getDrawable(
	    				R.drawable.selector_play_mode_shuffle);
	    		bt_playmode.setBackgroundDrawable(drawable);
	    		Toast.makeText(this,getString(R.string.change_mode_shuffle), Toast.LENGTH_SHORT).show();
	    	}
	    	intent.setAction("PLAY_SERVICE_ACTION");
	    	intent.putExtra("MSG", AppStateConstant.PLAY_MODE);
		    intent.putExtra("playMode",playMode);
		    sendBroadcast(intent);
	    	break;
	    case R.id.bt_playprevious:
		    intent.setAction("PLAY_SERVICE_ACTION");
		    intent.putExtra("MSG", AppStateConstant.PRIVIOUS_MSG);
		    sendBroadcast(intent);
			break;
	    case R.id.bt_play:
		    intent.setAction("PLAY_SERVICE_ACTION");
		    if(MusicPlayService.IS_PLAYING_MSG){
		      intent.putExtra("MSG", AppStateConstant.PAUSE_MSG);
		      drawable = getResources().getDrawable(
						R.drawable.selector_play_normal);
		      bt_play.setBackgroundDrawable(drawable);
		    }else{
		      intent.putExtra("MSG", AppStateConstant.PLAY_CURRENT_MSG);
		      drawable = getResources().getDrawable(
						R.drawable.selector_play_pause);
		      bt_play.setBackgroundDrawable(drawable);
		    }
		    sendBroadcast(intent);
			break;
	    case R.id.bt_playnext:
		    intent.setAction("PLAY_SERVICE_ACTION");
		    intent.putExtra("MSG", AppStateConstant.NEXT_MSG);
		    sendBroadcast(intent);
			break;	
		case R.id.exit_app:
		   mMenuDrawer.setActiveView(v);
		   ExitApplicationService.getInstance().exit();
		   break;	
		case R.id.bind_lrc:
		   mMenuDrawer.setActiveView(v);
		   bindLrc();
		   break;	
		default:
			break;
		}
	}
	
	public boolean onMenuItemClick(MenuItem item) {
       int index = item.getItemId();
       switch (index) {
	     case 0:
	    	 bindLrc();    
		  break;
	     case 1:
	    	 Intent intent=new Intent(Intent.ACTION_SEND);   
	         intent.setType("text/plain");   
	         intent.putExtra(Intent.EXTRA_SUBJECT, "����");   
	         intent.putExtra(Intent.EXTRA_TEXT, "MPlayer��Ѿ�ĺ��ã���ȥ����   http://pan.baidu.com/s/1bnrLQ1L");    
	         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
	         startActivity(Intent.createChooser(intent, getTitle()));
	      break;
	     case 2:
	    	 View view = mInflater.inflate(R.layout.appinfo,null);
	    	 new AlertDialog.Builder(this)
	    	 .setView(view) 
	    	 .show();     
	      break;
	     case 3:
	    	 ExitApplicationService.getInstance().exit();
	      break;

     	default:
		   break;
	    }
		
		return true;
	}
	
	
	/**
	 * �󶨸��
	 * */
	public void bindLrc() {
		if(mp3Info == null){
			return;
		}
		View view = mInflater.inflate(R.layout.bind_lrc_dialog, null);
	    final EditText musicName = (EditText)view.findViewById(R.id.bind_music_name);
	    final EditText singerName = (EditText)view.findViewById(R.id.bind_singer_name);
	    final String fileName = mp3Info.getDisplayName();
		new AlertDialog.Builder(this)  
		.setTitle(this.getString(R.string.bind_lrc))  
		.setView(view)  
        .setPositiveButton(this.getString(R.string.bt_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String title = musicName.getText().toString().trim();
				String artist = singerName.getText().toString().trim();
				downLoadLrc(title,artist,fileName); 
			}
		})
        .setNeutralButton(this.getString(R.string.bt_cancel), null)
		.show();  
	}

	/**
	 * ���û������������������������ϻ�ȡ���
	 * ��displayNameΪ�ļ���
	 * */
	  public void downLoadLrc(final String title,final String artist,final String displayName){
	    	new Thread(new Runnable() {//�½��߳̿����������
				@Override
				public void run() {
					boolean flag = new SearchLrcUtil(title, artist,MainPlayerActivity.this).saveLrcInScard(displayName.split("\\.")[0]+".lrc");
				    if(flag){
				    	Intent intent = new Intent();
				    	intent.setAction("PLAY_SERVICE_ACTION");
					    intent.putExtra("MSG", AppStateConstant.INIT_LRC_AGAIN);
					    sendBroadcast(intent);
				    }else{
				    	Intent intent = new Intent();
				    	intent.setAction("PLAY_SERVICE_ACTION");
					    intent.putExtra("MSG", AppStateConstant.LRC_NOT_FOUND);
					    sendBroadcast(intent);
				    }
				}
			  }).start();
	    }
	
	
	
	 
	 public class MainPlayerReceiver extends BroadcastReceiver{

			public void onReceive(Context context, Intent intent) {
				//����MusicListActivity��UI
				String action = intent.getAction();
				if("MUSIC_SERVICE_UPDATEUI_RECEIVER".equals(action)){//���½��������ؼ��Ĺ㲥
					mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
					boolean change = intent.getBooleanExtra("change", false);
					audio_player_music_name.setText(mp3Info.getTitle());
					audio_player_album_artist.setText(mp3Info.getArtist());
					if(change){loadImage(mp3Info);}
				    long time = mp3Info.getDuration();
				    tv_total_time.setText(MusicDao.formatTime(time));
				    progress_bar.setDuration((int)time, true);
				    
				    if(MusicPlayService.IS_PLAYING_MSG == false){
				       Drawable drawable = getResources().getDrawable(
							      R.drawable.selector_play_normal);
			           bt_play.setBackgroundDrawable(drawable);
				    }else if(MusicPlayService.IS_PLAYING_MSG == true){
				       Drawable drawable = getResources().getDrawable(
							      R.drawable.selector_play_pause);
			           bt_play.setBackgroundDrawable(drawable);
				    }
				}else if("MUSIC_SERVICE_SEEKBAR_LRC_UPDATE".equals(action)){      //���½���������ʵĹ㲥
					if(!isTrackingTouch){                               //�жϵ�ǰ�������Ƿ����϶����������϶��򲻸��½�����UI
					  int current_time = intent.getIntExtra("time", 0);
					  lrcIndex = intent.getIntExtra("lrcIndex", 0);
					  tv_current_time.setText(MusicDao.formatTime(current_time));
					  progress_bar.setProgress(current_time, true);
					  ((MusicLrcView)findViewById(R.id.lrcShowView)).setIndex(lrcIndex);
					  ((MusicLrcView)findViewById(R.id.lrcShowView)).invalidate();    //�����onDraw����ˢ�½���
					}
				}else if("MUSIC_SERVICE_INIT_LRC_FOR_CURRENT_SONG".equals(action)){ //��ʼ�����
					Mp3Info mp3= (Mp3Info)intent.getSerializableExtra("mp3");
					mLrcProcess = new LrcProcess(getApplicationContext());
					//��ȡ����ļ�
					mLrcProcess.readLRC(mp3);
					//���ش����ĸ���ļ�
					lrcList = mLrcProcess.getLrcList();
					((MusicLrcView)findViewById(R.id.lrcShowView)).setmLrcList(lrcList);
					((MusicLrcView)findViewById(R.id.lrcShowView)).setAnimation(AnimationUtils.loadAnimation(MainPlayerActivity.this,R.anim.lrc_anim));	
				}
			}
   }
	 
	 private void loadImage(Mp3Info mp3Info){
		     new LoadImages(mp3Info).execute();
	 }

	 /**
	  * ͼƬ����
	  * 
	  * */
	 class LoadImages extends AsyncTask<Void, Void, Bitmap>{
		    Mp3Info mp3Info;
	     public LoadImages(Mp3Info mp3Info){
	     this.mp3Info = mp3Info;
	     }
	 @Override
	 protected Bitmap doInBackground(Void... params) {
		// TODO Auto-generated method stub
	 	  Bitmap album = AlbumArtDao.getArtwork(MainPlayerActivity.this , mp3Info.getId() , mp3Info.getAlbumId() , true , false);
	      return album;
			}



		protected void onPostExecute(Bitmap result) {
	     if(result!=null){
	        ((ImageView)findViewById(R.id.iv_music_ablum)).startAnimation(anim);
	    	((ImageView)findViewById(R.id.iv_music_ablum)).setImageBitmap(result);
	     }
	 }
	}
}
