package ru.gb.antonov.j71;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.entities.dtos.OrderDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class Factory
{
    public static final String  STR_EMPTY   = "";
    public static final boolean RECALC_CART = false;  //< TODO вкл/выкл пересчёт корзины при её изменении.

    public static final Double MIN_PRICE = 0.0;
    public static final Double MAX_PRICE = Double.MAX_VALUE;

    public static final int PROD_PAGESIZE_DEF = 6;
    public static final int PROD_TITLELEN_MIN = 3;
    public static final int PROD_TITLELEN_MAX   = 255;
    public static final int PRODCAT_NAMELEN_MIN = 1;
    public static final int PRODCAT_NAMELEN_MAX = 255;

    public static final int LOGIN_LEN_MIN = 3;
    public static final int LOGIN_LEN_MAX = 32;
    public static final int PASS_LEN_MIN = 3;
    public static final int PASS_LEN_MAX = 128;

    public static final String ORDERSTATE_NONE = "NONE";
    public static final String ORDERSTATE_PENDING = "PENDING";
    public static final String ORDERSTATE_SERVING = "SERVING";
    public static final String ORDERSTATE_PAYED = "PAYED";
    public static final String ORDERSTATE_CANCELED = "CANCELED";
    public static final int ORDERSTATE_STATE_LEN = 16;
    //public static final int ORDERSTATE_STATE_FRIENDLYNAME_LEN = 32;

    public static final Locale RU_LOCALE = new Locale ("ru", "RU");
    public static final String CART_PREFIX_ = "GB_RU_J7_WEBSHOP_";

//------------------------------------------------------------------------
    public static String orderCreationTimeToString (LocalDateTime ldt)
    {
        return (ldt != null) ? ldt.format(DateTimeFormatter.ofPattern ("d MMMM yyyy, HH:mm:ss", RU_LOCALE))
                             : "(?)";
    }

    public static boolean hasEmailFormat (String email)
    {
        boolean ok = false;
        int at = email.indexOf ('@');
        if (at > 0 && email.indexOf ('@', at +1) < 0)
        {
            int point = email.indexOf ('.', at);
            ok = point >= at +2;
        }
        return ok;
    }

    public static String  validateString (String s, int minLen, int maxLen)
    {
        if (s != null && minLen > 0 && minLen <= maxLen)
        {
            s = s.trim();
            int len = s.length();
            if (len >= minLen && len <= maxLen)
            {
                return s;
            }
        }
        return null;
    }

    public static Collection<OrderDto> newOrderDtosCollection (int size)
    {
        return new ArrayList<>(size);
    }

    public static String cartKeyByLogin (String login)
    {
        String postfix = validateString (login, LOGIN_LEN_MIN, LOGIN_LEN_MAX);
        if (postfix != null)
            return CART_PREFIX_ + postfix;
        throw new UnableToPerformException ("cartKeyByLogin(): некорректный логин: "+ login);
    }
}
