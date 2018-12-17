package kr.androidteam.alarm;

import java.util.ArrayList;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AlarmContents extends Activity implements OnClickListener, OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener{


	ArrayList<String> timeTest = new ArrayList<String>();
	String string,time;
	Button btn2,btn3;
	CheckBox mon,tue,wed,thu,fri,sat,sun;
	String [] days=new String[7];
	RadioGroup rg;
	RadioButton decibel, game, word,catchMe;
	ImageButton sms,Memo;
	String number;
	String memo;
	boolean[] check=new boolean[2]; 
	 final static int DIALOG_1 =0;
	 final static int DIALOG_2 = 1;

	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_contents);
		check[0]=check[1]=false;
		
		for(int i=0;i<7;i++)
			days[i]="0";
		
		btn2=(Button)findViewById(R.id.insert);
		btn3=(Button)findViewById(R.id.cancel);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		
		mon=(CheckBox)findViewById(R.id.mon);
		thu=(CheckBox)findViewById(R.id.thu);
		wed=(CheckBox)findViewById(R.id.wed);
		tue=(CheckBox)findViewById(R.id.tue);
		fri=(CheckBox)findViewById(R.id.fri);
		sat=(CheckBox)findViewById(R.id.sat);
		sun=(CheckBox)findViewById(R.id.sun);
		
		rg=(RadioGroup)findViewById(R.id.radioGroup1);
		game=(RadioButton)findViewById(R.id.game);
		word=(RadioButton)findViewById(R.id.word);
		decibel=(RadioButton)findViewById(R.id.decibel);
		catchMe=(RadioButton)findViewById(R.id.catchme);
		inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

		sms=(ImageButton)findViewById(R.id.sms);
		sms.setOnClickListener(new OnClickListener(){


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sms.isClickable()){
					showDialog(DIALOG_1);
					check[0]=true;
					sms.setSelected(true);
					
				}
				else{
					number="";
					check[0]=false;
					sms.setSelected(false);
					
				}
				
			}

		});
		
		Memo=(ImageButton)findViewById(R.id.memo);
		Memo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Memo.isClickable()){
					showDialog(DIALOG_2);
					check[1]=true;
					Memo.setSelected(true);
					
				}
				else{
					memo="";
					check[1]=false;
					Memo.setSelected(false);
					
				}
				
			}

		});
		
		
		rg.setOnCheckedChangeListener(this);
		
		mon.setOnCheckedChangeListener(this);
		thu.setOnCheckedChangeListener(this);
		wed.setOnCheckedChangeListener(this);
		tue.setOnCheckedChangeListener(this);
		fri.setOnCheckedChangeListener(this);
		sat.setOnCheckedChangeListener(this);
		sun.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_1:
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.sendmsg, null);

			return new AlertDialog.Builder(AlarmContents.this)
					.setTitle("send messege")
					.setView(linear)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									number = phoneNumber.getText().toString();
									
									if(number!="")
										phoneNumber.setText(number);
									sms.setSelected(true);
									
									
									
								}
							}).setNegativeButton("cancel",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									check[0]=false;
									
									number="";
									phoneNumber.setText(number);
									sms.setSelected(false);
									}
							}).create();
		case DIALOG_2:
			final LinearLayout linear2 = (LinearLayout) inflater.inflate(
					R.layout.memo, null);

			return new AlertDialog.Builder(AlarmContents.this)
					.setTitle("memo")
					.setView(linear2)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear2
											.findViewById(R.id.memo);
									memo = userMemo.getText().toString();
									
									if(memo!="")
										userMemo.setText(memo);
									Memo.setSelected(true);
									

								}
							}).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear2
											.findViewById(R.id.memo);
									check[1]=false;
									
									memo="";
									userMemo.setText(memo);
									Memo.setSelected(false);
									
								}
							}).create();
		
		}
		
		return null;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		

		if(v==btn2){
		
			kr.androidteam.alarm.dragAndDrap drap=(kr.androidteam.alarm.dragAndDrap)findViewById(R.id.drag);
			if(drap.getTime()!=""){
				string=drap.getTime();
				time=drap.getTime();
			}
			
			String option="";
			String sendCheck="";
			if(string == null)
				Toast.makeText(this, "Please set a Time",Toast.LENGTH_SHORT).show();

			else if(!game.isChecked() && !word.isChecked()
					&& !decibel.isChecked() && !catchMe.isChecked())
				Toast.makeText(this, "Please check a option",Toast.LENGTH_SHORT).show();
			
			else{
				
				StringBuffer sendCheckedDays=new StringBuffer(""); 

			
				for(int i=0;i<7;i++)
					if(!days[i].equals("0"))
						sendCheckedDays.append(days[i]);
			
				if(game.isChecked())
					option="game";
				else if(word.isChecked())
					option="word";
				else if(decibel.isChecked())
					option="decibel";
				else
					option="catch";
				
				if(check[0])
					sendCheck+='1';
				if(check[1])
					sendCheck+='2';
				
				
				Intent it=new Intent(this,MainActivity.class);
				
			it.putExtra("info",new AlarmInfo(string,time,sendCheckedDays.toString(),memo,number
					,sendCheck,option,0,1,0) );
			setResult(RESULT_OK,it);
			finish();
			}
			
		}
		

		else if(v==btn3){
			Intent it=new Intent(this,MainActivity.class);
			setResult(RESULT_CANCELED,it);
			finish();
			
		}
	}



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(sun.isChecked())
			days[0]="1";
		
		else
			days[0]="0";
		
		
		if(mon.isChecked())
			days[1]="2";
		
		else
			days[2]="0";
		
		if(tue.isChecked())
			days[2]="3";
		
		else
			days[2]="0";
		
		if(wed.isChecked())
			days[3]="4";
		
		else
			days[3]="0";
		
		if(thu.isChecked())
			days[4]="5";
		
		else
			days[4]="0";
		
		if(fri.isChecked())
			days[5]="6";
		
		else
			days[5]="0";
		
		if(sat.isChecked())
			days[6]="7";
		
		else
			days[6]="0";
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
		switch (checkedId) {

		case R.id.game:
			word.setChecked(false);
			decibel.setChecked(false);
			game.setChecked(true);
			catchMe.setChecked(false);

			break;

		case R.id.word:
			game.setChecked(false);
			decibel.setChecked(false);
			word.setChecked(true);
			catchMe.setChecked(false);
			
			break;


		case R.id.decibel:
			word.setChecked(false);
			game.setChecked(false);
			decibel.setChecked(true);
			catchMe.setChecked(false);

			break;
		
		case R.id.catchme:
			word.setChecked(false);
			game.setChecked(false);
			decibel.setChecked(false);
			catchMe.setChecked(true);
			break;

		}

	}

}
