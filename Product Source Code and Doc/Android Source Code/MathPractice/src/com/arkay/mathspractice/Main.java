package com.arkay.mathspractice;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.appstate.AppStateStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

/**
 * Home screen that is first screen.
 * @author I-BALL
 *
 */
public class Main extends BaseGameActivity implements OnClickListener, GooglePlayServicesClient.OnConnectionFailedListener, PlayOptionAddtitionSubtraction.Listener, PlayOption.Listener, GamePlay.Listener{
	
	Button btnPlay, btnHighScore, btnAchivement , btnLeaderboard;
	
	public SharedPreferences settings;
	public static String SETTING = "setting";
	public Intent svc;
	  
	PlayOptionAddtitionSubtraction playOptionAddtitionSubtraction;
	PlayOption playOption;
	GamePlay gamePlay;
	
	 final int RC_RESOLVE = 5000, RC_UNUSED = 5001;
	 
	 public static String IS_LAST_LEVEL_COMPLETED ="is_last_level_completed";
	 public static String LEVEL_COMPLETED = "level_completed";
	 public static String LAST_PLAY_LEVEL_OPTION = "last_play_level_option";
	 
	 public static String LEVEL_COMPLETED_NO_MULTIPLICATION ="level_completed_no_on_multiplication";
	 public static String LEVEL_COMPLETED_NO_ADDITION ="level_completed_no_on_addtion";
	 
	 public static String LEVEL_COMPLETED_NO_FREE_MULTIPLICATION ="level_completed_no_free_addtion";
	 public static String LEVEL_COMPLETED_NO_FREE_ADDITION="level_completed_no_free_multiplication";
	 
	 public static String TOTAL_SCORE_ADDITION = "total_score_addition";
	 public static String TOTAL_SCORE_MULTIPLICATION = "total_score_multiplication";
	 
	 public static String HOW_MANY_TIMES_ADD_MUL_PLAY = "how_many_times_add_mul_play";
	 public static String HOW_MANY_TIMES_FREE_PLAY = "how_many_times_free_play";
	 
	 public static String THIS_lEVEL_TOTAL_SCORE = "this_level_total_score";
	 
	 private static final int OUR_STATE_KEY = 1;
	 
	 ProgressDialog progress;
	 
	 Button btnShareFacebook;
	 private Facebook mFacebook = null;
	 public static final String APP_FACEBOOK_ID="593715480690935";
	 
	 private InterstitialAd interstitial;
	 private GameData gameData;
	 
	 public Main() {
	        // request that superclass initialize and manage the AppStateClient for us
	     //   super(BaseGameActivity.CLIENT_ALL);
		 super( BaseGameActivity.CLIENT_GAMES | BaseGameActivity.CLIENT_APPSTATE);
	       
	}
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home_screen);
		
		/*
		 * Button and it's listener
		 */
		btnPlay = (Button)findViewById(R.id.btn_play);
		btnPlay.setOnClickListener(this);
		btnLeaderboard = (Button)findViewById(R.id.btn_option);
		btnLeaderboard.setOnClickListener(this);
		btnHighScore = (Button)findViewById(R.id.btn_high_score);
		btnHighScore.setOnClickListener(this);
		btnAchivement = (Button)findViewById(R.id.btn_help);
		btnAchivement.setOnClickListener(this);
		
		
		svc = new Intent(this, BackgroundSoundService.class);
		settings= getSharedPreferences(SETTING, MODE_PRIVATE);
		gameData = new GameData(settings);
		
		playOptionAddtitionSubtraction = new PlayOptionAddtitionSubtraction();
		playOptionAddtitionSubtraction.setListener(this);
		
		playOption = new PlayOption();
		playOption.setListener(this);
		
		gamePlay = new GamePlay();
		gamePlay.setListener(this);
		
		/**
		 * Progresss bar display for loading data from cloud
		 */
		 progress = new ProgressDialog(this);
	        progress.setTitle("Please Wait!!");
	        progress.setMessage("Data Loading..");
	        progress.setCancelable(false);
	        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progress.show();
	        SignInButton mSignInButton = (SignInButton)findViewById(R.id.sign_in_button);
	        
	        mSignInButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                // start the asynchronous sign in flow
	            	System.out.println("Click on Sign-in");
	                beginUserInitiatedSignIn();
	            }
	        });
	        
	        /** 
	         * Button for Facebook Sharing
	         */
	        btnShareFacebook = (Button)findViewById(R.id.btn_share_score);
			btnShareFacebook.setOnClickListener(this);
			mFacebook = new Facebook(APP_FACEBOOK_ID);
	       
			 // Create the interstitial.
		    interstitial = new InterstitialAd(this);
		    interstitial.setAdUnitId(getString(R.string.admob_interstitial_ads));

		    /*// Create ADMOB ad request.
		    AdRequest adRequest = new AdRequest.Builder().build();*/
		    /** Create ad request. */
		    Resources ress = getResources();
		    /**set testmost false when publish app*/
		    boolean isTestMode = ress.getBoolean(R.bool.istestmode); //test is testing mode
		    AdRequest adRequest =null;
		    if(isTestMode){
		    	  adRequest = new AdRequest.Builder()
		         .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		         .addTestDevice("B15149A4EC1ED23173A27B04134DD483").build();
		    }else{
		    	 adRequest = new AdRequest.Builder().build();
		    }

		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
		    
		    displayInterstitial();
		    
	}

	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
		
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_play){
			System.out.println("Click On Play");
			 this.findViewById(R.id.linearLayout1).setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container, playOptionAddtitionSubtraction ).addToBackStack( "tag" ).commit();
			
		}
		if(v.getId() == R.id.btn_option){
			System.out.println("Click On Option");
			Intent optionIntent = new Intent(this, SettingOption.class);
			startActivity(optionIntent);
		}
		
		if(v.getId() == R.id.btn_help){
			if (isSignedIn()) {
				unlockAchievement(R.string.achievement_curious, "Curious");
				startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),RC_UNUSED);
			}else {
				showAlert(getString(R.string.achievements_not_available));
			}
			
		}
		if(v.getId() == R.id.btn_high_score){
			if(isSignedIn()){
				unlockAchievement(R.string.achievement_very_curious,"Very Curious");
				startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),RC_UNUSED);
			} else {
				showAlert(getString(R.string.leaderboards_not_available));
			}
		}
		if(v.getId()==R.id.btn_share_score){
			facebookPost("Maths Practice", "https://play.google.com/store/apps/details?id=com.arkay.mathspractice", "Maths Practice", "I'm playing Maths Practice. Can you beat my "+gameData.getTotalAddtionScore() +" scores?", "https://lh4.googleusercontent.com/-keRhvF4VTK8/ULNpqPmErII/AAAAAAAAADU/6qJPvfPhoUQ/s512/android-512.png", "https://play.google.com/store/apps/details?id="+getPackageName());
		}
		
	}

	public FragmentManager getSupportManager(){
		return getSupportFragmentManager();
	}
	
	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		System.out.println("Sing In Fail");
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_button).setVisibility(View.GONE);
		
		findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
		findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
		progress.cancel();
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		System.out.println("Sing In Succcess");
		// show sign-out button, hide the sign-in button
		findViewById(R.id.sign_in_button).setVisibility(View.GONE);
		findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
		
		findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
		findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
		loadFromCloud();
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("Result Code: " + requestCode);
		// mGameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		System.out.println("Result Code: onConnectionFailed: " + arg0);
	}

	public void unlockAchievement(int achievementId, String fallbackString) {
		// TODO Auto-generated method stub
		if (isSignedIn()) {
			Games.Achievements.unlock(getApiClient(), getString(achievementId));
		} else {
			Toast.makeText(this,
					getString(R.string.achievement) + ": " + fallbackString,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onStartGameRequested(boolean hardMode) {
		
		 getSupportFragmentManager().popBackStack();
		 this.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
		 //this.findViewById(R.id.btn_share_score).setVisibility(View.VISIBLE);
	}

	@Override
	public void onShowAchievementsRequested() {
		// TODO Auto-generated method stub
		if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
	}

	@Override
	public void onShowLeaderboardsRequested() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInButtonClicked() {
		// TODO Auto-generated method stub
		beginUserInitiatedSignIn();
	}

	@Override
	public void onSignOutButtonClicked() {
		// TODO Auto-generated method stub
		
	}
	boolean addList = false;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getSupportFragmentManager().popBackStack();
		this.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
		//this.findViewById(R.id.btn_share_score).setVisibility(View.VISIBLE);
		if(getSupportFragmentManager().getBackStackEntryCount()==0){
			super.onBackPressed();
			displayInterstitial();
		}
		
	}
	
	@Override
	public void displyHomeScreen() {
		// TODO Auto-generated method stub
		getSupportFragmentManager().popBackStack();
		this.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
		//this.findViewById(R.id.btn_share_score).setVisibility(View.VISIBLE);
	}
	/**
	Update leaderboards with the user's score.
	*/
	@Override
	public void updateLeaderboards(int leaderboardId, int finalScore) {
		// TODO Auto-generated method stub
		if (isSignedIn()) {
	    	if (finalScore >= 0) {
	            Games.Leaderboards.submitScore(getApiClient(), getString(leaderboardId),
	                   finalScore);
	        }
		}
	}

	@Override
	public PlayOption getPlayOption() {
		// TODO Auto-generated method stub
		return this.playOption;
	}


	@Override
	public GamePlay getGamePlay() {
		// TODO Auto-generated method stub
		return this.gamePlay;
	}


	@Override
	public SharedPreferences getSharedPreferences() {
		// TODO Auto-generated method stub
		return this.settings;
	}


	
	 public  void loadFromCloud() {
		 if(isSignedIn()){
	        AppStateManager.load(getGameHelper().getApiClient(), OUR_STATE_KEY).setResultCallback(mResultCallback);
		 }
		
	 }

	 public   void saveToCloud() {
	    	//String data = gameData.toString();
	    	//System.out.println("Save Data: "+data);
		 if(isSignedIn()){
		 		AppStateManager.update(getGameHelper().getApiClient(), OUR_STATE_KEY, gameData.toBytes());
		 }
	        // Note: this is a fire-and-forget call. It will NOT trigger a call to any callbacks!
	    }


	    ResultCallback<AppStateManager.StateResult> mResultCallback = new
	            ResultCallback<AppStateManager.StateResult>() {
	        @Override
	        public void onResult(AppStateManager.StateResult result) {
	            AppStateManager.StateConflictResult conflictResult = result.getConflictResult();
	            AppStateManager.StateLoadedResult loadedResult = result.getLoadedResult();
	            if (loadedResult != null) {
	            	processStateLoaded(loadedResult);
	            	
	            } else if (conflictResult != null) {
	                processStateConflict(conflictResult);
	            }
	        }
	    };
	    private void processStateConflict(AppStateManager.StateConflictResult result) {
	        // Need to resolve conflict between the two states.
	        // We do that by taking the union of the two sets of cleared levels,
	        // which means preserving the maximum star rating of each cleared
	        // level:
	        byte[] serverData = result.getServerData();
	        byte[] localData = result.getLocalData();

	        GameData localGame = new GameData(localData);
	        GameData serverGame = new GameData(serverData);
	        GameData resolvedGame = localGame.unionWith(serverGame);

	        AppStateManager.resolve(getApiClient(), result.getStateKey(), result.getResolvedVersion(),
	                resolvedGame.toBytes()).setResultCallback(mResultCallback);
	    }
	    
	    private void processStateLoaded(AppStateManager.StateLoadedResult result) {
	        
	        switch (result.getStatus().getStatusCode()) {
	        case AppStateStatusCodes.STATUS_OK:
	            // Data was successfully loaded from the cloud: merge with local data.
	        	gameData = gameData.unionWith(new GameData(result.getLocalData()));
	            gameData.saveDataLocal(settings);
	            System.out.println("Marge Data: "+gameData);
	            progress.cancel();
	            break;
	        case AppStateStatusCodes.STATUS_STATE_KEY_NOT_FOUND:
	            // key not found means there is no saved data. To us, this is the same as
	            // having empty data, so we treat this as a success.
	        	progress.cancel();
	            break;
	        case AppStateStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
	            // can't reach cloud, and we have no local state. Warn user that
	            // they may not see their existing progress, but any new progress won't be lost.
	            //showAlertBar(R.string.no_data_warning);
	        	progress.cancel();
	            break;
	        case AppStateStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA:
	            // can't reach cloud, but we have locally cached data.
	            //showAlertBar(R.string.stale_data_warning);
	        	progress.cancel();
	            break;
	        case AppStateStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
	            // need to reconnect AppStateClient
	            reconnectClient();
	            break;
	        }
	    }
		@Override
		public GameData getGameData() {
			// TODO Auto-generated method stub
			return this.gameData;
		}

		@Override
		public void saveDataToCloud() {
			// TODO Auto-generated method stub
			saveToCloud();
		}
		
		public void facebookPost(String title, String href, String caption,
				String desc, String imageUrl, String imageHref) {
			Bundle b = new Bundle();
			b.putString("message", "");
			
			String attachment = "{\"name\":\"" + title + "\"," + "\"href\":\""
					+ href + "\"," + "\"caption\":\"" + caption + "\","
					+ "\"description\":\"" + desc + "\","
					+ "\"media\":[{\"type\":\"image\"," + "\"src\":\"" + imageUrl
					+ "\",\"href\":\"" + imageHref + "\"}]}";

			b.putString("attachment", attachment);
			mFacebook.dialog(this, "stream.publish", b, new DialogListener() {

				@Override
				public void onFacebookError(FacebookError e) {
					// TODO handle error in publishing
				}

				@Override
				public void onError(DialogError e) {
					// TODO handle dialog errors
				}

				@Override
				public void onComplete(Bundle values) {
					Toast.makeText(Main.this, "Post successful",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancel() {
					// TODO user don't want to share and presses cancel button
				}
			});
		}
		
		// Invoke displayInterstitial() when you are ready to display an interstitial.
		  public void displayInterstitial() {
		    if (interstitial.isLoaded()) {
		      interstitial.show();
		    }
		  }
}
