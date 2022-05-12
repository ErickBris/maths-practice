package com.arkay.mathspractice;

/**
 * Value pair of maths probem
 * @author Arkay Apps
 */
public class ValuePare {
	private boolean isMark;
	private boolean isTempMark;
	private int value;

	public ValuePare(boolean isMark, int value, boolean isTempMark) {
		this.isMark = isMark;
		this.value = value;
		this.isTempMark = isTempMark;
	}

	public boolean isIsMark() {
		return isMark;
	}

	public void setIsMark(boolean isMark) {
		this.isMark = isMark;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isMark() {
		return isMark;
	}

	public void setMark(boolean isMark) {
		this.isMark = isMark;
	}

	public boolean isTempMark() {
		return isTempMark;
	}

	public void setTempMark(boolean isTempMark) {
		this.isTempMark = isTempMark;
	}

	@Override
	public String toString() {
		return "Status: " + isMark + " value: " + value + " tempmark: "
				+ isTempMark;
	}

}
