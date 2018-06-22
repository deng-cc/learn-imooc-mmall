package com.mmall.util;

import java.math.BigDecimal;

/**
 * BigDecimalUtil.
 *
 * @author Dulk
 * @version 20180621
 * @date 2018/6/21
 */
public class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    public static BigDecimal add(double v1, double v2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(v1));
        BigDecimal bd2 = new BigDecimal(Double.toString(v2));
        return bd1.add(bd2);
    }

    public static BigDecimal sub(double v1, double v2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(v1));
        BigDecimal bd2 = new BigDecimal(Double.toString(v2));
        return bd1.subtract(bd2);
    }

    public static BigDecimal mul(double v1, double v2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(v1));
        BigDecimal bd2 = new BigDecimal(Double.toString(v2));
        return bd1.multiply(bd2);
    }

    public static BigDecimal div(double v1, double v2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(v1));
        BigDecimal bd2 = new BigDecimal(Double.toString(v2));
        return bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP);
    }

}
