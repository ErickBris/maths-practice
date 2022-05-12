package com.arkay.mathspractice;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class QuizCompletedDialog extends Dialog implements
		android.view.View.OnClickListener {

	public Activity c;
	public Dialog d;
	private Button   btnPlayAgain;
	private TextView txtLevelHeading, txtLevelScore,txtLevelTotalScore; 
	boolean isLevelCompleted=false;
	private SharedPreferences settings;
	
	int levelNo=1;
	int lastLevelScore = 0;
	public QuizCompletedDialog(Activity a) {
		super(a);
		
		this.c = a;
		settings = a.getSharedPreferences(Main.SETTING, a.MODE_PRIVATE);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		
		txtLevelHeading = (TextView)findViewById(R.id.txtLevelHeading);
		
		txtLevelScore = (TextView)findViewById(R.id.txtLevelScore);
		int totalScore =0;
		
		if(settings.getInt(Main.LAST_PLAY_LEVEL_OPTION,0)==PlayOptionAddtitionSubtraction.ADDITION){
			totalScore = settings.getInt(Main.TOTAL_SCORE_ADDITION, 0);
		}else{
			totalScore = settings.getInt(Main.TOTAL_SCORE_MULTIPLICATION, 0);
		}
		
		txtLevelScore.setText("Total Score: "+totalScore+" Points");
		
		int lastLevelScore = settings.getInt(Main.THIS_lEVEL_TOTAL_SCORE,0);
		txtLevelTotalScore = (TextView)findViewById(R.id.txtLevelTotalScore);
		txtLevelTotalScore.setText("This Level Score: "+lastLevelScore+" Points");

		btnPlayAgain = (Button) findViewById(R.id.btnPlayAgain);
		btnPlayAgain.setOnClickListener(this);
		
		boolean islevelcomplted = settings.getBoolean(Main.IS_LAST_LEVEL_COMPLETED, false);
		levelNo =settings.getInt(Main.LEVEL_COMPLETED, 1);
		
		if(islevelcomplted){
			levelNo--;
			txtLevelHeading.setText(c.getString(R.string.level)+" "+ levelNo +" "+  c.getResources().getString(R.string.finished));
		}else{
			txtLevelHeading.setText("Time UP!");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlayAgain:
			c.onBackPressed();
			break;
		default:
			break;
		}
		dismiss();
	}

}