package kr.androidteam.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, OnItemSelectedListener {
	
	public static Activity IntroAct;
	
	ImageView bottom;
	RelativeLayout background;
	ImageButton btn1;
	int CREATE_REQUEST = 1;
	int DELETEorCHANGE_REQUEST = 2;
	ListView list;
	int count = 0;
	int number;
	AlertDialog.Builder builder;
	AlarmInfo tempSaved;
	dataAdapter adapter=null;
	ArrayList<AlarmInfo> alarmInfo = new ArrayList<AlarmInfo>();
	
	public static final String MYPREFS="AlarmLOG";
	final int mode=Activity.MODE_PRIVATE;
	StringBuffer savedAlarmInfo=new StringBuffer();
	
	int request;
	int[] time = new int[2];
	int[] days;
	
	boolean toastFlag=false;
	boolean onBackground=false;
	
	int icon=R.drawable.time;
	String tickerText="alarm set";
	long when=System.currentTimeMillis();
	Notification notification=new Notification(icon,tickerText,when);
	String serName=Context.NOTIFICATION_SERVICE;
	NotificationManager NM;
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntroAct=this;
		bottom=(ImageView)findViewById(R.id.bottom);
		background=(RelativeLayout)findViewById(R.id.background);
		btn1 = (ImageButton) findViewById(R.id.button1);
		btn1.setOnClickListener(this);
		list = (ListView) findViewById(R.id.list);

		SharedPreferences sh_Pref = getSharedPreferences(MYPREFS,mode);


		if(sh_Pref!=null && sh_Pref.contains("saveInfo") && sh_Pref.contains("savedInfoSize"))
			showSavedAlarmInfo();


		
		Intent i=getIntent();
		int position=i.getIntExtra("position", -1);
		int singleAlarm = i.getIntExtra("singleAlarm", -1);

		if (singleAlarm != -1 && singleAlarm == 0 && position != -1)
			alarmInfo.get(position).setActive(0);
		

		adapter = new dataAdapter(this, R.layout.alarm_list, alarmInfo);
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		list.setOnItemSelectedListener(this);
		
		NM=(NotificationManager)getSystemService(serName);
		Intent notifiIntent=new Intent(MainActivity.this,MainActivity.class);
		
		PendingIntent pi=PendingIntent.getActivity(MainActivity.this,0,
				notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NM=(NotificationManager)getSystemService(serName);
		notification.flags=Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, "alarm", "alarm set.", pi);
		setAlarmChecked();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void registerNotification(){
		
			NM.notify(1, notification);
			
		}
		
		public void notificationCancel(){
			NM.cancel(1);
		}

	public void alarmOn(){
		Resources res=getResources();
		
		TransitionDrawable drawable=(TransitionDrawable) res.getDrawable(R.drawable.wakebackground);
		background.setBackground(drawable);
		drawable.startTransition(1000);

		TransitionDrawable drawable2=(TransitionDrawable) res.getDrawable(R.drawable.wakebottom);
		bottom.setImageDrawable(drawable2);
		drawable2.startTransition(1000);
		
		btn1.setImageResource(R.drawable.wakebutton);
		TransitionDrawable drawableBtn=(TransitionDrawable) btn1.getDrawable();
		drawableBtn.startTransition(1000);
	}
	
	public void alarmOff(){
		Resources res=getResources();
		
		TransitionDrawable drawable=(TransitionDrawable) res.getDrawable(R.drawable.convertbackground);
		background.setBackground(drawable);
		drawable.startTransition(1000);

		TransitionDrawable drawable2=(TransitionDrawable) res.getDrawable(R.drawable.convertbottom);
		bottom.setImageDrawable(drawable2);
		drawable2.startTransition(1000);
		

		btn1.setImageResource(R.drawable.convertbutton);
		TransitionDrawable drawableBtn=(TransitionDrawable) btn1.getDrawable();
		drawableBtn.startTransition(1000);
	}
	
	public void backgroundAdjust(){
		int offVs=0;
		
		if(alarmInfo.size()==0){
			alarmOff();
			
			if(NM!=null){
				
				notificationCancel();
					
			}
		}

		else{
			
			for(int i=0;i<alarmInfo.size();i++){
				if(alarmInfo.get(i).getActive()==0)
					offVs++;
			}
			
			if(offVs==alarmInfo.size() && !onBackground){
				alarmOff();
				if(NM!=null){
					notificationCancel();
						
				}				
				onBackground=true;
				
			}
			
			
		}
	}
	
	public void onBackground(){
		int onVs=0;
		for(int i=0;i<alarmInfo.size();i++){
			if(alarmInfo.get(i).getActive()==1)
				onVs++;
		}
		
		if(onVs==1){
			alarmOn();
			registerNotification();
				
			onBackground=false;
		}
		
	}
	

	public void setAlarmChecked(){
		Intent intent= new Intent(this,AlarmReceiver.class);
		int[] time=new int[2];
		String[] temp=new String[2];
		PendingIntent sender;
		AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);	
		
		for(int i=0;i<alarmInfo.size();i++){
		
			if(alarmInfo.get(i).getActive()==0){
				
			temp=alarmInfo.get(i).getReceive().split(":");
			time[0]=Integer.parseInt(temp[0]);
			time[1]=Integer.parseInt(temp[1]);
			intent.putExtra("time", time);
			intent.setAction("setAlarm");
			intent.putExtra("position", i);
			intent.putExtra("requestCode", alarmInfo.get(i).getRequestCode());
			intent.putExtra("repeat", alarmInfo.get(i).getDay());
			intent.putExtra("option", alarmInfo.get(i).getOption());
			if(alarmInfo.get(i).getMessage()!=null)
				intent.putExtra("message",alarmInfo.get(i).getMessage());

			if(alarmInfo.get(i).getNumber()!=null)
				intent.putExtra("number",alarmInfo.get(i).getNumber());

			sender = PendingIntent.getBroadcast(MainActivity.this,alarmInfo.get(i).getRequestCode(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
				am.cancel(sender);
				sender.cancel();
				
			}
		}
		backgroundAdjust();
	}
	
	
	public void checkboxAlarmRegister(int position){
		calculateDayAndTime(alarmInfo.get(position));
		alarm(alarmInfo.get(position),position);
		onBackground();
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(btn1)) {
			Intent it = new Intent(this, AlarmContents.class);
			startActivityForResult(it, CREATE_REQUEST);
			
		} 
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {


		if (requestCode == CREATE_REQUEST) {
			if (resultCode == RESULT_OK) {
				

				request=(int)(Math.random()*100000 % 1000000)+
						(int)(Math.random()*1000 % 10000)+1;
				
				alarmInfo.add((AlarmInfo) intent.getParcelableExtra("info"));
				
				adapter.notifyDataSetChanged();
				
				alarmInfo.get(count).setRequestCode(request);


				toastFlag=true;
				calculateDayAndTime(alarmInfo.get(count));
				alarm(alarmInfo.get(count),count);
				onBackground();
				
				count++;
				repeatWakeUp();
				

			}
		}


		else if (requestCode == DELETEorCHANGE_REQUEST) {

			if (resultCode == RESULT_OK) {


				Bundle bundle = new Bundle();
				bundle = intent.getExtras();
				tempSaved = bundle.getParcelable("change");
				alarmInfo.set(number, tempSaved);
				if(alarmInfo.get(number).getDelete()==0){
				toastFlag=true;
				
				calculateDayAndTime(alarmInfo.get(number));
				alarm(alarmInfo.get(number),number);
				}
				

				
				else {

					alarmCancel();
					alarmInfo.remove(number);

					reRegisterAlarmAfterCancel();
					count--;
					toastFlag = false;	
					if(alarmInfo.size()==0)
						cancelWakeUp();
					
					backgroundAdjust();
					
				}
				
				adapter.notifyDataSetChanged();
			}
		}

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub
		

		
		Intent intent = new Intent(this, AlarmInfoChange.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("change", alarmInfo.get(position));
		intent.putExtras(bundle);
		number = position;
		startActivityForResult(intent, DELETEorCHANGE_REQUEST);

	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		// TODO Auto-generated method stub

		builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Do you want to delete it ?");
		

		builder.setPositiveButton("Accept",	new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub



						alarmInfo.remove(position);

						reRegisterAlarmAfterCancel();
						
						adapter.notifyDataSetChanged();
						count--;
						toastFlag = false;	
						
						if(alarmInfo.size()==0)
							cancelWakeUp();
						
						backgroundAdjust();
						
						
					}
				});


		builder.setNegativeButton("Cancel", null);
		

		builder.show();
		return true;
	}
	

	public void calculateDayAndTime(AlarmInfo tempSaved) {
		String[] convertTime;

		if (tempSaved.getDay() != "") {
			this.days = new int[tempSaved.getDay().length()];

			for (int i = 0; i < tempSaved.getDay().length(); i++)
				this.days[i] = Integer.parseInt(""+ tempSaved.getDay().charAt(i));
		}

		convertTime = tempSaved.getReceive().split(":");

		this.time[0] = Integer.parseInt(convertTime[0]);
		this.time[1] = Integer.parseInt(convertTime[1]);
		
	}
	

	
	public void AlarmEnrollment(AlarmInfo tempSaved, int position, int alarmTimer) {
		long oneDay = 1000 * 60 * 60 * 24;
		Intent intent;
		PendingIntent sender;
		AlarmManager am;
		
		intent= new Intent(this,AlarmReceiver.class);
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
		sender = PendingIntent.getBroadcast(MainActivity.this, tempSaved.getRequestCode(),intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		am = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();
		
		

		if (days.length != 0 &&tempSaved.getActive()==1) {
			
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+oneDay*alarmTimer, sender);
			
			if (toastFlag)
					Toast.makeText(this, alarmTimer+"일 후"+time[0]+"시"+
					time[1]+"분 에 울립니다.", 0).show();
			
		}


		else if(days.length == 0 && tempSaved.getActive()==1){
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+oneDay*alarmTimer,sender);

			if (toastFlag)
				Toast.makeText(this,alarmTimer + "일 후" + time[0] +
						"시" + time[1]+ "분 에 울립니다.", 0).show();
		
		}
		toastFlag = false;
	}
	

	public void alarm(AlarmInfo tempSaved, int position){
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
			AlarmEnrollment(tempSaved,position,alarmTimer);
			
		}
		
		else if(days.length>1)
			
		for(int i=0;i<days.length;i++){
			
			if(days[i] ==cal.get(Calendar.DAY_OF_WEEK)){
				alarmTimer=multiTimeCal(cal,i);
				AlarmEnrollment(tempSaved,position,alarmTimer);	
				break;
				
			}
			
			else if(cal.get(Calendar.DAY_OF_WEEK) < days[i]){
				alarmTimer=days[i]-cal.get(Calendar.DAY_OF_WEEK);
				AlarmEnrollment(tempSaved,position,alarmTimer);
				break;
			}
				

			else if(i==days.length-1 && cal.get(Calendar.DAY_OF_WEEK) != days[i]){
				alarmTimer=7- cal.get(Calendar.DAY_OF_WEEK) + days[0];
				AlarmEnrollment(tempSaved,position,alarmTimer);
				
			}
		}
		
		else{
			alarmTimer=singleTimeCal(cal);
			AlarmEnrollment(tempSaved,position,alarmTimer);
		
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

	
	public void alarmCancel(){
		Intent intent= new Intent(this,AlarmReceiver.class);
		int[] time=new int[2];
		String[] temp=new String[2];
		PendingIntent sender;
		AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);	
		for(int i=0;i<alarmInfo.size();i++){
			temp=alarmInfo.get(i).getReceive().split(":");
			time[0]=Integer.parseInt(temp[0]);
			time[1]=Integer.parseInt(temp[1]);
			intent.putExtra("time", time);
			intent.setAction("setAlarm");
			intent.putExtra("position", i);
			intent.putExtra("requestCode", alarmInfo.get(i).getRequestCode());
			intent.putExtra("repeat", alarmInfo.get(i).getDay());
			intent.putExtra("option", alarmInfo.get(i).getOption());
			
			if(alarmInfo.get(i).getMessage()!=null)
				intent.putExtra("message",alarmInfo.get(i).getMessage());

			if(alarmInfo.get(i).getNumber()!=null)
				intent.putExtra("number",alarmInfo.get(i).getNumber());

			sender = PendingIntent.getBroadcast(MainActivity.this,alarmInfo.get(i).getRequestCode(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
				am.cancel(sender);
				sender.cancel();
				backgroundAdjust();
				
			
		}
	}
	

	public void reRegisterAlarmAfterCancel(){
		
		for(int i=0;i<alarmInfo.size();i++){
		
			if(alarmInfo.get(i).getActive()!=0){
			calculateDayAndTime(alarmInfo.get(i));
		
			alarm(alarmInfo.get(i),i);
			}
		}
		
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		savedAlarmInfo();
		super.onPause();
		
	}

	

	public void savedAlarmInfo(){

		SharedPreferences sh_Pref = getSharedPreferences(MYPREFS,mode);
		SharedPreferences.Editor toEdit = sh_Pref.edit();
		savedAlarmInfo.delete(0, savedAlarmInfo.length());
		toEdit.remove("saveInfo");
		toEdit.remove("savedInfoSize");
		
		if (alarmInfo.size() != 0) {
		
			emerge();
			toEdit.putString("saveInfo", savedAlarmInfo.toString());
			toEdit.putInt("savedInfoSize", alarmInfo.size());
			
		}
		
		
		toEdit.commit();
		
	}
	
public void repeatWakeUp(){
	Intent i=new Intent(MainActivity.this,wakeUp.class);
	PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, 
			i, PendingIntent.FLAG_UPDATE_CURRENT);
	AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	Calendar cal = Calendar.getInstance();
	am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),1000 * 60 * 120 , sender);

	}
	
public void cancelWakeUp(){
	Intent i=new Intent(MainActivity.this,wakeUp.class);
	
	PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0,
			i, PendingIntent.FLAG_UPDATE_CURRENT);

	AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	am.cancel(sender);
}

	public void emerge() {

		for(int i=0;i<alarmInfo.size();i++){
		savedAlarmInfo.append(alarmInfo.get(i).getTime() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getReceive() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getDay() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getMessage() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getNumber() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getCheck() + "_");
		savedAlarmInfo.append(alarmInfo.get(i).getOption() + "_");
		savedAlarmInfo.append(Integer.toString(alarmInfo.get(i).getDelete()) + "_");
		savedAlarmInfo.append(Integer.toString(alarmInfo.get(i).getActive())+"_");
		savedAlarmInfo.append(Integer.toString(alarmInfo.get(i).getRequestCode())+"=");
		}
	}


	public void showSavedAlarmInfo() {

		SharedPreferences my_Pref = getSharedPreferences(MYPREFS, mode);
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
				
				alarmInfo.add(tempSaved);	
			}	
		}
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}


}