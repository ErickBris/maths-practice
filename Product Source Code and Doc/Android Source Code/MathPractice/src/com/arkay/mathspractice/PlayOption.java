package com.arkay.mathspractice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Play Option Screen. User will selected Level Mode
 * @author I-BALL
 *
 */
public class PlayOption  extends Fragment implements OnClickListener {
	
	Button btnEasy, btnHard, btnChallange;
	public static String LEVEL_MODE ="levelMode";
	public static String LEVEL_TYPE ="levelType";
	public static String LEVEL_OPTION ="leveloption";
	int levelOption = 0;
	private SharedPreferences prefs;
	
	  public interface Listener {
	        public void onStartGameRequested(boolean hardMode);
	        public void onShowAchievementsRequested();
	        public void onShowLeaderboardsRequested();
	        public void onSignInButtonClicked();
	        public void onSignOutButtonClicked();
	        public void unlockAchievement(int achievementId, String fallbackString);
	        public FragmentManager getSupportManager();
	        public GamePlay getGamePlay();
	        public SharedPreferences getSharedPreferences();
	        public GameData getGameData();
	  }
	  Listener mListener = null;
	  
	  @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(
					R.layout.activity_play_option,
					container, false);
			final int[] CLICKABLES = new int[] { R.id.btn_hard,
					R.id.btn_challange };
			for (int i : CLICKABLES) {
				v.findViewById(i).setOnClickListener(this);
			}

			
			return v;
		}

		public void setListener(Listener l) {
			mListener = l;
		}

	@Override
	public void onClick(View v) {
		
		prefs = mListener.getSharedPreferences();
		int levelNo=prefs.getInt(PlayOptionAddtitionSubtraction.LEVEL_OPTION, 1);
		
		if(v.getId() == R.id.btn_hard){
			
			System.out.println("Click on hard");
			if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
				SharedPreferences.Editor edit=prefs.edit();
				edit.putInt(PlayOption.LEVEL_MODE, MathConstant.CHALLANGE_MODE);
				levelNo = mListener.getGameData().getLevelCompletedOnAdditon();
				
				if(levelNo>=1 && levelNo<=10){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_0_10);
				}else if(levelNo>=11 && levelNo <=20){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_10_20);
				}else if(levelNo>=21 && levelNo <=40){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_20_40);
				}else if(levelNo >= 40){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_40_ONWARD);
				}
				edit.commit();
				mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getGamePlay() ).addToBackStack( "tag" ).commit();
			}else{
				SharedPreferences.Editor edit=prefs.edit();
				edit.putInt(PlayOption.LEVEL_MODE, MathConstant.CHALLANGE_MODE);
				levelNo  = mListener.getGameData().getLevelCompletedOnMultiplication();
				
				if(levelNo>=1 && levelNo<=10){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_0_10);
				}else if(levelNo>=11 && levelNo <=20){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_10_20);
				}else if(levelNo>=21 && levelNo <=40){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_20_40);
				}else if(levelNo >= 41){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_40_ONWARD);
				}
				edit.commit();
				mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getGamePlay() ).addToBackStack( "tag" ).commit();
			}
			
		}
		if(v.getId() == R.id.btn_challange){
			
			System.out.println("Click on Challage");
			if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
				SharedPreferences.Editor edit=prefs.edit();
				edit.putInt(PlayOption.LEVEL_MODE, MathConstant.FREE_MODE);
				levelNo = prefs.getInt(Main.LEVEL_COMPLETED_NO_FREE_ADDITION, 1);
				if(levelNo>=1 && levelNo<=10){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_0_10);
				}else if(levelNo>=11 && levelNo <=20){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_10_20);
				}else if(levelNo>=21 && levelNo <=40){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_20_40);
				}else if(levelNo >= 41){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_40_ONWARD);
				}
				edit.commit();
				mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getGamePlay() ).addToBackStack( "tag" ).commit();
			}else{
				SharedPreferences.Editor edit=prefs.edit();
				edit.putInt(PlayOption.LEVEL_MODE, MathConstant.FREE_MODE);
				levelNo = prefs.getInt(Main.LEVEL_COMPLETED_NO_FREE_MULTIPLICATION, 1);
				
				if(levelNo>=1 && levelNo<=10){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_0_10);
				}else if(levelNo>=11 && levelNo <=20){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_10_20);
				}else if(levelNo>=21 && levelNo <=40){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_20_40);
				}else if(levelNo >= 41){
					edit.putInt(PlayOption.LEVEL_TYPE, MathConstant.LEVE_TYPE_40_ONWARD);
				}
				edit.commit();
				mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getGamePlay() ).addToBackStack( "tag" ).commit();
			}
			
		}
		
	
	}

}
