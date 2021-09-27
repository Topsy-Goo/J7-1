package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name="", propOrder={"soapProducts"})
@XmlRootElement (name="getProductSoapRangeByIdResponse")
public class GetProductSoapRangeByIdResponse
{
    protected List<ProductSoap> soapProducts;

    public List<ProductSoap> getSoapProducts () { return soapProducts; }
    public void setSoapProducts (List<ProductSoap> soapProducts) { this.soapProducts = soapProducts; }
}
