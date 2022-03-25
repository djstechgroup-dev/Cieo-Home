package com.kinetise.data.systemdisplay.views.scrolls.dataFeedHelpers;

/**
 * Created by admin on 2014-07-09.
 */
public class ChildPosition implements Comparable{
    private final int mEnd;
    private final int mStart;

    /**
     *
     * @param start pozycja poczatkowa dla horizontal odpowiednik left, dla vertical odpowiednik top
     * @param end pozycja koncowa dla horizontal odpowiednik right, dla vertical odpowiednik bottom
     */
    public ChildPosition(int start, int end) {
        mStart = start;
        mEnd = end;
    }

    public int getEnd() {
        return mEnd;
    }

    public int getStart() {
        return mStart;
    }

    public boolean isVisibleOnScreen(int start, int end){
        return mEnd >=start && mStart <= end;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof ChildPosition){
            if(mStart<((ChildPosition) another).mStart){
                return -1;
            } else {
                return 1;
            }
        }
        return -1;
    }
}
