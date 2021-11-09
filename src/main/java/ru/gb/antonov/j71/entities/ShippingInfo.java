package ru.gb.antonov.j71.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.gb.antonov.j71.entities.dtos.ShippingInfoDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Locale;

import static ru.gb.antonov.j71.Factory.STR_EMPTY;

@Entity  @Data  @NoArgsConstructor  @Table (name="shipping_info")
public class ShippingInfo {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column (name="country_code", nullable = false)
    private String countryCode = STR_EMPTY;

    @Column (name="postal_code", nullable = false)
    private String postalCode = STR_EMPTY;

    @Column (name="region", nullable = false)
    private String region = STR_EMPTY;

    @Column (name="town_village", nullable = false)
    private String townVillage = STR_EMPTY;

    @Column (name="street_house", nullable = false)
    private String streetHouse = STR_EMPTY;

    @Column (name="apartment", nullable = false)
    private String apartment = STR_EMPTY;

    @Column (name="phone", nullable=false)
    private String phone = STR_EMPTY;

    @CreationTimestamp    @Column(name="created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column (name="updated_at")
    private LocalDateTime updatedAt;
//-------------------------------------------------------------------------
/** Приводим в порядок некоторые поля. */
    public ShippingInfo adjust () {
        countryCode = countryCode.trim().toUpperCase(Locale.ROOT);
        return this;
    }


    public static ShippingInfo fromShippingInfoDto (ShippingInfoDto dto) {
        ShippingInfo si = new ShippingInfo();
        if (dto != null)
        {
            si.countryCode = setOrEmpty (dto.getCountryCode());
            si.postalCode  = setOrEmpty (dto.getPostalCode());
            si.region      = setOrEmpty (dto.getRegion());
            si.townVillage = setOrEmpty (dto.getTownVillage());
            si.streetHouse = setOrEmpty (dto.getStreetHouse());
            si.apartment   = setOrEmpty (dto.getApartment());
            si.phone       = setOrEmpty (dto.getPhone());
        }
        return si;
    }
    private static String setOrEmpty (String value) {   return (value == null) ? STR_EMPTY : value;   }
//-------------------------------------------------------------------------

    @Override public String toString()    {   return getAddress()+ " телефон: " + getPhone();    }

    public String getAddress () {
                           // RU, 125433, МО, Москва, Королёва, 12/4, 192.
        return String.format ("%s, %s, %s, %s, %s, %s.",
            countryCode, postalCode, region, townVillage, streetHouse, apartment);
    }
}
