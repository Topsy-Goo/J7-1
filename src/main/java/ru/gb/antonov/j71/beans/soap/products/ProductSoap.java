package ru.gb.antonov.j71.beans.soap.products;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSoap",
         propOrder = {"id", "title", "price", "rest", "categoryName", "createdAt", "updatedAt"})
public class ProductSoap
{
    @XmlElement(required=true)    protected long id;
    @XmlElement(required=true)    protected String title;
    @XmlElement(required=true)    protected double price;
    @XmlElement(required=true)    protected int rest;
    @XmlElement(required=true)    protected String categoryName/* = new ProductsCategorySoap (p.getCategory())*/;
    @XmlElement(required=true)    protected long createdAt;
    @XmlElement(required=true)    protected long updatedAt;

    public ProductSoap (){}
    public ProductSoap (long pId, String pTitle, double pPrice, int pRest,
                        String pCategoryName, long pCreatedAt, long pUpdatedAt)
    {   id        = pId;
        title     = pTitle;
        price     = pPrice;
        rest      = pRest;
        categoryName = pCategoryName;
        createdAt = pCreatedAt;
        updatedAt = pUpdatedAt;
    }
}
