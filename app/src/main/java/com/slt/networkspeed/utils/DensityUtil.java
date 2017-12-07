package com.slt.networkspeed.utils;

import android.content.Context;
import android.content.res.Resources;

public class DensityUtil {  

    /** 
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

    /** 
     * 根据手机的分辨率�?px(像素) 的单�?转成�?dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属�?scaledDensity�?
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属�?scaledDensity�?
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    } 
    public static int px2dip(int pxValue)
   	{
   		final float scale = Resources.getSystem().getDisplayMetrics().density;
   		return (int) (pxValue / scale + 0.5f);
   	}


   	public static float dip2px(float dipValue)
   	{
   		final float scale = Resources.getSystem().getDisplayMetrics().density;
   		return  (dipValue * scale + 0.5f);
   	}
} 