package kr.androidteam.alarm;

import kr.androidteam.alarm.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ViewHolder extends RelativeLayout implements OnCheckedChangeListener {
	 
	private View childView;
	private AlarmInfo item;
	private CheckBox check;
	private ImageView sms,memo,function;
	private TextView text,text1;
	private RelativeLayout frame;
	boolean[] checkbox=new boolean[2];
	Context context;
	int position;
	LayoutParams fl;
	
	public ViewHolder(Context context) {
		// TODO Auto-generated constructor stub
		
		super(context);	
		this.context=context;
		
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		childView=inflater.inflate(R.layout.alarm_list,null);

		fl=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

		
		addView(childView,fl);

		
		text=(TextView)findViewById(R.id.text1);
		text1=(TextView)findViewById(R.id.text2);
		check=(CheckBox)findViewById(R.id.btn1);
		frame=(RelativeLayout)findViewById(R.id.frame);
		sms=(ImageView)findViewById(R.id.message);
		memo=(ImageView)findViewById(R.id.memo);
		function=(ImageView)findViewById(R.id.function);
	}
	
	public void setMessage(AlarmInfo msg,int position){
		check.setFocusable(false);	
		item =msg;
		this.position=position;
		String time=item.getTime();
		String day=splitDay(item.getDay());
		setFunctions(item);
		
				
		if(item.getActive()==0){
			check.setChecked(false);
			frame.setBackgroundResource(R.color.gray);

			check.setButtonDrawable(R.drawable.pushsun);
			sms.setImageResource(R.drawable.offmessage);
			memo.setImageResource(R.drawable.offmemo);
			if(item.getOption().equals("word"))
				function.setImageResource(R.drawable.offshowlip);
			
			
			else if(item.getOption().equals("decibel"))
				function.setImageResource(R.drawable.offshowspeaker);
			
			else if(item.getOption().equals("game"))
				function.setImageResource(R.drawable.offshowgame);
			
			else
				function.setImageResource(R.drawable.offshowcatchme);

			
		}
		
		else{
			check.setChecked(true);
			frame.setBackgroundResource(R.color.white);
			check.setButtonDrawable(R.drawable.sun);
			sms.setImageResource(R.drawable.message);
			memo.setImageResource(R.drawable.memo);
			if(item.getOption().equals("word"))
				function.setImageResource(R.drawable.showagari);
			
			
			else if(item.getOption().equals("decibel"))
				function.setImageResource(R.drawable.showspeaker);
			
			else if(item.getOption().equals("game"))
				function.setImageResource(R.drawable.showgame);
			
			else
				function.setImageResource(R.drawable.showcatchme);
		
		}
			

		check.setOnCheckedChangeListener(this);
		
		text.setText(time);
		text1.setText(day);

	}
	
	

	public void setFunctions(AlarmInfo item){
		if (item.getCheck().length() == 1)

			for (int i = 0; i < item.getCheck().length(); i++) {

				if (item.getCheck().charAt(0) == '1') {
					checkbox[0] = true;
					checkbox[1] = false;
				}

				else {
					checkbox[0] = false;
					checkbox[1] = true;
				}

			}

		else if(item.getCheck().length()==2){
			checkbox[0]=checkbox[1]=true;
		}
		
		else
			checkbox[0] = checkbox[1] = false;

		if(checkbox[0])
			sms.setVisibility(View.VISIBLE);
		
		else
			sms.setVisibility(View.INVISIBLE);
		
		if(checkbox[1])
			memo.setVisibility(View.VISIBLE);
		
		else
			memo.setVisibility(View.INVISIBLE);
		
		if(item.getOption().equals("word"))
			function.setImageResource(R.drawable.showagari);
		
		
		else if(item.getOption().equals("decibel"))
			function.setImageResource(R.drawable.showspeaker);
		
		else if(item.getOption().equals("game"))
			function.setImageResource(R.drawable.showgame);
		
		else
			function.setImageResource(R.drawable.showcatchme);
		
	}
	
	public String splitDay(String numberedDays){
		String[] days=new String[7];
		StringBuffer sendDay=new StringBuffer("");
		
		for(int i=0;i<numberedDays.length();i++){
			
			if(numberedDays.charAt(i)=='1')
				days[6]="일";
			else if(numberedDays.charAt(i)=='2')
				days[0]="월";
			else if(numberedDays.charAt(i)=='3')
				days[1]="화";
			else if(numberedDays.charAt(i)=='4')
				days[2]="수";
			else if(numberedDays.charAt(i)=='5')
				days[3]="목";
			else if(numberedDays.charAt(i)=='6')
				days[4]="금";
			else if(numberedDays.charAt(i)=='7')
				days[5]="토";
		}
		
		for(int i=0;i<7;i++){
			
			if(days[i]!=null)
				sendDay.append(days[i]);
			else
				sendDay.append("");
		}
		return sendDay.toString();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
		if(buttonView.isChecked()){
			item.setActive(1);
			check.setChecked(true);
			check.setButtonDrawable(R.drawable.sun);
			frame.setBackgroundResource(R.color.white);

			sms.setImageResource(R.drawable.message);
			memo.setImageResource(R.drawable.memo);
			if(item.getOption().equals("word"))
				function.setImageResource(R.drawable.showagari);
			
			
			else if(item.getOption().equals("decibel"))
				function.setImageResource(R.drawable.showspeaker);
			
			else if(item.getOption().equals("game"))
				function.setImageResource(R.drawable.showgame);
			
			else
				function.setImageResource(R.drawable.showcatchme);
		
			
			((MainActivity)context).checkboxAlarmRegister(position);
			
		}
		
		else{
			item.setActive(0);
			check.setChecked(false);

			check.setButtonDrawable(R.drawable.pushsun);
			frame.setBackgroundResource(R.color.gray);
			sms.setImageResource(R.drawable.offmessage);
			memo.setImageResource(R.drawable.offmemo);
			if(item.getOption().equals("word"))
				function.setImageResource(R.drawable.offshowlip);
			
			
			else if(item.getOption().equals("decibel"))
				function.setImageResource(R.drawable.offshowspeaker);
			
			else if(item.getOption().equals("game"))
				function.setImageResource(R.drawable.offshowgame);
			
			else
				function.setImageResource(R.drawable.offshowcatchme);
		
			
			((MainActivity)context).setAlarmChecked();
			
		}
	}	
}
