package kr.androidteam.alarm;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

public class selectedcatch extends Activity{

	int time=0;
	RandomMusic music;
	boolean flag=true;
	public PowerManager.WakeLock mWakeLock;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catched);
		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"catched");
		mWakeLock.acquire();
		
		if(flag){
		music=new RandomMusic(this);
		music.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
			Toast.makeText(this, "you don't use back button!!", 0).show();

		return false;
	}
}
