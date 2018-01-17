package com.android.mplayer.utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.mplayer.constants.AppStateConstant;
/**
 * �α깤���࣬ͨ������������ȡ��Ӧ���α�
 * **/
public class CursorUtils{

	Context ctx;
	
	private static CursorUtils  cursorUtil = null;
	static final String[] albumProjection = 
	{
		MediaStore.Audio.Albums._ID,
		MediaStore.Audio.Albums.ALBUM,
		MediaStore.Audio.Albums.ARTIST,
		MediaStore.Audio.Albums.ALBUM_ART,
		MediaStore.Audio.Albums.NUMBER_OF_SONGS,
		MediaStore.Audio.Albums.LAST_YEAR
	};
	
	static final String[] artistProjection = 
	{
		MediaStore.Audio.Artists._ID,
		MediaStore.Audio.Artists.ARTIST,
	};
	
	static final String[] songProjection = 
	{
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.ALBUM_ID,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.ARTIST_ID,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.TITLE_KEY,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.DISPLAY_NAME,
		MediaStore.Audio.Media.DURATION,
		MediaStore.Audio.Media.IS_MUSIC,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.SIZE
	};
	
	static final String[] genreProjection = 
	{
		MediaStore.Audio.Genres._ID,
		MediaStore.Audio.Genres.NAME
	};
	
	static final String[] genreMemberProjection = 
	{
		MediaStore.Audio.Genres.Members._ID,
		MediaStore.Audio.Genres.Members.ALBUM_ID,
		MediaStore.Audio.Genres.Members.ALBUM,
		MediaStore.Audio.Genres.Members.ARTIST,
		MediaStore.Audio.Genres.Members.ARTIST_ID,
		MediaStore.Audio.Genres.Members.TITLE,
		MediaStore.Audio.Genres.Members.TRACK,
		MediaStore.Audio.Genres.Members.DURATION
	};
	
	static final String[] playlistProjection = 
	{
		MediaStore.Audio.Playlists._ID,
		MediaStore.Audio.Playlists.NAME
	};
	
	static final String[] playlistMembersProjection = 
	{
		MediaStore.Audio.Playlists.Members.AUDIO_ID,
		MediaStore.Audio.Playlists.Members.IS_MUSIC,
		MediaStore.Audio.Playlists.Members.DISPLAY_NAME,
		MediaStore.Audio.Playlists.Members.SIZE,
		MediaStore.Audio.Playlists.Members.DATA,
		MediaStore.Audio.Playlists.Members.ALBUM_ID,
		MediaStore.Audio.Playlists.Members.ALBUM,
		MediaStore.Audio.Playlists.Members.ARTIST,
		MediaStore.Audio.Playlists.Members.ARTIST_ID,
		MediaStore.Audio.Playlists.Members.TITLE,
		MediaStore.Audio.Playlists.Members.DURATION
	};
	
	static final String genreMembersAlbumSorting = 
		MediaStore.Audio.Genres.Members.ALBUM_ID + " ASC";
	
	static final String genreAlphabeticalSorting = 
		MediaStore.Audio.Genres.NAME + " COLLATE NOCASE ASC";
	
	static final String playlistMembersAlbumSorting = 
		MediaStore.Audio.Playlists.Members.ALBUM_ID + " ASC";
	
	static final String playlistAlphabeticalSorting = 
		MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE ASC";
	
	static final String albumAlphabeticalSortOrder = 
		MediaStore.Audio.Albums.ALBUM_KEY + " ASC";
	
	static final String albumAlphabeticalSortOrderByArtist = 
		MediaStore.Audio.Albums.ARTIST + " COLLATE NOCASE ASC"
		+ ", " + 
		MediaStore.Audio.Albums.LAST_YEAR + " DESC";
	
	static final String artistAlbumsYearSortOrder = 
		MediaStore.Audio.Albums.LAST_YEAR + " DESC";

	static final String artistAlphabeticalSortOrder = 
		MediaStore.Audio.Artists.ARTIST_KEY + " ASC";
	
	static final String songListNumericalSorting = 
		MediaStore.Audio.Media.TRACK + " ASC";
	
	static final String songListPlaylistSorting = 
			MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC";
	
	static final String songListAlbumAndNumericalSorting = 
		MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC"+
		", "+
		MediaStore.Audio.Media.TRACK + " ASC";
	
	
	static final String songListTitleSorting =
		MediaStore.Audio.Media.TITLE_KEY + " ASC";
	

	
	
	
	
	/**
	 * ��ʼ���α깤��
	 * @author JIANGPENG
	 */
	CursorUtils(Context ctx){
		this.ctx = ctx;
	}

	/**
	 * �򵥵ĵ���ģʽ
	 * @author JIANGPENG
	 * */
	public static CursorUtils getInstance(Context ctx){
		if(cursorUtil == null){
			cursorUtil = new CursorUtils(ctx);
		}
		return cursorUtil;
	}
	
	/**
	 * ��������ר����Ϣ��Cursor
	 * @author JIANGPENG
	 * @return Cursor
	 */
	public Cursor getAllAlbumCursor(){
		ContentResolver resolver = ctx.getContentResolver();
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
			c = resolver.query(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					albumProjection, 
					null, 
					null, 
					albumAlphabeticalSortOrder);
		} else {
			c = resolver.query(
					MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
					albumProjection, 
					null, 
					null, 
					albumAlphabeticalSortOrder);
		}
		return c;
	}
	
	/**
	 * ����ר��ID��������ר����Ϣ��Cursor
	 * @author JIANGPENG
	 * @param  ר��ID
	 * @return Cursor
	 */
	public Cursor getAlbumCursorByAlbumId(int id){
		ContentResolver resolver = ctx.getContentResolver();
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
			c = resolver.query(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					albumProjection, 
					MediaStore.Audio.Albums._ID +"=" +id, 
					null, 
					albumAlphabeticalSortOrder);
		} else {
			c = resolver.query(
					MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
					albumProjection, 
					MediaStore.Audio.Albums._ID +"=" +id, 
					null, 
					albumAlphabeticalSortOrder);
		}
		return c;
	}
	
	/**
	 * ����������Id������ר����Ϣ�����ã�
	 * @author JIANGPENG
	 * @param  artistId ������ID
	 * @param  playlistId �����б�ID
	 * @return Cursor
	 */
	public Cursor getAlbumListFromArtistId(long artistId, int playlistId)
	{
		switch(playlistId)
		{
		case AppStateConstant.PLAYLIST_ALL:
			return getAlbumListFromArtistId(artistId);
		}
		return null;
	}
	
	/**
	 * ����������Id������ר����Ϣ�����ã�
	 * @author JIANGPENG
	 * @param  artistId ������ID
	 * @return Cursor
	 */
	public Cursor getAlbumListFromArtistId(long artistId)
	{
		ContentResolver resolver = ctx.getContentResolver();
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
			c = resolver.query(
					MediaStore.Audio.Artists.Albums.getContentUri("external", artistId), 
					albumProjection, 
					null, 
					null, 
					artistAlbumsYearSortOrder);
		} else {
			c = resolver.query(
					MediaStore.Audio.Artists.Albums.getContentUri("internal", artistId), 
					albumProjection, 
					null, 
					null, 
					artistAlbumsYearSortOrder);
		}
		return c;
	}
	
	/**
	 * ���ز����б��ר����Ϣ�����ã�
	 * @author JIANGPENG
	 * @param  preferArtistSorting �Ƿ�����������
	 * @param  playlistId �����б�ID
	 * @return Cursor
	 */
	public Cursor getAlbumListFromPlaylist(int playlistId, boolean preferArtistSorting){
		
		if(AppStateConstant.PLAYLIST_ALL == playlistId)
		{
			ContentResolver resolver = ctx.getContentResolver();
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				if(preferArtistSorting) {
					c = resolver.query(
							MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							albumProjection, 
							null, 
							null, 
							albumAlphabeticalSortOrderByArtist);
				} else {
					c = resolver.query(
							MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							albumProjection, 
							null, 
							null, 
							albumAlphabeticalSortOrder);
				}
			} else {
				if(preferArtistSorting) {
					c = resolver.query(
							MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
							albumProjection, 
							null, 
							null, 
							albumAlphabeticalSortOrderByArtist);
				} else {
					c = resolver.query(
							MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
							albumProjection, 
							null, 
							null, 
							albumAlphabeticalSortOrder);
				}
			}
			return c;
		}
				
		else {
			return null;
		}
	}
	
	/**
	 * ͨ��������ID������������Ϣ
	 * @author JIANGPENG
	 * @param  id ������ID
	 * @return Cursor
	 */
	public Cursor getArtistCursorByArtistId(int id)
	{
			ContentResolver resolver = ctx.getContentResolver();
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				c = resolver.query(
						MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
						artistProjection, 
						MediaStore.Audio.Artists._ID + "=" + id , 
						null, 
						artistAlphabeticalSortOrder);
			} else {
				c = resolver.query(
						MediaStore.Audio.Artists.INTERNAL_CONTENT_URI,
						artistProjection, 
						MediaStore.Audio.Artists._ID + "=" + id ,
						null, 
						artistAlphabeticalSortOrder);
			}
			return c;
	}
	
	/**
	 * ����������������Ϣ
	 * @author JIANGPENG
	 * @return Cursor
	 */
	public Cursor getAllArtistCursor()
	   {
			ContentResolver resolver = ctx.getContentResolver();
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				c = resolver.query(
						MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
						artistProjection, 
						null, 
						null, 
						artistAlphabeticalSortOrder);
			} else {
				c = resolver.query(
						MediaStore.Audio.Artists.INTERNAL_CONTENT_URI,
						artistProjection, 
						null, 
						null, 
						artistAlphabeticalSortOrder);
			}
			return c;
						
		} 
	
	/**
	 * ��������������Ϣ
	 * @author JIANGPENG
	 * @return Cursor
	 */
	public Cursor	getGenres(){
		try{
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
						genreProjection,
						null,
						null,
						genreAlphabeticalSorting);
			} else {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Genres.INTERNAL_CONTENT_URI,
						genreProjection,
						null,
						null,
						genreAlphabeticalSorting);
			}
			return c;
		} catch(SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ��ȡ���в����б���Ϣ
	 * @author JIANGPENG
	 * @return Cursor
	 */
	public Cursor	getAllPlayListsCursor(){
		try{
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
						playlistProjection,
						null,
						null,
						playlistAlphabeticalSorting);
			} else {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI,
						playlistProjection,
						null,
						null,
						playlistAlphabeticalSorting);
			}
			return c;
		} catch(SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ��ȡָ��ID�����б���Ϣ
	 * @author JIANGPENG
	 * @param id �����б�ID
	 * @return Cursor
	 */
	public Cursor	getPlayListsCursorByListId(int id){
		try{
			Cursor c = null;
			if(FileUtil.usesExternalStorage()) {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
						playlistProjection,
						MediaStore.Audio.Playlists._ID + "=" +id,
						null,
						playlistAlphabeticalSorting);
			} else {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI,
						playlistProjection,
						MediaStore.Audio.Playlists._ID + "=" +id,
						null,
						playlistAlphabeticalSorting);
			}
			return c;
		} catch(SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ��ȡ���и�����Ϣ��������������
	 * @author JIANGPENG
	 * @return Cursor
	 */
	public Cursor getAllSongsListOrderedBySongTitle()
	{
		return getSongListOrderedBySongTitle(null);
	}
	
	/**
	 * ��ָ��������ȡ���и�����Ϣ��������������
	 * @author JIANGPENG
	 * @param constraint ����
	 * @return Cursor
	 */
	public Cursor getSongListOrderedBySongTitle(String constraint)
	{
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
				if(constraint != null) {
					c = ctx.getContentResolver().query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							songProjection,
							constraint,
							null,
							songListTitleSorting);
				} else {
					c = ctx.getContentResolver().query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							songProjection,
							null,
							null,
							songListTitleSorting);
				}
		}else {
				if(constraint != null) {
					c = ctx.getContentResolver().query(
							MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
						    songProjection,
							constraint,
							null,
							songListTitleSorting);
				} else {
					c = ctx.getContentResolver().query(
							MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
							songProjection,
							null,
							null,
							songListTitleSorting);
				}
		}
		return c;
	}
	
	
	/**
	 * ��ר��Id��ȡ��ר��������Ϣ
	 * @author JIANGPENG
	 * @param albumId ר��ID
	 * @return Cursor
	 */
	public Cursor	getSongListCursorFromAlbumId(long albumId){
		Cursor c = null;
		return
				getSongsWithConstraint(
					MediaStore.Audio.Media.ALBUM_ID + " = " + albumId+
						" AND "+
						MediaStore.Audio.Media.IS_MUSIC + "=1");
		
	}
	
	/**
	 * ��ר��Id��������Id��ȡ������Ϣ
	 * @author JIANGPENG
	 * @param albumId ר��ID
	 * @param artistId ������ID
	 * @return Cursor
	 */
	public Cursor	getSongListCursorFromAlbumAndArtistId(long albumId, long artistId){
		return getSongsWithConstraint(
				MediaStore.Audio.Media.ALBUM_ID + " = " + albumId+
					" AND "+
					MediaStore.Audio.Media.ARTIST_ID + " = " + artistId+
					" AND "+
					MediaStore.Audio.Media.IS_MUSIC + "=1");
		
	}
	
	/**
	 * ������Id��ȡ������Ϣ
	 * @author JIANGPENG
	 * @param artistId ������ID
	 * @return Cursor
	 */
	public Cursor	getSongListCursorFromArtistId(long artistId){
		return
			getSongsWithConstraint(
					MediaStore.Audio.Media.ARTIST_ID + " = " + artistId+
					" AND "+
					MediaStore.Audio.Media.IS_MUSIC + "=1");
		
	}
	
	/**
	 * ���������ֻ�ȡ������Ϣ
	 * @author JIANGPENG
	 * @param artistName ����������
	 * @return Cursor
	 */
	public Cursor	getSongListCursorFromArtistName(String artistName){
		return
			getSongsWithConstraint(
				MediaStore.Audio.Media.ARTIST + " = '" + artistName+ "'" +
					" AND "+
					MediaStore.Audio.Media.IS_MUSIC + "=1");
	}
	
	/**
	 * ��ȡָ��ID�Ĳ����б�����и���
	 * @author JIANGPENG
	 * @param playlistId �����б�ID
	 * @return Cursor
	 */
	public Cursor	getSongListCursorFromPlayListId(long playlistId){
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.Members.getContentUri(
								"external", 
								playlistId),
						playlistMembersProjection,
						null,
						null,
						songListPlaylistSorting);
			} else {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Playlists.Members.getContentUri(
								"internal", 
								playlistId),
						playlistMembersProjection,
						null,
						null,
						songListPlaylistSorting);
			}
		return c;	
	}
	
	/**
	 * ������������ȡ������Ϣ
	 * @author JIANGPENG
	 * @param constraint ����
	 * @return Cursor
	 */
	public Cursor	getSongsWithConstraint( String constraint){
		String finalConstraint = constraint;
		Cursor c = null;
		 if(FileUtil.usesExternalStorage()) {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						songProjection,
						finalConstraint,
						null,
						songListAlbumAndNumericalSorting);
			} else {
				c = ctx.getContentResolver().query(
						MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
						songProjection,
						finalConstraint,
						null,
						songListAlbumAndNumericalSorting);
			}
			return c;
	}
	
	/**
	 * @param list
	 * @param position
	 * @return
	 */
	public Cursor	getSongListCursorFromSongList(long[] list, int position, int limit){
		if(list != null && list.length > 0)
		{
			Cursor[]	cursor = 
				new Cursor[Math.min(list.length-position, position + limit + 1)];
			boolean external = FileUtil.usesExternalStorage(); 
			String	constraint;
			for(int i = position; i < Math.min(list.length, position + limit + 1) ; i++){
				constraint = 
					MediaStore.Audio.Media._ID+
					"="+
					String.valueOf(list[i]);
				if(external) {
					cursor[i-position] = 
						ctx.getContentResolver().query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
							songProjection, 
							constraint, 
							null, 
							null);
				} else {
					cursor[i-position] = 
						ctx.getContentResolver().query(
							MediaStore.Audio.Media.INTERNAL_CONTENT_URI, 
							songProjection, 
							constraint, 
							null, 
							null);
				}
			}
			MergeCursor playQueueCursor = null;
			if(cursor != null)
				playQueueCursor = new MergeCursor(cursor);
			return playQueueCursor;
		} else {
			return null;
		}
	}
	
	

	/**
	 * ���������б�
	 * @author JIANGPENG
	 * @param name �б���
	 * @return boolean
	 */
  public boolean createPlaylist(String name)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(
					MediaStore.Audio.Playlists.NAME,
					name);
			ContentResolver res = ctx.getContentResolver();
			if(FileUtil.usesExternalStorage()) {
				res.insert(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, 
					values);
			} else {
				res.insert(
						MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI, 
						values);
			}
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
    /**
	 * ɾ������
	 * @author JIANGPENG
	 * @param songId ����ID
	 * @return boolean
	 */
	public boolean deleteSong(int songId)
	{
		try
		{
			long[] songIdList = {songId};
			deleteTracks(songIdList);
			return true;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ɾ����������
	 * @author JIANGPENG
	 * @param list ����ID����
	 */
    public void deleteTracks(long [] list) {
        
        String [] cols = new String [] { 
        		MediaStore.Audio.Media._ID, 
                MediaStore.Audio.Media.DATA, 
                MediaStore.Audio.Media.ALBUM_ID };
        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media._ID + " IN (");
        for (int i = 0; i < list.length; i++) {
            where.append(list[i]);
            if (i < list.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        
        Cursor c = null;
        boolean external = FileUtil.usesExternalStorage(); 
        if(external) {
        	c = ctx.getContentResolver().query(
            		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
            		cols,
                    where.toString(), 
                    null, 
                    null);
        } else {
        	c = ctx.getContentResolver().query(
            		MediaStore.Audio.Media.INTERNAL_CONTENT_URI, 
            		cols,
                    where.toString(), 
                    null, 
                    null); 
        }

        if (c != null) 
        {
            c.moveToFirst();
            while (! c.isAfterLast()) {
                long id = c.getLong(0);
                long artIndex = c.getLong(2);
                c.moveToNext();
            }
            
            if(external)
            	ctx.getContentResolver().delete(
            		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
            		where.toString(), 
            		null);
            else
            	ctx.getContentResolver().delete(
            		MediaStore.Audio.Media.INTERNAL_CONTENT_URI, 
            		where.toString(), 
            		null);

            c.moveToFirst();
            while (! c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {  // File.delete can throw a security exception
                    if (!f.delete()) {
                    }
                    c.moveToNext();
                } catch (SecurityException ex) {
                    c.moveToNext();
                }
            }
            c.close();
        }

        ctx.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }

	
	/**
	 * ɾ�������б�
	 * @author JIANGPENG
	 * @param playlistId �����б�ID
	 * @return boolean
	 */
	public boolean deletePlaylist(long playlistId)
	{
		if(clearPlaylist(playlistId))
		{
			try{
				if(FileUtil.usesExternalStorage())
					ctx.getContentResolver().delete(
		        		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, 
		        		MediaStore.Audio.Playlists._ID + "=" + playlistId, 
		        		null);
				else
					ctx.getContentResolver().delete(
		        		MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI, 
		        		MediaStore.Audio.Playlists._ID + "=" + playlistId, 
		        		null);
				return true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * ��ղ����б�
	 * @author JIANGPENG
	 * @param playlistId �����б�ID
	 * @return boolean
	 */
	public boolean clearPlaylist(long playlistId)
	{
		try
		{
			if(FileUtil.usesExternalStorage()) {
				Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
		        ctx.getContentResolver().delete(uri, null, null);
			} else {
		        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("internal", playlistId);
		        ctx.getContentResolver().delete(uri, null, null);
			}
	        return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ɾ������
	 * @author JIANGPENG
	 * @param genreId ����ID
	 * @return boolean
	 */
	public boolean deleteGenre(int genreId)
	{
		clearGenre(genreId); // this does not work -- cannot alter mp3?
		try{
			if(FileUtil.usesExternalStorage())
				ctx.getContentResolver().delete(
	        		MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, 
	        		MediaStore.Audio.Media._ID + "=" + genreId, 
	        		null);
			else
				ctx.getContentResolver().delete(
	        		MediaStore.Audio.Genres.INTERNAL_CONTENT_URI, 
	        		MediaStore.Audio.Media._ID + "=" + genreId, 
	        		null);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * �������
	 * @author JIANGPENG
	 * @param genreId ����ID
	 * @return boolean
	 */
	public boolean clearGenre(int genreId)
	{
		try
		{
			if(FileUtil.usesExternalStorage()) {
				Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
		        ctx.getContentResolver().delete(uri, null, null);
			} else {
		        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("internal", genreId);
		        ctx.getContentResolver().delete(uri, null, null);
			}
	        return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * ͨ������������ȡ��ID
	 * @author JIANGPENG
	 * @param playlistName �б���
	 * @return long
	 */
	public long getPlaylistIdFromName(String playlistName)
	{
		ContentResolver res = ctx.getContentResolver();
		Cursor c = null;
		if(FileUtil.usesExternalStorage()) {
			c = res.query(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, 
					playlistProjection, 
					MediaStore.Audio.Playlists.NAME + "='" + playlistName + "'", 
					null, 
					null);
		} else {
			c = res.query(
					MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI, 
					playlistProjection, 
					MediaStore.Audio.Playlists.NAME + "='" + playlistName + "'", 
					null, 
					null);
		}
		c.moveToFirst();
		return c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID));
	}
	
}