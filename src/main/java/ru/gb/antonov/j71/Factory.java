package ru.gb.antonov.j71;

public class Factory
{
    public static final String  STR_EMPTY   = "";
    public static final boolean RECALC_CART = false;  //< TODO вкл/выкл пересчёт корзины при её изменении.

    public static final Double MIN_PRICE = 0.0;
    public static final Double MAX_PRICE = Double.MAX_VALUE;

    public static final int PROD_PAGESZ_DEF   = 6;
    public static final int PROD_TITLELEN_MIN   = 3;
    public static final int PROD_TITLELEN_MAX   = 255;
    public static final int PRODCAT_NAMELEN_MIN = 1;
    public static final int PRODCAT_NAMELEN_MAX = 255;

    public  static final String ORDERSTATE_NONE = "NONE";
    public  static final String ORDERSTATE_PENDING = "PENDING";
    public  static final String ORDERSTATE_SERVING = "SERVING";
    public  static final String ORDERSTATE_PAYED = "PAYED";
    public  static final String ORDERSTATE_CANCELED = "CANCELED";
}
