package me.wxide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends AbsActivity {
	
	private TextView tv1,tv2,tv3,tv4,tv5;
	private Button bt1,bt2;
	private Handler handle;	
	protected void initServer(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
			try {
				if(server==null||server.isClosed())	server=new ServerSocket(PORT);
	
				while(true){
					client=server.accept();
					String msg=getMsg();
					if(msg.equals("JOIN")){	//加入游戏
						String ss="RED";
						if(redSide)	{
							ss="BLK";
						}
						sendMsg(ss);
						
						targetIp=client.getInetAddress().getHostAddress();
						
						Message message=Message.obtain();
						message.obj=MES_SETOTHER;
						handle.sendMessage(message);
						break;	//跳出	循环				
					}//JOIN
					if(msg.equals("ASK")){
						sendMsg("EJCC");
					}
					
				}
				
						
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			}
			
		}).start();
	
		
	}//initServer
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ready);
		handle=new Handler(){
			public void handleMessage(Message msg){
				String message=(String)msg.obj;//obj不一定是String类，可以是别的类，看用户具体的应用
				 Log.v("R_MESSAGE", message);
				if(message.equals(MES_SETOTHER)){
					 setOtherSide();
				 }
				
				//根据message中的信息对主线程的UI进行改动
				  //……                                                      }
				}
		};
		
		
		
		  initListen();
		  //choiceRed(true);	//当前为红
	    //  choiceRed(true);//设当前为红方
		initServer();
		  
	}

	private void noIpDialog(){
		NetWorkStatus();
	}
	
	private boolean NetWorkStatus() {

		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		cwjManager.getActiveNetworkInfo();

		if (cwjManager.getActiveNetworkInfo() != null) {
		netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}

		if (netSataus) {
		Builder b = new AlertDialog.Builder(this).setTitle("没有可用的网络")
		.setMessage("是否对网络进行设置？");
		b.setPositiveButton("是", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		Intent mIntent = new Intent("/");
		ComponentName comp = new ComponentName(
		"com.android.settings",
		"com.android.settings.WirelessSettings");
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		startActivityForResult(mIntent,0); // 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写
		}
		}).setNeutralButton("否", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		dialog.cancel();
		}
		}).show();
		}

		return netSataus;
		}
		 

	private void initListen(){
		
		
		tv2=(TextView) findViewById(R.id.textView2);
		tv4=(TextView) findViewById(R.id.textView4);
		tv5=(TextView) findViewById(R.id.textView5);
		bt2=(Button) findViewById(R.id.button2);
		bt1=(Button) findViewById(R.id.button1);
		
		bt1.setOnClickListener(new View.OnClickListener() {
			//开始游戏
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				tv5.setText("等待其它玩家加入!");
				startGame();
				
			}
		});
		
		bt2.setOnClickListener(new View.OnClickListener() {
			//换桌
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				choiceRed(!redSide);
			}
		});
		
		choiceRed(redSide);
	}
	
	
	
	
    private void choiceRed(boolean flag){
    	if(flag)	{
    		tv2.setText(this.getIp());
    		tv4.setText("等待中...");
    	}	else{
    		tv4.setText(this.getIp());
    		tv2.setText("等待中...");
    	}
    	
    	redSide=flag;
    }
    

	protected void setOtherSide(){
    	
    	if(redSide){
    		tv4.setText(targetIp);
    	}else{
    		tv2.setText(targetIp);
    	}
    	bt1.setText("开始");
    }
    
  
}
