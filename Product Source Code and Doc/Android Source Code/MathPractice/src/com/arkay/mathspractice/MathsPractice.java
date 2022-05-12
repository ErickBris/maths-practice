package com.arkay.mathspractice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;

public class MathsPractice extends Activity {
	protected boolean _active=true;
	protected int _splashTime=35000;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maths_practice);
		Thread splshThread = new Thread(){
			@Override
			public void run(){
					startMainScreen();
					finish();
				}
		};
        Handler handler = new Handler();
        handler.postDelayed(splshThread, 2000);
	}
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
	
	public void startMainScreen(){
		Intent inst = new Intent(this,Main.class);
		startActivity(inst);
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			_active = false;
		}
		return true;
	}
}
