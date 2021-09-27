package ru.gb.antonov.j71.beans.soap.products;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name="", propOrder={"soapProducts"})
@XmlRootElement (name="getProductSoapRangeByIdResponse")
public class GetProductSoapRangeByIdResponse
{
    @XmlElement (required=true)    protected List<ProductSoap> soapProducts;
}
