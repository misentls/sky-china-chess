package me.wxide;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class GameActivity extends AbsActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
			
		Log.v("STARTGAME!", "init start game!");
    	if(gv==null)	{gv=new GameView(this,this);}
          setContentView(gv);
          qipan.init();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		   if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
               dialog();
            }
		return super.onKeyDown(keyCode, event);
	}
	
	
}
