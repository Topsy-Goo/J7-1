package ru.gb.antonov.j71;

import org.springframework.core.env.Environment;
import org.springframework.util.MultiValueMap;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.entities.Product;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Factory {

    public static final boolean DRYCART = true;
    public static final boolean FLOAT_POINT = true; //< в isDecimalNumber() указывает, нужно ли считать точку/запятую частью числа.

    public static       BigDecimal MIN_PRICE = BigDecimal.valueOf (0.01);
    public static final BigDecimal MAX_PRICE = BigDecimal.valueOf (Double.MAX_VALUE);

    public static int PROD_PAGESIZE_DEF = 6;
    public static final int PROD_TITLELEN_MIN   = 3;
    public static final int PROD_TITLELEN_MAX   = 255;
    public static final int PRODCAT_NAMELEN_MIN = 1;
    public static final int PRODCAT_NAMELEN_MAX = 255;
    public static final int LOGIN_LEN_MIN = 3;
    public static final int LOGIN_LEN_MAX = 36;
    public static final int PASS_LEN_MIN  = 3;
    public static final int PASS_LEN_MAX  = 128;
    public static final int EMAIL_LEN_MIN = 5;
    public static final int EMAIL_LEN_MAX = 64;
    public static final int DELIVERING_ADDRESS_LEN_MAX  = 255;
    public static final int DELIVERING_PHONE_LEN_MAX    = 16;
    public static final int DELIVERING_PHONE_LEN_MIN    = 1;
    public static final int ORDERSTATE_SHORTNAME_LEN    = 16;
    public static final int ORDERSTATE_FRIENDLYNAME_LEN = 64;
    public static final int DELIVERING_COUNTRYCODE_LEN      = 2;
    public static final int DELIVERING_POSTALCODE_LEN       = 6;
    public static final int DELIVERING_REGION_LEN_MAX       = 100;
    public static final int DELIVERING_TOWN_VILLAGE_LEN_MAX = 100;
    public static final int DELIVERING_STREET_HOUSE_LEN_MAX = 100;
    public static final int DELIVERING_APARTMENT_LEN_MAX    = 100;

    public static final String USE_DEFAULT_STRING = null;
    public static final String BRAND_NAME_ENG = "Marketplace";
    public static final String STR_EMPTY      = "";
    public static final String BEARER_        = "Bearer ";
    public static final String PRODUCT_PRICE_FIELD_NAME = Product.getPriceFieldName();
    public static final String PRODUCT_TITLE_FIELD_NAME = Product.getTitleFieldName();
    public static final String ORDERSTATE_NONE     = "NONE";
    public static final String ORDERSTATE_PENDING  = "PENDING";
    public static final String ORDERSTATE_SERVING  = "SERVING";
    public static final String ORDERSTATE_PAYED    = "PAYED";
    public static final String ORDERSTATE_CANCELED = "CANCELED";
    public static final String ROLE_USER       = "ROLE_USER";
    public static final String ROLE_ADMIN      = "ROLE_ADMIN";
    public static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";
    public static final String ROLE_MANAGER    = "ROLE_MANAGER";
    public static final String PERMISSION_EDIT_PRODUCT = "EDIT_PRODUCTS";
    public static final String PERMISSION_SHOPPING     = "SIMLE_SHOPPING";
    public static final String AUTHORIZATION_HDR_TITLE = "Authorization";
    public static final String ORDER_IS_EMPTY = " Заказ пуст. ";
    public static final String ERR_MINPRICE_OUTOF_RANGE = "Указаная цена (%f)\rменьше минимальной (%f).";

    public static String   CART_PREFIX_        = STR_EMPTY;
    public static Duration CART_LIFE_GUEST     = Duration.ofDays(30L);  //< срок жизни гостевой корзины
    public static Duration CART_LIFE_DELETED   = Duration.ofSeconds(1L); //< срок жизни удалённой корзины
    public static Duration DONOT_SET_CART_LIFE = null;

    public static final Locale RU_LOCALE = new Locale ("ru", "RU");
    public static final MultiValueMap<String, String> NO_FILTERS = null;
//------------------------------------------------------------------------

/** Считываем настройки из файла настроек. */
    public static void init (Environment env) {

        System.out.println ("\n************************* Считывание настроек: *************************");

        CART_PREFIX_ = env.getProperty ("app.cart.prefix");
        System.out.println ("app.cart.prefix: "+ CART_PREFIX_);

        String s = env.getProperty ("app.cart.life");

        if (isDecimalNumber (s, !FLOAT_POINT)) {
            CART_LIFE_GUEST = Duration.ofDays (Long.parseLong (s));
            System.out.println ("app.cart.life: " + CART_LIFE_GUEST);
        }

        if (isDecimalNumber (s = env.getProperty ("views.shop.page.items"), !FLOAT_POINT)) {

            PROD_PAGESIZE_DEF = Integer.parseInt (s);
            System.out.println ("views.shop.page.items: "+ PROD_PAGESIZE_DEF);
        }

        if (isDecimalNumber (s = env.getProperty ("app.product.price.min"), FLOAT_POINT)) {

            MIN_PRICE = BigDecimal.valueOf(Double.parseDouble(s));
            if (MIN_PRICE.compareTo(BigDecimal.ZERO) < 0)
                MIN_PRICE = BigDecimal.ZERO;
            System.out.println ("app.product.price.min: "+ MIN_PRICE);
        }
//        if (isDecimalNumber (s = env.getProperty (""), FLOAT_POINT))      ;
        System.out.println ("************************** Настройки считаны: **************************");
    }

/** Составляем строку даты и времени как:  {@code d MMMM yyyy, HH:mm:ss}<p>
    Пример строки:  {@code 20 сентября 2021, 23:10:29}
*/
    public static String orderCreationTimeToString (LocalDateTime ldt) {

        return (ldt != null) ? ldt.format(DateTimeFormatter.ofPattern ("d MMMM yyyy, HH:mm:ss", RU_LOCALE))
                             : "(?)";
    }

    public static boolean hasEmailFormat (String email) {

        boolean ok = false;
        int at = email.indexOf ('@');

        if (at > 0 && email.indexOf ('@', at +1) < 0) {
            int point = email.indexOf ('.', at);
            ok = point >= at +2;
        }
        return ok;
    }

    public static String  validateString (String s, int minLen, int maxLen) {

        if (s != null && minLen > 0 && minLen <= maxLen) {
            s = s.trim();
            int len = s.length();

            if (len >= minLen && len <= maxLen)
                return s;
        }
        return null;
    }

/** Составляем ключ как:  GB_RU_J7_WEBSHOP_ + login
*/
    public static String cartKeyByLogin (String login) {

        String postfix = validateString (login, LOGIN_LEN_MIN, LOGIN_LEN_MAX);
        if (postfix != null)
            return CART_PREFIX_ + postfix;
        throw new UnableToPerformException ("cartKeyByLogin(): некорректный логин: "+ login);
    }

/** Проверяем, содержатся ли в строке посторонние символы, которые могут вызвать исключение при переводе строки вчисло. (Занятно, что разработчики Java не позаботились отакой мелочи.)
 @param s собственно срока.
 @param floatPoint {@code true} означает, что число может содержать десятичную точку или запятую. */
    public static boolean isDecimalNumber (String s, boolean floatPoint) {

        if (s == null)
            return false;
        char[] arrchar = s.trim().toCharArray();

        for (char ch : arrchar) {
            if (ch < '0' || ch > '9') {
                if (!floatPoint || (ch != '.' && ch != ','))
                    return false;
                floatPoint = false;
            }
        }
        return true;
    }

/** Пробуем преобразовать строку в Double. Если не получилось, то пробуем преобразовать в Integer, а
потом в Double.
@param s строка-число
@return double
@throws NumberFormatException если строка s не может быть преобразована к числу. */
    public static double stringToDouble (String s) {
        if (s == null || s.isBlank())
            throw new NumberFormatException();
        double result;
        s = s.trim();
        try {
            result = Double.parseDouble (s);
        }
        catch (NumberFormatException e) {
            result = Integer.valueOf (s).doubleValue();
        }
        return result;
    }

/** Пробуем преобразовать строку в Integer. Если не получилось, то пробуем преобразовать в Double, а
потом в Integer.
@param s строка-число
@return int.
@throws NumberFormatException если строка s не может быть преобразована к числу. */
    public static int stringToInteger (String s) {
        if (s == null || s.isBlank())
            throw new NumberFormatException();
        int result;
        s = s.trim();
        try {
            result = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            result = Double.valueOf (s).intValue();
        }
        return result;
    }
}
