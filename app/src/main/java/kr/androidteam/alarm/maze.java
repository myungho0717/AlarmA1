package kr.androidteam.alarm;

import java.util.Calendar;

import kr.androidteam.alarm.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class maze extends View {

	Context c;
	boolean[][] check = new boolean[8][7];
	
	private Bitmap start;
	private Bitmap finish;
	private Bitmap block;
	private Bitmap line;
	
	Path path;
	Point point;

	Paint tracing;

	position[][] position = new position[8][7];
	
	int width;
	int height;


	int[] blockX = new int[56];
	int[] blockY = new int[56];
	

	int[] pathX=new int[7*8];
	int[] pathY=new int[7*8];


	int countX; 
	int countY;
	

	boolean collect;
	boolean flag;
	
	String repeat;
	int location;
	int request;
	AudioManager audio;
	String message;
	String number;
	CountDownTimer timer;
	
	private static PowerManager.WakeLock mWakeLock;

	
	public void init() {
		flag=false;
		countX=0;
		countY=0;
		collect=true;
		Display display = ((WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight() - 16;

		path = new Path();

		tracing = new Paint();
		tracing.setDither(true);
		tracing.setColor(0xFFFF00FF);
		tracing.setStyle(Paint.Style.STROKE);
		tracing.setStrokeJoin(Paint.Join.ROUND);
		tracing.setStrokeCap(Paint.Cap.ROUND);
		tracing.setStrokeWidth(width / 100);

		point = new Point();

		Resources res = getResources();
		start = BitmapFactory.decodeResource(res, R.drawable.start);
		start = Bitmap.createScaledBitmap(start, width / 8, height / 8, true);

		finish = BitmapFactory.decodeResource(res, R.drawable.finish);
		finish = Bitmap.createScaledBitmap(finish, width / 8, height / 8, true);

		block = BitmapFactory.decodeResource(res, R.drawable.block);
		block = Bitmap.createScaledBitmap(block, width / 8, height / 8, true);

		line = BitmapFactory.decodeResource(res, R.drawable.path);
		line = Bitmap.createScaledBitmap(line, width / 8, height / 8, true);


		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 7; j++) {
				position[i][j] = new position(width / 8 * i, height / 8 * j);
			check[i][j]=false;	
			}

				check[0][0]=true;
				

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 7; j++) {

				if ((int) (Math.random() * 10) % 2 == 0) {

					if (countX == 7) {

						break;
					}

					pathX[i * 7 + j] = countX++;
					pathY[i * 7 + j] = countY;

				} else {

					if (countY == 6) {

						break;
					}

					else {
						pathX[i * 7 + j] = countX;
						pathY[i * 7 + j] = countY++;
					}
				}
				check[countX][countY] = true;

			}
		}
		


		for (int i = 0; i < 56; i++) {
			blockX[i] = (int) (Math.random() * 10) % 8;
			blockY[i] = (int) (Math.random() * 10) % 7;
			
			if(check[blockX[i]][blockY[i]])
				i--;
			else 
				check[blockX[i]][blockY[i]] = false;
		}

		audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
	
		
		
		Intent intent=((selectedMaze)c).getIntent();
		
		location=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat"); //�ݺ��������� �ƴ��� Ȯ�� �ϱ�����
		request=intent.getIntExtra("request", 0);
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
	}

	public maze(Context c) {
		super(c);
		this.c = c;
		init();
		timer();
	}

	public maze(Context c, AttributeSet a) {
		super(c, a);
		this.c = c;
		init();
		timer();
	}
	
	public void timer(){
		
		timer= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              ((selectedMaze)c).time++;
              
              if(((selectedMaze)c).time ==60){
            	  
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

	protected void onDraw(Canvas canvas) {
		Paint back=new Paint();
		back.setColor(Color.WHITE);
		canvas.drawPaint(back);
		
		

		for(int i=0;i<pathX.length;i++)
			canvas.drawBitmap(line, position[pathX[i]][pathY[i]].getX(),
					position[pathX[i]][pathY[i]].getY(), null);
			
		

				for (int i = 0; i < 56; i++) //
					canvas.drawBitmap(block, position[blockX[i]][blockY[i]].getX(),
							position[blockX[i]][blockY[i]].getY(), null);
				

		canvas.drawBitmap(finish, position[countX][countY].getX(),
				position[countX][countY].getY(), null);

		canvas.drawBitmap(start, position[0][0].getX(), position[0][0].getY(),
				null);
		
				canvas.drawPath(path, tracing);
	}

	public boolean onTouchEvent(MotionEvent event) {

		float eventX = event.getX();
		float eventY = event.getY();
		
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			

			if (eventX >= position[1][0].getX()
					|| eventY >= position[0][1].getY()){
				
				collect=false;
			}
			

			else{
				
				flag=true;
				collect=true;
				path.moveTo(eventX, eventY);
				

			}
			
			break;
			
		case MotionEvent.ACTION_MOVE:

			
			if (collect) {
			
				path.quadTo(eventX, eventY, (point.x + eventX) / 2,
						(point.y + eventY) / 2);
			

				if (eventX >= position[countX][countY].getX()
						&& eventY >= position[countX][countY].getY()) {
				
					if(flag){
						flag=false;
						stop();
					}
					collect=false;
					path.reset();

				}
				

				for (int i = 0; i < 8 * 7; i++)


					if (position[blockX[i]][blockY[i]].getX() <= (int) eventX
							&& (int) eventX <= position[blockX[i]][blockY[i]]
									.getX() + (int) width / 8)
				
						if (position[blockX[i]][blockY[i]].getY() <= (int) eventY
								&& (int) eventY <= position[blockX[i]][blockY[i]]
										.getY() + (int) height / 8){

							path.moveTo(0,0);
							collect=false;
							eventX=0;
							eventY=0;
							point.x=(int)eventX;
							point.y=(int)eventY;
							path.reset();
							
						}
			}

			break;

		case MotionEvent.ACTION_UP:
			
			collect=false;
			eventX=0;
			eventY=0;
			point.x=(int)eventX;
			point.y=(int)eventY;
			path.reset();
			break;

		}

		point.x = (int) eventX;
		point.y = (int) eventY;
		invalidate();
		return true;
	}
	

	public void stop(){
		((selectedMaze)c).music.stop();
		timer.cancel();
		
		((selectedMaze)c).mWakeLock.release();
		Intent intent= new Intent(c,AlarmWeather.class);
		
		if(message!=null)
			intent.putExtra("message", message);
		intent.putExtra("position", location);
		
		intent.putExtra("repeat", repeat);
		
		
		PendingIntent sender = PendingIntent.getActivity(c,request,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+1, sender);
		
		((selectedMaze)c).finish();
	}
	

		@Override
		 public boolean  onKeyDown(int keyCode, KeyEvent event)
		 {
		  
		  if(keyCode == KeyEvent.KEYCODE_BACK)
		  {
			  
			  
		  }
		  return false;
		 }
		
}