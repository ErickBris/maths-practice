package com.arkay.mathspractice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * This is Game Play Screen all square are created dynamic as code on it.
 * @author I-BALL
 *
 */
public class GamePlay extends Fragment implements OnClickListener, OnItemClickListener {

	public int MIN = 1;
	//public int MAX = 10;

	private ArrayList<ValuePare> valuePares;
	private int size = 6;

	private  long startTime = 16000;
	private long interval = 1000;
	
	private TextView txt_timer, txt_answare,txtScoreDisplay, txtLevel,btn, txtOperation;
	private Button btn_pause;
	private GridView gridview;
	
	private boolean timerHasStarted = false;
	private CountDownTimer countDownTimer;
	private boolean isPause =false;
	private Context context;
	private Vibrator vibe;
	
	private int tempSum =0;
	private int levelMode=0;
	private int levelOption=0;
	boolean isVib;
	private SharedPreferences prefs;
	private SharedPreferences.Editor eidter;
	int ansVal =0;
	private int scoreCal=0;
	Drawable numberBox, numberBoxOff;
	int currentLevel = 0; 
	int levelno = 0;  
	private ValuePare currentClickPare;
	Typeface tf;
	Random ran;
	List<ValuePare> tempValuePares;
	int levelType=0;
	
	
	RelativeLayout txtrelativelayout;
	
	private MediaPlayer background_music,_shootMP;
	private boolean isMusicSound=false;
	private boolean isSoundEffect=false;
	
	boolean isMistakeOccure = false;
	boolean isProbDifficult = false;
	
	private InterstitialAd interstitial;
	
	public interface Listener {
	        public void onStartGameRequested(boolean hardMode);
	        public void onShowAchievementsRequested();
	        public void onShowLeaderboardsRequested();
	        public void onSignInButtonClicked();
	        public void onSignOutButtonClicked();
	        public void unlockAchievement(int achievementId, String fallbackString);
	        public void displyHomeScreen();
	        public void  updateLeaderboards(int leaderboardId, int finalScore);
	        public GameData getGameData();
	        public void saveDataToCloud();
	}
	
	Listener mListener = null;

	  @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.play_game,container, false);
			tf = Typeface.createFromAsset(getActivity().getAssets(), "GOUDYSTO.TTF");
			prefs = getActivity().getSharedPreferences(Main.SETTING, getActivity().MODE_PRIVATE);
			levelMode = prefs.getInt(PlayOption.LEVEL_MODE,MathConstant.CHALLANGE_MODE);
			
			levelType  = prefs.getInt(PlayOption.LEVEL_TYPE,MathConstant.LEVE_TYPE_0_10);
			levelOption = prefs.getInt(PlayOption.LEVEL_OPTION,PlayOptionAddtitionSubtraction.ADDITION);

			txtOperation = (TextView)v.findViewById(R.id.txt_operation_selected);
			if(levelMode==MathConstant.CHALLANGE_MODE){
				size = 8;
				startTime = 16000;
				if(levelOption==PlayOptionAddtitionSubtraction.MULTIPLICATION){
					startTime = 16000;
				}
					
			}else{
				size = 7;
				levelType = 0;
			}
			
			
			
			valuePares = new ArrayList<ValuePare>();
			this.context = getActivity().getApplicationContext();
			
			if(levelOption==PlayOptionAddtitionSubtraction.ADDITION){
				if(levelMode==MathConstant.CHALLANGE_MODE){
					levelno = mListener.getGameData().getLevelCompletedOnAdditon();
					int howManyTimePlay = mListener.getGameData().getCountHowManyTimeAddMulPlay();
					howManyTimePlay++;
					unlockAddMulPlayTime(howManyTimePlay);
					mListener.getGameData().setCountHowManyTimeAddMulPlay(howManyTimePlay);
					
				}else{
					levelno = prefs.getInt(Main.LEVEL_COMPLETED_NO_FREE_ADDITION,1);
					int howManyTimePlay = mListener.getGameData().getCountHowManyTimeFreePlay();
					howManyTimePlay++;
					unlockFreePlayTime(howManyTimePlay);
					mListener.getGameData().setCountHowManyTimeFreePlay(howManyTimePlay);
				}
				
				txtOperation.setText("Addition");
				scoreCal = mListener.getGameData().getTotalAddtionScore();
				setRandomValue();
			}else{
				if(levelMode==MathConstant.CHALLANGE_MODE){
					levelno = mListener.getGameData().getLevelCompletedOnMultiplication();
					int howManyTimePlay = mListener.getGameData().getCountHowManyTimeAddMulPlay();
					howManyTimePlay++;
					unlockAddMulPlayTime(howManyTimePlay);
					mListener.getGameData().setCountHowManyTimeAddMulPlay(howManyTimePlay);
				}else{
					int howManyTimePlay = mListener.getGameData().getCountHowManyTimeFreePlay();
					howManyTimePlay++;
					unlockFreePlayTime(howManyTimePlay);
					mListener.getGameData().setCountHowManyTimeFreePlay(howManyTimePlay);
					levelno = prefs.getInt(Main.LEVEL_COMPLETED_NO_FREE_MULTIPLICATION,1);
					
				}
				mListener.getGameData().saveDataLocal(prefs);
				txtOperation.setText("Multiplication");
				scoreCal = mListener.getGameData().getTotalMultiplicationScore();
				setRandomMulValue();
			}
			
			txtLevel  = (TextView)v.findViewById(R.id.txt_level_no);
			txtLevel.setText("Level No: "+levelno);
			
			Log.i("INFO", "Level No:: "+levelno);
			Log.i("INFO", "Level Type:: "+levelType);
			Log.i("INFO", "Level Mode:: "+levelMode);
			
			txt_timer = (TextView)v.findViewById(R.id.txt_time);
			txt_answare = (TextView) v.findViewById(R.id.txt_answare);
			if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
				ansVal = getNextSumValue(1);
			}else{
				ansVal = getNextMulValue(1);
			}
			
			txt_answare.setText(""+ansVal);
			
			txtScoreDisplay = (TextView)v.findViewById(R.id.txt_score_display);
			
			txtScoreDisplay.setText(""+scoreCal);
			
			btn_pause = (Button) v.findViewById(R.id.btn_pause);
			
			vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
			
			
			eidter = prefs.edit();

			numberBox = getResources().getDrawable(R.drawable.button_white);
			numberBoxOff = getResources().getDrawable(R.drawable.button_blue);
			if(levelMode!=2){
				restartCounter();
			}
			isVib =  prefs.getBoolean("vibrent", true);
			isMusicSound = prefs.getBoolean("sound", false);
			if(isMusicSound){
				playBackgroundMusic();
			}
			isSoundEffect= prefs.getBoolean("soundeffect", true);
			
			gridview = (GridView) v.findViewById(R.id.gridview);
			gridview.setAdapter(new MyAdapter(this.context));
			
		
			gridview.setNumColumns(size);
			
			gridview.setOnItemClickListener(this);
		
			if(levelMode==2){
				btn_pause.setBackgroundColor(Color.TRANSPARENT);
				txt_timer.setBackgroundColor(Color.TRANSPARENT);
				RelativeLayout clock_layout = (RelativeLayout)v.findViewById(R.id.clock_layout);
				if(clock_layout!=null){
					clock_layout.setBackgroundColor(Color.TRANSPARENT);
				}
				txt_timer.setText("");
				txtScoreDisplay.setText("00");
			}else{
				btn_pause.setOnClickListener(this);
			}
			
			 /** Create ad request. */
		    Resources ress = getResources();
		    /**set testmost false when publish app*/
		    boolean isTestMode = ress.getBoolean(R.bool.istestmode); 
		    AdRequest adRequest =null;
		    if(isTestMode){
		    	  adRequest = new AdRequest.Builder()
		         .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		         .addTestDevice("B15149A4EC1ED23173A27B04134DD483").build();
		    }else{
		    	 adRequest = new AdRequest.Builder().build();
		    }
			 
			// Create the interstitial.
		    interstitial = new InterstitialAd(getActivity());
		    interstitial.setAdUnitId(getString(R.string.admob_interstitial_ads));

		   
		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
		    
			return v;
		}

		public void setListener(Listener l) {
			mListener = l;
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			if(countDownTimer!=null){
				countDownTimer.cancel();
			}
			Log.i("INOF", "Stop Call");
			if(countDownTimer!=null){
				
				countDownTimer.cancel();
				startTime=   startTime + 1000;
				btn_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_150));
			}
			if(prefs.getBoolean("sound", true)){
				getActivity().stopService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
				//stopService(svc);
			}
			if(background_music!=null){
				if(background_music.isPlaying()){
					background_music.stop();
				}
			}
		}
		
	@Override
			public void onResume() {
				// TODO Auto-generated method stub
				super.onResume();
				Log.i("INOF", "Restart Call");
				if(levelMode!=2){
					if(countDownTimer!=null){
						restartCounter();
						btn_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_150));
					}else{
						restartCounter();
					}
				}
				if(background_music!=null){
					if(!background_music.isPlaying()){
						_shootMP = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.completed_1);
						playBackgroundMusic();
					}
				}
		}

	/** 
	 * Create Adapter for set cell (Square)
	 * @author I-BALL
	 *
	 */

	public class MyAdapter extends BaseAdapter {

		private Context mContext;

		public MyAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return valuePares.size();
		}

		@Override
		public Object getItem(int arg0) {
			return valuePares.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View grid;
			
			if (convertView == null) {
				grid = new View(mContext);
				LayoutInflater inflater = getActivity().getLayoutInflater();
				grid = inflater.inflate(R.layout.mygrid_layout, parent, false);
				
				  
		           
			} else {
				grid = (View) convertView;
			}


			btn = (TextView) grid.findViewById(R.id.textView1);
			
			btn.setTypeface(tf);
			currentClickPare = valuePares.get(position);
			if(currentClickPare.isTempMark()){
				grid.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_blue));
			}
			
			int val = currentClickPare.getValue();
			if(isPause){
				if(!currentClickPare.isIsMark()){
					btn.setText("?");
				}else{
					btn.setText("");
				}
					btn.setTextColor(Color.RED);
			}else{
				if(currentClickPare.isIsMark()==true){
					
					txtrelativelayout = (RelativeLayout)grid.findViewById(R.id.txtrelativelayout);
					txtrelativelayout.setBackgroundColor(Color.TRANSPARENT);
					txtrelativelayout.setClickable(false);
					txtrelativelayout.setVisibility(View.GONE);
					btn.setText("");
					
				}else{
					btn.setText("" + val);
					
				}
				
				switch(val){
					case 1:
						btn.setTextColor(Color.RED);
					break;
					case 2:
						btn.setTextColor(getResources().getColor(R.color.RoyalBlue));
					break;
					case 3:
						btn.setTextColor(getResources().getColor(R.color.MyNine));
					break;
					case 4:
						btn.setTextColor(getResources().getColor(R.color.MyDarkGreen));
					break;
					case 5:
						btn.setTextColor(Color.MAGENTA);
					break;
					case 6:
						btn.setTextColor(getResources().getColor(R.color.Aqua));
					break;
					case 7:
						btn.setTextColor(Color.BLUE);
					break;
					case 8:
						btn.setTextColor(getResources().getColor(R.color.MyDarkOrange));
					break;
					case 9:
						btn.setTextColor(getResources().getColor(R.color.purple));
					break;

				}
				
			}
			return grid;
		}
		

		
	}
	public void restartCounter(){
		if(countDownTimer!=null){
			countDownTimer.cancel();
		}
		countDownTimer = null;
		countDownTimer = new CountDownTimer(startTime, interval) {

			public void onTick(long millisUntilFinished) {
				timerHasStarted = true;
				startTime = millisUntilFinished;
				txt_timer.setText("" + millisUntilFinished / 1000);
			}

			public void onFinish() {
				
				txt_timer.setText("00");
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean(Main.IS_LAST_LEVEL_COMPLETED, false);
				edit.putInt(Main.LEVEL_COMPLETED, levelno);
				edit.putInt(Main.LAST_PLAY_LEVEL_OPTION,levelOption);
				int thisLevelScore = 0;
				//THIS_lEVEL_TOTAL_SCORE
				if(levelOption==PlayOptionAddtitionSubtraction.ADDITION){
					mListener.getGameData().setTotalAddtionScore(scoreCal);
					mListener.getGameData().setLevelCompletedOnAdditon(levelno);
					thisLevelScore = scoreCal - prefs.getInt(Main.TOTAL_SCORE_ADDITION,0);
					unlockAdditonScoreAchivement(scoreCal);
					unlockAdditionLevelAchivement(levelno);
					mListener.updateLeaderboards(R.string.leaderboard_addition_challenge, scoreCal);
				}else{
					mListener.getGameData().setTotalMultiplicationScore(scoreCal);
					mListener.getGameData().setLevelCompletedOnMultiplication(levelno);
					thisLevelScore = scoreCal - prefs.getInt(Main.TOTAL_SCORE_MULTIPLICATION,0);
					unlockMultiplicationScoreAchivement(scoreCal);
					unlockMultiplicationLevelAchivement(levelno);
					mListener.updateLeaderboards(R.string.leaderboard_multiplication_challenge, scoreCal);
				}
				edit.putInt(Main.THIS_lEVEL_TOTAL_SCORE,thisLevelScore);
				
				edit.commit();
				System.out.println("TemP: "+mListener.getGameData().getCountHowManyTimeAddMulPlay());
				mListener.getGameData().saveDataLocal(prefs);
				mListener.saveDataToCloud();
				displayInterstitial();
				QuizCompletedDialog cdd=new QuizCompletedDialog(getActivity());
				cdd.show(); 
				
			}
		}.start();
	}
	
	
	/**
	 * Rendom Valud set for addition on Array List.
	 */
	public void setRandomValue() {
		ran = new Random();
		int row = size +1;
		int valset = size * row;
		for (int i = 0; i < valset; i++) {
			int val = ran.nextInt(9) + 1;
			valuePares.add(new ValuePare(false, val, false));
		}
		
	}
	/**
	 * Rendom Value for set  Multiplication on Array List.
	 */
	public void setRandomMulValue() {
		int row = size +1;
		ran = new Random();
		int valset = size * row;
		for (int i = 0; i < valset; i++) {
			int val = ran.nextInt(8) + 2;
			valuePares.add(new ValuePare(false, val, false));
		}
	}

	@Override
	public void onClick(View v) {
		
		if (!timerHasStarted)
		{
			gridview.setAdapter(new MyAdapter(context));
			restartCounter();
			isPause = false;
			btn_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_150));
			timerHasStarted = true;
		}
		else{

			resetAdaptor();
			if(countDownTimer!=null){
				countDownTimer.cancel();
			}
			countDownTimer = null;
			timerHasStarted = false;
			isPause=true;
			btn_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_150));
		}
		
	}
	public void resetAdaptor(){
		gridview.setAdapter(new MyAdapter(context));
	}
	/**
	 * Get rendom value for next addition value according remainding cell
	 * @param level
	 * @return
	 */
	public int getNextSumValue(int level){
		int sumVal1=0;
		if (tempValuePares != null) {
			tempValuePares.clear();
		}
		tempValuePares = new ArrayList<ValuePare>();
		for (ValuePare valuePare : valuePares) {
			if (!valuePare.isIsMark()) {
				tempValuePares.add(valuePare);
			}
		}
		int tempSize = tempValuePares.size();
		Collections.shuffle(tempValuePares);

		if (tempSize == 0) {
			sumVal1 = 0;
			return sumVal1;
		}
		
		switch(level){
			case MathConstant.LEVE_TYPE_0_10:
				switch(tempSize){
				case 1:
					sumVal1 = tempValuePares.get(0).getValue();
					break;
				default:
					sumVal1 = tempValuePares.get(0).getValue()
					+ tempValuePares.get(1).getValue();
					break;
				}
			break;
			case MathConstant.LEVE_TYPE_10_20:
					switch(tempSize){
					case 1:
						sumVal1 = tempValuePares.get(0).getValue();
						break;
					case 2:
						sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue();
						break;
					case 3:
						sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue();
						break;
					default:
			           sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue() + tempValuePares.get(3).getValue();
						break;
					}
		
			break;
			case MathConstant.LEVE_TYPE_20_40:
					switch(tempSize){
					case 1:
						sumVal1 = tempValuePares.get(0).getValue();
						break;
					case 2:
						sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue();
						break;
					case 3:
						sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue();
						break;
					case 4:
						sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() +  tempValuePares.get(2).getValue() +  tempValuePares.get(3).getValue();
						break;
					default:
			            sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue() + tempValuePares.get(3).getValue() + tempValuePares.get(4).getValue();
						break;
					}
					
			case MathConstant.LEVE_TYPE_40_ONWARD:
				switch(tempSize){
				case 1:
					sumVal1 = tempValuePares.get(0).getValue();
					break;
				case 2:
					sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue();
					break;
				case 3:
					sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue();
					break;
				case 4:
					sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() +  tempValuePares.get(2).getValue() +  tempValuePares.get(3).getValue();
					break;
				case 5:
					sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() +  tempValuePares.get(2).getValue() +  tempValuePares.get(3).getValue() +  tempValuePares.get(4).getValue();
					break;
				default:
		            sumVal1 = tempValuePares.get(0).getValue() + tempValuePares.get(1).getValue() + tempValuePares.get(2).getValue() + tempValuePares.get(3).getValue() +  tempValuePares.get(4).getValue() +  tempValuePares.get(5).getValue();

		            break;
				}
			break;
		}
		if(sumVal1==54 || sumVal1 == 63 || sumVal1 == 72 ||  sumVal1 >= 80){
			isProbDifficult = true;
		}
			return sumVal1;
	}
	/**
	 * Get rendom value for next Multiplication value according remainding cell
	 * @param level
	 * @return
	 */
	public int getNextMulValue(int level){
		int mulVal=0;
		if (tempValuePares != null) {
			tempValuePares.clear();
		}
		tempValuePares = new ArrayList<ValuePare>();
		for (ValuePare valuePare : valuePares) {
			if (!valuePare.isIsMark()) {
				tempValuePares.add(valuePare);
			}
		}
		int tempSize = tempValuePares.size();
		Collections.shuffle(tempValuePares);

		if (tempSize == 0) {
			mulVal = 0;
			return mulVal;
		}
		
		switch(level){
			case MathConstant.LEVE_TYPE_0_10:
				switch(tempSize){
				case 1:
					mulVal = tempValuePares.get(0).getValue();
					break;
				default:
					mulVal = tempValuePares.get(0).getValue()
					* tempValuePares.get(1).getValue();
					break;
				}
			break;
			case MathConstant.LEVE_TYPE_10_20:
					switch(tempSize){
					case 1:
						mulVal = tempValuePares.get(0).getValue();
						break;
					case 2:
						mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue();
						break;
					case 3:
						mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
						break;
					default:
			           mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue();
						break;
					}
		
			break;
			case MathConstant.LEVE_TYPE_20_40:
					switch(tempSize){
					case 1:
						mulVal = tempValuePares.get(0).getValue();
						break;
					case 2:
						mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue();
						break;
					case 3:
						mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue()  ;
						break;
					case 4:
						mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
						break;
					default:
			            mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue()  ;
						break;
					}
			case MathConstant.LEVE_TYPE_40_ONWARD:
				switch(tempSize){
				case 1:
					mulVal = tempValuePares.get(0).getValue();
					break;
				case 2:
					mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue();
					break;
				case 3:
					mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
					break;
				case 4:
					mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
					break;
				case 5:
					mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
					break;
				default:
		            mulVal = tempValuePares.get(0).getValue() * tempValuePares.get(1).getValue() ;
					break;
				}

			break;
		}
		return mulVal;
	}

	/**
	 * Add cell value click listing check all condition that required. like check is alredy selected cell then count sum and mul
	 * if sum or mul completed then remove that cell on grid.
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		currentClickPare = valuePares.get(position);
		if(currentClickPare.isMark() || isPause){
			return;
		}
		if(currentClickPare.isTempMark()){
			if(levelOption==PlayOptionAddtitionSubtraction.ADDITION){
				tempSum  = tempSum - currentClickPare.getValue();
				valuePares.get(position).setTempMark(false);
				v.setBackgroundDrawable(numberBox);
			}else{
				//for multiplication
				tempSum  = tempSum / currentClickPare.getValue();
				valuePares.get(position).setTempMark(false);
				v.setBackgroundDrawable(numberBox);
			}
		}else{
			if(levelOption==PlayOptionAddtitionSubtraction.ADDITION){
				tempSum  = tempSum + currentClickPare.getValue();
				v.setBackgroundDrawable(numberBoxOff);
				valuePares.get(position).setTempMark(true);
			}else{
				//for multiplication
				if(tempSum==0){
					tempSum=1;
				}
				tempSum  = tempSum * currentClickPare.getValue();
				v.setBackgroundDrawable(numberBoxOff);
				valuePares.get(position).setTempMark(true);
			}
		}
		
				boolean issetTrue=false;
				
				if(tempSum == ansVal){
					for(ValuePare val : valuePares){
						if(val.isTempMark()==true){
							val.setIsMark(true);
							issetTrue=true;
						}
						val.setTempMark(false);
					}
					resetAdaptor();
					
					if(isVib){
						//vibe.vibrate(50);
						shootSound();
					}
					
					tempSum=0;
					switch(levelMode){
					case 1:
						if(countDownTimer != null){
							countDownTimer.cancel();
							countDownTimer=null;
						}
						try{
							int timeRemainding = Integer.parseInt(txt_timer.getText().toString());
							System.out.println("Time Remainding:: "+timeRemainding);
							unlockNarrowEscape(timeRemainding);
						}catch(NumberFormatException nfe){
							
						}
						
						int score =0;
						if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
							score =  (Integer.parseInt(""+txt_timer.getText())* 2 * levelno) + 30;
							startTime = 16000;
						}else{
							score =  (Integer.parseInt(""+txt_timer.getText())* 2 * levelno) + 50;
							startTime = 16000;
						}
						
						scoreCal = scoreCal + score;
						txtScoreDisplay.setText(""+scoreCal);
						restartCounter();
						
							
					}
					
				}
				if(tempSum > ansVal){
					for(ValuePare val : valuePares){
						val.setTempMark(false);
						isMistakeOccure=true;
					}
					
					vibe.vibrate(30);
					resetAdaptor();
					tempSum = 0;
					if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
						switch(levelType){
						case MathConstant.LEVE_TYPE_0_10:
							ansVal = getNextSumValue(MathConstant.LEVE_TYPE_0_10);
							break;
						case MathConstant.LEVE_TYPE_10_20:
							ansVal = getNextSumValue(MathConstant.LEVE_TYPE_10_20);
							break;
						case MathConstant.LEVE_TYPE_20_40:
							ansVal = getNextSumValue(MathConstant.LEVE_TYPE_20_40);
							break;
						case MathConstant.LEVE_TYPE_40_ONWARD:
							ansVal = getNextSumValue(MathConstant.LEVE_TYPE_40_ONWARD);
							break;
						default:
							ansVal = getNextSumValue(MathConstant.LEVE_TYPE_0_10);
							break;
						}
					}else{
						switch(levelType){
						case MathConstant.LEVE_TYPE_0_10:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_0_10);
							break;
						case MathConstant.LEVE_TYPE_10_20:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_10_20);
							break;
						case MathConstant.LEVE_TYPE_20_40:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_20_40);
							break;
						case MathConstant.LEVE_TYPE_40_ONWARD:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_40_ONWARD);
							break;
						default:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_0_10);
							break;
						}
					}

					txt_answare.setText(""+ansVal);
				}
				if(issetTrue){
					if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
						switch(levelType){
							case 0:
								ansVal = getNextSumValue(MathConstant.LEVE_TYPE_0_10);
								break;
							case 1:
								ansVal = getNextSumValue(MathConstant.LEVE_TYPE_10_20);
								break;
							case 2:
								ansVal = getNextSumValue(MathConstant.LEVE_TYPE_20_40);
								break;
							default:
								ansVal = getNextSumValue(MathConstant.LEVE_TYPE_40_ONWARD);
								break;
						}
					}else{
						switch(levelType){
						case MathConstant.LEVE_TYPE_0_10:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_0_10);
							break;
						case MathConstant.LEVE_TYPE_10_20:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_10_20);
							break;
						case MathConstant.LEVE_TYPE_20_40:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_20_40);
							break;
						case MathConstant.LEVE_TYPE_40_ONWARD:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_40_ONWARD);
							break;
						default:
							ansVal = getNextMulValue(MathConstant.LEVE_TYPE_0_10);
							break;
					}
					}
					if(ansVal==0){
						if(levelOption == PlayOptionAddtitionSubtraction.ADDITION){
							switch(levelMode){
							case 0:
								eidter.putInt("level1add", scoreCal);
								levelno = levelno + 1;
								eidter.putInt("easyLeveladd", levelno);
								eidter.commit();
								break;
							case 1:
								eidter.putInt("level2add", scoreCal);
								levelno = levelno + 1;
								eidter.putInt("hardLeveladd", levelno);
								eidter.commit();
								break;
							default:
								levelno = levelno + 1;
								eidter.putInt("freeplayadd", levelno);
								break;
							}
						}else{
							switch(levelMode){
							case 0:
								eidter.putInt("level1mul", scoreCal);
								levelno = levelno + 1;
								eidter.putInt("easyLevelmul", levelno);
								eidter.commit();
								break;
							case 1:
								eidter.putInt("level2mul", scoreCal);
								levelno = levelno + 1;
								eidter.putInt("hardLevelmul", levelno);
								eidter.commit();
								break;
							default:
								levelno = levelno + 1;
								eidter.putInt("freeplaymul", levelno);
								break;
							}
						}
						SharedPreferences.Editor edit = prefs.edit();
						edit.putBoolean(Main.IS_LAST_LEVEL_COMPLETED, true);
						edit.putInt(Main.LEVEL_COMPLETED, levelno);
						edit.putInt(Main.LAST_PLAY_LEVEL_OPTION,levelOption);
						int thisLevelScore =0;
						if(levelOption==PlayOptionAddtitionSubtraction.ADDITION){
							thisLevelScore = scoreCal - prefs.getInt(Main.TOTAL_SCORE_ADDITION,0);
							mListener.getGameData().setTotalAddtionScore(scoreCal);
							edit.putInt(Main.TOTAL_SCORE_ADDITION,scoreCal);
							mListener.getGameData().setLevelCompletedOnAdditon(levelno);
							unlockAdditonScoreAchivement(scoreCal);
							unlockAdditionLevelAchivement(levelno);
							mListener.updateLeaderboards(R.string.leaderboard_addition_challenge, scoreCal);
						}else{
							thisLevelScore = scoreCal - prefs.getInt(Main.TOTAL_SCORE_MULTIPLICATION,0);
							mListener.getGameData().setTotalMultiplicationScore(scoreCal);
							mListener.getGameData().setLevelCompletedOnMultiplication(levelno);
							unlockMultiplicationLevelAchivement(levelno);
							unlockMultiplicationScoreAchivement(scoreCal);
							mListener.updateLeaderboards(R.string.leaderboard_multiplication_challenge, scoreCal);
						}
						System.out.println("IS Perfet : "+isMistakeOccure);
						if(isMistakeOccure==false){
							mListener.unlockAchievement(R.string.achievement_perfectionist, "perfectionist");
						}
						
						edit.putInt(Main.THIS_lEVEL_TOTAL_SCORE,thisLevelScore);
						edit.commit();
						if(countDownTimer!=null){
							countDownTimer.cancel();
						}
						
						mListener.getGameData().saveDataLocal(prefs);
						mListener.saveDataToCloud();
						
						displayInterstitial();
						QuizCompletedDialog cdd=new QuizCompletedDialog(getActivity());
						cdd.show(); 
						
						
					}else{
						txt_answare.setText(""+ansVal);
						issetTrue=false;
					}
					
				}
	}
	
	
	/**
	 * Google game service unlock Achivement according to how many time play Multiplication
	 * @param playTime
	 */
	private void unlockAddMulPlayTime(int playTime){
		if(playTime>=2 && playTime<=3){
			mListener.unlockAchievement(R.string.achievement_good_luck, "Good Luck");
		}
		if(playTime>=50 && playTime<=51){
			mListener.unlockAchievement(R.string.achievement_fifty, "Fifty");
		}
		if(playTime>=100 && playTime<=101){
			mListener.unlockAchievement(R.string.achievement_hundred, "Hundred");
		}
		if(playTime>=1000 && playTime<=101){
			mListener.unlockAchievement(R.string.achievement_thousand, "Thousand");
		}
		if(playTime>=2500 && playTime<=2501){
			mListener.unlockAchievement(R.string.achievement_no_higher, "No Higher");
		}
	}
	
	/**
	 * Google game service unlock Achivement according to how many time play Free Play
	 * @param playTime
	 */
	private void unlockFreePlayTime(int playTime){
		if(playTime>=2 && playTime<=3){
			mListener.unlockAchievement(R.string.achievement_basic_in_maths, "Basic in Maths");
		}
		if(playTime>=10 && playTime<=11){
			mListener.unlockAchievement(R.string.achievement_starting_in_maths, "Starting in Maths");
		}
		if(playTime>=20 && playTime<=21){
			mListener.unlockAchievement(R.string.achievement_biggner_in_maths, "Biggner in Maths");
		}
		if(playTime>=30 && playTime<=31){
			mListener.unlockAchievement(R.string.achievement_learner_in_maths, "Learner in Maths");
		}
		
	}
	
	/**
	 * Google game service unlock Achivement according to how many time play addition
	 * @param playTime
	 */
	private void unlockAdditonScoreAchivement(int totalScore){
		if(totalScore>=5000 && totalScore<=6000){
			mListener.unlockAchievement(R.string.achievement_good_in_addition, "Good in addition");
		}
		if(totalScore>=25000 && totalScore<=26000){
			mListener.unlockAchievement(R.string.achievement_cool_in_addition, "Cool in additionr");
		}
		if(totalScore>=50000 && totalScore<=51000){
			mListener.unlockAchievement(R.string.achievement_very_good_in_addtion, "Very good in addtion");
		}
		if(totalScore>=100000 && totalScore<=101000){
			mListener.unlockAchievement(R.string.achievement_fentastic_in_addtion, "Fentastic in addtion");
		}
		if(totalScore>=200000 && totalScore<=201000){
			mListener.unlockAchievement(R.string.achievement_phenomenal_in_addition, "Phenomenal in addition");
		}
		if(totalScore>=300000 && totalScore<=301000){
			mListener.unlockAchievement(R.string.achievement_perfect_in_addition, "Perfect in addition");
		}
		if(totalScore>=500000 && totalScore<=501000){
			mListener.unlockAchievement(R.string.achievement_arkay_maths_additon_master_award, "Maths additon master award");
		}
		
	}
	
	/**
	 * Google game service unlock Achivement according to How Many score on Muliplication
	 * @param playTime
	 */
	private void unlockMultiplicationScoreAchivement(int totalScore){
		if(totalScore>=5000 && totalScore<=6000){
			mListener.unlockAchievement(R.string.achievement_good_in_multiplication, "Good in multiplication");
		}
		if(totalScore>=25000 && totalScore<=26000){
			mListener.unlockAchievement(R.string.achievement_cool_in_multiplication, "Cool in multiplication");
		}
		if(totalScore>=50000 && totalScore<=51000){
			mListener.unlockAchievement(R.string.achievement_very_good_in_multiplication, "Very good in multiplication");
		}
		if(totalScore>=100000 && totalScore<=101000){
			mListener.unlockAchievement(R.string.achievement_fentastic_in_multiplication, "Fentastic in multiplication");
		}
		if(totalScore>=200000 && totalScore<=201000){
			mListener.unlockAchievement(R.string.achievement_phenomenal_in_multiplication, "Phenomenal in multiplication");
		}
		if(totalScore>=300000 && totalScore<=301000){
			mListener.unlockAchievement(R.string.achievement_perfect_in_multiplication, "Perfect in multiplication");
		}
		if(totalScore>=500000 && totalScore<=501000){
			mListener.unlockAchievement(R.string.achievement_arkay_maths_multiplication_master_award, "Arkay maths multiplication master award");
		}
		
	}
	/**
	 * Google game service unlock Achivement according to How Many level Completed on Muliplication
	 * @param playTime
	 */
	private void unlockMultiplicationLevelAchivement(int lelvelCompleted){
		if(lelvelCompleted>=10 && lelvelCompleted<=11){
			mListener.unlockAchievement(R.string.achievement_right_track_on_multiplication, "Right track on multiplication");
		}
		if(lelvelCompleted>=20 && lelvelCompleted<=21){
			mListener.unlockAchievement(R.string.achievement_good_in_maths_multiplication, "Good in maths multiplication");
		}
		if(lelvelCompleted>=50 && lelvelCompleted<=51){
			mListener.unlockAchievement(R.string.achievement_master_in_maths_multiplication, "Master in maths multiplication");
		}
		if(lelvelCompleted>=100 && lelvelCompleted<=101){
			mListener.unlockAchievement(R.string.achievement_expert_in_maths_multiplication, "Expert in maths multiplication");
		}
	}
	/**
	 * Google game service unlock Achivement according to How Many Level Completed on Addition
	 * @param playTime
	 */
	private void unlockAdditionLevelAchivement(int lelvelCompleted){
		if(lelvelCompleted>=1 && lelvelCompleted<=2){
			mListener.unlockAchievement(R.string.achievement_beginner, "Beginner");
		}
		if(lelvelCompleted>=2 && lelvelCompleted<=3){
			mListener.unlockAchievement(R.string.achievement_started, "Started");
		}
		if(lelvelCompleted>=10 && lelvelCompleted<=11){
			mListener.unlockAchievement(R.string.achievement_right_track_on_addition, "Right track on addition");
		}
		if(lelvelCompleted>=20 && lelvelCompleted<=21){
			mListener.unlockAchievement(R.string.achievement_good_in_maths_addition, "Good in maths addition");
		}
		if(lelvelCompleted>=50 && lelvelCompleted<=51){
			mListener.unlockAchievement(R.string.achievement_master_in_maths_addition, "Master in maths addition");
		}
		if(lelvelCompleted>=100 && lelvelCompleted<=101){
			mListener.unlockAchievement(R.string.achievement_expert_in_maths_addition, "Expert in maths addition");
		}
	}
	/**
	 * Google game service unlock Achivement when get narrow escape user means  small time remain and completed problem
	 * @param playTime
	 */
	private void unlockNarrowEscape(int remaindSecond){
		if(remaindSecond ==1){
			mListener.unlockAchievement(R.string.achievement_narrow_escape, "narrow_escape");
		}
		if(remaindSecond==9){
			mListener.unlockAchievement(R.string.achievement_quick_solver, "quick_solver");
		}
		if(isProbDifficult){
			if(remaindSecond==9){
				mListener.unlockAchievement(R.string.achievement_quick_solver, "quick_solver");
			}
		}
	}

	/**
	 * Play Background Music
	 */
	public void playBackgroundMusic(){
		 if(isMusicSound){
			 AudioManager meng = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		     int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
	
		     if (volume != 0)
		     {
		         if (background_music == null)
		         	 background_music = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.sound);
		         if (background_music != null)
		        	 background_music.reset();
		         	 background_music = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.sound);
		        	 if(background_music!=null){
		        		 background_music.start();
		        	 }
		     }
		 }
	 }
	
	/**
	 * Play Shoot sound
	 */
	 public void shootSound()
	 {
		 if(isSoundEffect){
		     AudioManager meng = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		     int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
	
		     if (volume != 0)
		     {
		         if (_shootMP == null)
		         	_shootMP = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.completed_1);
		         if (_shootMP != null)
		             _shootMP.start();
		     }
		 }
	 }
		  public void displayInterstitial() {
		    if (interstitial.isLoaded()) {
		      interstitial.show();
		    }
		  }

	
}

