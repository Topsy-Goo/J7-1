package ru.gb.antonov.j71.beans.soap.products;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"productSoap"})
@XmlRootElement(name="getProductSoapByIdResponse")
public class GetProductSoapByIdResponse {

    @XmlElement (required=true)    protected ProductSoap productSoap;
}
