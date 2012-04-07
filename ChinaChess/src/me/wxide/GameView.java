package me.wxide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Bitmap pic_backgroud;
	private Bitmap pic_chessgrid;
	private Bitmap 	qizibackground,qizicbackground ;
	private Bitmap cells[]=new Bitmap[14];
	private Bitmap pic_win,pic_lost;	//胜利和失败的图片
	
	private GameThread game=new GameThread();	//绘图的线程
	
	private AbsActivity activity;
	
	private SurfaceHolder holder;
	
	private QiPan qipan;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		   if(keyCode == KeyEvent.KEYCODE_BACK){
			   activity.dialog();
		   }
		return true;
	}

	private QueneThread quene;//接收到的信息的队列


	
	public GameView(Context context,AbsActivity chess) {
		
		super(context);
		System.out.println("GameView start create");
		
		this.activity=chess;
		initGameData();	//图片资源实例化
		this.qipan=AbsActivity.qipan;
		holder=getHolder();		
		holder.addCallback(this);
		//initQipan();
		quene=new QueneThread();
		quene.start();
		System.out.println("GameView create over!");
		
		// TODO Auto-generated constructor stub
	}
	
	
	private void initGameData(){
		

			pic_win=	BitmapFactory.decodeResource(getResources(), R.drawable.win);//胜利
			pic_win=	BitmapFactory.decodeResource(getResources(), R.drawable.lost);//失败
		
			cells[11] = BitmapFactory.decodeResource(getResources(), R.drawable.heishuai);//黑帅
			cells[7] = BitmapFactory.decodeResource(getResources(), R.drawable.heiju);//黑车
			cells[8] = BitmapFactory.decodeResource(getResources(), R.drawable.heima);//黑马
			cells[12] = BitmapFactory.decodeResource(getResources(), R.drawable.heipao);//黑炮
			cells[10] = BitmapFactory.decodeResource(getResources(), R.drawable.heishi);//黑士
			cells[9] = BitmapFactory.decodeResource(getResources(), R.drawable.heixiang);//黑象
			cells[13] = BitmapFactory.decodeResource(getResources(), R.drawable.heibing);//黑兵
			
			cells[4] = BitmapFactory.decodeResource(getResources(), R.drawable.hongjiang);//红将
			cells[0] = BitmapFactory.decodeResource(getResources(), R.drawable.hongju);//红车
			cells[1] = BitmapFactory.decodeResource(getResources(), R.drawable.hongma);//红马
			cells[5] = BitmapFactory.decodeResource(getResources(), R.drawable.hongpao);//红砲
			cells[3] = BitmapFactory.decodeResource(getResources(), R.drawable.hongshi);//红仕
			cells[2] = BitmapFactory.decodeResource(getResources(), R.drawable.hongxiang);//红相
			cells[6] = BitmapFactory.decodeResource(getResources(), R.drawable.hongzu);//红卒
			
		qizibackground = BitmapFactory.decodeResource(getResources(), R.drawable.qizi);//棋子的背景
		qizicbackground=BitmapFactory.decodeResource(getResources(), R.drawable.qizic);
		pic_backgroud = BitmapFactory.decodeResource(getResources(), R.drawable.bacnground);
		pic_chessgrid = BitmapFactory.decodeResource(getResources(), R.drawable.qipan);
		
	
	}
	
public Queue<String> queue= new ConcurrentLinkedQueue<String>();
	
	class QueneThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true){
					String msg=AbsActivity.getMsg();
					Log.v("Queue msg",msg);
					queue.offer(msg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	public boolean getRemoteMove(){
		
		String msg=queue.poll();
		if(msg.subSequence(0, 1).equals("M")){
			int x=Integer.valueOf(msg.substring(1,2));
			int y=Integer.valueOf(msg.substring(2,3));
			qipan.selectPlace(x, y);

		}
	 
	return true;
}
	/**
	 * 绘图线程
	 * @author f
	 *
	 */
class GameThread extends Thread{
	public GameThread(){
		Log.v("GameThread", "constructor!");
	}
	
	//查询对应图片的index
	private int getReourceid(int index){
		int res;
		if(index>0)	res= index-1;	//1-7   0-6   
		else {					
			res=6+Math.abs(index);		//   1-7   6-1  6-7
		}
		return res;
	}
	
	

	public GameThread(SurfaceHolder holder) {
		this.holder = holder;
	}
	SurfaceHolder holder;
	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		while(true){
			if(queue.size()!=0)	getRemoteMove();
			draw();
			try {
				Thread.sleep(300);	//每过300 ms绘一次图
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	private int i=0;
	
	public void draw(){
		
		if(AbsActivity.isClientClose()) toastMsg("客户端已断开！");
		Canvas c=this.holder.lockCanvas();
		c.drawBitmap(pic_backgroud, 0, 0, null);
		c.drawBitmap(pic_chessgrid, 10,10,  null);
		
		if(getWidth()==480){
			c.setDensity(240);
			zoom=1.5f;
		}	
			
			for(int i=0;i<9;i++)
				for(int j=0;j<10;j++){
					int 	index=qipan.data[i][j];
					//if(!AbsActivity.redSide)	index=qipan.data[i][9-j];
					if(index==0)	continue;
					if(qipan.isHasSource()&&qipan.isTheSource(i, j))	c.drawBitmap(qizicbackground, (i*34+10)*zoom,( j*34+13)*zoom, null);
					else c.drawBitmap(qizibackground, (i*34+10)*zoom, (j*34+13)*zoom, null);
					
					c.drawBitmap(cells[getReourceid(index)], (i*34+12)*zoom, (j*34+13)*zoom, null);

				}
			
		
		this.holder.unlockCanvasAndPost(c);
	}
	
	
}
/**
 * 	
 * @return	>0	红胜,=0	未结束   <0   黑胜利
 */

private int getWin(){
	int flag=0;
	for(int i=0;i<9;i++){
		for(int j=0;j<10;j++){
				int  index=qipan.data[i][j];
				if(Math.abs(index)==5)	flag+=index;
			}
		}
	return flag;
}

private float zoom=1f;


@Override
public boolean onTouchEvent(MotionEvent event) {
	System.out.println("TouchEvent");
	if(AbsActivity.redSide==qipan.redTure&&event.getAction()==MotionEvent.ACTION_DOWN){
		System.out.println("TouchEvent");
		int x=(int) ((event.getX()-12)/(34*zoom));
		int y=(int) ((event.getY()-13)/(35*zoom));
		if(x>=0&&x<9&&y>=0&&y<10){
			qipan.selectPlace(x, y);
			try {
				System.out.println("clientis:"+activity.client.isClosed());
				if(!activity.sendMsg(String.format("M%d%d", x,y)))	Log.v("sendMsg", "fail!") ;
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}//if end
	else{
		toastMsg("还没轮到你呢!",true);
	}
	// TODO Auto-generated method stub
	return true;
	//return super.onTouchEvent(event);
}

private void toastMsg(String msg){
	Toast t=Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT);
	t.show();
}
public void toastMsg(String msg,boolean flag){
	if(flag)	Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
	
}   	
public void myDraw(){
	if(!game.isAlive()){	//如果DrawThread没有启动，就启动这个线程
		game=new GameThread(this.holder);
		game.start();
	}
	
}

@Override
public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
	// TODO Auto-generated method stub
	
}
@Override
public void surfaceCreated(SurfaceHolder holder) {

	// TODO Auto-generated method stub
	myDraw();

}

@Override
public void surfaceDestroyed(SurfaceHolder holder) {
	// TODO Auto-generated method stub
		quene.stop();
		game.stop();
	
}
	
}
