package ru.gb.antonov.j71.beans.soap.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"id"})
@XmlRootElement(name="getProductSoapByIdRequest")
public class GetProductSoapByIdRequest
{
    protected long id;

    public long getId() { return id; }
    public void setId(long value) { id = value; }
}
