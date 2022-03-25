package com.kinetise.data.systemdisplay.views.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kinetise.helpers.RWrapper;
import com.kinetise.support.logger.Logger;

public class TiledView extends FrameLayout{

	private int mTileSize = 256;
	private LayoutInflater mLayoutInflater;
	private String mText;

	public TiledView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public TiledView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public TiledView(Context context) {
		super(context);
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setTileSize(int tileSize){
		mTileSize = tileSize;
	}
	
	public void setText(String text){
		mText = text;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		int width = right-left;
		int height = bottom - top;
		
		int rows = (int) Math.ceil(height/(float)mTileSize);
		int cols = (int) Math.ceil(width/(float)mTileSize);
		
		if(getChildCount() != rows*cols){
			removeAllViews();
			for(int x= 0 ; x < rows*cols ; x++){
				TextView view = (TextView) mLayoutInflater.inflate(RWrapper.layout.mapPlaceholder, this, false);
				view.setText(mText);
//				view.setGravity(Gravity.CENTER);
//				FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(mTileSize, mTileSize);
//				params.gravity = Gravity.CENTER;
				addView(view);
			}
		}

		int d = 0;
			for(int x = 0 ; x < cols ; x++){
				for(int y = 0 ; y < rows ; y++){
				Logger.e("layout", "index " + d);
				getChildAt(d).layout(x*(mTileSize), y*(mTileSize), (x+1)*mTileSize, (y+1)*mTileSize);
				getChildAt(d).measure(MeasureSpec.makeMeasureSpec(mTileSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mTileSize, MeasureSpec.EXACTLY));
				d++;
			}
		}
	}
	
}
