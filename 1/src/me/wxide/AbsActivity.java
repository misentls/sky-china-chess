package me.wxide;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AbsActivity extends Activity {
	protected final static int PORT=2378;
	
	protected static boolean redSide=true;//��ǰ�����ִ�����Ǻ췽���Ǻڷ�
	
	protected static GameView gv;		
	protected static ServerSocket server;		
	protected static Socket client;
	protected static String targetIp;		//�Լ�ip
	protected static String currentIp;		//�������ip
	protected static QiPan qipan=new QiPan();
	
	protected final static String  MES_SETOTHER="SET_OTHER_SIDE";
	
	public static String getMsg() throws IOException{
		 if(client==null||client.isClosed()){
			 throw new IOException();//�����ѱ��ر�
		  }
		 BufferedReader br=new BufferedReader(new InputStreamReader(client.getInputStream()));
		 
		 return br.readLine();
	}
	
    public boolean sendMsg(String msg) throws IOException{
		  if(client==null||client.isClosed()){
			 return false;//�����ѱ��ر�
		  }
		  	BufferedWriter bw;
			
				bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				bw.write(msg);
			  	bw.newLine();
			  	bw.flush();

		  return true;
		  
	  } 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		super.onCreate(savedInstanceState);
	}
	

	public void ShowMsg(String msg){
		Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	  protected String getIp(){
	    	WifiManager wifi=(WifiManager)getSystemService(WIFI_SERVICE);
	    	WifiInfo wifiinfo=wifi.getConnectionInfo();
	    	int ip=wifiinfo.getIpAddress();
	    	return intToIp(ip);
	    }
	    
	   protected String intToIp(int ip){
	    	StringBuilder sb=new StringBuilder(15);
	    	sb.append((ip)&0xff);
	    	sb.append(".");
	    	sb.append((ip>>8)&0xff);
	    	sb.append(".");
	    	sb.append((ip>>16)&0xff);
	    	sb.append(".");
	    	sb.append((ip>>24)&0xff);
	    	return sb.toString();
	    }

	   protected void startGame(){
		   
	    	Log.v("STARTGAME!", "init start game!");
	    	if(gv==null)	{gv=new GameView(this,this);}
	          setContentView(gv);
	         qipan.init();
	      
	    }
	   
	   	public static boolean isClientClose(){
	   			try {
					client.sendUrgentData(0xff);
					return false;
				} catch (IOException e) {
					return true;
					// TODO Auto-generated catch block
				}
	   		
	   	}

		   
	      public int getWidth(){
	    	  return getWindowManager().getDefaultDisplay().getWidth();  
	    	  
	      }
	      public int getHeight(){
	    	 return getWindowManager().getDefaultDisplay().getHeight();
	      }
	   
}
