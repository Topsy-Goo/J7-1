package ru.gb.antonov.j71;

import org.springframework.core.env.Environment;
import org.springframework.util.MultiValueMap;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.entities.Product;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Factory {

    public static final boolean DRYCART = true;
    //public static final boolean FLOAT_POINT = true; //< в isDecimalNumber() указывает, нужно ли считать точку/запятую частью числа.

    public static       BigDecimal MIN_PRICE = BigDecimal.valueOf (0.0);
    public static final BigDecimal MAX_PRICE = BigDecimal.valueOf (Double.MAX_VALUE);

    public static Integer PROD_PAGESIZE_DEF = 6;
    public static final int PROD_TITLELEN_MIN   = 3;
    public static final int PROD_TITLELEN_MAX   = 255;
    public static final int PRODCAT_NAMELEN_MIN = 1;
    public static final int PRODCAT_NAMELEN_MAX = 128;
    public static final int LOGIN_LEN_MIN = 3;
    public static final int LOGIN_LEN_MAX = 36;
    public static final int PASS_LEN_MIN  = 3;
    public static final int PASS_LEN_MAX  = 128;
    public static final int EMAIL_LEN_MIN = 5;
    public static final int EMAIL_LEN_MAX = 64;
    public static final int ORDERSTATE_SHORTNAME_LEN    = 16;
    public static final int ORDERSTATE_FRIENDLYNAME_LEN = 64;
    public static final int DELIVERING_COUNTRYCODE_LEN      = 2;
    public static final int DELIVERING_POSTALCODE_LEN       = 6;
    public static final int DELIVERING_REGION_LEN_MAX       = 60;
    public static final int DELIVERING_TOWN_VILLAGE_LEN_MAX = 100;
    public static final int DELIVERING_STREET_HOUSE_LEN_MAX = 100;
    public static final int DELIVERING_APARTMENT_LEN_MAX    = 20;
    public static final int DELIVERING_PHONE_LEN_MAX    = 20;
    public static final int DELIVERING_PHONE_LEN_MIN = 1;
    public static final int PRODUCTREVIEW_LEN_MAX    = 2048;
    public static final int PRICE_PRECISION          = 10;
    public static final int PRICE_SCALE = 2;

    public static final String COLNAME_CREATED_AT = "created_at";
    public static final String COLNAME_UPDATED_AT = "updated_at";

    public static final String USE_DEFAULT_STRING = null;
    public static final String BRAND_NAME_ENG = "Marketplace";
    public static final String STR_EMPTY      = "";
    public static final String BEARER_        = "Bearer ";
    public static final String PRODUCT_TITLE_FIELD_NAME = "title";
    public static final String PRODUCT_PRICE_FIELD_NAME = "price";
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

    public static String   CART_PREFIX_DEFAULT = STR_EMPTY;
    public static String   CART_PREFIX_        = CART_PREFIX_DEFAULT;
    public static Long     CART_LIFE_GUEST_DEFAULT = 30L;  //< срок жизни гостевой корзины
    public static Duration CART_LIFE_GUEST     = Duration.ofDays(30L);  //< срок жизни гостевой корзины
    public static Duration CART_LIFE_DELETED   = Duration.ofSeconds(1L); //< срок жизни удалённой корзины
    public static Duration DONOT_SET_CART_LIFE = null;

    public static final Locale RU_LOCALE = new Locale ("ru", "RU");
    public static final MultiValueMap<String, String> NO_FILTERS = null;
//------------------------------------------------------------------------
    static {
        checkClassForPresenceOfFields (Product.class, PRODUCT_TITLE_FIELD_NAME, PRODUCT_PRICE_FIELD_NAME);
    }

/** Считываем настройки из файла настроек. */
    public static void init (Environment env) {
        println ("\n************************* Считывание настроек: *************************");

        CART_PREFIX_ = env.getProperty ("app.cart.prefix", CART_PREFIX_DEFAULT);
        println ("app.cart.prefix: "+ CART_PREFIX_);

        Long l = stringToLong (env.getProperty ("app.cart.life", CART_LIFE_GUEST_DEFAULT.toString()));
        if (l != null) {
            CART_LIFE_GUEST = Duration.ofDays (l);
            println ("app.cart.life: " + CART_LIFE_GUEST);
        }

        Integer i = stringToInteger (env.getProperty ("views.shop.page.items", PROD_PAGESIZE_DEF.toString()));
        if (i != null) {
            PROD_PAGESIZE_DEF = i;
            println ("views.shop.page.items: "+ PROD_PAGESIZE_DEF);
        }
        println ("************************** Настройки считаны: **************************");
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

    public static Integer stringToInteger (String s) {
        Integer result;
        try {
            result = Integer.parseInt (s.trim());
        }
        catch (NumberFormatException e) { result = null; }
        return result;
    }

    public static Long stringToLong (String s) {
        Long result;
        try {
            result = Long.parseLong (s.trim());
        }
        catch (NumberFormatException e) { result = null; }
        return result;
    }

    public static Double stringToDouble (String s) {
        Double result;
        try {
            result = Double.parseDouble (s.trim());
        }
        catch (NumberFormatException e) { result = null; }
        return result;
    }

/** Проверка на наличие в классе {@code tClass} полей с указанными именами. Метод предназначен для проверки на
стадии запуска приложения; при пом.исключения метод сигнализирует о том, что было изменено имя поля класса,
 без согласования с методами или переменными, которые это имя используют.
@param tClass класс, в котором проверяется наличие поля с указанным именем.
@param fieldNames имена полей, наличие которых требуется проверить.
@throws RuntimeException если хотя бы одно поле с именем из fieldNames не нашлось. */
    public static <T> void checkClassForPresenceOfFields (Class<T> tClass, String... fieldNames) {

        if (fieldNames != null && fieldNames.length > 0) {
            List<String> classFields = Arrays.stream (tClass.getDeclaredFields())
                                             .map (Field::getName)
                                             .collect (Collectors.toList());
            List<String> names = new ArrayList<>(Arrays.asList (fieldNames));

            names.removeIf (classFields::contains);
            if (!names.isEmpty())
                throw new RuntimeException (String.format ("\nОШИБКА! В классе %s не найдены поля:\n\t%s\n",
                                            tClass.getName(), names));
        }
    }

    public static void println (String s) { System.out.println (s); }
}
