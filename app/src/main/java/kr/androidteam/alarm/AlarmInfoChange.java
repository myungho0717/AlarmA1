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

public class AlarmInfoChange extends Activity implements OnClickListener, OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener{

	ArrayList<String> timeTest = new ArrayList<String>();
	
	String string;
	Button btn2,btn3;
	Button btn4;
	Bundle bundle;
	AlarmInfo change;
	String[] timeArray=null;
	Intent it;	
	String day;
	CheckBox mon,tue,wed,thu,fri,sat,sun;
	String[] days=new String[7];
	RadioGroup rg;
	RadioButton decibel, game, word,catched;
	String option;
	ImageButton sms,Memo;
	String number,memo;
	boolean[] check=new boolean[2];
	LayoutInflater inflater;
	kr.androidteam.alarm.dragAndDrap drag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarminfochange);
		btn2=(Button)findViewById(R.id.insert);
		btn3=(Button)findViewById(R.id.cancel);
		btn4=(Button)findViewById(R.id.delete);
		
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		
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
		catched=(RadioButton)findViewById(R.id.catchme);
		rg.setOnCheckedChangeListener(this);
	
		mon.setOnCheckedChangeListener(this);
		thu.setOnCheckedChangeListener(this);
		wed.setOnCheckedChangeListener(this);
		tue.setOnCheckedChangeListener(this);
		fri.setOnCheckedChangeListener(this);
		sat.setOnCheckedChangeListener(this);
		sun.setOnCheckedChangeListener(this);



		
		for(int i=0;i<7;i++)
			days[i]="0";
		
		
		Intent intent=getIntent();
		bundle=intent.getExtras();
		change=bundle.getParcelable("change");
		timeArray=change.getReceive().split(":");
		option=change.getOption();
		day=change.getDay();
		number=change.getNumber();
		memo=change.getMessage();
		String checkBool=change.getCheck();
		
		drag=(kr.androidteam.alarm.dragAndDrap)findViewById(R.id.drag);
		drag.setStart(timeArray[0], timeArray[1]);
		
		if(option.equals("word"))
			word.setChecked(true);
		else if(option.equals("decibel"))
			decibel.setChecked(true);
		else if(option.equals("game"))
			game.setChecked(true);
		else if(option.equals("catch"))
			catched.setChecked(true);
		
		if (checkBool.length() == 1)

			for (int i = 0; i < checkBool.length(); i++) {

				if (checkBool.charAt(0) == '1') {
					check[0] = true;
					check[1] = false;
				}

				else {
					check[0] = false;
					check[1] = true;
				}

			}

		else if(checkBool.length()==2){
			check[0]=check[1]=true;
		}
		
		else
			check[0] = check[1] = false;
		
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		sms = (ImageButton)findViewById(R.id.sms);
		
		if(check[0]){
			sms.setClickable(true);

			sms.setSelected(true);
		}
		else
			sms.setSelected(false);
		
		sms.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sms.isClickable()){
					showDialog(1);
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
		
		if(check[1]){
			Memo.setClickable(true);
			Memo.setSelected(true);
		}
	
		else
			Memo.setSelected(false);

		Memo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Memo.isClickable()){
					showDialog(2);
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

		

		if (day != "") 
			for (int i = 0; i < day.length(); i++) {
				if(1==Integer.parseInt("" + day.charAt(i))){
					sun.setChecked(true);
					days[0]="1";
				}
				else if(2==Integer.parseInt("" + day.charAt(i))){
					mon.setChecked(true);
					days[1]="2";
				}
				else if(3==Integer.parseInt("" + day.charAt(i))){
					tue.setChecked(true);
					days[2]="3";
				}
				else if(4==Integer.parseInt("" + day.charAt(i))){
					wed.setChecked(true);
					days[3]="4";
				}
				else if(5==Integer.parseInt("" + day.charAt(i))){
					thu.setChecked(true);
					days[4]="5";
				}
				else if(6==Integer.parseInt("" + day.charAt(i))){
					fri.setChecked(true);
					days[5]="6";
				}
				else if(7==Integer.parseInt("" + day.charAt(i))){
					sat.setChecked(true);
					days[6]="7";
				}
			}
		
		it=new Intent(this,MainActivity.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.sendmsg, null);

			return new AlertDialog.Builder(AlarmInfoChange.this)
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

								}
							}).setNegativeButton("cancel",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									sms.setSelected(false);
									check[0]=false;
									
									number="";
									phoneNumber.setText(number);
								}
							}).create();
		}
		
		else if(id==2){
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.memo, null);

			return new AlertDialog.Builder(AlarmInfoChange.this)
					.setTitle("memo")
					.setView(linear)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear
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
									EditText userMemo = (EditText) linear
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
					
				}
			
			if(string == null)
				Toast.makeText(this, "Please set a Time",Toast.LENGTH_SHORT).show();
			
			else if(!game.isChecked() && !word.isChecked() 
					&& !decibel.isChecked() && !catched.isChecked())
				Toast.makeText(this, "Please check a option",Toast.LENGTH_SHORT).show();

			
			else{


				
				if(game.isChecked())
					option="game";
				else if(word.isChecked())
					option="word";
				else if(decibel.isChecked())
					option="decibel";
				else
					option="catch";
				
				String sendCheck="";
				StringBuffer sendCheckedDays=new StringBuffer("");
				
				for(int i=0;i<7;i++)
					if(!days[i].equals("0"))
						sendCheckedDays.append(days[i]);
				if(check[0])
					sendCheck+='1';
				if(check[1])
					sendCheck+='2';
			
				change.setCheck(sendCheck);
			change.setDay(sendCheckedDays.toString());
			change.setTime(string);
			change.setReceive(string);
			change.setOption(option);
			change.setMessage(memo);
			change.setNumber(number);
			bundle.putParcelable("change", change);
			it.putExtras(bundle);
			setResult(Activity.RESULT_OK,it);
			finish();
			}

		 }
		
		else if(v==btn3){

			bundle.putParcelable("change", change);
			it.putExtras(bundle);
			
			setResult(Activity.RESULT_CANCELED,it);			
			finish();
			
		}
		 
		else if(v==btn4){
			
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			
			builder.setTitle("Do you want to delete it ?");
			builder.setPositiveButton("Accept",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					change.delete=1;
					bundle.putParcelable("change",change);
					it.putExtras(bundle);
					setResult(Activity.RESULT_OK,it);
					finish();

				}
			});
			
		builder.setNegativeButton("Cancel",null);
			

			builder.show();

						
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
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
			catched.setChecked(false);

			break;

		case R.id.word:
			game.setChecked(false);
			decibel.setChecked(false);
			word.setChecked(true);
			catched.setChecked(false);
			
			break;


		case R.id.decibel:
			word.setChecked(false);
			game.setChecked(false);
			decibel.setChecked(true);
			catched.setChecked(false);

			break;
		
		case R.id.catchme:
			word.setChecked(false);
			game.setChecked(false);
			decibel.setChecked(false);
			catched.setChecked(true);
			break;


	}

	}

}
