package com.kinetise.data.systemdisplay.views;

import java.util.ArrayList;

/**
 * User: Mateusz
 * Date: 18.04.13
 * Time: 16:05
 */
public interface IAGCollectionView extends IAGView {

    public ArrayList<IAGView> getChildrenViews();

    public void addChildView(IAGView view);

    public void addChildView(IAGView view, int index);

    public void removeChildView(IAGView view);

    public void removeChildView(int index);
}
