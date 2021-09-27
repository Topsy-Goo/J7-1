package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"productSoap"})
@XmlRootElement(name="getProductSoapByIdResponse")
public class GetProductSoapByIdResponse {

    @XmlElement(required=true)
    protected ProductSoap productSoap;

    public ProductSoap getProductSoap () { return productSoap; }
    public void setProductSoap (ProductSoap value) { productSoap = value; }

}
