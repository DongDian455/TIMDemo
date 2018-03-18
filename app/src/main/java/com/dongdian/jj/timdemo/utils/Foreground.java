package com.dongdian.jj.timdemo.utils;



import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Foreground implements Application.ActivityLifecycleCallbacks {
	
	//单例
	private static Foreground instance = new Foreground();
	
	private static String TAG = Foreground.class.getSimpleName();
	private final int CHECK_DELAY = 500;
	
	//用于判断是否程序在前台
	private boolean foreground = false, paused = true;
	//handler用于处理切换activity时的短暂时期可能出现的判断错误
	private Handler handler = new Handler();
	private Runnable check;
	
	public static void init(Application app){
		app.registerActivityLifecycleCallbacks(instance);
    }

    public static Foreground get(){
        return instance;
    }

    private Foreground(){}
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityPaused(Activity activity) {
		paused = true;
	    if (check != null)
	        handler.removeCallbacks(check);
	    handler.postDelayed(check = new Runnable(){
	        @Override
	        public void run() {
	            if (foreground && paused) {
	                foreground = false;
	                Log.i(TAG, "went background");	                
	            } else {
	                Log.i(TAG, "still foreground");
	            }
	        }
	    }, CHECK_DELAY);
		
	}

	@Override
	public void onActivityResumed(Activity activity) {
		paused = false;	    
	    foreground = true;
	    if (check != null)
	        handler.removeCallbacks(check);	    
		
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isForeground(){
	    return foreground;
	}

}
