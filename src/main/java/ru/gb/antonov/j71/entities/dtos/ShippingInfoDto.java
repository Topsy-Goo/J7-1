package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.TestOnly;

import javax.validation.constraints.NotNull;

import static ru.gb.antonov.j71.Factory.*;

@Data    @NoArgsConstructor
public class ShippingInfoDto {

    private Long id;

    @Length (min=DELIVERING_COUNTRYCODE_LEN, max=DELIVERING_COUNTRYCODE_LEN, message="\rКод страны: 2 латинских символа.")
    private String countryCode = "RU";

    @Length (min=DELIVERING_POSTALCODE_LEN, max=DELIVERING_POSTALCODE_LEN, message="\rПочтовый индекс: 6 цифр.")
    private String postalCode;

    @Length (max=DELIVERING_REGION_LEN_MAX, message= "\n" + "Название региона (субъекта федерации): до 60 символов.")
    private String region;

    @Length (max=DELIVERING_TOWN_VILLAGE_LEN_MAX, message="\rНазвание населённого пункта: до 100 символов.")
    private String townVillage;

    @Length (max=DELIVERING_STREET_HOUSE_LEN_MAX, message="\rУлица и номер дома: до 100 символов.")
    private String streetHouse;

    @Length (max=DELIVERING_APARTMENT_LEN_MAX, message="\rНомер квартиры/офиса: до 20 символов.")
    private String apartment;

    @NotNull (message="\rУкажите номер телефона.")
    @Length (min=DELIVERING_PHONE_LEN_MIN, max=DELIVERING_PHONE_LEN_MAX, message="\rНомер телефона, 10…20 символов.\rПример: 8006004050 или +7(800) 600-40-50.")
    private String phone;
//-------------------------------------------------------------------------

    @Override public String toString() {
        return getAddress()+ " телефон: " + getPhone();
    }

    public String getAddress () {//RU, 125433, МО, Москва, Королёва 12/4, 192,
       return String.format ("%s, %s, %s, %s, %s, %s.",
                              countryCode, postalCode, region, townVillage, streetHouse, apartment);
    }

    @TestOnly
    public static ShippingInfoDto dummyShippingInfoDto () {

        ShippingInfoDto sidto = new ShippingInfoDto();
        sidto.countryCode = "RU";
        sidto.postalCode  = STR_EMPTY;
        sidto.region      = STR_EMPTY;
        sidto.townVillage = STR_EMPTY;
        sidto.streetHouse = STR_EMPTY;
        sidto.apartment   = STR_EMPTY;
        return sidto;
    }
}
