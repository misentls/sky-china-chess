package me.wxide;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinActivity extends AbsActivity {

	private Handler handle;
	private EditText et01;
	private Button bt1;
	protected final static String MES_SET_CONN="SUCCESS CONNECT TARGET";
	protected final static String START_GAME="START_GAME";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.join);
		initListen();
		
		//initClient();
		handle=new Handler(){
			public void handleMessage(Message msg){
				String message=(String)msg.obj;//obj��һ����String�࣬�����Ǳ���࣬���û������Ӧ��
				 Log.v("SET_CONN", message);
				if(message.equals(MES_SET_CONN)){
				bt1.setText("�����ӣ��ȴ���ʼ");
				 }
				if(message.equals(START_GAME)){
				startGame();
				}
				
				//����message�е���Ϣ�����̵߳�UI���иĶ�
				  //����                                                      }
				}
		};
		
	}
	private void initClient(){
		new Thread(new Runnable(){
		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(client==null||client.isClosed()){
					System.out.println("initclient"+targetIp);
					try {
						client=new Socket(targetIp,PORT);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
					}//if over
				
				try {
					boolean flag=sendMsg("JOIN");
					Log.v("SUCC", "JOIN");
					System.out.println("initclient"+flag);
					if(!flag) Log.v("MERROR","sendMsg ERROR!");
					else{
						String msg=AbsActivity.getMsg();
						System.out.println(msg);
						if(msg.equals("RED")){
							redSide=true;
							Log.v("SUCC", "RED");
							Message message=Message.obtain();
							message.obj=START_GAME;
							handle.sendMessage(message);
						}
						else if(msg.equals("BLK")){
							Log.v("SUCC", "RED");
							redSide=false;
							
							Message message=Message.obtain();
							message.obj=START_GAME;
							handle.sendMessage(message);
							
						}else{
							Log.v("SUCC", "ERROR");
							ShowMsg("���ӳ���");
						}
					}//else voer
					
				} catch (IOException e) {
					Log.v("ERROR", "����ʧ��!");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					
					System.out.println("will sendMsg");
			
					Message message=Message.obtain();
					message.obj=MES_SET_CONN;
					handle.sendMessage(message);
					
					//setOtherSide(targetIp);
					Log.v("target", targetIp);
					
					
				}//JOIN
				
						
			
			
		}).start();
		
		
	}
	
	//init set widget listen
	private void initListen(){
		bt1=(Button) this.findViewById(R.id.button1);
	
		et01=(EditText)this.findViewById(R.id.editText1);

	bt1.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			targetIp=et01.getText().toString();
			Log.v("SET", targetIp);
			initClient();
		}
	});
	
	}
}
