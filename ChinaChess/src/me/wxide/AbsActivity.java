package me.wxide;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;




public class AbsActivity extends Activity {
	protected final static int PORT=2378;
	protected Handler handle;
	protected static boolean redSide=true;//当前玩家所执的棋是红方还是黑方
	 public static ArrayList<Activity> activityList = new ArrayList<Activity>();
	protected static GameView gv;		
	protected static ServerSocket server;		
	protected static Socket client;
	protected static String targetIp;		//对家ip
	protected static String currentIp;		//本机玩家ip
	protected static QiPan qipan=new QiPan();
	
	protected static ArrayList<String> serverList=new ArrayList<String>();
	protected final static String MES_SET_CONN="SUCCESS CONNECT TARGET";
	protected final static String MES_START_GAME="START_GAME";
	protected final static String MES_SETOTHER="SET_OTHER_SIDE";
	protected final static String MES_SURE_EXIT="SURE_EXIT";
	protected final static String MES_SCAN_OVER="SCAN_OVER";
	protected final static String MES_PDIALOG_UPDATE="PDIALOG_UPDATE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activityList.add(this);	
	
		
	}
	
	public  void exitClient()
    {
      
        // 关闭所有Activity
        for (int i = 0; i < activityList.size(); i++)
        {
            if (null != activityList.get(i))
            {
                activityList.get(i).finish();
            }
        }
        ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        activityManager.restartPackage("包路径");
        System.exit(0);//Android的程序只是让Activity finish()掉,而单纯的finish掉,退出并不完全
    }
	

	
	
	protected void handleMes(String msg){
	
		if(msg.equals(MES_SURE_EXIT)){
			this.dialog();
		}
		if(msg.equals(MES_SCAN_OVER)){
			
		}
		
	}
	
	
	public static String getMsg() throws IOException{
		 if(client==null||client.isClosed()){
			 throw new IOException();//连接已被关闭
		  }
		 BufferedReader br=new BufferedReader(new InputStreamReader(client.getInputStream()));
		 
		 return br.readLine();
	}
	
    public boolean sendMsg(String msg) throws IOException{
    	
		  if(client==null||client.isClosed()){
			  
			 return false;//连接已被关闭
		  }
		  		
		  		BufferedWriter bw;
				bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				bw.write(msg);
			  	bw.newLine();
			  	bw.flush();

		  return true;
		  
	  } 
    	protected String getNetmass(){
    		WifiManager wifi=(WifiManager)getSystemService(WIFI_SERVICE);
	    	DhcpInfo wifiinfo=wifi.getDhcpInfo();
	    	int ip=wifiinfo.netmask;
	    	return intToIp(ip);	
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
		   
	    startActivity(new Intent(AbsActivity.this,GameActivity.class));
	      
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
	   	public void ShowMsg(String msg){
			Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
	 	public void ShowMsg(String msg,boolean flag){
			if(flag)
	 		Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}   	
	      public int getWidth(){
	    	  return getWindowManager().getDefaultDisplay().getWidth();  
	    	  
	      }

	      public int getHeight(){
	    	 return getWindowManager().getDefaultDisplay().getHeight();
	      }
	      /**
	       * 	判断 ip是否是正确格式的ip
	       * @param ip
	       * @return
	       */
	     public static boolean isCorrectIp(String ip){
	    	  String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"; 
		       Pattern pattern = Pattern.compile(regex); 
		        Matcher matcher = pattern.matcher(ip); 
		        return matcher.matches(); 
	     }
	     
	     protected void dialog() {
	    	  AlertDialog.Builder builder = new Builder(AbsActivity.this);
	    	  builder.setMessage("确认退出吗？");
	    	  builder.setTitle("提示");
	    	  
	    	  builder.setPositiveButton("确认", new OnClickListener() {
	    	   @Override
	    	   public void onClick(DialogInterface dialog, int which) {
	    		   exitClient();

	    	   }
	    	  });
	    	  
	    	  builder.setNegativeButton("取消", new OnClickListener() {
	    	   @Override
	    	   public void onClick(DialogInterface dialog, int which) {
	    	    dialog.dismiss();
	    	   }
	    	  });
	    	  builder.create().show();
	     }
	   
	     protected void sendMessageToHandler(String msg){
	    	 	Message message=Message.obtain();
	    	 	message.obj=msg;
				 handle.sendMessage(message);
	     }
	 	@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, 1, 1, "退出");
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			dialog();
			
			return super.onOptionsItemSelected(item);
		}
	   
}
