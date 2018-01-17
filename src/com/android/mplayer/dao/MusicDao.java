package com.android.mplayer.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.android.mplayer.domain.Mp3Info;
import com.android.mplayer.utils.CursorUtils;



public class MusicDao {
	
	
	/**
	 * ���ڴ����ݿ��в�ѯ��������Ϣ��������List����
	 * 
	 * @return
	 */
	public static List<Mp3Info> getAllMp3Infos(Context context) {
		
		Cursor cursor = CursorUtils.getInstance(context).getAllSongsListOrderedBySongTitle();
		
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));	//����id
			String title = cursor.getString((cursor	
					.getColumnIndex(MediaStore.Audio.Media.TITLE))); // ���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // ������
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));	//ר��
			String artistId = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
			String displayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION)); // ʱ��
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // �ļ�·��
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // �Ƿ�Ϊ����
			if (isMusic != 0) { // ֻ��������ӵ����ϵ���
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setArtist(artist);
				mp3Info.setAlbum(album);
				mp3Info.setDisplayName(displayName);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUrl(url);
				mp3Info.setArtistId(artistId);
				mp3Infos.add(mp3Info);
			}
		}
		return mp3Infos;
	}
	
	/**
	 * ͨ��ר��ID��ȡר������ĸ���
	 * */
public static List<Mp3Info> getMp3InfosByAlbumID(Context context,long albumId) {
		
		Cursor cursor = CursorUtils.getInstance(context).getSongListCursorFromAlbumId(albumId);
		
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));	//����id
			String title = cursor.getString((cursor	
					.getColumnIndex(MediaStore.Audio.Media.TITLE))); // ���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // ������
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));	//ר��
			String artistId = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
			String displayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION)); // ʱ��
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // �ļ�·��
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // �Ƿ�Ϊ����
			if (isMusic != 0) { // ֻ��������ӵ����ϵ���
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setArtist(artist);
				mp3Info.setAlbum(album);
				mp3Info.setDisplayName(displayName);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUrl(url);
				mp3Info.setArtistId(artistId);
				mp3Infos.add(mp3Info);
			}
		}
		return mp3Infos;
	}

/**
 * ͨ��������ID��ȡ�������ҵĸ���
 * */
public static List<Mp3Info> getMp3InfosByArtistID(Context context,long artistId) {
	
	Cursor cursor = CursorUtils.getInstance(context).getSongListCursorFromArtistId(artistId);
	
	List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
	for (int i = 0; i < cursor.getCount(); i++) {
		cursor.moveToNext();
		Mp3Info mp3Info = new Mp3Info();
		long id = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media._ID));	//����id
		String title = cursor.getString((cursor	
				.getColumnIndex(MediaStore.Audio.Media.TITLE))); // ���ֱ���
		String artist = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // ������
		String album = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ALBUM));	//ר��
		String albumId = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		String displayName = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		long duration = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.DURATION)); // ʱ��
		long size = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
		String url = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DATA)); // �ļ�·��
		int isMusic = cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // �Ƿ�Ϊ����
		if (isMusic != 0) { // ֻ��������ӵ����ϵ���
			mp3Info.setId(id);
			mp3Info.setTitle(title);
			mp3Info.setArtist(artist);
			mp3Info.setAlbum(album);
			mp3Info.setDisplayName(displayName);
			mp3Info.setAlbumId(Long.parseLong(albumId));
			mp3Info.setDuration(duration);
			mp3Info.setSize(size);
			mp3Info.setUrl(url);
			mp3Info.setArtistId(Long.toString(artistId));
			mp3Infos.add(mp3Info);
		}
	}
	return mp3Infos;
}

/**
 * ͨ�������б�ID��ȡ�ò����б�ĸ���
 * */
public static List<Mp3Info> getMp3InfosByPlayListId(Context context,long playListId) {
	
	Cursor cursor = CursorUtils.getInstance(context).getSongListCursorFromPlayListId(playListId);
	
	List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
	for (int i = 0; i < cursor.getCount(); i++) {
		cursor.moveToNext();
		Mp3Info mp3Info = new Mp3Info();
		long id = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));	//����id
		String title = cursor.getString((cursor	
				.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE))); // ���ֱ���
		String artist = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)); // ������
		String artistId = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID));
		String album = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));	//ר��
		String albumId = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID));
		long duration = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)); // ʱ��
		long size = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.SIZE)); // �ļ���С
		String displayName = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.DISPLAY_NAME));
		String url = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));// �ļ�·��
		int isMusic = cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Playlists.Members.IS_MUSIC)); // �Ƿ�Ϊ����
		if (isMusic != 0) { // ֻ��������ӵ����ϵ���
			mp3Info.setId(id);
			mp3Info.setTitle(title);
			mp3Info.setArtist(artist);
			mp3Info.setAlbum(album);
			mp3Info.setDisplayName(displayName);
			mp3Info.setAlbumId(Long.parseLong(albumId));
			mp3Info.setDuration(duration);
			mp3Info.setSize(size);
			mp3Info.setUrl(url);
			mp3Info.setArtistId(artistId);
			mp3Infos.add(mp3Info);
		}
	}
	return mp3Infos;
}
	
	
	/**
	 * ��ʽ��ʱ�䣬������ת��Ϊ��:���ʽ
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60)) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}
	
	
	
	
}
