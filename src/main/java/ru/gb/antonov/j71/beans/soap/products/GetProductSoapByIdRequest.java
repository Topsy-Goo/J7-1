package ru.gb.antonov.j71.beans.soap.products;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"id"})
@XmlRootElement(name="getProductSoapByIdRequest")
public class GetProductSoapByIdRequest
{
    @XmlElement (required=true)    protected long id;
}
