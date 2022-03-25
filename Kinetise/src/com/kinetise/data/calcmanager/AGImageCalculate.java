package com.kinetise.data.calcmanager;

public class AGImageCalculate extends AGTextCalculate {

    protected AGImageCalculate(){}

	private static AGImageCalculate mInstance;

	public static AGImageCalculate getInstance(){
		if(mInstance == null){
			mInstance = new AGImageCalculate();
		}
		
		return mInstance;
	}

	public static void clearInstance(){
		mInstance = null;
	}

}
