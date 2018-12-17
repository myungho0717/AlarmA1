package kr.androidteam.alarm;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

public class RandomMusic {
	Context context;
	ArrayList<String> musicList=new ArrayList<String>();
	MediaPlayer mp=new MediaPlayer();
	int timer=0;
	CountDownTimer thread;
	boolean flag=false;
	public RandomMusic(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void start(){
		String[] mListData=new String[]{MediaColumns._ID , AudioColumns.IS_MUSIC};
		 //private Object ReadmediaTitle;
		  int mediaID;
		  int mediaIsMusic;
		  
		  Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.
				  EXTERNAL_CONTENT_URI , mListData, null, null, null);

		 
		  cur.moveToFirst();
		do {

			mediaID = cur.getColumnIndex(MediaColumns._ID);
			mediaIsMusic = cur.getColumnIndex(AudioColumns.IS_MUSIC);


			
			
			if (cur.getInt(mediaIsMusic) == 1)
				musicList.add(cur.getString(mediaID));

			
		} while(cur.moveToNext());
		cur.close();

		

		if (musicList.size() != 0) {
			int position = (int) (Math.random() * 1000 % musicList.size());
		
			Uri musicUri = Uri.withAppendedPath(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""
							+ getMusicID(position));
			
			try {
				if(mp.isPlaying())
					mp.stop();
				
				mp.setDataSource(context, musicUri);
				mp.prepare();
				mp.start();
				
			}
			catch(IllegalStateException e){
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else{
			try {
				if(mp.isPlaying())
					mp.stop();
				mp.create(context, R.raw.song);
				mp.prepare();
				mp.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(thread==null)
		nextMusic();
		
	}

    public int getMusicID(int position) {
        return Integer.parseInt((musicList.get(position)));
    }
    
    
    public void nextMusic(){
		

	thread= new CountDownTimer(60*60 * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
           
        	if(!mp.isPlaying() && !flag){
        		mp.seekTo(0);
        		mp.start();
        	}
        }

        public void onFinish() {
           
        }
    };
		thread.start();
		
    }
    @SuppressWarnings("deprecation")
	public void stop(){
    	mp.stop();
    	thread.cancel();
    }
    
    public void pause(){
    	timer=mp.getCurrentPosition();
    	mp.pause();
    	flag=true;
    	}
    public void reStart(){
    	mp.seekTo(timer);
    	mp.start();
    	flag=false;
    }

}
