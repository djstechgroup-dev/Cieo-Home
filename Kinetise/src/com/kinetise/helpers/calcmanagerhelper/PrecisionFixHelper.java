package com.kinetise.helpers.calcmanagerhelper;

import java.math.BigDecimal;

/**
 * Class allow achieve precision fix for floating values to 6 decimal places. Used by calculates classes to remove
 * found on web problem with floating point operations eg. 6.4 + 9.8 = 16.200000000000003.
 * In Java best solution is to use {@link java.math.BigDecimal} class that offers currently best known rounding solutions.
 * !WARNING!
 * Do not ever use something like <p>{@code a=a*1000; a=Math.round(a/1000);}<p/> it may fail in many cases
 * <br/>
 * @author: Marcin Narowski
 * Date: 17.10.13
 * Time: 10:52
 */
public class PrecisionFixHelper {

    private static final int DEFAULT_PRECISION = 6;
    /**
     * Converts number to given precision - n decimal places.
     * @param pNumer Number to be rounded
     * @param pPrecision Requested precision
     * @return Properly rounded value of number
     * */
    public final static double toPrecision(double pNumer, int pPrecision) {
        BigDecimal bigDecimal = new BigDecimal(pNumer);
        return bigDecimal.setScale(pPrecision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * Converts number to default precision {@link com.kinetise.helpers.calcmanagerhelper.PrecisionFixHelper#DEFAULT_PRECISION}
     * @see com.kinetise.helpers.calcmanagerhelper.PrecisionFixHelper#toPrecision(double, int)
     * */
    public final static double toPrecision(double pNumber) {
        return toPrecision(pNumber, DEFAULT_PRECISION);
    }
}
