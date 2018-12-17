package kr.androidteam.alarm;

import java.util.Calendar;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class selectedDecibel extends Activity{


	String repeat;
	int position;
	String message;
	String number;
	AudioManager audio;
	int time=0;
	MediaRecorder mRecorder;
	Thread runner;
	private static double mEMA = 0.0;
	static final private double EMA_FILTER = 0.6;
	private static PowerManager.WakeLock mWakeLock;

	RandomMusic music;
	boolean flag=true;
	CountDownTimer timer;
	
	final Runnable updater = new Runnable() { // ����ؼ� db�� ���� �ٲ��ִ� �Լ�.
		public void run() {
			updateTv();
		};
	};
	
	final Handler mHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decibel);
		if (runner == null) {
			runner = new Thread() {
				public void run() {
					while (runner != null) {
						try {
							Thread.sleep(500);
							
						} catch (InterruptedException e) {
						};
						mHandler.post(updater);
					}
				}
			};

			runner.start();
		}
		
		Intent intent=getIntent();
		position=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat");
		
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
		
		if(flag){
		music=new RandomMusic(this);
		music.start();
		}
		
		startRecorder();
		
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
		PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "selectedDecibel");
		mWakeLock.acquire();
		timer();

	}

	public void startRecorder() {

		if (mRecorder == null) {

			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");
			
			try {
				mRecorder.prepare();
				mRecorder.start();
			}

			catch (java.io.IOException ioe) {
				
			}

			// mEMA = 0.0;
		}
	}
	public void stopRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void updateTv() {

			double stopProgram1 = soundDb(1.9);

			if (stopProgram1 >= 84.3) {
				Toast.makeText(getApplicationContext(), "84.3Db이 넘어서 음악이 종료됩니다."+stopProgram1,
						Toast.LENGTH_SHORT).show();
				stopRecorder();
				stop();
			}
	}

	public double soundDb(double ampl) {
		return 20 * Math.log10(getAmplitudeEMA() / ampl);
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude());
		else
			return 0;
	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
	
	public void stop(){
		music.stop();
		timer.cancel();
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
	 public boolean  onKeyDown(int keyCode, KeyEvent event)
	 {
	  
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
