package kr.androidteam.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;



public class AlarmReceiver extends BroadcastReceiver{

	int[] days;
	public static final String MYPREFS="AlarmLOG";
	final int mode=Activity.MODE_PRIVATE;
	StringBuffer savedAlarmInfo=new StringBuffer();
	AlarmInfo tempSaved;
	int count=0;
	int request;
	int position;
	int[] time=new int[2];
	String repeat;
	String option;
	String message=null;
	String number =null;
	AlarmNotification notification;
	Intent intent;
	Context context;
	final Handler handler = new Handler();
	Thread runner;
	
	private static PowerManager.WakeLock mWakeLock;
	
	final Runnable start = new Runnable() { 
		public void run() {
			excute();
			mWakeLock.release();
			
		};
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		

		
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
		PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver");
		
		mWakeLock.acquire();
		

		this.intent=intent;
		this.context=context;


		if (runner == null) {
			runner = new Thread() {
				public void run() {

					handler.post(start);
				}

			};
			runner.start();
		}

	}

	public void excute(){
		
		if (intent.getAction().equals("setAlarm")) {
			request = intent.getIntExtra("requestCode", 0);
			position = intent.getIntExtra("position", 0);
			time = intent.getIntArrayExtra("time");
			repeat = intent.getStringExtra("repeat");
			option=intent.getStringExtra("option");
			
			if(intent.getStringExtra("message")!=null)
				message=intent.getStringExtra("message");

			if(intent.getStringExtra("number")!=null)
				number=intent.getStringExtra("number");

			Log.e("setAlarm","alarm ready");
			calculateDay();
			nextActivity(context);


			if (repeat.length() != 0)
				nextAlarm(context);

		}
		

		else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Log.e("booting","booting ready");
				SharedPreferences sh_Pref = context.getSharedPreferences(MYPREFS,mode);
				 

				if (sh_Pref != null && sh_Pref.contains("saveInfo")
						&& sh_Pref.contains("savedInfoSize")) {
					showSavedAlarmInfo(context);
					wakeCPU(context);
					
				}
			}

	}
		public void wakeCPU(Context context){
		
		Intent intent=new Intent(context,wakeUp.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),1000 * 60 * 120, sender);

	}
	

	public void showSavedAlarmInfo(Context context) {
		SharedPreferences my_Pref = context.getSharedPreferences(MYPREFS, mode);
		
		count = my_Pref.getInt("savedInfoSize", 0);
		String restoreInfo = my_Pref.getString("saveInfo", "");

		if (restoreInfo != "") {

			String[] restoreInfoArray = restoreInfo.split("=");


			for (int i = 0; i < restoreInfoArray.length; i++) {

				String[] eachComponent = restoreInfoArray[i].split("_");


				tempSaved = new AlarmInfo(eachComponent[0], eachComponent[1],
						eachComponent[2], eachComponent[3],eachComponent[4],eachComponent[5]
						, eachComponent[6],Integer.parseInt(eachComponent[7]),
						Integer.parseInt(eachComponent[8]),Integer.parseInt(eachComponent[9]));


				if(tempSaved.getActive()==1){
					
					if(notification==null){
					notification=new AlarmNotification(context);
					notification.registerNotification();
					}
					calculateDayTime(tempSaved);
				

				registAlarm(context,tempSaved,i);
				}
			}
			
		}

	}


	public void calculateDayTime(AlarmInfo tempSaved){
		String[] convertTime;

		if (tempSaved.getDay() != "") {
			days = new int[tempSaved.getDay().length()];


			for (int i = 0; i < tempSaved.getDay().length(); i++)
				days[i] = Integer.parseInt(""+ tempSaved.getDay().charAt(i));
		}
		

		
		convertTime = tempSaved.getReceive().split(":");

		time[0] = Integer.parseInt(convertTime[0]);
		time[1] = Integer.parseInt(convertTime[1]);
	}
	

	public void registAlarm(Context context,AlarmInfo tempSaved, int position){
		Calendar cal = Calendar.getInstance();
		int alarmTimer=0;
		
		if(days.length==1){
			if(cal.get(Calendar.DAY_OF_WEEK)>days[0])
			alarmTimer=7-cal.get(Calendar.DAY_OF_WEEK)+days[0]; 

			
			else if(cal.get(Calendar.DAY_OF_WEEK) < days[0])
				alarmTimer=days[0]-cal.get(Calendar.DAY_OF_WEEK);
				
			else{
				alarmTimer=oneDayTimeCal(cal);
				
			}
			AlarmReEnrollment(context,tempSaved,position,alarmTimer);
			
		}
		
		else if(days.length>1)
			
		for(int i=0;i<days.length;i++){
			
			if(days[i] ==cal.get(Calendar.DAY_OF_WEEK)){
				alarmTimer=multiTimeCal(cal,i);
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);	
				break;
			}
			
			else if(cal.get(Calendar.DAY_OF_WEEK) < days[i]){
				alarmTimer=days[i]-cal.get(Calendar.DAY_OF_WEEK);
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);
				break;
			}
				

			else if(i==days.length-1 && cal.get(Calendar.DAY_OF_WEEK) != days[i]){
				alarmTimer=7- cal.get(Calendar.DAY_OF_WEEK) + days[0];
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);
				
			}
		}
		
		else{
			alarmTimer=singleTimeCal(cal);
			AlarmReEnrollment(context,tempSaved,position,alarmTimer);
		
		}
			
	}
	

	public int multiTimeCal(Calendar cal, int position){
		
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			
			else{
			
				if(position==days.length-1)
					return (7- cal.get(Calendar.DAY_OF_WEEK) + days[0]);
				
				else
					return (days[position+1] - days[position]);
			}
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{

			if(position==days.length-1)
				return (7- cal.get(Calendar.DAY_OF_WEEK) + days[0]);
			
			else
				return (days[position+1] - days[position]);

		}

	}

	public int singleTimeCal(Calendar cal){
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			else
				return 1;
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{
			return 1;
		}

		
	}

	public int oneDayTimeCal(Calendar cal){
		
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			else
				return 7;
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{
			return 7;
		}
		
	}
	

	public void AlarmReEnrollment(Context context,AlarmInfo tempSaved, int position, int alarmTimer) {
		long oneDay = 1000 * 60 * 60 * 24;
		Intent intent= new Intent(context,AlarmReceiver.class);

		intent.putExtra("position", position);
		intent.putExtra("requestCode", tempSaved.getRequestCode());
		intent.putExtra("repeat", tempSaved.getDay());
		intent.putExtra("time", time);
		intent.putExtra("option", tempSaved.getOption());
		
		if(tempSaved.getMessage()!=null)
			intent.putExtra("message",tempSaved.getMessage());

		if(tempSaved.getNumber()!=null)
			intent.putExtra("number",tempSaved.getNumber());

		
		intent.setAction("setAlarm");
		
		PendingIntent sender = PendingIntent.getBroadcast(context,tempSaved.getRequestCode(),
				intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();

		if (tempSaved.getActive() == 1) {
			
			if (days.length != 0) {
			
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+oneDay*alarmTimer, sender);
			
		}


		else {
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+oneDay*alarmTimer,sender);

		}
		}
	}

	public void calculateDay(){
		
			if (repeat != "") {
				days = new int[repeat.length()];

				for (int i = 0; i < repeat.length(); i++)
					days[i] = Integer.parseInt(""+ repeat.charAt(i));
			}
			
		}


	public void nextAlarm(Context context){
		Calendar cal = Calendar.getInstance();
		int nextAlarmTimer=0;
		
		if(days.length==1){
			nextAlarmTimer=7;
			changedAlarmRegister(context,nextAlarmTimer);	
		}
		
		else
			
		for(int i=0;i<days.length;i++)

			if(days[i] ==cal.get(Calendar.DAY_OF_WEEK)){
			
				if(i==days.length-1){ 
					nextAlarmTimer=7-days[i]+days[0]; 
					changedAlarmRegister(context,nextAlarmTimer);	
				}
				
				else{
					nextAlarmTimer=days[i+1]-days[i];
					changedAlarmRegister(context,nextAlarmTimer);
				}
			}
	}


	public void changedAlarmRegister(Context context,int nextAlarmTimer) {
		
		long oneDay = 1000 * 60 * 60 * 24;
		Calendar cal = Calendar.getInstance();

		Intent intent= new Intent(context,AlarmReceiver.class);
		
		intent.putExtra("position", position);
		intent.putExtra("requestCode", request);
		intent.putExtra("repeat", repeat);
		intent.putExtra("time", time);
		intent.putExtra("option", option);
		
		if(message!=null)
			intent.putExtra("message", message);

		if(number!=null)
			intent.putExtra("number", number);

		intent.setAction("setAlarm");
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context,request ,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (cal.get(Calendar.HOUR_OF_DAY) <= time[0])
		
			if (cal.get(Calendar.MINUTE) <= time[1]) {
		
				cal.set(Calendar.HOUR_OF_DAY, time[0]);
				cal.set(Calendar.MINUTE, time[1]);
				cal.set(Calendar.SECOND, 0);
				am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+oneDay*nextAlarmTimer, sender);
			}
	}
	

	public void nextActivity(Context context){
		Calendar cal = Calendar.getInstance();
		
		Intent i=null;
		if(option.equals("word"))
			i= new Intent(context,selectedWord.class);
		
		else if(option.equals("decibel"))
			i= new Intent(context,selectedDecibel.class);
			
		else if(option.equals("game"))
			i= new Intent(context,selectedMaze.class);
		else
			i=new Intent(context,selectedcatch.class);
		i.putExtra("repeat", repeat);
		i.putExtra("position", position);
		i.putExtra("request", request);
		if(message!=null)
			i.putExtra("message", message);
		
		if(number!=null)
			i.putExtra("number", number);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getActivity(context,request ,i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), sender);
		
	}

}
