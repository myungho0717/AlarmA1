package kr.androidteam.alarm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.androidteam.alarm.R;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmWeather extends Activity implements OnClickListener{

	TextView weather,memo;
	MyWeather weatherResult;
	String repeat;
	int position;
	Button btn;
	Thread myThread;
	String message;
	ImageView weatherImage;
	String conditiontext;
	int request;
	class MyWeather{
			String conditiontext;
		String temperature;

		public String toString(){
			
			return "\n " +  temperature +"'C"+ "\n";
				
		}	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
		weather = (TextView)findViewById(R.id.weather);
		memo=(TextView)findViewById(R.id.memo);
		btn=(Button)findViewById(R.id.weatherBtn);
		weatherImage=(ImageView)findViewById(R.id.weatherImage);
		btn.setOnClickListener(this);
		Intent intent=getIntent();
		position=intent.getIntExtra("position", 0);
		request=intent.getIntExtra("request", 0);
		repeat=intent.getStringExtra("repeat");
		if(intent.getStringExtra("message")!=null){
			message=intent.getStringExtra("message");
			if(!message.equalsIgnoreCase("null"))
				memo.setText(message);
			
		}
		
		if(weatherResult != null){
			weather.setText(weatherResult.toString());
		}
		
		else{
			loadYahooWeather();
		}
	}

	protected void loadYahooWeather(){

		myThread = new Thread(new Runnable(){
        	
        	@Override
        	public void run() {
        		
        		String weatherString = QueryYahooWeather();
        		Document weatherDoc = convertStringToDocument(weatherString);
    		        
        		weatherResult = parseWeather(weatherDoc);
        		Activity parentActivity = AlarmWeather.this;
        		
        		
        		if(parentActivity != null){
        			parentActivity.runOnUiThread(new Runnable(){
            			
            			@Override
            			public void run() {
            				imageMatching();
            				weather.setText(weatherResult.toString());
            				
            			}});
        		}

        	}});
        myThread.start();

	}

	private MyWeather parseWeather(Document srcDoc){
    	
    	MyWeather myWeather = new MyWeather();


    	


    	Node conditionNode = srcDoc.getElementsByTagName("yweather:condition").item(0);
    	myWeather.conditiontext = conditionNode.getAttributes()
    			.getNamedItem("text")
    			.getNodeValue()
    			.toString();
    	conditiontext=myWeather.conditiontext;
    	
    	
    	myWeather.temperature = conditionNode.getAttributes()
    			.getNamedItem("temp")
    			.getNodeValue()
    			.toString();
    	
    	float convert= (Integer.parseInt(myWeather.temperature) -32) * 5/9;
    	myWeather.temperature=Float.toString(convert);
    	
    	return myWeather;	
    }
    
	public void imageMatching(){

		if(conditiontext.equalsIgnoreCase("tornado") ||conditiontext.equalsIgnoreCase("tropical storm") ||
				conditiontext.equalsIgnoreCase("hurricane") ||conditiontext.equalsIgnoreCase("severe thunderstorms") ||
				conditiontext.equalsIgnoreCase("thunderstorms") ||conditiontext.equalsIgnoreCase("isolated thunderstorms")
				||conditiontext.equalsIgnoreCase("scattered thunderstorms")||conditiontext.equalsIgnoreCase("thundershowers")
				||conditiontext.equalsIgnoreCase("isolated thundershowers")){
			weatherImage.setImageResource(R.drawable.lightning);
		}

		else if(conditiontext.equalsIgnoreCase("mixed rain and snow")||conditiontext.equalsIgnoreCase("mixed rain and sleet")||
				conditiontext.equalsIgnoreCase("mixed snow and sleet")||conditiontext.equalsIgnoreCase("freezing drizzle")||
				conditiontext.equalsIgnoreCase("snow flurries")||conditiontext.equalsIgnoreCase("light snow showers")||
				conditiontext.equalsIgnoreCase("blowing snow")||conditiontext.equalsIgnoreCase("snow")
				||conditiontext.equalsIgnoreCase("heavy snow")||conditiontext.equalsIgnoreCase("scattered snow showers")||
				conditiontext.equalsIgnoreCase("snow showers")){
			weatherImage.setImageResource(R.drawable.snow);
		}

		else if(conditiontext.equalsIgnoreCase("drizzle")||conditiontext.equalsIgnoreCase("freezing rain")||
		conditiontext.equalsIgnoreCase("showers")||conditiontext.equalsIgnoreCase("hail")||conditiontext.equalsIgnoreCase("sleet")
		||conditiontext.equalsIgnoreCase("mixed rain and hail")||conditiontext.equalsIgnoreCase("scattered showers")){
			weatherImage.setImageResource(R.drawable.rainy);
		}

		else if(conditiontext.equalsIgnoreCase("dust")||conditiontext.equalsIgnoreCase("foggy")||
				conditiontext.equalsIgnoreCase("haze")||conditiontext.equalsIgnoreCase("smoky")
				||conditiontext.equalsIgnoreCase("blustery")||conditiontext.equalsIgnoreCase("windy")
				||conditiontext.equalsIgnoreCase("partly cloudy")){
			weatherImage.setImageResource(R.drawable.cloudsmall);
			}

		else if(conditiontext.equalsIgnoreCase("cold")||conditiontext.equalsIgnoreCase("foggy")||
				conditiontext.equalsIgnoreCase("haze")||conditiontext.equalsIgnoreCase("smoky")
				||conditiontext.equalsIgnoreCase("blustery")||conditiontext.equalsIgnoreCase("windy")){
			weatherImage.setImageResource(R.drawable.cold);
			}

		else if(conditiontext.equalsIgnoreCase("cloudy")||conditiontext.equalsIgnoreCase("Mostly Cloudy")||
				conditiontext.equalsIgnoreCase("haze")||conditiontext.equalsIgnoreCase("smoky")
				||conditiontext.equalsIgnoreCase("blustery")||conditiontext.equalsIgnoreCase("windy")){
			weatherImage.setImageResource(R.drawable.cloudsmall);
			}

		else {
			weatherImage.setImageResource(R.drawable.sunny);
			}
		
	}
    private Document convertStringToDocument(String src){
    	
    	Document dest = null;
    	DocumentBuilderFactory dbFactory =
    			DocumentBuilderFactory.newInstance();
    	DocumentBuilder parser;
    	
    	try {
    		parser = dbFactory.newDocumentBuilder();
    		dest = parser.parse(new ByteArrayInputStream(src.getBytes()));	
    	} catch (ParserConfigurationException e1) {
    		e1.printStackTrace();
    		Toast.makeText(this,
    				e1.toString(), Toast.LENGTH_LONG).show();	
    	} catch (SAXException e) {
    		e.printStackTrace();
    		Toast.makeText(this,
    				e.toString(), Toast.LENGTH_LONG).show();	
    	} catch (IOException e) {
    		e.printStackTrace();
    		Toast.makeText(this,
    				e.toString(), Toast.LENGTH_LONG).show();	
    	}
    	
    	return dest;	
    }

    private String QueryYahooWeather(){
    	
    	String qResult = "";
    	findWoeid woeid=new findWoeid(this);
		
    	String returnWoeid=woeid.getWoeid();
    	
    	String queryString = "http://weather.yahooapis.com/forecastrss?w="+returnWoeid;
    	
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(queryString);
    	  
    	try {
    		HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
    		
    		if (httpEntity != null){
    			InputStream inputStream = httpEntity.getContent();
    			Reader in = new InputStreamReader(inputStream);
    			BufferedReader bufferedreader = new BufferedReader(in);
    			StringBuilder stringBuilder = new StringBuilder();
    	     
    			String stringReadLine = null;

    			while ((stringReadLine = bufferedreader.readLine()) != null) {
    				stringBuilder.append(stringReadLine + "\n");	
    			}
    	     
    			qResult = stringBuilder.toString();	
    		}	
    	} catch (ClientProtocolException e) {
    		e.printStackTrace();
    		Toast.makeText(this,
    				e.toString(), Toast.LENGTH_LONG).show();	
    	} catch (IOException e) {
    		e.printStackTrace();
    		Toast.makeText(this,
    				e.toString(), Toast.LENGTH_LONG).show();	
    	}
    	Log.e("sslog",qResult);
    	return qResult;	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.equals(btn)){
			stop();
		}
	}
	
	public void stop(){
		Intent intent= new Intent(this,MainActivity.class);
		
		if(repeat.length()==0)
			intent.putExtra("singleAlarm", 0);
		
		intent.putExtra("position", position);
		intent.putExtra("repeat", repeat);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent sender = PendingIntent.getActivity(this,request,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis(), sender);
		if(MainActivity.IntroAct!=null)
			MainActivity.IntroAct.finish();
		finish();
		
		
	}
	

	@Override
	 public boolean  onKeyDown(int keyCode, KeyEvent event)
	 {
	  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
		  Toast.makeText(this, "you don't use back button!!", 0).show();
		
	  return false;
	 }
	

    
}