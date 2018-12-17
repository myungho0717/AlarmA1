package kr.androidteam.alarm;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;



public class dataAdapter extends ArrayAdapter<AlarmInfo> {

	private Context context;
	private ArrayList<AlarmInfo> alarmInfo;
	private AlarmInfo temp;
	ViewHolder holder;
	
	public dataAdapter(Context context, int layoutResourceId,
			ArrayList<AlarmInfo> alarmInfo) {
		// TODO Auto-generated constructor stub
		super(context, layoutResourceId, alarmInfo);

		this.context = context;
		this.alarmInfo = alarmInfo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = null;

		if (view == null){ 
			view=convertView;
			holder = new ViewHolder(context);
		}

		temp = alarmInfo.get(position);
		holder.setMessage(temp,position);
		view = holder;
		
		return view;
		

	}

}
