package com.kinetise.helpers;

import android.content.Context;
import android.os.Bundle;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.data.application.screenhistory.ScreenHistoryManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.support.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;

public class AppSaveStateHelper {
	
	private static final String SCREEN_DATA_DESC = "screenDataDesc";
	private static final String SCROLL_MAP = "scrollMap";
	private static final String APPLICATION_STATE = "applicationState";
	private static final String SCREEN_HISTORY_MANAGER = "screenHistoryManager";
	private static final String APP_SAVE_DIR = "appSaveDir";
    
    public static void saveAlterApiSessionId(Bundle outState) {
    	outState.putString("AlterApiSessionId", AlterApiManager.getAlterApiSesionID());
    }
    
    
    public static void saveApplicationState(Context context) throws IOException{
    	createCacheDir(context);
    	com.kinetise.data.application.screenhistory.ApplicationState state = AGApplicationState.getInstance().getApplicationState();
    	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilePath(context, APPLICATION_STATE)));
    	oos.writeObject(state);
    	oos.close();
    }
    
    public static void saveScreenHistoryManager(Context context) throws IOException{
    	createCacheDir(context);
    	ScreenHistoryManager shm = AGApplicationState.getInstance().getHistoryManager();
    	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilePath(context, SCREEN_HISTORY_MANAGER)));
    	oos.writeObject(shm);
    	oos.close();
    	
    	AGScreenDataDesc agSDD = AGApplicationState.getInstance().getCurrentScreenDesc();
    	Logger.v("AppSaveStateHelper", "saveScreenHistoryManager" , "saving to: " + getFilePath(context, SCREEN_DATA_DESC));
    	ObjectOutputStream oosSDD = new ObjectOutputStream(new FileOutputStream(getFilePath(context, SCREEN_DATA_DESC)));
    	oosSDD.writeObject(agSDD);
    	oosSDD.close();
    }
	
	private static String getFilePath(Context context, String string){
		return String.format(Locale.US, "%s%s%s%s%s", context.getFilesDir().getAbsoluteFile(), File.separator, APP_SAVE_DIR, File.separator, string);
	}
	
	private static void createCacheDir(Context context){
		File file = new File(getFilePath(context, ""));
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	public static void clearCacheDir(Context context){
		File file = new File(getFilePath(context, ""));
		if(file.exists() && file.isDirectory()){
			File[] list = file.listFiles();
			for(File f : list){
				Logger.v("AppSaveStateHelper","clearCacheDir","Deleted file:\n" + f.getAbsolutePath() + "\n");
				f.delete();
			}
		}
	}
}
