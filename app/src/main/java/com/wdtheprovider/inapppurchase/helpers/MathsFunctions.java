package com.wdtheprovider.inapppurchase.helpers;

/**
 * File Created by Dingaan Letjane
 * 2023/05/13
 **/

public class MathsFunctions {

    public static double roundTo2Decimal(double value){
        return  (double) Math.round(value * 100) / 100;
    }
}
