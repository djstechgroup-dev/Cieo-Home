package com.kinetise.support.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author: Marcin Narowski
 * Date: 20.05.13
 * Time: 09:02
 */
public class SizeAwareRelativeLayout extends RelativeLayout {
    private OnMeasureListener mOnMeasureListener;

    public SizeAwareRelativeLayout(Context context) {
        super(context);
        initLayerType();
    }

    private void initLayerType() {
    }

    public SizeAwareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayerType();
    }

    public SizeAwareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayerType();
    }

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        mOnMeasureListener = onMeasureListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Troche brzydkie jednak... Ten layout jest rootem, dzieki czemu przy mierzeniu 1 layoutu wywolujemy
		//calc managera, Mapa wymaga posiadania juz okreslonego rozmiaru w onMeasure inaczej fragment mapy bedzie sie renderowal wiekszy niz 
		//AgMapView. W poprzedniej implementacji (onMeasure wywoluje sie praktycznie na koncu layoutowania), tam poznawalismy rozmiar ekranu (przestrzen z ktorej moglismy korzystac)
		//co sprawialo ze Mapa miala OnMeasure ze zlymi parametrami i wystepowal blad.
		//Jesli mapa zmierzy sie tak jak jej kaze parent to wtedy bez wzgledu na rozmiar AGMapView, AGSupportMapFragment wyliczy sobie rozmiar pelnoekranowy
        if(mOnMeasureListener !=null){
            mOnMeasureListener.onMeasure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
