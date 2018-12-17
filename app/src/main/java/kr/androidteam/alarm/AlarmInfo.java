package kr.androidteam.alarm;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmInfo implements Parcelable{

	
	String time,receive,day,message,option,number,check;

	int delete;
	int active;
	int requestCode;
	public AlarmInfo(String time,String receive, String day, String message,
			String number,String check,String option,int delete,int active,int requestCode){
		this.time=time;
		this.receive=receive;
		this.day=day;
		this.message=message;
		this.check=check;
		this.option=option;
		this.delete=delete;
		this.active=active;
		this.requestCode=requestCode;
		this.number=number;
	}
	
	public AlarmInfo(Parcel source){
		time=source.readString();
		receive=source.readString();
		day=source.readString();
		message=source.readString();
		number=source.readString();
		check=source.readString();
		option=source.readString();
		delete=source.readInt();
		active=source.readInt();
		requestCode=source.readInt();
		
	}
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getDelete() {
		return delete;
	}
	public void setDelete(int delete) {
		this.delete = delete;
	}
	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(time);
		dest.writeString(receive);
		dest.writeString(day);
		dest.writeString(message);
		dest.writeString(number);
		dest.writeString(check);
		dest.writeString(option);
		dest.writeInt(delete);
		dest.writeInt(active);
		dest.writeInt(requestCode);
	}
	
	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public static Parcelable.Creator<AlarmInfo> CREATOR= new Parcelable.Creator<AlarmInfo>() {

		@Override
		public AlarmInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new AlarmInfo(source);
		}

		@Override
		public AlarmInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new AlarmInfo[size];
		}
		
	};
	}