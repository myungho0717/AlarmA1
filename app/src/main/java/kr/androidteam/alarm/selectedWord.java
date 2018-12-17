package kr.androidteam.alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class selectedWord extends Activity {

	TextView text;
	String repeat;
	String message;
	String number;
	int position;
	AudioManager audio;
	private static PowerManager.WakeLock mWakeLock;
	RandomMusic music;
	String[] matchWords = {"일어나자", "화이팅", "밥 먹자", "학교 가자", "과제는"};
	String speak = "";
	String matchWord;
	CountDownTimer timer;
	CountDownTimer count;
	int time=0;
	int cnt=0;
	boolean flag=true;
		@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.word);
		text = (TextView) findViewById(R.id.textLog);

		text.setText("");
		
		if(flag){
			int number=(int)(Math.random()*5);
			
			matchWord=matchWords[number];
			Toast.makeText(this, matchWords.length+matchWord, 0).show();
		music=new RandomMusic(this);
		music.start();
		}
		
		Intent intent=getIntent();
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
		

		position=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat");
		timer();
		
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
		PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "selectedWord");
		mWakeLock.acquire();
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				music.pause();
				voice();
				cnt=0;
				if(count!=null)
				count.cancel();
				countDown();
				
			}

		});

	}
	
	public void voice()

	{


		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // �����νı⸦
						
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음악 끄고 싶으면" + "\n'"
				+ matchWord + "'" + "라고 말해주세요.");

		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 1);
		
	}
	
	public void countDown(){
		count= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              cnt++;
          	if(cnt==10){
    		
          	  music.reStart();
          	  
    		}
            }
    
            public void onFinish() {
               
            }
            
        };
			count.start();

	}
	
		@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)

	{

		if (requestCode == 1 && resultCode == RESULT_OK)

		{

			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (int i = 0; i < results.size(); i++) {

				text.setText(results.get(i));

				speak = results.get(i).toString().trim();

			}

			if (speak.equals(matchWord.trim())) {
				stop();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
		

	}
	
	public void stop(){
		music.stop();
		timer.cancel();
		count.cancel();
		mWakeLock.release();
		Intent intent= new Intent(this,AlarmWeather.class);
		
		intent.putExtra("position", position);
		intent.putExtra("repeat", repeat);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent sender = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis(), sender);
		finish();
	}

	@Override
	 public boolean  onKeyDown(int keyCode, KeyEvent event){
	  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
		  Toast.makeText(this, "you don't use back button!!", 0).show();
		  
	  return false;
	 }
	

public void timer(){
		
		timer= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              time++;
              
              if(time ==60){
            	  
            	  if(number!=null && number!="")
            	  SmsManager.getDefault().sendTextMessage(number, null,
            			  "wake me up plz", null,null);
              }
            }
    
            public void onFinish() {
               
            }
            
        };
			timer.start();
			
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		flag=savedInstanceState.getBoolean("flag");
		time=savedInstanceState.getInt("time");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("flag",false);
		outState.putInt("time", time);
		
	}

}
