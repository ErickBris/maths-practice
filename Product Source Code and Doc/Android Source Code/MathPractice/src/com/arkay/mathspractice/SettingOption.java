package com.arkay.mathspractice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ToggleButton;

/**
 * Setting screen user can change setting. and it will save on SharedPreferences local.
 * @author I-BALL
 *
 */
public class SettingOption extends Activity implements OnClickListener{
	ToggleButton togBtnVibrant, togBtnSound,btn_opt_sound_effect;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		
		togBtnVibrant = (ToggleButton)findViewById(R.id.tog_btn_vibrent);
		togBtnVibrant.setOnClickListener(this);
		togBtnSound = (ToggleButton)findViewById(R.id.tog_btn_sound);
		togBtnSound.setOnClickListener(this);
		btn_opt_sound_effect = (ToggleButton)findViewById(R.id.btn_opt_sound_effect);
		btn_opt_sound_effect.setOnClickListener(this);
		
		prefs = getSharedPreferences(Main.SETTING, MODE_PRIVATE);
		editor = prefs.edit();
		
		boolean isVib =  prefs.getBoolean("vibrent", true);
		if(isVib){
			togBtnVibrant.setChecked(true);
		}else{
			togBtnVibrant.setChecked(false);
		}
		editor.commit();
		boolean isSound =  prefs.getBoolean("sound", false);
		if(isSound){
			togBtnSound.setChecked(true);
		}else{
			togBtnSound.setChecked(false);
			
		}
		boolean isSoundEffect = prefs.getBoolean("sound_effect", true);
		if(isSoundEffect){
			btn_opt_sound_effect.setChecked(true);
		}else{
			btn_opt_sound_effect.setChecked(false);
		}
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.tog_btn_sound){
			if(togBtnSound.isChecked()){
				editor.putBoolean("sound", true);
				stopService(new Intent(this, BackgroundSoundService.class));
			}else{
				editor.putBoolean("sound", false);
				
			}
			editor.commit();
		}
		if(v.getId() == R.id.tog_btn_vibrent){
			if(togBtnVibrant.isChecked()){
				editor.putBoolean("vibrent", true);
			}else{
				editor.putBoolean("vibrent", false);
			}
			editor.commit();
		}
		if(v.getId() == R.id.btn_opt_sound_effect){
			if(togBtnVibrant.isChecked()){
				editor.putBoolean("sound_effect", true);
			}else{
				editor.putBoolean("sound_effect", false);
			}
			editor.commit();
		}
		
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
}