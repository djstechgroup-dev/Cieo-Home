package com.kinetise.data.systemdisplay;


import java.io.Serializable;

public class ScrollMap implements Serializable{

    /*private final HashMap<String, SerializablePoint> mHashMap = new HashMap<String, SerializablePoint>();
    private static ScrollMap mInstance;

    public ScrollMap() {
        super();
    }

    public synchronized static ScrollMap getInstance() {
        if (mInstance == null) {
            mInstance = new ScrollMap();
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    public synchronized void setScroll(String tag, int x, int y) {
        mHashMap.put(tag, new SerializablePoint(x, y));
        return;
    }

    public synchronized int getScrollX(String tag) {
    	SerializablePoint p = getScroll(tag);

        if (p == null) {
            return 0;
        }

        return p.x;
    }

    public synchronized int getScrollY(String tag) {
    	SerializablePoint point = getScroll(tag);

        if (point == null) {
            return 0;
        }

        return point.y;
    }

    private SerializablePoint getScroll(String tag) {
        return mHashMap.get(tag);
    }

    public synchronized void clear() {
        mHashMap.clear();
    }

    public synchronized void clear(String tag) {
        mHashMap.remove(tag);
    }
    
    public boolean isEmpty(){
    	return mHashMap.isEmpty();
    }

	public static void setInstance(ScrollMap sm) {
		mInstance = sm;
	}
	
	
	private class SerializablePoint implements Serializable{
		int x;
		int y;
		
		public SerializablePoint(int x, int y){
			this.x = x;
			this.y = y;
		}
	}*/
}

