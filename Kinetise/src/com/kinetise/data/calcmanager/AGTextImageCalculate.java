package com.kinetise.data.calcmanager;

public class AGTextImageCalculate extends AGImageCalculate{

    protected AGTextImageCalculate(){
    }

    private static AGTextImageCalculate mInstance;

    public static AGTextImageCalculate getInstance(){
        if(mInstance == null){
            mInstance = new AGTextImageCalculate();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

}
