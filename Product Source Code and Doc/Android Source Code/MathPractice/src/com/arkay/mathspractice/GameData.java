package com.arkay.mathspractice;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

/**
 * Game Data that will store on Google cloud.
 * You can add more data if we want that need to store on cloud. 
 * @author I-BALL
 *
 */
public class GameData {
	
	 // serialization format version
    private static final String SERIAL_VERSION = "1.1";
    
    
	private int totalAddtionScore;
	private int totalMultiplicationScore;
	
	private int levelCompletedOnAdditon;
	private int levelCompletedOnMultiplication;
	
	private int countHowManyTimeAddMulPlay;
	private int countHowManyTimeFreePlay;
	
	/**
	 * Load data on Local SharedPreferences if internet not connection
	 * @param settings
	 */
	public GameData(SharedPreferences settings){
		totalAddtionScore = settings.getInt(Main.TOTAL_SCORE_ADDITION, 0);
		totalMultiplicationScore = settings.getInt(Main.TOTAL_SCORE_MULTIPLICATION, 0);
		
		levelCompletedOnAdditon = settings.getInt(Main.LEVEL_COMPLETED_NO_ADDITION, 0);
		levelCompletedOnMultiplication = settings.getInt(Main.LEVEL_COMPLETED_NO_MULTIPLICATION, 0);
		
		countHowManyTimeAddMulPlay = settings.getInt(Main.HOW_MANY_TIMES_ADD_MUL_PLAY, 0);
		countHowManyTimeFreePlay= settings.getInt(Main.HOW_MANY_TIMES_FREE_PLAY, 0);
	}
	
	/**
	 * Load data on Cloud that time provide game Data object.
	 * @param settings
	 */
	public GameData(GameData gameData) {
		totalAddtionScore = gameData.getTotalAddtionScore();
		totalMultiplicationScore = gameData.getTotalMultiplicationScore();
		
		levelCompletedOnAdditon = gameData.getLevelCompletedOnAdditon();
		levelCompletedOnMultiplication = gameData.getLevelCompletedOnMultiplication();
		
		countHowManyTimeAddMulPlay = gameData.getCountHowManyTimeAddMulPlay();
		countHowManyTimeFreePlay = gameData.getCountHowManyTimeFreePlay();
		
		
	}
	
	 /** Constructs a SaveGame object from serialized data. */
    public GameData(byte[] data) {
        if (data == null) return; // default progress
        loadFromString(new String(data));
    }
    
    
	public GameData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Convert String to Object. Extract all data form JSON object. JOSON string return from google cloud.
	 * @param json
	 */
	private void loadFromString(String json) {
		 if (json == null || json.trim().equals("")) return;
		 
		 try {
	            JSONObject obj = new JSONObject(json);
	            String format = obj.getString("version");
	            if (!format.equals(SERIAL_VERSION)) {
	                throw new RuntimeException("Unexpected loot format " + format);
	            }
	            JSONObject gameData = obj.getJSONObject("score");
	            this.setTotalAddtionScore(gameData.getInt(Main.TOTAL_SCORE_ADDITION));
	            this.setTotalMultiplicationScore(gameData.getInt(Main.TOTAL_SCORE_MULTIPLICATION));
	            this.setLevelCompletedOnAdditon(gameData.getInt(Main.LEVEL_COMPLETED_NO_ADDITION));
	            this.setLevelCompletedOnMultiplication(gameData.getInt(Main.LEVEL_COMPLETED_NO_MULTIPLICATION));
	            this.setCountHowManyTimeAddMulPlay(gameData.getInt(Main.HOW_MANY_TIMES_ADD_MUL_PLAY));
	            this.setCountHowManyTimeFreePlay(gameData.getInt(Main.HOW_MANY_TIMES_FREE_PLAY));
	           
	        }
	        catch (JSONException ex) {
	            ex.printStackTrace();
	        }
	        catch (NumberFormatException ex) {
	            ex.printStackTrace();
	        }
		
	}

	 /** Serializes this SaveGame to an array of bytes. */
    public byte[] toBytes() {
        return toString().getBytes();
    }
    
    /**
     * Covert JSON string from GameData object for seting data to cloud for saving purpose.
     */
	@Override
	public String toString() {
		  try {
	            JSONObject gameData = new JSONObject();
	            gameData.put(Main.TOTAL_SCORE_ADDITION, getTotalAddtionScore());
	            gameData.put(Main.TOTAL_SCORE_MULTIPLICATION, getTotalMultiplicationScore());
	            gameData.put(Main.LEVEL_COMPLETED_NO_ADDITION, getLevelCompletedOnAdditon());
	            gameData.put(Main.LEVEL_COMPLETED_NO_MULTIPLICATION, getLevelCompletedOnMultiplication());
	            gameData.put(Main.HOW_MANY_TIMES_ADD_MUL_PLAY, getCountHowManyTimeAddMulPlay());
	            gameData.put(Main.HOW_MANY_TIMES_FREE_PLAY, getCountHowManyTimeFreePlay());
	            JSONObject obj = new JSONObject();
	            obj.put("version", SERIAL_VERSION);
	            obj.put("score", gameData);
	            return obj.toString();
	        }
	        catch (JSONException ex) {
	            ex.printStackTrace();
	            throw new RuntimeException("Error converting save data to JSON.", ex);
	        }
	}

	/**
	 * Also save that data on local it's use full when internet connection not available
	 * @param settings
	 */
	public void saveDataLocal(SharedPreferences settings){
		SharedPreferences.Editor editor = settings.edit();
		System.out.println("Hoe manby time Play: "+getCountHowManyTimeAddMulPlay());
		editor.putInt(Main.TOTAL_SCORE_ADDITION,getTotalAddtionScore());
		editor.putInt(Main.TOTAL_SCORE_MULTIPLICATION, getTotalMultiplicationScore());
		editor.putInt(Main.LEVEL_COMPLETED_NO_ADDITION,getLevelCompletedOnAdditon());
		editor.putInt(Main.LEVEL_COMPLETED_NO_MULTIPLICATION,getLevelCompletedOnMultiplication());
		editor.putInt(Main.HOW_MANY_TIMES_ADD_MUL_PLAY,getCountHowManyTimeAddMulPlay());
		editor.putInt(Main.HOW_MANY_TIMES_FREE_PLAY, getCountHowManyTimeFreePlay());
		editor.commit();
	}
	
	/**
     * Computes the union of this SaveGame with the given SaveGame. The union will have any
     * levels present in either operand. If the same level is present in both operands,
     * then the number of stars will be the greatest of the two.
     *
     * @param other The other operand with which to compute the union.
     * @return The result of the union.
     */
    public GameData unionWith(GameData other) {
    	GameData result = clone();
    	
    	 
        int existingPoint = result.getLevelCompletedOnAdditon();
        int newPoint = other.getLevelCompletedOnAdditon();
        if (newPoint > existingPoint) {
        	result.setLevelCompletedOnAdditon(newPoint);
        }
        
        existingPoint = result.getLevelCompletedOnMultiplication();
        newPoint = other.getLevelCompletedOnMultiplication();
        if (newPoint > existingPoint) {
        	result.setLevelCompletedOnMultiplication(newPoint);
        }
        
        existingPoint = result.getTotalAddtionScore();
        newPoint = other.getTotalAddtionScore();
        if (newPoint > existingPoint) {
        	result.setTotalAddtionScore(newPoint);
        }
        
        existingPoint = result.getTotalMultiplicationScore();
        newPoint = other.getTotalMultiplicationScore();
        if (newPoint > existingPoint) {
        	result.setTotalMultiplicationScore(newPoint);
        }
        
        existingPoint = result.getCountHowManyTimeAddMulPlay();
        newPoint = other.getCountHowManyTimeAddMulPlay();
        if (newPoint > existingPoint) {
        	result.setCountHowManyTimeAddMulPlay(newPoint);
        }
        existingPoint = result.getCountHowManyTimeFreePlay();
        newPoint = other.getCountHowManyTimeFreePlay();
        if (newPoint > existingPoint) {
        	result.setCountHowManyTimeFreePlay(newPoint);
        }
    	
        return result;
    }
    
    /** Returns a clone of this SaveGame object. */
    public GameData clone() {
    	GameData result = new GameData();
    	result.setLevelCompletedOnAdditon(levelCompletedOnAdditon);
    	result.setLevelCompletedOnMultiplication(levelCompletedOnMultiplication);
    	result.setTotalAddtionScore(totalAddtionScore);
    	result.setTotalMultiplicationScore(totalMultiplicationScore);
    	result.setCountHowManyTimeAddMulPlay(countHowManyTimeAddMulPlay);
    	result.setCountHowManyTimeFreePlay(countHowManyTimeFreePlay);
    	return result;
    }

	public int getTotalAddtionScore() {
		return totalAddtionScore;
	}

	public void setTotalAddtionScore(int totalAddtionScore) {
		this.totalAddtionScore = totalAddtionScore;
	}

	public int getTotalMultiplicationScore() {
		return totalMultiplicationScore;
	}

	public void setTotalMultiplicationScore(int totalMultiplicationScore) {
		this.totalMultiplicationScore = totalMultiplicationScore;
	}

	public int getLevelCompletedOnAdditon() {
		return levelCompletedOnAdditon;
	}

	public void setLevelCompletedOnAdditon(int levelCompletedOnAdditon) {
		this.levelCompletedOnAdditon = levelCompletedOnAdditon;
	}

	public int getLevelCompletedOnMultiplication() {
		return levelCompletedOnMultiplication;
	}

	public void setLevelCompletedOnMultiplication(int levelCompletedOnMultiplication) {
		this.levelCompletedOnMultiplication = levelCompletedOnMultiplication;
	}

	public static String getSerialVersion() {
		return SERIAL_VERSION;
	}

	public int getCountHowManyTimeAddMulPlay() {
		return countHowManyTimeAddMulPlay;
	}

	public void setCountHowManyTimeAddMulPlay(int countHowManyTimeAddMulPlay) {
		this.countHowManyTimeAddMulPlay = countHowManyTimeAddMulPlay;
	}

	public int getCountHowManyTimeFreePlay() {
		return countHowManyTimeFreePlay;
	}

	public void setCountHowManyTimeFreePlay(int countHowManyTimeFreePlay) {
		this.countHowManyTimeFreePlay = countHowManyTimeFreePlay;
	}

    
	
}
