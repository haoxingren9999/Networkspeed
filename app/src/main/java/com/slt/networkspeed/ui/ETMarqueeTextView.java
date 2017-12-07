package com.slt.networkspeed.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ETMarqueeTextView extends TextView{
		
		private boolean mMarquee = true;
	
	    public ETMarqueeTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    public ETMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	    }

	    public ETMarqueeTextView(Context context) {
	        super(context);
	    }
	    
//	    public void startStopMarquee(boolean marquee){
//	    	mMarquee = marquee;
//			try {
//				Class localClass = Class.forName("android.widget.TextView");
//			    Method localMethod = localClass.getDeclaredMethod("startStopMarquee",Boolean.TYPE);
//		        localMethod.setAccessible(true);
//		        localMethod.invoke(this, marquee);
//			} catch (Exception e) {
//				
//			}
//	    
//	    }
	    
	    
	    @Override
	    public boolean isFocused() {
	    	return mMarquee;
	    }
}
