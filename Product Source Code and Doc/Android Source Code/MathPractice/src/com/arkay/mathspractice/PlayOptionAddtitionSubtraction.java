package com.arkay.mathspractice;



import android.content.Intent;
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
 * On this screen user select which game he want to play means Addtion or Multiplication
 * @author I-BALL
 *
 */
public class PlayOptionAddtitionSubtraction  extends Fragment implements OnClickListener {
	
	Button btnAddition, btnSubtraction;
	public static String LEVEL_OPTION ="levelOption";
	
	public static int ADDITION =0;
	public static int MULTIPLICATION =1;
	private SharedPreferences prefs;
	
	  public interface Listener {
	        public void onStartGameRequested(boolean hardMode);
	        public void onShowAchievementsRequested();
	        public void onShowLeaderboardsRequested();
	        public void onSignInButtonClicked();
	        public void onSignOutButtonClicked();
	        public void unlockAchievement(int achievementId, String fallbackString);
	        public FragmentManager getSupportManager();
	        public PlayOption getPlayOption();
	        public SharedPreferences getSharedPreferences();
	    }
	  Listener mListener = null;
	
	  
	Intent playOptionIntent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.activity_play_option_addition_multiplication,
				container, false);
		final int[] CLICKABLES = new int[] { R.id.btn_addition,
				R.id.btn_multplication };
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
		if(v.getId() == R.id.btn_addition){
			prefs = mListener.getSharedPreferences();
			SharedPreferences.Editor edit=prefs.edit();
			System.out.println("Click on Addition");
			edit.putInt(PlayOption.LEVEL_OPTION, ADDITION);
			edit.commit();
			mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getPlayOption() ).addToBackStack( "tag" ).commit();
			
		}
		
		if(v.getId() == R.id.btn_multplication){
			prefs = mListener.getSharedPreferences();
			SharedPreferences.Editor edit=prefs.edit();
			edit.putInt(PlayOption.LEVEL_OPTION, MULTIPLICATION);
			edit.commit();
			System.out.println("Click on Multiplication");
			mListener.getSupportManager().beginTransaction().replace( R.id.fragment_container, mListener.getPlayOption() ).addToBackStack( "tag" ).commit();
		}

		
	}

	


	
}
