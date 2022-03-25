package com.kinetise.data.application.screenhistory;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

import java.io.Serializable;

public class ApplicationState implements Serializable{
	
	private String mScreenId;
    private AbstractAGElementDataDesc mScreenContext;
    private String mAlterApiContext;
    private String mGuid;

    public ApplicationState(String screenId, AbstractAGElementDataDesc screenContext, String alterApiContext, String guid){
        this(screenId);
        mScreenContext = screenContext;
        mAlterApiContext = alterApiContext;
        mGuid = guid;
    }

    public ApplicationState(String screenId, AbstractAGElementDataDesc screenContext, String alterApiContext){
        this(screenId,screenContext, alterApiContext, null);
    }

    public ApplicationState(String screenId){
        mScreenId = screenId;
    }
		
	public String getScreenId(){
		return mScreenId;
	}

    public AbstractAGElementDataDesc getContext(){
        return mScreenContext;
    }

    public String getAlterApiContext() {
        return mAlterApiContext;
    }

    public String getGuid(){
        return mGuid;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof ApplicationState))
            return false;
        ApplicationState comparedObj = (ApplicationState)obj;

        return compare(mScreenId,comparedObj.mScreenId) && compare(mScreenContext,comparedObj.mScreenContext) && compare(mAlterApiContext,comparedObj.mAlterApiContext) && compare(mGuid, comparedObj.mGuid);
    }

    private boolean compare(Object obj1,Object obj2) {
        if(obj1 == null) {
            if (obj2 != null)
                return false;
        }
        else
        if(!obj1.equals(obj2))
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        return (mScreenId+mAlterApiContext+mGuid).hashCode();
    }

    public ApplicationState copy() {
        return new ApplicationState(mScreenId, mScreenContext == null?null:mScreenContext.copy(), mAlterApiContext, mGuid);
    }
}
