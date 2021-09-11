package ru.gb.antonov.j71;

public class Factory
{
    public static final String  STR_EMPTY   = "";
    public static final boolean RECALC_CART = false;  //< вкл/выкл пересчёт корзины при её изменении.
    //public static final boolean CREATE_IFABSENT = true;

    public static final int PROD_PAGESZ_DEF   = 6;
    public static final int PROD_TITLELEN_MIN   = 3;
    public static final int PROD_TITLELEN_MAX   = 255;
    public static final int PRODCAT_NAMELEN_MIN = 1;
    public static final int PRODCAT_NAMELEN_MAX = 255;

    public static final String PROMPT_PROD_NOTNULL = "задайте название для продукта";
    public static final String PROMPT_PROD_TITLEN =
        String.format ("длина названия [%d…%d] символов",
                       PROD_TITLELEN_MIN, PROD_TITLELEN_MAX);

    public static final String PROMPT_PRODCAT_NOTNULL = "задайте название для категории продукта";
    public static final String PROMPT_PRODCAT_NAMELEN =
        String.format ("длина названия категории продуктов [%d…%d] символов",
                       PRODCAT_NAMELEN_MIN, PRODCAT_NAMELEN_MAX);

    //public static final String PROMPT_PRICE_MIN_S = "НЕотрицательная цена продуктов";
    public static final String PROMPT_PRICE_MIN = "НЕотрицательная цена продукта";
    public static final String PROMPT_QUANTITY_MIN = "НЕотрицательное количество продуктов";
    //
}
//