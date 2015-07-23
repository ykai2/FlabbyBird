package com.example.ykai.flabbybird;

import android.content.Context;
import android.util.TypedValue;

import java.security.KeyRep;

/**
 * Created by ykai on 2015/7/23.
 */
public class Util {
    public static int dp2px(Context context,float dp){
        int px=Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources()
                .getDisplayMetrics()
        ));
        return px;
    }
}
