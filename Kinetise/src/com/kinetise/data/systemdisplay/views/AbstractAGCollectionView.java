package com.kinetise.data.systemdisplay.views;

import android.view.View;
import android.view.ViewParent;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IAGCollectionDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.data.systemdisplay.viewvisitors.OnParentDetachedVisitor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAGCollectionView<T extends AbstractAGElementDataDesc> extends AGControl<T> implements IAGCollectionView {

    private ArrayList<IAGView> mChildrenViews = new ArrayList<>();

    public AbstractAGCollectionView(SystemDisplay display, T desc) {
        super(display, desc);
    }

    protected void detachViewFromParent(View child) {
        super.detachViewFromParent(child);
        IViewVisitor visitor = new OnParentDetachedVisitor();
        ((IAGView)child).accept(visitor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AGElementCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int specWidth = MeasureSpec.makeMeasureSpec(calcDesc.getViewWidth(), MeasureSpec.EXACTLY);
        int specHeight = MeasureSpec.makeMeasureSpec(calcDesc.getViewHeight(), MeasureSpec.EXACTLY);

        setMeasuredDimension(specWidth, specHeight);

        final int count = getChildCount();
        AGElementCalcDesc childCalcDesc;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE && child instanceof IAGView) {
                childCalcDesc = ((IAGView)child).getDescriptor().getCalcDesc();
                int childSpecWidth = MeasureSpec.makeMeasureSpec(childCalcDesc.getViewWidth(), MeasureSpec.EXACTLY);
                int childSpecHeight = MeasureSpec.makeMeasureSpec(childCalcDesc.getViewHeight(), MeasureSpec.EXACTLY);
                child.measure(childSpecWidth, childSpecHeight);
            }
        }
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor){
        mDescriptor = (T)descriptor;
        // TODO: this logic should be in rebuildView of Data Feed controls, not in call collections
        List<AbstractAGElementDataDesc> descs = ((IAGCollectionDataDesc) mDescriptor).getAllControls();
        int maxSize = descs.size();
        for(int i=0;i<maxSize;i++){
            if(i < getChildrenViews().size()){
                IAGView iagView = getChildrenViews().get(i);
                while(!isDescriptorMatching((AbstractAGViewDataDesc)iagView.getDescriptor(), (AbstractAGViewDataDesc)descs.get(i)) && i < getChildrenViews().size()){
                    iagView = getChildrenViews().get(i);
                    removeChildView(iagView);
                }
                if(i < getChildrenViews().size()) {
                    iagView.setDescriptor(descs.get(i));
                }
            }
        }
    }

    protected boolean isDescriptorMatching(AbstractAGViewDataDesc desc, AbstractAGViewDataDesc descToCheck){
        if (desc.getClass().equals(descToCheck.getClass())) {
            return desc.getTemplateNumber() == descToCheck.getTemplateNumber();
        }
        return false;
    }

    protected void setDescriptorScrolls(int x, int y) {
        getDescriptor().setScrollX(x);
        getDescriptor().setScrollY(y);
    }

    public ArrayList<IAGView> getChildrenViews() {
        return mChildrenViews;
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException("Parent of AbstractAGCollectionView have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public void addChildView(final IAGView view) {
        mChildrenViews.add(view);
        addView((View) view);
    }

    @Override
    public void addChildView(IAGView view, int index) {
        mChildrenViews.add(index, view);
        addView((View) view, index);
    }

    public void removeChildView(int index) {
        removeViewAt(index);
        mChildrenViews.remove(index);
    }

    public void removeAllChildrenViews(){
        removeAllViews();
        mChildrenViews.clear();
    }

    public void removeChildView(IAGView view) {
        removeView((View) view);
        mChildrenViews.remove(view);
    }

    @Override
    public boolean accept(IViewVisitor visitor) {

        for (IAGView view : mChildrenViews) {
            if (view.accept(visitor)) {
                return true;
            }
        }

        return visitor.visit(this);
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        for(IAGView view:getChildrenViews()){
            view.loadAssets();
        }
    }

}
