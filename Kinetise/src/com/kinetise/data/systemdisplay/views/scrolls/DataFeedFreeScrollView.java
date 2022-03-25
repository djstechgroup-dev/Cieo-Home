package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.descriptors.types.AGLayoutType;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.scrolls.dataFeedHelpers.AGDataFeedAdapter;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.util.List;

public class DataFeedFreeScrollView extends FreeScrollView implements IFeedScrollView {

    private AGViewCalcDesc mCalcDesc;
    private SystemDisplay mDisplay;
    private AGDataFeedAdapter mAdapter;
    private boolean mIsScrollStartOrEnd;

    public DataFeedFreeScrollView(SystemDisplay display, AbstractAGContainerDataDesc desc) {
        super(display, desc);
        mCalcDesc = desc.getCalcDesc();
        mDisplay = display;
        setAdapter(new AGDataFeedAdapter(this, mDisplay, desc.getPresentControls()));
    }

    /**
     * Ustawia adapter dla datafeeda, automatycznie wywoluje {@link #refreshList()};
     *
     * @param adapter adapter do ustawienia
     */
    public void setAdapter(AGDataFeedAdapter adapter) {
        mAdapter = adapter;
        refreshList();
        mAdapter.initFeed();
    }

    /**
     * Powoduje od�wie�enie listy dla potrzeb adaptera, usuwa wszystkie widoki z listy,
     */
    private void refreshList() {
        Logger.d("Adapter", "refreshList");
        getInnerSroll().removeAllViews();
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        // Nic nie robimy i nie przkazujemy dalej - adapter za�aduje assety swoim widokom kt�rymi zarz�dza (wywo�a im loadAssets())
    }

    /**
     * Metoda odpowiedzialna za wy�wietlenie dziecka na liscie, tworzy dla niego LayoutParams
     * podczepia mu parenta, wywo�uje layout (UWAGA!!! w kinetise parametry layout s� bardzo cz�sto ignorowane, a zamiast tego korzysta ona z calc deskryptora
     *
     * @param view widok do dodania na liste
     */
    protected void layoutChild(View view) {
        getInnerSroll().layoutChild(view);
    }

    protected void attachChild(View view, boolean isRecycled) {
        getInnerSroll().attachChild(view, isRecycled);
    }

    @Override
    public void attachAndLayoutChild(View view, boolean isRecycled) {
        attachChild(view, isRecycled);
        layoutChild(view);
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        getInnerSroll().accept(visitor);
        mAdapter.accept(visitor);
        return super.accept(visitor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAdapter.recalculateChildPositions();
        mAdapter.sortOutChildrenByScrollDirection();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Logger.d("Adapter", "OnLayout");
        getViewDrawer().refresh();
        //Jaako ze standardowa metoda onLayout liczy tylko srollPosition dla  dzieci dodanych aktualnei do widoku, to nizej jest rozszerzenie
        //ktore wylicza maksymaln� pozycj� dziecka bottom i right, w celu zapewnienia scrollowania
        //calculateChildPositions();
        AbstractAGContainerDataDesc desc = getDescriptor();
        AGContainerCalcDesc calcDesc = desc.getCalcDesc();

        mMaxChildBottomPosition = 0;
        mMaxChildRightPosition = 0;

        //Foreach jest zrobiony by na podstawie deskryptorow policzyc obszar scrollowalny feeda, kolejna petla jest uzyta po to by na dzieciach wywolac layout,
        //jako ze ten widok korzysta z adaptera, liczba deskryptorow (potencjalnych dzieci) zwykle jest wieksza niz liczba aktualnie dodanych dzieci w hierarchii

        List<AbstractAGElementDataDesc> childDataDesc = desc.getPresentControls();
        for (AbstractAGElementDataDesc d : childDataDesc) {
            AGViewCalcDesc calc = (AGViewCalcDesc) d.getCalcDesc();
            int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTopAsInt() + Math.round(calc.getPositionY()) + calc.getMarginTop());
            int left = (int) Math.round(calcDesc.getPaddingLeft() +calcDesc.getBorder().getLeftAsInt() + Math.round(calc.getPositionX()) + calc.getMarginLeft());
            int height = (int) (Math.round(calc.getHeight() + Math.round(calc.getBorder().getVerticalBorderHeight())));
            int width = (int) (Math.round(calc.getWidth() + Math.round(calc.getBorder().getHorizontalBorderWidth())));
            int right = left + width;
            int bottom = top + height;
            if (bottom > mMaxChildBottomPosition)
                mMaxChildBottomPosition = (int) Math.round(bottom + calc.getMarginBottom());
            if (right > mMaxChildRightPosition)
                mMaxChildRightPosition = (int) Math.round(right + calc.getMarginRight());
        }
        mMaxChildRightPosition += (int) calcDesc.getBorder().getRight() + (int) calcDesc.getPaddingRight();
        mMaxChildBottomPosition += (int) calcDesc.getBorder().getBottom() + (int) calcDesc.getPaddingBottom();

        int bottom = (int) Math.round(mCalcDesc.getContentHeight());
        int topSpacing = (int) Math.round(mCalcDesc.getPaddingTop()) + mCalcDesc.getBorder().getTopAsInt();
        int bottomSpacing = (int) Math.round(mCalcDesc.getPaddingBottom()) + mCalcDesc.getBorder().getBottomAsInt();
        getInnerSroll().layout(0, 0, r - l, bottom + topSpacing + bottomSpacing);

        mAdapter.sortOutChildrenByScrollDirection();
        for (View view : mAdapter.getItems()) {
            if (view != null)
                layoutChild(view);
        }
        //jesli feed ma szerokosc na max, a wysokosc na min, to po obrocie ekranu zmieniaja sie jego proporcje.
        //przykladowo w portrecie ma 540x100px, a w landscape 960x100px
        //jesli w portrecie przescrollujemy widok, to mMaxChildBottom/Right position beda mniejsze od krawedzi Prawej/Dolnej widoku
        //co za tym idzie nie bedzie dalo sie go scrollowac, a widoki wyrysuja sie przescrollowane, i beda uciete.
        if (!isLoadingDisplayed())
            resetScrollsIfNeeded();

        mIsScrollStartOrEnd = isScrollStartOrEnd();
    }

    private boolean isLoadingDisplayed() {
        return getDescriptor().getPresentControls().size()==1 && getDescriptor().getPresentControls().get(0) instanceof AGLoadingDataDesc;
    }

    private void resetScrollsIfNeeded() {
        int y = getScrollY();
        int x = getInnerSroll().getScrollX();
        if (mMaxChildBottomPosition <= y + mCalcDesc.getHeight()) {
            y = mMaxChildBottomPosition - (int) (mCalcDesc.getHeight());
            if (y < 0)
                y = 0;
        }
        if (mMaxChildRightPosition <= x + mCalcDesc.getWidth()) {
            x = mMaxChildRightPosition - (int) (mCalcDesc.getWidth());
            if (x < 0)
                x = 0;
        }
        scrollViewTo(x, y);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //Wiele rzeczy jest zrobionych w p�tlach ze wzgl�du na to �e delta mo�e by� wi�ksza od wielko�ci itemu na feedzie.
        //czasami metoda moze zwrocic informacje o przescrollowaniu 500px podczas gdy dziecko ma 200px, z t�d musimy obs�uzy� usuwanie/dodawanie pomini�tych dzieci.
        super.onScrollChanged(l, t, oldl, oldt);

        mIsScrollStartOrEnd = isScrollStartOrEnd();

        mAdapter.sortOutChildrenByScrollDirection();
    }

    private boolean isScrollStartOrEnd() {
        if (mDescriptor.isScrollVertical()) {
            if (mDescriptor.isInverted())
                return getScrollY() + getHeight() >= getContentHeight() - (getLastChildHeight() * 0.5);
            else
                return getScrollY() <= getFirstChildHeight() * 0.5;
        } else {
            if (mDescriptor.isInverted())
                return getScrollX() + getWidth() >= getContentWidth() - (getLastChildWidth() * 0.5);
            else
                return getScrollX() <= getFirstChildWidth() * 0.5;
        }
    }

    private double getFirstChildHeight() {
        AbstractAGElementDataDesc firstChild = getFirstChild();
        if (firstChild == null)
            return 0;
        return firstChild.getCalcDesc().getHeight();
    }

    private double getFirstChildWidth() {
        AbstractAGElementDataDesc firstChild = getFirstChild();
        if (firstChild == null)
            return 0;
        return firstChild.getCalcDesc().getWidth();
    }

    private AbstractAGElementDataDesc getFirstChild() {
        if (mDescriptor.getPresentControls().size() > 0)
            return mDescriptor.getPresentControls().get(0);
        return null;
    }

    private double getLastChildHeight() {
        AbstractAGElementDataDesc lastChild = getLastChild();
        if (lastChild == null)
            return 0;
        return lastChild.getCalcDesc().getHeight();
    }

    private double getLastChildWidth() {
        AbstractAGElementDataDesc lastChild = getLastChild();
        if (lastChild == null)
            return 0;
        return lastChild.getCalcDesc().getWidth();
    }

    private AbstractAGElementDataDesc getLastChild() {
        if (mDescriptor.getPresentControls().size() > 0)
            return mDescriptor.getPresentControls().get(mDescriptor.getPresentControls().size() - 1);
        return null;
    }


    @Override
    public void rebuildView() {
        Logger.d("Adapter", "RepaintView");
        AbstractAGDataFeedDataDesc feedClient = (AbstractAGDataFeedDataDesc) getDescriptor();
        List<AbstractAGElementDataDesc> views = feedClient.getPresentControls();
        if (feedClient.isLoadingMore() && mAdapter != null) {
            loadMoreViews();
        } else {
            setAdapter(new AGDataFeedAdapter(this, mDisplay, views));
            if (views.size()!=1 || !(views.get(0) instanceof AGLoadingDataDesc))
                restoreScroll();
        }

        if (mIsScrollStartOrEnd) {
            int contentChange;
            if (mDescriptor.getLayout() == AGLayoutType.VERTICAL) {
                double oldContentHeight = ((AbstractAGDataFeedDataDesc) getDescriptor()).getContentHeight();
                contentChange = (int) Math.ceil(mCalcDesc.getContentHeight() - oldContentHeight);
                if (oldContentHeight > 0 && contentChange > 0) {
                    if (mDescriptor.isInverted()) {
                        int delta = (int) Math.ceil(
                                mCalcDesc.getContentHeight()
                                        + mCalcDesc.getPaddingTop()
                                        + mCalcDesc.getPaddingBottom()
                                        - mCalcDesc.getHeight()
                                        - getScrollY());
                        if (delta > 0)
                            smoothScrollBy(0, delta);
                    } else {
                        scrollBy(0, contentChange + getScrollY());
                        smoothScrollBy(0, -getScrollY());
                    }
                }
            } else {
                double oldContentWidth = ((AbstractAGDataFeedDataDesc) getDescriptor()).getContentWidth();
                contentChange = (int) Math.ceil(mCalcDesc.getContentWidth() - oldContentWidth);
                if (oldContentWidth > 0 && contentChange > 0) {

                    if (mDescriptor.isInverted()) {
                        int delta = (int) Math.ceil(
                                mCalcDesc.getContentWidth()
                                        + mCalcDesc.getPaddingLeft()
                                        + mCalcDesc.getPaddingRight()
                                        - mCalcDesc.getWidth()
                                        - getInnerSroll().getScrollX());
                        if (delta > 0)
                            getInnerSroll().smoothScrollBy(delta, 0);
                    } else {
                        getInnerSroll().scrollBy(contentChange + getInnerSroll().getScrollX(), 0);
                        getInnerSroll().smoothScrollBy(-getInnerSroll().getScrollX(), 0);
                    }
                }
            }
        }

        if (!isLoadingDisplayed()) {
            ((AbstractAGDataFeedDataDesc) getDescriptor()).setContentHeight(mCalcDesc.getContentHeight());
            ((AbstractAGDataFeedDataDesc) getDescriptor()).setContentWidth(mCalcDesc.getContentWidth());
        }
    }

    private void loadMoreViews() {
        //remove loading view
        getInnerSroll().removeView(mAdapter.pollLastItem());
        mAdapter.initFeed();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDescriptor instanceof IFeedClient) {
            IFeedClient feedClient = (IFeedClient) mDescriptor;
            DownloadFeedCommand command = feedClient.getDownloadCommad();
            if (command!=null)
                command.cancel();
        }
    }

    @Override
    public AGDataFeedScrollView getScrollView() {
        return getInnerSroll();
    }

    @Override
    public int getFeedScrollX() {
        return getScrollX();
    }

    @Override
    public int getFeedScrollY() {
        return getScrollY();
    }

    @Override
    public AbstractAGContainerDataDesc getFeedDescriptor() {
        return getDescriptor();
    }

    @Override
    public ScrollType getFeedScrollType() {
        return getScrollType();
    }
}
