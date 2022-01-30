package ru.gb.antonov.j71.beans.soap.products;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

@Data
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (
    name = "ProductSoap",
    propOrder = {"id", "title", "price", "rest", "measure", "categoryName",
                 "createdAt", "updatedAt"})
public class ProductSoap {

    @XmlElement(required=true)  protected long id;
    @XmlElement(required=true)  protected String title;
    @XmlElement(required=true)  protected BigDecimal price;
    @XmlElement(required=true)  protected int rest;
    @XmlElement(required=true)  protected String measure;
    @XmlElement(required=true)  protected String categoryName/* = new ProductsCategorySoap (p.getCategory())*/;
    @XmlElement(required=true)  protected long createdAt;
    @XmlElement(required=true)  protected long updatedAt;

    public ProductSoap (){}
    public ProductSoap (long pId, String pTitle, BigDecimal pPrice, int pRest, String pMeasure,
                        String pCategoryName, long pCreatedAt, long pUpdatedAt)
    {   id    = pId;
        title = pTitle;
        price = pPrice;
        rest  = pRest;
        measure      = pMeasure;
        categoryName = pCategoryName;
        createdAt    = pCreatedAt;
        updatedAt    = pUpdatedAt;
    }
}
