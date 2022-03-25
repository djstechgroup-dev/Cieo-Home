package com.kinetise.data.systemdisplay;

import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Marcin Narowski
 * Date: 2014-06-30
 * Time: 08:09
 */
public class FindViewByTypeVisitor<T extends IAGView> implements IViewVisitor {
    private final Class mClass;
    private final Set<T> mViews;

    public FindViewByTypeVisitor(Class pAGMapViewClass) {
        mClass = pAGMapViewClass;
        mViews = new HashSet<T>();
    }

    @Override
    public boolean visit(IAGView view) {
        if(mClass.isInstance(view)){
            mViews.add((T)view);
        }
        return false;
    }

    public Set<T> getViews(){
        return mViews;
    }
}
