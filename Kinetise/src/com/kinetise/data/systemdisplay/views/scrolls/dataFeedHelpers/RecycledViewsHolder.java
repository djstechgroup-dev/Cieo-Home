package com.kinetise.data.systemdisplay.views.scrolls.dataFeedHelpers;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RecycledViewsHolder {
    List<Stack<View>> mRecycledViews;

    public RecycledViewsHolder(){
        mRecycledViews = new ArrayList<>();
    }

    public void clear(){
        mRecycledViews.clear();
    }

    public View getViewForIndex(int index){
        if(mRecycledViews.size()<=index)
            return null;
        Stack<View> viewStack = mRecycledViews.get(index);
        if(viewStack.empty())
            return null;
        return viewStack.pop();
    }

    public void addViewForIndex(View view, int index){
        addStacksToIndex(index);
        mRecycledViews.get(index).push(view);
    }

    private void addStacksToIndex(int index){
        while(mRecycledViews.size()<=index)
            mRecycledViews.add(new Stack<View>());
    }


}
